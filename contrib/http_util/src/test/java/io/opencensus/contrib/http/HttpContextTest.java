/*
 * Copyright 2018, OpenCensus Authors
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

package io.opencensus.contrib.http;

import static com.google.common.truth.Truth.assertThat;

import io.opencensus.trace.Span;
import io.opencensus.trace.Tracing;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link HttpContext}. */
@RunWith(JUnit4.class)
public class HttpContextTest {
  @Rule public final ExpectedException thrown = ExpectedException.none();
  private final Span span = Tracing.getTracer().spanBuilder("testSpan").startSpan();
  private final HttpContext context = new HttpContext(span, 1L);

  @Test
  public void testDisallowNullSpan() {
    thrown.expect(NullPointerException.class);
    new HttpContext(null, 0L);
  }

  @Test
  public void testInitValues() {
    assertThat(context.requestStartTime).isGreaterThan(0L);
    assertThat(context.requestMessageSize.longValue()).isEqualTo(0L);
    assertThat(context.responseMessageSize.longValue()).isEqualTo(0L);
  }
}
