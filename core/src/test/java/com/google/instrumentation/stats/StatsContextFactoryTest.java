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

import static com.google.common.truth.Truth.assertThat;

import com.google.io.base.VarInt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link StatsContextFactory}.
 */
@RunWith(JUnit4.class)
public class StatsContextFactoryTest {
  private static final int VALUE_TYPE_STRING = 0;
  private static final int VALUE_TYPE_INTEGER = 1;
  private static final int VALUE_TYPE_BOOLEAN = 2;

  private static final String KEY1 = "Key";
  private static final String VALUE_STRING = "String";
  private static final int VALUE_INT = 10;
  private static final boolean VALUE_BOOL = true;

  @Test
  public void testDeserializeEmptyReturnDefaultStatsContext() throws Exception {
    StatsContext expected = Stats.getStatsContextFactory().getDefault();
    StatsContext actual = testDeserialize(new ByteArrayInputStream(new byte[0]));
    assertThat(actual).isEqualTo(expected);
  }

  @Test(expected = IOException.class)
  public void testDeserializeValueTypeInteger() throws Exception {
    // TODO(songya): test should pass after we add support for type integer
    testDeserialize(constructInputStream(VALUE_TYPE_INTEGER));
  }

  @Test(expected = IOException.class)
  public void testDeserializeValueTypeBoolean() throws Exception {
    // TODO(songya): test should pass after we add support for type boolean
    testDeserialize(constructInputStream(VALUE_TYPE_BOOLEAN));
  }

  @Test(expected = IOException.class)
  public void testDeserializeWrongFormat() throws Exception {
    // encoded tags should follow the format [tag_type key_len key_bytes value_len value_bytes]*
    testDeserialize(new ByteArrayInputStream(new byte[1]));
  }

  private static StatsContext testDeserialize(InputStream inputStream) throws IOException {
    return Stats.getStatsContextFactory().deserialize(inputStream);
  }

  /*
   * TODO(songya): after supporting serialize integer and boolean,
   * remove this method and use StatsContext.serialize() instead.
   * Currently StatsContext.serialize() can only serialize strings.
   */
  private static InputStream constructInputStream(int valueType) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    VarInt.putVarInt(valueType, byteArrayOutputStream);
    encodeString(KEY1, byteArrayOutputStream);
    switch (valueType) {
      case VALUE_TYPE_STRING:
        encodeString(VALUE_STRING, byteArrayOutputStream);
        break;
      case VALUE_TYPE_INTEGER:
        encodeInteger(VALUE_INT, byteArrayOutputStream);
        break;
      case VALUE_TYPE_BOOLEAN:
        encodeBoolean(VALUE_BOOL, byteArrayOutputStream);
        break;
      default:
        return null;
    }
    return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
  }

  private static void encodeString(String input, ByteArrayOutputStream byteArrayOutputStream)
      throws IOException {
    VarInt.putVarInt(input.length(), byteArrayOutputStream);
    byteArrayOutputStream.write(input.getBytes("UTF-8"));
  }

  private static void encodeInteger(int input, ByteArrayOutputStream byteArrayOutputStream)
      throws IOException {
    VarInt.putVarInt(Integer.toString(input).length(), byteArrayOutputStream);
    byteArrayOutputStream.write((byte) input);
  }

  private static void encodeBoolean(boolean input, ByteArrayOutputStream byteArrayOutputStream)
      throws IOException {
    VarInt.putVarInt(Boolean.toString(input).length(), byteArrayOutputStream);
    byteArrayOutputStream.write((byte) (input ? 1 : 0));
  }
}
