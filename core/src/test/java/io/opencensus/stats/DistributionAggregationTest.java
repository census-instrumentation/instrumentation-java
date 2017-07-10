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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link DistributionAggregation}
 */
@RunWith(JUnit4.class)
public final class DistributionAggregationTest {
  @Test
  public void testDistributionAggregationEmpty() {
    DistributionAggregation d = DistributionAggregation.create();
    assertThat(d.getBucketBoundaries()).isNull();
  }

  @Test
  public void testDistributionAggregation() {
    Double[] buckets = new Double[] { 0.1, 2.2, 33.3 };
    DistributionAggregation d =
        DistributionAggregation.create(Arrays.asList(buckets));
    assertThat(d.getBucketBoundaries()).isNotNull();
    assertThat(d.getBucketBoundaries()).hasSize(buckets.length);
    for (int i = 0; i < buckets.length; i++) {
      assertThat(d.getBucketBoundaries().get(i))
          .isWithin(0.00000001).of(buckets[i]);
    }
  }

  @Test
  public void testDistributionAggregationEquals() {
    new EqualsTester()
        .addEqualityGroup(
            DistributionAggregation.create(Arrays.asList(1.0, 2.0, 5.0)),
            DistributionAggregation.create(Arrays.asList(1.0, 2.0, 5.0)))
        .addEqualityGroup(
            DistributionAggregation.create(),
            DistributionAggregation.create())
        .testEquals();
  }
}
