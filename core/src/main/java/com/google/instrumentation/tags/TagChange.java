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

package com.google.instrumentation.tags;

import com.google.auto.value.AutoValue;
import com.google.instrumentation.internal.StringUtil;
import javax.annotation.concurrent.Immutable;

/** An object representing a change to be applied to a tag in a {@link TagContext}. */
@Immutable
@AutoValue
public abstract class TagChange {
  /** The maximum length for a string tag value. */
  public static final int MAX_STRING_LENGTH = StringUtil.MAX_LENGTH;

  TagChange() {}

  /**
   * Returns a {@code TagChange} for updating a string key.
   *
   * @param key the key to update.
   * @param op the operation to apply to the key.
   * @param value the new tag value.
   * @return a {@code TagChange} representing a change to a {@link TagContext}.
   */
  public static TagChange create(TagKey<String> key, TagOp op, String value) {
    return new AutoValue_TagChange(key, op, StringUtil.sanitize(value));
  }

  /**
   * Returns a {@code TagChange} for updating an integer key.
   *
   * @param key the key to update.
   * @param op the operation to apply to the key.
   * @param value the new tag value.
   * @return a {@code TagChange} representing a change to a {@link TagContext}.
   */
  public static TagChange create(TagKey<Long> key, TagOp op, long value) {
    return new AutoValue_TagChange(key, op, value);
  }

  /**
   * Returns a {@code TagChange} for updating a boolean key.
   *
   * @param key the key to update.
   * @param op the operation to apply to the key.
   * @param value the new tag value.
   * @return a {@code TagChange} representing a change to a {@link TagContext}.
   */
  public static TagChange create(TagKey<Boolean> key, TagOp op, boolean value) {
    return new AutoValue_TagChange(key, op, value);
  }

  abstract TagKey<?> getKey();

  abstract TagOp getOp();

  abstract Object getValue();
}
