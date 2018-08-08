/*
 * Copyright 2018, OpenCensus Authors
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

package io.opencensus.implcore.stats;

import io.opencensus.common.Timestamp;
import io.opencensus.metrics.LabelValue;
import io.opencensus.metrics.Metric;
import io.opencensus.metrics.MetricDescriptor;
import io.opencensus.metrics.MetricDescriptor.Type;
import io.opencensus.metrics.Point;
import io.opencensus.metrics.TimeSeriesCumulative;
import io.opencensus.metrics.TimeSeriesGauge;
import io.opencensus.metrics.TimeSeriesList;
import io.opencensus.metrics.TimeSeriesList.TimeSeriesCumulativeList;
import io.opencensus.metrics.TimeSeriesList.TimeSeriesGaugeList;
import io.opencensus.tags.TagValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.concurrent.GuardedBy;

/*>>>
import org.checkerframework.checker.nullness.qual.Nullable;
*/

// A class that stores a mapping from MetricDescriptor to lists of MutableMetricRows.
final class MetricMap {

  @GuardedBy("this")
  private final Map<MetricDescriptor, MutableMetricRows> map =
      new HashMap<MetricDescriptor, MutableMetricRows>();

  // Registers a MetricDescriptor, creates an entry in the map.
  // This method should only be called from MeasureToMap.registerView().
  synchronized void registerMetricDescriptor(
      MetricDescriptor metricDescriptor, Timestamp timestamp) {
    if (map.containsKey(metricDescriptor)) {
      return;
    }
    map.put(metricDescriptor, MutableMetricRows.create(metricDescriptor.getType(), timestamp));
  }

  // Records a data point to this MetricMap.
  // This method should only be called from CumulativeMutableViewData.record().
  synchronized void record(
      MetricDescriptor metricDescriptor,
      List</*@Nullable*/ TagValue> tagValues,
      MutableAggregation mutableAggregation,
      Timestamp now) {
    if (!map.containsKey(metricDescriptor)) {
      return;
    }
    map.get(metricDescriptor)
        .record(tagValues, mutableAggregation, now, metricDescriptor.getType());
  }

  synchronized void clearStats() {
    for (Entry<MetricDescriptor, MutableMetricRows> entry : map.entrySet()) {
      entry.getValue().map.clear();
    }
  }

  synchronized void resumeStatsCollection(Timestamp now) {
    for (Entry<MetricDescriptor, MutableMetricRows> entry : map.entrySet()) {
      entry.getValue().resumeStatsCollection(now);
    }
  }

  synchronized List<Metric> toMetrics() {
    List<Metric> metrics = new ArrayList<Metric>();
    for (Entry<MetricDescriptor, MutableMetricRows> entry : map.entrySet()) {
      MutableMetricRows mutableMetricRows = entry.getValue();
      if (mutableMetricRows.map.isEmpty()) {
        continue; // Skip MetricDescriptor with no data.
      }
      metrics.add(Metric.create(entry.getKey(), mutableMetricRows.toTimeSeriesList()));

      // Reset the data map once the rows are exported, so that we don't export duplicated Points.
      mutableMetricRows.map.clear();
    }
    return metrics;
  }

  // A class that stores a mapping from lists of label values to lists of points.
  // Each MutableMetricRows correspond to one MetricDescriptor.
  // Think of this class as a set of mutable time series.
  private abstract static class MutableMetricRows {

    /*
     * Each entry in this map is a list of rows, for example:
     *   [v1, v2] -> [1, 5, 10]
     *   [v1, v3] -> [-5, -8]
     *   ...
     */
    private final Map<List<LabelValue>, List<Point>> map =
        new LinkedHashMap<List<LabelValue>, List<Point>>();

    // Create MutableMetricRows based on the given type.
    private static MutableMetricRows create(Type type, Timestamp timestamp) {
      switch (type) {
        case GAUGE_INT64:
        case GAUGE_DOUBLE:
          return createGauge();
        case CUMULATIVE_DISTRIBUTION:
        case CUMULATIVE_DOUBLE:
        case CUMULATIVE_INT64:
          return createCumulative(timestamp);
      }
      throw new AssertionError();
    }

    private static MutableMetricRows createCumulative(Timestamp timestamp) {
      return new MutableMetricRowsCumulative(timestamp);
    }

    private static MutableMetricRows createGauge() {
      return new MutableMetricRowsGauge();
    }

    private void record(
        List</*@Nullable*/ TagValue> tagValues,
        MutableAggregation mutableAggregation,
        Timestamp timestamp,
        Type type) {
      List<LabelValue> labelValues = MetricUtils.tagValuesToLabelValues(tagValues);
      Point point = MetricUtils.mutableAggregationToPoint(mutableAggregation, timestamp, type);
      if (!map.containsKey(labelValues)) {
        map.put(labelValues, new ArrayList<Point>());
      }
      map.get(labelValues).add(point);
    }

    abstract TimeSeriesList toTimeSeriesList();

    abstract void resumeStatsCollection(Timestamp now);

    private static final class MutableMetricRowsCumulative extends MutableMetricRows {

      // Only cumulative time series has a start timestamp.
      private Timestamp startTime;

      private MutableMetricRowsCumulative(Timestamp startTime) {
        this.startTime = startTime;
      }

      @Override
      TimeSeriesList toTimeSeriesList() {
        List<TimeSeriesCumulative> timeSeriesCumulatives = new ArrayList<TimeSeriesCumulative>();
        for (Entry<List<LabelValue>, List<Point>> entry : super.map.entrySet()) {
          timeSeriesCumulatives.add(
              TimeSeriesCumulative.create(entry.getKey(), entry.getValue(), startTime));
        }
        return TimeSeriesCumulativeList.create(timeSeriesCumulatives);
      }

      @Override
      void resumeStatsCollection(Timestamp now) {
        // Reset start time to current time.
        this.startTime = now;
      }
    }

    private static final class MutableMetricRowsGauge extends MutableMetricRows {

      @Override
      TimeSeriesList toTimeSeriesList() {
        List<TimeSeriesGauge> timeSeriesGauges = new ArrayList<TimeSeriesGauge>();
        for (Entry<List<LabelValue>, List<Point>> entry : super.map.entrySet()) {
          timeSeriesGauges.add(TimeSeriesGauge.create(entry.getKey(), entry.getValue()));
        }
        return TimeSeriesGaugeList.create(timeSeriesGauges);
      }

      @Override
      void resumeStatsCollection(Timestamp now) {
        // Do nothing for Gauge stats.
      }
    }
  }
}
