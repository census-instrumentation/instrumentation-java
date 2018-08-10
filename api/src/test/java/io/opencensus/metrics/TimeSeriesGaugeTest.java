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

package io.opencensus.metrics;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import io.opencensus.common.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link TimeSeriesGauge}. */
@RunWith(JUnit4.class)
public class TimeSeriesGaugeTest {

  @Rule public ExpectedException thrown = ExpectedException.none();

  private static final LabelValue LABEL_VALUE_1 = LabelValue.create("value1");
  private static final LabelValue LABEL_VALUE_2 = LabelValue.create("value2");
  private static final Value VALUE_LONG = Value.longValue(12345678);
  private static final Value VALUE_DOUBLE = Value.doubleValue(-345.77);
  private static final Timestamp TIMESTAMP_1 = Timestamp.fromMillis(1000);
  private static final Timestamp TIMESTAMP_2 = Timestamp.fromMillis(2000);
  private static final Point POINT_1 = Point.create(VALUE_DOUBLE, TIMESTAMP_1);
  private static final Point POINT_2 = Point.create(VALUE_LONG, TIMESTAMP_2);

  @Test
  public void testGet_TimeSeriesGauge() {
    TimeSeriesGauge gaugeTimeSeries =
        TimeSeriesGauge.create(
            Arrays.asList(LABEL_VALUE_1, LABEL_VALUE_2), Arrays.asList(POINT_1, POINT_2));
    assertThat(gaugeTimeSeries.getLabelValues())
        .containsExactly(LABEL_VALUE_1, LABEL_VALUE_2)
        .inOrder();
    assertThat(gaugeTimeSeries.getPoints()).containsExactly(POINT_1, POINT_2).inOrder();
  }

  @Test
  public void create_WithNullLabelValueList() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage(CoreMatchers.equalTo("labelValues"));
    TimeSeriesGauge.create(null, Collections.<Point>emptyList());
  }

  @Test
  public void create_WithNullLabelValue() {
    List<LabelValue> labelValues = Arrays.asList(LABEL_VALUE_1, null);
    thrown.expect(NullPointerException.class);
    thrown.expectMessage(CoreMatchers.equalTo("labelValue"));
    TimeSeriesGauge.create(labelValues, Collections.<Point>emptyList());
  }

  @Test
  public void create_WithNullPointList() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage(CoreMatchers.equalTo("points"));
    TimeSeriesGauge.create(Collections.<LabelValue>emptyList(), null);
  }

  @Test
  public void create_WithNullPoint() {
    List<Point> points = Arrays.asList(POINT_1, null);
    thrown.expect(NullPointerException.class);
    thrown.expectMessage(CoreMatchers.equalTo("point"));
    TimeSeriesGauge.create(Collections.<LabelValue>emptyList(), points);
  }

  @Test
  public void testEquals() {
    new EqualsTester()
        .addEqualityGroup(
            TimeSeriesGauge.create(
                Arrays.asList(LABEL_VALUE_1, LABEL_VALUE_2), Arrays.asList(POINT_1, POINT_2)),
            TimeSeriesGauge.create(
                Arrays.asList(LABEL_VALUE_1, LABEL_VALUE_2), Arrays.asList(POINT_1, POINT_2)))
        .addEqualityGroup(
            TimeSeriesGauge.create(
                Arrays.asList(LABEL_VALUE_1, LABEL_VALUE_2), Arrays.asList(POINT_1)))
        .addEqualityGroup(
            TimeSeriesGauge.create(Arrays.asList(LABEL_VALUE_1), Arrays.asList(POINT_1)))
        .testEquals();
  }
}
