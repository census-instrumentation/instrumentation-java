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

package io.opencensus.implcore.tags;

import io.opencensus.tags.Tag;
import io.opencensus.tags.TagContext;
import io.opencensus.tags.TagContextBuilder;
import io.opencensus.tags.TagContexts;
import java.util.Iterator;

public final class TagContextsImpl extends TagContexts {

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
    // TODO(sebright): Consider treating an unknown TagContext as empty.  That would allow us to
    // remove TagContext.unsafeGetIterator().

    // Copy the tags more efficiently in the expected case, when the TagContext is a TagContextImpl.
    if (tags instanceof TagContextImpl) {
      return new TagContextBuilderImpl(((TagContextImpl) tags).getTags());
    } else {
      TagContextBuilderImpl builder = new TagContextBuilderImpl();
      for (Iterator<Tag> i = tags.unsafeGetIterator(); i.hasNext(); ) {
        TagContextUtils.addTagToBuilder(i.next(), builder);
      }
      return builder;
    }
  }
}
