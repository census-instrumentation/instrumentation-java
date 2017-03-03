/*
 * Copyright 2016, Google Inc.
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

package com.google.instrumentation.stats;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link StatsContextFactory}.
 */
@RunWith(JUnit4.class)
public class StatsContextFactoryTest {
  @Test
  public void testDeserializeEmpty() throws Exception {
    Stats.getStatsContextFactory().deserialize(new ByteArrayInputStream(new byte[0]));
  }

  @Test(expected = IOException.class)
  public void testDeserializeWrongFormat() throws Exception {
    // encoded tags should follow the format [tag_type key_len key_bytes value_len value_bytes]*
    Stats.getStatsContextFactory().deserialize(new ByteArrayInputStream(new byte[1]));
  }
}
