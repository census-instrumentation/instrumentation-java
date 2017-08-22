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

import com.google.common.base.Preconditions;
import io.opencensus.tags.TagContext;
import io.opencensus.tags.unsafe.ContextUtils;
import javax.annotation.concurrent.Immutable;

/** Provides methods to record stats against tags. */
public abstract class StatsRecorder {
  private static final StatsRecorder NOOP_STATS_RECORDER = new NoopStatsRecorder();

  /**
   * Records a set of measurements with the tags in the current context.
   *
   * @param measureValues the measurements to record.
   */
  public final void record(MeasureMap measureValues) {
    // Use the context key directly, to avoid depending on the tags implementation.
    record(ContextUtils.TAG_CONTEXT_KEY.get(), measureValues);
  }

  /**
   * Records a set of measurements with a set of tags.
   *
   * @param tags the tags associated with the measurements.
   * @param measureValues the measurements to record.
   */
  public abstract void record(TagContext tags, MeasureMap measureValues);

  /**
   * Returns a {@code StatsRecorder} that does not record any data.
   *
   * @return a {@code StatsRecorder} that does not record any data.
   */
  static StatsRecorder getNoopStatsRecorder() {
    return NOOP_STATS_RECORDER;
  }

  @Immutable
  private static final class NoopStatsRecorder extends StatsRecorder {

    @Override
    public void record(TagContext tags, MeasureMap measureValues) {
      Preconditions.checkNotNull(tags);
      Preconditions.checkNotNull(measureValues);
    }
  }
}
