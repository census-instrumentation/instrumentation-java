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

import static com.google.common.truth.Truth.assertThat;

import com.google.instrumentation.internal.MillisClock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Test for accessing the {@link TraceComponent} through the {@link Tracing} class. */
@RunWith(JUnit4.class)
public class TracingTest {
  @Test
  public void implementationOfTracer() {
    assertThat(Tracing.getTracer()).isInstanceOf(TracerImpl.class);
  }

  @Test
  public void implementationOfBinaryPropagationHandler() {
    assertThat(Tracing.getBinaryPropagationHandler())
        .isInstanceOf(BinaryPropagationHandlerImpl.class);
  }

  @Test
  public void implementationOfClock() {
    assertThat(Tracing.getClock()).isInstanceOf(MillisClock.class);
  }

  @Test
  public void implementationOfTraceExporter() {
    assertThat(Tracing.getTraceExporter()).isInstanceOf(TraceExporterImpl.class);
  }
}
