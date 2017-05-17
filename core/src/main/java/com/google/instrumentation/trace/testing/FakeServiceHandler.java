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

package com.google.instrumentation.trace.testing;

import com.google.instrumentation.trace.SpanData;
import com.google.instrumentation.trace.TraceExporter.ServiceHandler;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.concurrent.GuardedBy;

/** Fake {@link ServiceHandler} for testing only. */
public final class FakeServiceHandler extends ServiceHandler {
  private final Object monitor = new Object();

  @GuardedBy("monitor")
  private final List<SpanData> spanDataList = new LinkedList<SpanData>();

  @Override
  public void export(List<SpanData> spanDataList) {
    synchronized (monitor) {
      this.spanDataList.addAll(spanDataList);
      monitor.notifyAll();
    }
  }

  /**
   * Waits until we received numberOfSpans spans to export. Returns the list of exported {@link
   * SpanData} objects, otherwise {@code null} if the current thread is interrupted.
   *
   * @param numberOfSpans the number of minimum spans to be collected.
   * @return the list of exported {@link SpanData} objects, otherwise {@code null} if the current
   *     thread is interrupted.
   */
  public List<SpanData> waitForExport(int numberOfSpans) {
    List<SpanData> ret;
    synchronized (monitor) {
      while (spanDataList.size() < numberOfSpans) {
        try {
          monitor.wait();
        } catch (InterruptedException e) {
          // Preserve the interruption status as per guidance.
          Thread.currentThread().interrupt();
          return null;
        }
      }
      ret = new ArrayList<SpanData>(spanDataList);
      spanDataList.clear();
    }
    return ret;
  }
}
