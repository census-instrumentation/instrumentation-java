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

package io.opencensus.trace;

import io.grpc.Context;
import io.opencensus.common.Scope;
import io.opencensus.trace.unsafe.ContextUtils;

/** Util methods/functionality to interact with the {@link Span} in the {@link io.grpc.Context}. */
final class CurrentSpanUtils {
  // No instance of this class.
  private CurrentSpanUtils() {}

  /**
   * Returns The {@link Span} from the current context.
   *
   * @return The {@code Span} from the current context.
   */
  static Span getCurrentSpan() {
    return ContextUtils.CONTEXT_SPAN_KEY.get(Context.current());
  }

  /**
   * Enters the scope of code where the given {@link Span} is in the current context, and returns an
   * object that represents that scope. The scope is exited when the returned object is closed.
   *
   * <p>Supports try-with-resource idiom.
   *
   * @param span The {@code Span} to be set to the current context.
   * @return An object that defines a scope where the given {@code Span} is set to the current
   *     context.
   */
  static Scope withSpan(Span span) {
    return new WithSpan(span, ContextUtils.CONTEXT_SPAN_KEY);
  }

  // Defines an arbitrary scope of code as a traceable operation. Supports try-with-resources idiom.
  private static final class WithSpan implements Scope {
    private final Context origContext;

    /**
     * Constructs a new {@link WithSpan}.
     *
     * @param span is the {@code Span} to be added to the current {@code io.grpc.Context}.
     * @param contextKey is the {@code Context.Key} used to set/get {@code Span} from the {@code
     *     Context}.
     */
    WithSpan(Span span, Context.Key<Span> contextKey) {
      origContext = Context.current().withValue(contextKey, span).attach();
    }

    @Override
    public void close() {
      Context.current().detach(origContext);
    }
  }
}
