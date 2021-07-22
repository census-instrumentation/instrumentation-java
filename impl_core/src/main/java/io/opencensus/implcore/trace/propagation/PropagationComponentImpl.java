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

package io.opencensus.implcore.trace.propagation;

import io.opencensus.trace.propagation.B3InjectionFormat;
import io.opencensus.trace.propagation.BinaryFormat;
import io.opencensus.trace.propagation.PropagationComponent;
import io.opencensus.trace.propagation.TextFormat;

/** Implementation of the {@link PropagationComponent}. */
public class PropagationComponentImpl extends PropagationComponent {
  private final BinaryFormat binaryFormat = new BinaryFormatImpl();
  private final TextFormat traceContextFormat = new TraceContextFormat();

  @Override
  public BinaryFormat getBinaryFormat() {
    return binaryFormat;
  }

  @Override
  public TextFormat getB3Format(final B3InjectionFormat... formats) {
    return new B3Format(formats);
  }

  @Override
  public TextFormat getTraceContextFormat() {
    return traceContextFormat;
  }
}
