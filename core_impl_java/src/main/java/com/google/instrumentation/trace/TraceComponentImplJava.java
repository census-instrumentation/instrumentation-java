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

import com.google.instrumentation.common.Clock;
import com.google.instrumentation.common.DisruptorEventQueue;

/** Java 7 and 8 implementation of the {@link TraceComponent}. */
public abstract class TraceComponentImplJava extends TraceComponentImplBase {

  public TraceComponentImplJava(Clock clock) {
    super(clock, DisruptorEventQueue.getInstance());
  }
}
