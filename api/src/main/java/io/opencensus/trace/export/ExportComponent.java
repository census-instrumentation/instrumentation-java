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

import io.opencensus.trace.TraceOptions;
import javax.annotation.Nullable;

/**
 * Class that holds the implementation instances for {@link SpanExporter} and {@link
 * InProcessDebuggingHandler}.
 *
 * <p>Unless otherwise noted all methods (on component) results are cacheable.
 */
public abstract class ExportComponent {

  private static final NoopExportComponent NOOP_EXPORT_COMPONENT = new NoopExportComponent();

  /**
   * Returns the no-op implementation of the {@code ExportComponent}.
   *
   * @return the no-op implementation of the {@code ExportComponent}.
   */
  public static ExportComponent getNoopExportComponent() {
    return NOOP_EXPORT_COMPONENT;
  }

  /**
   * Returns the {@link SpanExporter} which can be used to register handlers to export all the spans
   * that are part of a distributed sampled trace (see {@link TraceOptions#isSampled()}).
   *
   * @return the implementation of the {@code SpanExporter} or no-op if no implementation linked in
   *     the binary.
   */
  public abstract SpanExporter getSpanExporter();

  /**
   * Returns the {@link InProcessDebuggingHandler} that can be used to get useful debugging
   * information such as (active spans, latency based sampled spans, error based sampled spans).
   *
   * @return the {@code InProcessDebuggingHandler} or {@code null} if in-process debugging is not
   *     supported.
   */
  @Nullable
  public abstract InProcessDebuggingHandler getInProcessDebuggingHandler();

  private static final class NoopExportComponent extends ExportComponent {
    @Override
    public SpanExporter getSpanExporter() {
      return SpanExporter.getNoopSpanExporter();
    }

    @Nullable
    @Override
    public InProcessDebuggingHandler getInProcessDebuggingHandler() {
      return null;
    }
  }
}
