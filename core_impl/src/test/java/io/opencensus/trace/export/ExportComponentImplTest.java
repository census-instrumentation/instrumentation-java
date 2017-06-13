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

package io.opencensus.trace.export;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link ExportComponentImpl}. */
@RunWith(JUnit4.class)
public class ExportComponentImplTest {
  private final ExportComponent exportComponent = new ExportComponentImpl();

  @Test
  public void implementationOfSpanExporter() {
    assertThat(exportComponent.getSpanExporter()).isInstanceOf(SpanExporterImpl.class);
  }

  @Test
  public void implementationOfActiveSpans() {
    // TODO(bdrutu): Change this when implementation is available.
    assertThat(exportComponent.getActiveSpans()).isNull();
  }

  @Test
  public void implementationOfSampledSpanStore() {
    // TODO(bdrutu): Change this when implementation is available.
    assertThat(exportComponent.getSampledSpanStore()).isNull();
  }
}
