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

import com.google.common.base.Preconditions;
import com.google.instrumentation.internal.StringUtil;
import com.google.instrumentation.tags.TagKey.TagType;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.concurrent.Immutable;

/**
 * A map from keys to values that can be used to label anything that is associated with a specific
 * operation.
 *
 * <p>For example, {@code TagMap}s can be used to label stats, log messages, or debugging
 * information.
 */
@Immutable
public final class TagMap {
  /** The maximum length for a string tag value. */
  public static final int MAX_STRING_LENGTH = StringUtil.MAX_LENGTH;

  // The types of the TagKey and value must match for each entry.
  private final Map<TagKey<?>, Object> tags;

  TagMap(Map<TagKey<?>, Object> tags) {
    this.tags = tags;
  }

  Map<TagKey<?>, Object> getTags() {
    return tags;
  }

  /**
   * Returns a builder based on this {@code TagMap}.
   *
   * @return a builder based on this {@code TagMap}.
   */
  public Builder toBuilder() {
    return new Builder(getTags());
  }

  /** Builder for the {@link TagMap} class. */
  public static final class Builder {
    private final Map<TagKey<?>, Object> tags;

    private Builder(Map<TagKey<?>, Object> tags) {
      this.tags = new HashMap<TagKey<?>, Object>(tags);
    }

    Builder() {
      this.tags = new HashMap<TagKey<?>, Object>();
    }

    /**
     * Adds the key/value pair if the key is not present. If the key is present, it logs an error.
     *
     * @param key the key to look up.
     * @param value the value to insert for the given key.
     * @return this
     */
    public Builder insert(TagKey<String> key, String value) {
      Preconditions.checkArgument(key.getTagType() == TagType.TAG_STRING);
      return insertInternal(key, StringUtil.sanitize(value));
    }

    /**
     * Adds the key/value pair if the key is not present. If the key is present, it logs an error.
     *
     * @param key the key to look up.
     * @param value the value to insert for the given key.
     * @return this
     */
    public Builder insert(TagKey<Long> key, long value) {
      Preconditions.checkArgument(key.getTagType() == TagType.TAG_INT);
      return insertInternal(key, value);
    }

    /**
     * Adds the key/value pair if the key is not present. If the key is present, it logs an error.
     *
     * @param key the key to look up.
     * @param value the value to insert for the given key.
     * @return this
     */
    public Builder insert(TagKey<Boolean> key, boolean value) {
      Preconditions.checkArgument(key.getTagType() == TagType.TAG_BOOL);
      return insertInternal(key, value);
    }

    private <TagValueT> Builder insertInternal(TagKey<TagValueT> key, TagValueT value) {
      if (!tags.containsKey(key)) {
        tags.put(key, value);
      }
      return this;
    }

    /**
     * Adds the key/value pair regardless of whether the key is present.
     *
     * @param key the key to look up.
     * @param value the value to set for the given key.
     * @return this
     */
    public Builder set(TagKey<String> key, String value) {
      Preconditions.checkArgument(key.getTagType() == TagType.TAG_STRING);
      return setInternal(key, StringUtil.sanitize(value));
    }

    /**
     * Adds the key/value pair regardless of whether the key is present.
     *
     * @param key the key to look up.
     * @param value the value to set for the given key.
     * @return this
     */
    public Builder set(TagKey<Long> key, long value) {
      Preconditions.checkArgument(key.getTagType() == TagType.TAG_INT);
      return setInternal(key, value);
    }

    /**
     * Adds the key/value pair regardless of whether the key is present.
     *
     * @param key the key to look up.
     * @param value the value to set for the given key.
     * @return this
     */
    public Builder set(TagKey<Boolean> key, boolean value) {
      Preconditions.checkArgument(key.getTagType() == TagType.TAG_BOOL);
      return setInternal(key, value);
    }

    private <TagValueT> Builder setInternal(TagKey<TagValueT> key, TagValueT value) {
      tags.put(key, value);
      return this;
    }

    /**
     * Adds the key/value pair only if the key is already present.
     *
     * @param key the key to look up.
     * @param value the value to update for the given key.
     * @return this
     */
    public Builder update(TagKey<String> key, String value) {
      Preconditions.checkArgument(key.getTagType() == TagType.TAG_STRING);
      return updateInternal(key, StringUtil.sanitize(value));
    }

    /**
     * Adds the key/value pair only if the key is already present.
     *
     * @param key the key to look up.
     * @param value the value to update for the given key.
     * @return this
     */
    public Builder update(TagKey<Long> key, long value) {
      Preconditions.checkArgument(key.getTagType() == TagType.TAG_INT);
      return updateInternal(key, value);
    }

    /**
     * Adds the key/value pair only if the key is already present.
     *
     * @param key the key to look up.
     * @param value the value to update for the given key.
     * @return this
     */
    public Builder update(TagKey<Boolean> key, boolean value) {
      Preconditions.checkArgument(key.getTagType() == TagType.TAG_BOOL);
      return updateInternal(key, value);
    }

    private <TagValueT> Builder updateInternal(TagKey<TagValueT> key, TagValueT value) {
      if (tags.containsKey(key)) {
        tags.put(key, value);
      }
      return this;
    }

    /**
     * Removes the key if it exists.
     *
     * @param key the key to look up.
     * @return this
     */
    public Builder clear(TagKey<?> key) {
      tags.remove(key);
      return this;
    }

    /**
     * Creates a {@code TagMap} from this builder.
     *
     * @return a {@code TagMap} with the same tags as this builder.
     */
    public TagMap build() {
      return new TagMap(new HashMap<TagKey<?>, Object>(tags));
    }
  }
}
