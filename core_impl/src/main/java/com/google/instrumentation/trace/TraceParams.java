/*
 * Copyright 2017, Google Inc.
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

package com.google.instrumentation.trace;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.auto.value.AutoValue;
import javax.annotation.concurrent.Immutable;

/** Class that holds global trace parameters. */
@AutoValue
@Immutable
abstract class TraceParams {

  // These values are the default values for all the global parameters.
  // TODO(aveitch): Change this when a rate/probability sampler will be available.
  private static final Sampler DEFAULT_SAMPLER = Samplers.neverSample();
  private static final int DEFAULT_SPAN_MAX_NUM_ATTRIBUTES = 32;
  private static final int DEFAULT_SPAN_MAX_NUM_ANNOTATIONS = 32;
  private static final int DEFAULT_SPAN_MAX_NUM_NETWORK_EVENTS = 128;
  private static final int DEFAULT_SPAN_MAX_NUM_LINKS = 128;

  static final TraceParams DEFAULT =
      TraceParams.builder()
          .setSampler(DEFAULT_SAMPLER)
          .setMaxNumberOfAttributes(DEFAULT_SPAN_MAX_NUM_ATTRIBUTES)
          .setMaxNumberOfAnnotations(DEFAULT_SPAN_MAX_NUM_ANNOTATIONS)
          .setMaxNumberOfNetworkEvents(DEFAULT_SPAN_MAX_NUM_NETWORK_EVENTS)
          .setMaxNumberOfLinks(DEFAULT_SPAN_MAX_NUM_LINKS)
          .build();

  /**
   * Returns the global default {@code Sampler}. Used if no {@code Sampler} provided in {@link
   * StartSpanOptions}.
   *
   * @return the global default {@code Sampler}.
   */
  abstract Sampler getSampler();

  /**
   * Returns the global default max number of {@link Attributes} per {@link Span}.
   *
   * @return the global default max number of {@code Attributes} per {@link Span}.
   */
  abstract int getMaxNumberOfAttributes();

  /**
   * Returns the global default max number of {@link Annotation} events per {@link Span}.
   *
   * @return the global default max number of {@code Annotation} events per {@code Span}.
   */
  abstract int getMaxNumberOfAnnotations();

  /**
   * Returns the global default max number of {@link NetworkEvent} events per {@link Span}.
   *
   * @return the global default max number of {@code NetworkEvent} events per {@code Span}.
   */
  abstract int getMaxNumberOfNetworkEvents();

  /**
   * Returns the global default max number of {@link Link} entries per {@link Span}.
   *
   * @return the global default max number of {@code Link} entries per {@code Span}.
   */
  abstract int getMaxNumberOfLinks();

  private static Builder builder() {
    return new AutoValue_TraceParams.Builder();
  }

  abstract Builder toBuilder();

  @AutoValue.Builder
  abstract static class Builder {

    /**
     * Sets the global default {@code Sampler}. It must be not {@code null} otherwise
     * {@link #build()} will throw an exception.
     *
     * @param sampler the global default {@code Sampler}.
     * @return this.
     */
    abstract Builder setSampler(Sampler sampler);

    /**
     * Sets the global default max number of {@link Attributes} per {@link Span}.
     *
     * @param maxNumberOfAttributes the global default max number of {@link Attributes} per {@link
     *     Span}. It must be positive otherwise {@link #build()} will throw an exception.
     * @return this.
     */
    abstract Builder setMaxNumberOfAttributes(int maxNumberOfAttributes);

    /**
     * Sets the global default max number of {@link Annotation} events per {@link Span}.
     *
     * @param maxNumberOfAnnotations the global default max number of {@link Annotation} events per
     *     {@link Span}. It must be positive otherwise {@link #build()} will throw an exception.
     * @return this.
     */
    abstract Builder setMaxNumberOfAnnotations(int maxNumberOfAnnotations);

    /**
     * Sets the global default max number of {@link NetworkEvent} events per {@link Span}.
     *
     * @param maxNumberOfNetworkEvents the global default max number of {@link NetworkEvent} events
     *     per {@link Span}. It must be positive otherwise {@link #build()} will throw an exception.
     * @return this.
     */
    abstract Builder setMaxNumberOfNetworkEvents(int maxNumberOfNetworkEvents);

    /**
     * Sets the global default max number of {@link Link} entries per {@link Span}.
     *
     * @param maxNumberOfLinks the global default max number of {@link Link} entries per {@link
     *     Span}. It must be positive otherwise {@link #build()} will throw an exception.
     * @return this.
     */
    abstract Builder setMaxNumberOfLinks(int maxNumberOfLinks);

    abstract TraceParams autoBuild();

    /**
     * Builds and returns a {@code TraceParams} with the desired values.
     *
     * @return a {@code TraceParams} with the desired values.
     * @throws NullPointerException if the sampler is null.
     * @throws IllegalStateException if any of the max numbers are not positive.
     */
    TraceParams build() {
      TraceParams traceParams = autoBuild();
      checkNotNull(traceParams.getSampler(), "sampler");
      checkState(traceParams.getMaxNumberOfAttributes() > 0, "maxNumberOfAttributes");
      checkState(traceParams.getMaxNumberOfAnnotations() > 0, "maxNumberOfAnnotations");
      checkState(traceParams.getMaxNumberOfNetworkEvents() > 0, "maxNumberOfNetworkEvents");
      checkState(traceParams.getMaxNumberOfLinks() > 0, "maxNumberOfLinks");
      return traceParams;
    }
  }
}
