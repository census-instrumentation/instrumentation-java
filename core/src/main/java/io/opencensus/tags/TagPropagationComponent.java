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

/** Object containing all supported {@link TagContext} propagation formats. */
// TODO(sebright): Add a link to the specification for the cross-language OpenCensus tag context
// serialization format.
// TODO(sebright): Add an HTTP serializer.
public abstract class TagPropagationComponent {
  private static final TagPropagationComponent NOOP_TAG_PROPAGATION_COMPONENT =
      new NoopTagPropagationComponent();

  /**
   * Returns the {@link TagContextBinarySerializer} for this implementation.
   *
   * @return the {@code TagContextBinarySerializer} for this implementation.
   */
  public abstract TagContextBinarySerializer getBinarySerializer();

  /** Returns a {@code TagPropagationComponent} that contains no-op serializers. */
  static TagPropagationComponent getNoopTagPropagationComponent() {
    return NOOP_TAG_PROPAGATION_COMPONENT;
  }

  @Immutable
  private static final class NoopTagPropagationComponent extends TagPropagationComponent {

    @Override
    public TagContextBinarySerializer getBinarySerializer() {
      return TagContextBinarySerializer.getNoopTagContextBinarySerializer();
    }
  }
}
