/*
 * Copyright 2019, OpenCensus Authors
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

package io.opencensus.trace.propagation;

/**
 * Controls how trace context is propagated. Multiple formats may be specified when configuring B3
 * propagation. An enumeration is used (as opposed to a boolean) to allow for future formats.
 *
 * @since 0.25
 */
public enum B3InjectionFormat {

  /**
   * Trace context is encoded with multiple "X-B3-*" fields (default). More information available at
   * https://github.com/openzipkin/b3-propagation#multiple-headers.
   */
  MULTI,

  /**
   * Trace context is encoded with a single "b3" field. More information available at
   * https://github.com/openzipkin/b3-propagation#single-header.
   */
  SINGLE;
}
