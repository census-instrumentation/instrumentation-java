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

package com.google.instrumentation.stats;

import static com.google.common.truth.Truth.assertThat;

import io.opencensus.common.Duration;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link IntervalAggregationDescriptor}
 */
@RunWith(JUnit4.class)
public final class IntervalAggregationDescriptorTest {
  @Test
  public void testIntervalAggregationDescriptor() {
    Duration[] intervals =
        new Duration[] { Duration.fromMillis(1), Duration.fromMillis(22), Duration.fromMillis(333)};
    IntervalAggregationDescriptor iDescriptor =
        IntervalAggregationDescriptor.create(12, Arrays.asList(intervals));
    assertThat(iDescriptor.getNumSubIntervals()).isEqualTo(12);
    assertThat(iDescriptor.getIntervalSizes()).isNotNull();
    assertThat(iDescriptor.getIntervalSizes()).hasSize(intervals.length);
    for (int i = 0; i < intervals.length; i++) {
      assertThat(iDescriptor.getIntervalSizes().get(i)).isEqualTo(intervals[i]);
    }
  }

  @Test
  public void testIntervalAggregationDescriptorWithDefaultNumSubIntervals() {
    assertThat(
        IntervalAggregationDescriptor.create(
            Arrays.asList(Duration.fromMillis(1))).getNumSubIntervals())
        .isEqualTo(5);
  }

  @Test
  public void testIntervalAggregationDescriptorNumSubIntervalsRange() {
    assertThat(
        IntervalAggregationDescriptor.create(
            2, Arrays.asList(Duration.fromMillis(1))).getNumSubIntervals())
        .isEqualTo(2);
    assertThat(
        IntervalAggregationDescriptor.create(
            20, Arrays.asList(Duration.fromMillis(1))).getNumSubIntervals())
        .isEqualTo(20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIntervalAggregationDescriptorLowNumSubIntervals() {
    IntervalAggregationDescriptor.create(1, Arrays.asList(Duration.fromMillis(1)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIntervalAggregationDescriptorHighNumSubIntervals() {
    IntervalAggregationDescriptor.create(21, Arrays.asList(Duration.fromMillis(1)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIntervalAggregationDescriptorEmptyIntervalSizes() {
    IntervalAggregationDescriptor.create(Arrays.asList(new Duration[] { }));
  }
}
