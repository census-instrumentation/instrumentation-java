/*
 * Copyright 2017, Google Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opencensus.impl.stats;

import io.opencensus.common.Clock;
import io.opencensus.impl.internal.EventQueue;
import io.opencensus.stats.MeasureMap;
import io.opencensus.stats.View;
import io.opencensus.stats.ViewData;
import io.opencensus.tags.TagContext;

/** Object that stores all views and stats. */
final class StatsManager {

  private final EventQueue queue;

  // clock used throughout the stats implementation
  private final Clock clock;

  private final MeasureToViewMap measureToViewMap = new MeasureToViewMap();

  StatsManager(EventQueue queue, Clock clock) {
    this.queue = queue;
    this.clock = clock;
  }

  void registerView(View view) {
    if (view.getWindow() instanceof View.Window.Interval) {
      throw new UnsupportedOperationException("IntervalView not supported yet.");
    }
    measureToViewMap.registerView(view, clock);
  }

  ViewData getView(View.Name viewName) {
    ViewData view = measureToViewMap.getView(viewName, clock);
    if (view == null) {
      throw new IllegalArgumentException(
          "ViewData for view " + viewName + " not found.");
    } else {
      return view;
    }
  }

  void record(TagContext tags, MeasureMap measurementValues) {
    queue.enqueue(new StatsEvent(this, tags, measurementValues));
  }

  // An EventQueue entry that records the stats from one call to StatsManager.record(...).
  private static final class StatsEvent implements EventQueue.Entry {
    private final TagContext tags;
    private final MeasureMap stats;
    private final StatsManager viewManager;

    StatsEvent(StatsManager viewManager, TagContext tags, MeasureMap stats) {
      this.viewManager = viewManager;
      this.tags = tags;
      this.stats = stats;
    }

    @Override
    public void process() {
      viewManager.measureToViewMap.record(tags, stats);
    }
  }
}
