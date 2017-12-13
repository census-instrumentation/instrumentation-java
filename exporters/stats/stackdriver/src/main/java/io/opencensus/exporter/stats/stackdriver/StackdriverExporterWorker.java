/*
 * Copyright 2017, OpenCensus Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opencensus.exporter.stats.stackdriver;

import com.google.api.MetricDescriptor;
import com.google.api.MonitoredResource;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.monitoring.v3.CreateMetricDescriptorRequest;
import com.google.monitoring.v3.CreateTimeSeriesRequest;
import com.google.monitoring.v3.ProjectName;
import com.google.monitoring.v3.TimeSeries;
import io.opencensus.common.Duration;
import io.opencensus.common.Scope;
import io.opencensus.stats.View;
import io.opencensus.stats.ViewData;
import io.opencensus.stats.ViewManager;
import io.opencensus.trace.Sampler;
import io.opencensus.trace.Span;
import io.opencensus.trace.Status;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Worker {@code Runnable} that polls ViewData from Stats library and batch export to StackDriver.
 *
 * <p>{@code StackdriverExporterWorker} will be started in a daemon {@code Thread}.
 *
 * <p>The state of this class should only be accessed from the thread which {@link
 * StackdriverExporterWorker} resides in.
 */
@NotThreadSafe
final class StackdriverExporterWorker implements Runnable {

  private static final Logger logger = Logger.getLogger(StackdriverExporterWorker.class.getName());

  @VisibleForTesting static final int MAX_BATCH_EXPORT_SIZE = 3;

  private final long scheduleDelayMillis;
  private final String projectId;
  private final ProjectName projectName;
  private final MetricServiceClient metricServiceClient;
  private final ViewManager viewManager;
  private final MonitoredResource monitoredResource;
  private final Map<View.Name, View> registeredViews = new HashMap<View.Name, View>();

  private static final Tracer tracer = Tracing.getTracer();
  private static final Sampler probabilitySpampler = Samplers.probabilitySampler(0.0001);

  StackdriverExporterWorker(
      String projectId,
      MetricServiceClient metricServiceClient,
      Duration exportInterval,
      ViewManager viewManager,
      MonitoredResource monitoredResource) {
    this.scheduleDelayMillis = toMillis(exportInterval);
    this.projectId = projectId;
    projectName = ProjectName.newBuilder().setProject(projectId).build();
    this.metricServiceClient = metricServiceClient;
    this.viewManager = viewManager;
    this.monitoredResource = monitoredResource;
  }

  // Returns true if the given view is successfully registered to Stackdriver Monitoring, or the
  // exact same view has already been registered. Returns false otherwise.
  @VisibleForTesting
  boolean registerView(View view) {
    View existing = registeredViews.get(view.getName());
    if (existing != null) {
      if (existing.equals(view)) {
        // Ignore views that are already registered.
        return true;
      } else {
        // If we upload a view that has the same name with a registered view but with different
        // attributes, Stackdriver client will throw an exception.
        logger.log(
            Level.WARNING,
            "A different view with the same name is already registered: " + existing);
        return false;
      }
    }
    registeredViews.put(view.getName(), view);

    Span span = tracer.getCurrentSpan();
    span.addAnnotation("Create Stackdriver Metric.");
    try (Scope scope = tracer.withSpan(span)) {
      // TODO(songya): don't need to create MetricDescriptor for RpcViewConstants once we defined
      // canonical metrics. Registration is required only for custom view definitions. Canonical
      // views should be pre-registered.
      MetricDescriptor metricDescriptor =
          StackdriverExportUtils.createMetricDescriptor(view, projectId);
      if (metricDescriptor == null) {
        // Don't register interval views in this version.
        return false;
      }

      CreateMetricDescriptorRequest request =
          CreateMetricDescriptorRequest.newBuilder()
              .setNameWithProjectName(projectName)
              .setMetricDescriptor(metricDescriptor)
              .build();
      try {
        metricServiceClient.createMetricDescriptor(request);
        tracer.getCurrentSpan().addAnnotation("Finish creating MetricDescriptor.");
        tracer.getCurrentSpan().setStatus(Status.OK);
        return true;
      } catch (ApiException e) {
        logger.log(Level.WARNING, "ApiException thrown when creating MetricDescriptor.", e);
        tracer
            .getCurrentSpan()
            .addAnnotation("ApiException thrown when creating MetricDescriptor.");
        tracer
            .getCurrentSpan()
            .setStatus(Status.CanonicalCode.valueOf(e.getStatusCode().getCode().name()).toStatus());
        return false;
      } catch (Throwable e) {
        logger.log(Level.WARNING, "Exception thrown when creating MetricDescriptor.", e);
        tracer.getCurrentSpan().addAnnotation("Exception thrown when creating MetricDescriptor.");
        tracer.getCurrentSpan().setStatus(Status.UNKNOWN);
        return false;
      }
    }
  }

