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

package io.opencensus.tags;

final class TagContextFactoryImpl extends TagContextFactory {
  TagContextFactoryImpl() {}

  @Override
  public TagContextImpl empty() {
    return TagContextImpl.EMPTY;
  }

  @Override
  public TagContextBuilder emptyBuilder() {
    return new TagContextBuilderImpl();
  }

  @Override
  public TagContextBuilder toBuilder(TagContext tags) {
    // Copy the tags more efficiently in the expected case, when the TagContext is a TagContextImpl.
    if (tags instanceof TagContextImpl) {
      return new TagContextBuilderImpl(((TagContextImpl) tags).getTags());
    } else {
      // TODO(sebright): Write a test for this.
      TagContextBuilder builder = new TagContextBuilderImpl();
      for (Tag tag : tags) {
        TagContextUtils.addTagToBuilder(tag, builder);
      }
      return builder;
    }
  }
}
