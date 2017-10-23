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

import io.opencensus.tags.propagation.TagPropagationComponent;

/**
 * Class that holds the implementation for {@link Tagger} and {@link TagPropagationComponent}.
 *
 * <p>All objects returned by methods on {@code TagsComponent} are cacheable.
 */
public abstract class TagsComponent {

  /** Returns the {@link Tagger} for this implementation. */
  public abstract Tagger getTagger();

  /** Returns the {@link TagPropagationComponent} for this implementation. */
  public abstract TagPropagationComponent getTagPropagationComponent();

  /**
   * Returns the current {@code TaggingState}.
   *
   * <p>When no implementation is available, {@code getState} always returns {@link
   * TaggingState#DISABLED}.
   *
   * @return the current {@code TaggingState}.
   */
  public abstract TaggingState getState();

  /**
   * Sets the current {@code TaggingState}.
   *
   * <p>When no implementation is available, {@code setState} has no effect.
   *
   * @param state the new {@code TaggingState}.
   */
  public abstract void setState(TaggingState state);
}
