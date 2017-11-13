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

package io.opencensus.stats;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import io.opencensus.common.Functions;
import io.opencensus.common.Timestamp;
import io.opencensus.stats.Measure.MeasureDouble;
import io.opencensus.stats.Measure.MeasureLong;
import io.opencensus.stats.ViewData.AggregationWindowData;
import io.opencensus.stats.ViewData.AggregationWindowData.CumulativeData;
import io.opencensus.stats.ViewData.AggregationWindowData.IntervalData;
import io.opencensus.tags.TagContext;
import io.opencensus.tags.TagValue;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/** No-op implementations of stats classes. */
final class NoopStats {

  private NoopStats() {}

  /**
   * Returns a {@code StatsComponent} that has a no-op implementation for {@link StatsRecorder}.
   *
   * @return a {@code StatsComponent} that has a no-op implementation for {@code StatsRecorder}.
   */
  static StatsComponent newNoopStatsComponent() {
    return new NoopStatsComponent();
  }

  /**
   * Returns a {@code StatsRecorder} that does not record any data.
   *
   * @return a {@code StatsRecorder} that does not record any data.
   */
  static StatsRecorder getNoopStatsRecorder() {
    return NoopStatsRecorder.INSTANCE;
  }

  /**
   * Returns a {@code MeasureMap} that ignores all calls to {@link MeasureMap#put}.
   *
   * @return a {@code MeasureMap} that ignores all calls to {@code MeasureMap#put}.
   */
  static MeasureMap getNoopMeasureMap() {
    return NoopMeasureMap.INSTANCE;
  }

  /**
   * Returns a {@code ViewManager} that maintains a map of views, but always returns empty {@link
   * ViewData}s.
   *
   * @return a {@code ViewManager} that maintains a map of views, but always returns empty {@code
   *     ViewData}s.
   */
  static ViewManager newNoopViewManager() {
    return new NoopViewManager();
  }

  @ThreadSafe
  private static final class NoopStatsComponent extends StatsComponent {
    private final ViewManager viewManager = newNoopViewManager();
    private volatile boolean isRead;

    @Override
    public ViewManager getViewManager() {
      return viewManager;
    }

    @Override
    public StatsRecorder getStatsRecorder() {
      return getNoopStatsRecorder();
    }

    @Override
    public StatsCollectionState getState() {
      isRead = true;
      return StatsCollectionState.DISABLED;
    }

    @Override
    @Deprecated
    public void setState(StatsCollectionState state) {
      Preconditions.checkNotNull(state, "state");
      checkState(!isRead, "State was already read, cannot set state.");
    }
  }

  @Immutable
  private static final class NoopStatsRecorder extends StatsRecorder {
    static final StatsRecorder INSTANCE = new NoopStatsRecorder();

    @Override
    public MeasureMap newMeasureMap() {
      return getNoopMeasureMap();
    }
  }

  @Immutable
  private static final class NoopMeasureMap extends MeasureMap {
    static final MeasureMap INSTANCE = new NoopMeasureMap();

    @Override
    public MeasureMap put(MeasureDouble measure, double value) {
      return this;
    }

    @Override
    public MeasureMap put(MeasureLong measure, long value) {
      return this;
    }

    @Override
    public void record() {}

    @Override
    public void record(TagContext tags) {
      checkNotNull(tags, "tags");
    }
  }

  @ThreadSafe
  private static final class NoopViewManager extends ViewManager {
    private static final Timestamp ZERO_TIMESTAMP = Timestamp.create(0, 0);

    @GuardedBy("views")
    private final Map<View.Name, View> views = Maps.newHashMap();

    @Override
    public void registerView(View newView) {
      checkNotNull(newView, "newView");
      synchronized (views) {
        View existing = views.get(newView.getName());
        checkArgument(
            existing == null || newView.equals(existing),
            "A different view with the same name already exists.");
        if (existing == null) {
          views.put(newView.getName(), newView);
        }
      }
    }

    @Override
    public ViewData getView(View.Name name) {
      checkNotNull(name, "name");
      synchronized (views) {
        View view = views.get(name);
        if (view == null) {
          return null;
        } else {
          return ViewData.create(
              view,
              Collections.<List<TagValue>, AggregationData>emptyMap(),
              view.getWindow()
                  .match(
                      Functions.<AggregationWindowData>returnConstant(
                          CumulativeData.create(ZERO_TIMESTAMP, ZERO_TIMESTAMP)),
                      Functions.<AggregationWindowData>returnConstant(
                          IntervalData.create(ZERO_TIMESTAMP)),
                      Functions.<AggregationWindowData>throwAssertionError()));
        }
      }
    }
  }
}
