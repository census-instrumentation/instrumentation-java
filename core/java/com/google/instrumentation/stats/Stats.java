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

import com.google.instrumentation.common.Provider;

/**
 * {@link Stats}.
 */
public final class Stats {
  private static final StatsContextFactory CONTEXT_FACTORY = Provider.newInstance(
      "com.google.instrumentation.stats.StatsContextFactoryImpl", null);

  private static final StatsManager STATS_MANAGER = Provider.newInstance(
      "com.google.instrumentation.stats.StatsManagerImpl", null);

  /**
   * Returns the default {@link StatsContextFactory}.
   */
  public static StatsContextFactory getStatsContextFactory() {
    return CONTEXT_FACTORY;
  }

  /**
   * Returns the default {@link StatsManager}.
   */
  public static StatsManager getStatsManager() {
    return STATS_MANAGER;
  }

  // VisibleForTesting
  Stats() {
    throw new AssertionError();
  }
}
