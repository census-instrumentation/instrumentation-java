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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import io.opencensus.common.ExperimentalApi;
import io.opencensus.trace.Span;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class provides storage per request context on http client and server.
 *
 * @since 0.19
 */
@ExperimentalApi
public class HttpContext {
  @VisibleForTesting static final long INVALID_STARTTIME = -1;

  @VisibleForTesting final long requestStartTime;
  @VisibleForTesting AtomicLong sentMessageSize = new AtomicLong();
  @VisibleForTesting AtomicLong receiveMessageSize = new AtomicLong();
  @VisibleForTesting final Span span;
  @VisibleForTesting final long reqId;

  HttpContext(Span span, long reqId) {
    checkNotNull(span, "span");
    this.reqId = reqId;
    this.span = span;
    requestStartTime = System.nanoTime();
  }
}
