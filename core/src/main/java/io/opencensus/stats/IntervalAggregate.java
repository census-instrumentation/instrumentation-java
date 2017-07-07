/*
 * Copyright 2016, Google Inc.
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

package io.opencensus.stats;

import io.opencensus.common.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains summary stats over various time intervals.
 */
public final class IntervalAggregate {
  /**
   * Constructs new {@link IntervalAggregate}.
   * TODO(dpo): Determine what we should do it intervals is empty.
   */
  public static final IntervalAggregate create(List<Tag> tags, List<Interval> intervals) {
    return new IntervalAggregate(tags, intervals);
  }

  /**
   * {@link Tag}s associated with this aggregation.
   *
   * <p>Note: The returned list is unmodifiable, attempts to update it will throw an
   * UnsupportedOperationException.
   */
  public final List<Tag> getTags() {
    return tags;
  }

  /**
   * Sequence of intervals for this aggregation.
   */
  public List<Interval> getIntervals() {
    return intervals;
  }

  private final List<Tag> tags;
  private final List<Interval> intervals;

  private IntervalAggregate(List<Tag> tags, List<Interval> intervals) {
    this.tags = tags;
    this.intervals = Collections.unmodifiableList(new ArrayList<Interval>(intervals));
  }

  /**
   * Summary statistic over a single time interval.
   */
  public static final class Interval {
    /**
     * Constructs a new {@link Interval}.
     *
     * <p>Note: {@code intervalSize} must be positive otherwise behavior is unspecified.
     */
    public static Interval create(Duration intervalSize, double count, double sum) {
      return new Interval(intervalSize, count, sum);
    }

    /**
     * The interval duration.
     */
    public Duration getIntervalSize() {
      return intervalSize;
    }

    /**
     * The number of measurements in this interval.
     */
    public double getCount() {
      return count;
    }

    /**
     * The cumulative sum of measurements in this interval.
     */
    public double getSum() {
      return sum;
    }

    private final Duration intervalSize;
    private final double count;
    private final double sum;

    private Interval(Duration intervalSize, double count, double sum) {
      this.intervalSize = intervalSize;
      this.count = count;
      this.sum = sum;
    }
  }
}
