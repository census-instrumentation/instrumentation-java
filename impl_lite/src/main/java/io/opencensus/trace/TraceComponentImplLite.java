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

import io.opencensus.implcore.common.MillisClock;
import io.opencensus.implcore.internal.SimpleEventQueue;
import io.opencensus.implcore.trace.TraceComponentImplBase;
import io.opencensus.implcore.trace.internal.RandomHandler.SecureRandomHandler;

/** Android-compatible implementation of the {@link TraceComponent}. */
// TraceComponentImplLite was moved to io.opencensus.impllite.trace. This class exists for backwards
// compatibility, so that it can be loaded by opencensus-api 0.5.
@Deprecated
public final class TraceComponentImplLite extends TraceComponentImplBase {

  public TraceComponentImplLite() {
    super(MillisClock.getInstance(), new SecureRandomHandler(), new SimpleEventQueue());
  }
}
