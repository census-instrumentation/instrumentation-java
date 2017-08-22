/*
 * Copyright 2016-17, OpenCensus Authors
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

package io.opencensus.examples.stats;

import io.opencensus.stats.Measure.MeasureDouble;
import io.opencensus.tags.TagContexts;
import io.opencensus.tags.TagKey.TagKeyString;
import io.opencensus.tags.TagValueString;
import io.opencensus.tags.Tags;

/** Simple program that uses Stats contexts. */
public class StatsRunner {

  private static final TagKeyString K1 = TagKeyString.create("k1");
  private static final TagKeyString K2 = TagKeyString.create("k2");
  private static final TagKeyString K3 = TagKeyString.create("k3");
  private static final TagKeyString K4 = TagKeyString.create("k4");

  private static final TagValueString V1 = TagValueString.create("v1");
  private static final TagValueString V2 = TagValueString.create("v2");
  private static final TagValueString V3 = TagValueString.create("v3");
  private static final TagValueString V4 = TagValueString.create("v4");

  private static final String UNIT = "1";
  private static final MeasureDouble M1 = MeasureDouble.create("m1", "1st test metric", UNIT);
  private static final MeasureDouble M2 = MeasureDouble.create("m2", "2nd test metric", UNIT);

  private static final TagContexts tagContexts = Tags.getTagContexts();

  /**
   * Main method.
   *
   * @param args the main arguments.
   */
  // TODO(sebright): Uncomment all code in this method once we add methods that work with the
  // current TagContext.
  public static void main(String[] args) {
    System.out.println("Hello Stats World");
    System.out.println("Default Tags: " + tagContexts.empty());
    //    System.out.println("Current Tags: " + factory.getCurrentStatsContext());
    //    TagContext tags1 = tagContexts.emptyBuilder().set(K1, V1).set(K2, V2).build();
    //    try (Scope scopedStatsCtx1 = factory.withStatsContext(tags1)) {
    //      System.out.println("  Current Tags: " + factory.getCurrentStatsContext());
    //      System.out.println(
    //          "  Current == Default + tags1: " + factory.getCurrentStatsContext().equals(tags1));
    //    TagContext tags2 = tagContexts.toBuilder(tags1).set(K3, V3).set(K4, V4).build();
    //      try (Scope scopedStatsCtx2 = factory.withStatsContext(tags2)) {
    //        System.out.println("    Current Tags: " + factory.getCurrentStatsContext());
    //        System.out.println(
    //            "    Current == Default + tags1 + tags2: "
    //                + factory.getCurrentStatsContext().equals(tags2));
    //        factory.getCurrentStatsContext().record(
    //            MeasureMap.builder().set(M1, 0.2).set(M2, 0.4).build());
    //      }
    //    }
    //    System.out.println("Current == Default: " +
    // factory.getCurrentStatsContext().equals(DEFAULT));
  }
}
