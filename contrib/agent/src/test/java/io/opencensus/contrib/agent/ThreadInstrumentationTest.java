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

package io.opencensus.contrib.agent;

import static com.google.common.truth.Truth.assertThat;

import io.grpc.Context;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.After;
import org.junit.Test;

/** Integration tests for {@link ThreadInstrumentation}. */
public class ThreadInstrumentationTest {

  private static final Context.Key<String> KEY = Context.key("mykey");

  private Context previousContext;

  @After
  public void afterMethod() {
    Context.current().detach(previousContext);
  }

  @Test(timeout = 5000)
  public void start_Runnable() throws Exception {
    final Thread callerThread = Thread.currentThread();
    final Context context = Context.current().withValue(KEY, "myvalue");
    previousContext = context.attach();

    final AtomicBoolean tested = new AtomicBoolean(false);

    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        assertThat(Thread.currentThread()).isNotSameAs(callerThread);
        assertThat(Context.current()).isSameAs(context);
        assertThat(KEY.get()).isEqualTo("myvalue");
        tested.set(true);
      }
    });

    thread.start();
    thread.join();

    assertThat(tested.get()).isTrue();
  }

  @Test(timeout = 5000)
  public void start_Subclass() throws Exception {
    final Thread callerThread = Thread.currentThread();
    final Context context = Context.current().withValue(KEY, "myvalue");
    previousContext = context.attach();

    final AtomicBoolean tested = new AtomicBoolean(false);

    Thread thread = new Thread() {
      @Override
      public void run() {
        assertThat(Thread.currentThread()).isNotSameAs(callerThread);
        assertThat(Context.current()).isSameAs(context);
        assertThat(KEY.get()).isEqualTo("myvalue");
        tested.set(true);
      }
    };

    thread.start();
    thread.join();

    assertThat(tested.get()).isTrue();
  }
}
