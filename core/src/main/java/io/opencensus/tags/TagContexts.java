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

package io.opencensus.tags;

import javax.annotation.concurrent.Immutable;

/**
 * Object for creating new {@link TagContext}s.
 *
 * <p>This class returns {@link TagContextBuilder builders} that can be used to create the
 * implementation-dependent {@link TagContext}s.
 */
public abstract class TagContexts {
  private static final TagContexts NOOP_TAG_CONTEXTS = new NoopTagContexts();

  /**
   * Returns an empty {@code TagContext}.
   *
   * @return an empty {@code TagContext}.
   */
  public abstract TagContext empty();

  /**
   * Returns a new empty {@code Builder}.
   *
   * @return a new empty {@code Builder}.
   */
  public TagContextBuilder emptyBuilder() {
    return toBuilder(empty());
  }

  /**
   * Returns a builder based on this {@code TagContext}.
   *
   * @return a builder based on this {@code TagContext}.
   */
  public abstract TagContextBuilder toBuilder(TagContext tags);

  /**
   * Returns a {@code TagContexts} that only produces {@link TagContext}s with no tags.
   *
   * @return a {@code TagContexts} that only produces {@code TagContext}s with no tags.
   */
  static TagContexts getNoopTagContexts() {
    return NOOP_TAG_CONTEXTS;
  }

  @Immutable
  private static final class NoopTagContexts extends TagContexts {

    @Override
    public TagContext empty() {
      return TagContext.getNoopTagContext();
    }

    @Override
    public TagContextBuilder toBuilder(TagContext tags) {
      return TagContextBuilder.getNoopTagContextBuilder();
    }
  }
}
