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

package com.google.instrumentation.internal.logging;

import java.util.logging.Level;

/** Logging interface that can be used for testing log messages. */
public interface Logger {

  /**
   * Log the given message at the given level.
   *
   * @param level the logging level.
   * @param message the message to log.
   */
  void log(Level level, String message);
}