  // Polls ViewData from Stats library for all exported views, and upload them as TimeSeries to
  // StackDriver.
  @VisibleForTesting
  void export() {
    List<ViewData> viewDataList = Lists.newArrayList();
    for (View view : viewManager.getAllExportedViews()) {
      if (registerView(view)) {
        // Only upload stats for valid views.
        viewDataList.add(viewManager.getView(view.getName()));
      }
    }

    List<TimeSeries> timeSeriesList = Lists.newArrayList();
    for (ViewData viewData : viewDataList) {
      timeSeriesList.addAll(
          StackdriverExportUtils.createTimeSeriesList(viewData, monitoredResource));
    }
    for (List<TimeSeries> batchedTimeSeries :
        Lists.partition(timeSeriesList, MAX_BATCH_EXPORT_SIZE)) {
      Span span = tracer.getCurrentSpan();
      span.addAnnotation("Export Stackdriver TimeSeries.");
      try (Scope scope = tracer.withSpan(span)) {
        // Batch export 3 TimeSeries at one call, to avoid exceeding RPC header size limit.
        CreateTimeSeriesRequest request =
            CreateTimeSeriesRequest.newBuilder()
                .setNameWithProjectName(projectName)
                .addAllTimeSeries(batchedTimeSeries)
                .build();
        metricServiceClient.createTimeSeries(request);
        tracer.getCurrentSpan().addAnnotation("Finish exporting TimeSeries.");
        tracer.getCurrentSpan().setStatus(Status.OK);
      } catch (ApiException e) {
        logger.log(Level.WARNING, "ApiException thrown when exporting TimeSeries.", e);
        tracer.getCurrentSpan().addAnnotation("ApiException thrown when exporting TimeSeries.");
        tracer
            .getCurrentSpan()
            .setStatus(Status.CanonicalCode.valueOf(e.getStatusCode().getCode().name()).toStatus());
      } catch (Throwable e) {
        logger.log(Level.WARNING, "Exception thrown when exporting TimeSeries.", e);
        tracer.getCurrentSpan().addAnnotation("Exception thrown when exporting TimeSeries.");
        tracer.getCurrentSpan().setStatus(Status.UNKNOWN);
      }
    }
  }

  @Override
  public void run() {
    while (true) {
      // Start a new span with explicit 1/10000 sampling probability to avoid the case when user
      // sets the default sampler to always sample and we get the gRPC span of the stackdriver
      // export call always sampled and go to an infinite loop.
      try (Scope scope =
          tracer
              .spanBuilder("ExportStatsToStackdriverMonitoring")
              .setSampler(probabilitySpampler)
              .startScopedSpan()) {
        export();
        Thread.sleep(scheduleDelayMillis);
      } catch (InterruptedException ie) {
        // Preserve the interruption status as per guidance and stop doing any work.
        Thread.currentThread().interrupt();
        return;
      } catch (Throwable e) {
        logger.log(Level.WARNING, "Exception thrown by the Stackdriver stats exporter.", e);
      }
    }
  }

  private static final long MILLIS_PER_SECOND = 1000L;
  private static final long NANOS_PER_MILLI = 1000 * 1000;

  private static long toMillis(Duration duration) {
    return duration.getSeconds() * MILLIS_PER_SECOND + duration.getNanos() / NANOS_PER_MILLI;
  }
}
