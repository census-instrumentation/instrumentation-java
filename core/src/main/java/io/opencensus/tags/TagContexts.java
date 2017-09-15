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

import static com.google.common.base.Preconditions.checkNotNull;

import io.opencensus.common.Scope;
import javax.annotation.concurrent.Immutable;

/**
 * Object for creating new {@link TagContext}s and {@code TagContext}s based on the current context.
 *
 * <p>This class returns {@link TagContextBuilder builders} that can be used to create the
 * implementation-dependent {@link TagContext}s.
 *
 * <p>Implementations may have different constraints and are free to convert tag contexts to their
 * own subtypes. This means callers cannot assume the {@link #getCurrentTagContext() current
 * context} is the same instance as the one {@link #withTagContext(TagContext) placed into scope}.
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
   * Returns the current {@code TagContext}.
   *
   * <p>This implementation calls {@link #transformTagContext} on its result.
   *
   * @return the current {@code TagContext}.
   */
  public final TagContext getCurrentTagContext() {
    return transformTagContext(CurrentTagContextUtils.getCurrentTagContext());
  }

  /**
   * Returns a new empty {@code Builder}.
   *
   * @return a new empty {@code Builder}.
   */
  public abstract TagContextBuilder emptyBuilder();

  /**
   * Returns a builder based on this {@code TagContext}.
   *
   * @return a builder based on this {@code TagContext}.
   */
  public abstract TagContextBuilder toBuilder(TagContext tags);

  /**
   * Returns a new builder created from the current {@code TagContext}.
   *
   * <p>This implementation calls {@link #toBuilder} on the current {@code TagContext}.
   *
   * @return a new builder created from the current {@code TagContext}.
   */
  public final TagContextBuilder currentBuilder() {
    return toBuilder(CurrentTagContextUtils.getCurrentTagContext());
  }

  /**
   * Enters the scope of code where the given {@code TagContext} is in the current context and
   * returns an object that represents that scope. The scope is exited when the returned object is
   * closed.
   *
   * <p>This implementation calls {@link #transformTagContext} on its input.
   *
   * @param tags the {@code TagContext} to be set to the current context.
   * @return an object that defines a scope where the given {@code TagContext} is set to the current
   *     context.
   */
  public final Scope withTagContext(TagContext tags) {
    return CurrentTagContextUtils.withTagContext(transformTagContext(tags));
  }

  /**
   * Hook for transforming {@link TagContext}s that are processed by this {@link TagContexts}. For
   * example, if the implementation supports disabling tagging, this method could return a no-op
   * {@code TagContext} whenever tagging is disabled. The default implementation returns its input
   * unchanged.
   *
   * <p>See {@link #getCurrentTagContext} and {@link #withTagContext}.
   *
   * @param tags the tags to transform.
   * @return tags optionally transformed using the current state of this {@code TagContexts}.
   */
  protected TagContext transformTagContext(TagContext tags) {
    return checkNotNull(tags);
  }

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
    public TagContextBuilder emptyBuilder() {
      return TagContextBuilder.getNoopTagContextBuilder();
    }

    @Override
    public TagContextBuilder toBuilder(TagContext tags) {
      return TagContextBuilder.getNoopTagContextBuilder();
    }

    @Override
    protected TagContext transformTagContext(TagContext tags) {
      return TagContext.getNoopTagContext();
    }
  }
}
