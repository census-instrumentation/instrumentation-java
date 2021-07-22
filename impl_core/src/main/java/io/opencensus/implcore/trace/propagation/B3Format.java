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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import io.opencensus.trace.SpanContext;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.TraceId;
import io.opencensus.trace.TraceOptions;
import io.opencensus.trace.Tracestate;
import io.opencensus.trace.propagation.B3InjectionFormat;
import io.opencensus.trace.propagation.SpanContextParseException;
import io.opencensus.trace.propagation.TextFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/*>>>
import org.checkerframework.checker.nullness.qual.NonNull;
*/

/**
 * Implementation of the B3 propagation protocol. See <a
 * href=https://github.com/openzipkin/b3-propagation>b3-propagation</a>.
 */
final class B3Format extends TextFormat {
  private static final Tracestate TRACESTATE_DEFAULT = Tracestate.builder().build();
  @VisibleForTesting static final String X_B3_TRACE_ID = "X-B3-TraceId";
  @VisibleForTesting static final String X_B3_SPAN_ID = "X-B3-SpanId";
  @VisibleForTesting static final String X_B3_PARENT_SPAN_ID = "X-B3-ParentSpanId";
  @VisibleForTesting static final String X_B3_SAMPLED = "X-B3-Sampled";
  @VisibleForTesting static final String X_B3_FLAGS = "X-B3-Flags";
  @VisibleForTesting static final String B3 = "b3";

  // Used as the upper TraceId.SIZE hex characters of the traceID. B3-propagation used to send
  // TraceId.SIZE hex characters (8-bytes traceId) in the past.
  private static final String UPPER_TRACE_ID = "0000000000000000";
  // Sampled value via the X_B3_SAMPLED header.
  private static final String SAMPLED_VALUE = "1";
  // "Debug" sampled value.
  private static final String FLAGS_VALUE = "1";

  private final Collection<? extends B3InjectionFormat> injectionFormats;
  private final List<String> fields;

  public B3Format() {
    this(B3InjectionFormat.MULTI);
  }

  public B3Format(final B3InjectionFormat... formats) {
    checkNotNull(formats, "formats cannot be null");
    checkArgument(formats.length > 0, "At least one injection format must be specified");

    this.injectionFormats = Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(formats)));
    final List<String> fields = new ArrayList<>(6);
    if (injectionFormats.contains(B3InjectionFormat.MULTI)) {
      fields.addAll(
          Arrays.asList(
              X_B3_TRACE_ID, X_B3_SPAN_ID, X_B3_PARENT_SPAN_ID, X_B3_SAMPLED, X_B3_FLAGS));
    }
    if (injectionFormats.contains(B3InjectionFormat.SINGLE)) {
      fields.add(B3);
    }
    this.fields = Collections.unmodifiableList(fields);
  }

  @Override
  public List<String> fields() {
    return fields;
  }

  @Override
  public <C /*>>> extends @NonNull Object*/> void inject(
      SpanContext spanContext, C carrier, Setter<C> setter) {
    checkNotNull(spanContext, "spanContext");
    checkNotNull(setter, "setter");
    checkNotNull(carrier, "carrier");
    if (getInjectionFormats().contains(B3InjectionFormat.SINGLE)) {
      final String value =
          spanContext.getTraceId().toLowerBase16()
              + "-"
              + spanContext.getSpanId().toLowerBase16()
              + "-"
              + (spanContext.getTraceOptions().isSampled() ? "1" : "0");
      setter.put(carrier, B3, value);
    }
    if (getInjectionFormats().contains(B3InjectionFormat.MULTI)) {
      setter.put(carrier, X_B3_TRACE_ID, spanContext.getTraceId().toLowerBase16());
      setter.put(carrier, X_B3_SPAN_ID, spanContext.getSpanId().toLowerBase16());
      if (spanContext.getTraceOptions().isSampled()) {
        setter.put(carrier, X_B3_SAMPLED, SAMPLED_VALUE);
      }
    }
  }

  @Override
  public <C /*>>> extends @NonNull Object*/> SpanContext extract(C carrier, Getter<C> getter)
      throws SpanContextParseException {
    checkNotNull(carrier, "carrier");
    checkNotNull(getter, "getter");

    try {
      // check for single header format
      final String b3Header = getter.get(carrier, B3);
      if (b3Header != null) {
        return parseB3Value(b3Header);
      }

      // assume multi-header format
      TraceId traceId;
      String traceIdStr = getter.get(carrier, X_B3_TRACE_ID);
      if (traceIdStr != null) {
        traceId = parseTraceId(traceIdStr);
      } else {
        throw new SpanContextParseException("Missing X_B3_TRACE_ID.");
      }
      SpanId spanId;
      String spanIdStr = getter.get(carrier, X_B3_SPAN_ID);
      if (spanIdStr != null) {
        spanId = SpanId.fromLowerBase16(spanIdStr);
      } else {
        throw new SpanContextParseException("Missing X_B3_SPAN_ID.");
      }
      TraceOptions traceOptions = TraceOptions.DEFAULT;
      if (SAMPLED_VALUE.equals(getter.get(carrier, X_B3_SAMPLED))
          || FLAGS_VALUE.equals(getter.get(carrier, X_B3_FLAGS))) {
        traceOptions = TraceOptions.builder().setIsSampled(true).build();
      }
      return SpanContext.create(traceId, spanId, traceOptions, TRACESTATE_DEFAULT);
    } catch (IllegalArgumentException e) {
      throw new SpanContextParseException("Invalid input.", e);
    }
  }

  protected TraceId parseTraceId(String traceIdStr) {
    if (traceIdStr.length() == TraceId.SIZE) {
      // This is an 8-byte traceID.
      traceIdStr = UPPER_TRACE_ID + traceIdStr;
    }
    return TraceId.fromLowerBase16(traceIdStr);
  }

  /**
   * Parses trace information from the "B3 single" header format.
   *
   * @param b3Header A b3 single header string in the form [traceid]-[spanid]-[if flags 'd' else
   *     sampled]-[parentspanid]
   */
  protected SpanContext parseB3Value(final String b3Header) throws SpanContextParseException {
    final String[] components = b3Header.split("-", 4);
    if (components.length < 2) {
      throw new SpanContextParseException("Invalid b3 (single) header: " + b3Header);
    }
    final String traceIdString = components[0];
    final String spanIdString = components[1];
    final String flag = components.length > 2 ? components[2] : null;
    if (flag != null && flag.length() != 1) {
      throw new SpanContextParseException("Invalid b3 flag: " + flag);
    }
    // parent trace ID (optional components[3]) is ignored

    final TraceId traceId = parseTraceId(traceIdString);
    final SpanId spanId = SpanId.fromLowerBase16(spanIdString);
    final TraceOptions traceOptions =
        flag != null
            ? TraceOptions.builder()
                .setIsSampled("1".contentEquals(flag) || "d".equalsIgnoreCase(flag))
                .build()
            : TraceOptions.DEFAULT;
    final Tracestate tracestate = Tracestate.builder().build();

    return SpanContext.create(traceId, spanId, traceOptions, tracestate);
  }

  protected Collection<? extends B3InjectionFormat> getInjectionFormats() {
    return injectionFormats;
  }
}
