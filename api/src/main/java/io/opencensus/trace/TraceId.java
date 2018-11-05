/*
 * Copyright 2016-17, OpenCensus Authors
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

package io.opencensus.trace;

import io.opencensus.common.Internal;
import io.opencensus.internal.Utils;
import java.util.Arrays;
import java.util.Random;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * A class that represents a trace identifier. A valid trace identifier is a 16-byte array with at
 * least one non-zero byte.
 *
 * @since 0.5
 */
@Immutable
public final class TraceId implements Comparable<TraceId> {
  /**
   * The size in bytes of the {@code TraceId}.
   *
   * @since 0.5
   */
  public static final int SIZE = 16;

  private static final int HEX_SIZE = 32;
  private static final long INVALID_ID = 0;

  /**
   * The invalid {@code TraceId}. All bytes are '\0'.
   *
   * @since 0.5
   */
  public static final TraceId INVALID = new TraceId(INVALID_ID, INVALID_ID);

  // The internal representation of the TraceId.
  private final long idHi;
  private final long idLo;

  private TraceId(long idHi, long idLo) {
    this.idHi = idHi;
    this.idLo = idLo;
  }

  /**
   * Returns a {@code TraceId} built from a byte representation.
   *
   * <p>Equivalent with:
   *
   * <pre>{@code
   * TraceId.fromBytes(buffer, 0);
   * }</pre>
   *
   * @param buffer the representation of the {@code TraceId}.
   * @return a {@code TraceId} whose representation is given by the {@code buffer} parameter.
   * @throws NullPointerException if {@code buffer} is null.
   * @throws IllegalArgumentException if {@code buffer.length} is not {@link TraceId#SIZE}.
   * @since 0.5
   */
  public static TraceId fromBytes(byte[] buffer) {
    Utils.checkNotNull(buffer, "buffer");
    Utils.checkArgument(
        buffer.length == SIZE, "Invalid size: expected %s, got %s", SIZE, buffer.length);
    return new TraceId(
        BigendianEncoding.longFromByteArray(buffer, 0),
        BigendianEncoding.longFromByteArray(buffer, BigendianEncoding.LONG_BYTES));
  }

  /**
   * Returns a {@code TraceId} whose representation is copied from the {@code src} beginning at the
   * {@code srcOffset} offset.
   *
   * @param src the buffer where the representation of the {@code TraceId} is copied.
   * @param srcOffset the offset in the buffer where the representation of the {@code TraceId}
   *     begins.
   * @return a {@code TraceId} whose representation is copied from the buffer.
   * @throws NullPointerException if {@code src} is null.
   * @throws IndexOutOfBoundsException if {@code srcOffset+TraceId.SIZE} is greater than {@code
   *     src.length}.
   * @since 0.5
   */
  public static TraceId fromBytes(byte[] src, int srcOffset) {
    return new TraceId(
        BigendianEncoding.longFromByteArray(src, srcOffset),
        BigendianEncoding.longFromByteArray(src, srcOffset + BigendianEncoding.LONG_BYTES));
  }

  /**
   * Returns a {@code TraceId} built from a lowercase base16 representation.
   *
   * @param src the lowercase base16 representation.
   * @return a {@code TraceId} built from a lowercase base16 representation.
   * @throws NullPointerException if {@code src} is null.
   * @throws IllegalArgumentException if {@code src.length} is not {@code 2 * TraceId.SIZE} OR if
   *     the {@code str} has invalid characters.
   * @since 0.11
   */
  public static TraceId fromLowerBase16(CharSequence src) {
    Utils.checkArgument(
        src.length() == HEX_SIZE, "Invalid size: expected %s, got %s", HEX_SIZE, src.length());
    return fromBytes(LowerCaseBase16Encoding.decodeToBytes(src));
  }

  /**
   * Generates a new random {@code TraceId}.
   *
   * @param random the random number generator.
   * @return a new valid {@code TraceId}.
   * @since 0.5
   */
  public static TraceId generateRandomId(Random random) {
    long idHi;
    long idLo;
    do {
      idHi = random.nextLong();
      idLo = random.nextLong();
    } while (idHi == INVALID_ID && idLo == INVALID_ID);
    return new TraceId(idHi, idLo);
  }

  /**
   * Returns the 16-bytes array representation of the {@code TraceId}.
   *
   * @return the 16-bytes array representation of the {@code TraceId}.
   * @since 0.5
   */
  public byte[] getBytes() {
    byte[] bytes = new byte[SIZE];
    BigendianEncoding.longToByteArray(idHi, bytes, 0);
    BigendianEncoding.longToByteArray(idLo, bytes, BigendianEncoding.LONG_BYTES);
    return bytes;
  }

  /**
   * Copies the byte array representations of the {@code TraceId} into the {@code dest} beginning at
   * the {@code destOffset} offset.
   *
   * <p>Equivalent with (but faster because it avoids any new allocations):
   *
   * <pre>{@code
   * System.arraycopy(getBytes(), 0, dest, destOffset, TraceId.SIZE);
   * }</pre>
   *
   * @param dest the destination buffer.
   * @param destOffset the starting offset in the destination buffer.
   * @throws NullPointerException if {@code dest} is null.
   * @throws IndexOutOfBoundsException if {@code destOffset+TraceId.SIZE} is greater than {@code
   *     dest.length}.
   * @since 0.5
   */
  public void copyBytesTo(byte[] dest, int destOffset) {
    BigendianEncoding.longToByteArray(idHi, dest, destOffset);
    BigendianEncoding.longToByteArray(idLo, dest, destOffset + BigendianEncoding.LONG_BYTES);
  }

  /**
   * Returns whether the {@code TraceId} is valid. A valid trace identifier is a 16-byte array with
   * at least one non-zero byte.
   *
   * @return {@code true} if the {@code TraceId} is valid.
   * @since 0.5
   */
  public boolean isValid() {
    return idHi != INVALID_ID || idLo != INVALID_ID;
  }

  /**
   * Returns the lowercase base16 encoding of this {@code TraceId}.
   *
   * @return the lowercase base16 encoding of this {@code TraceId}.
   * @since 0.11
   */
  public String toLowerBase16() {
    return LowerCaseBase16Encoding.encodeToString(getBytes());
  }

  /**
   * Returns the lower 8 bytes of the trace-id as a long value, assuming little-endian order. This
   * is used in ProbabilitySampler.
   *
   * <p>This method is marked as internal and subject to change.
   *
   * @return the lower 8 bytes of the trace-id as a long value, assuming little-endian order.
   */
  @Internal
  public long getLowerLong() {
    return (idHi < 0) ? -idHi : idHi;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof TraceId)) {
      return false;
    }

    TraceId that = (TraceId) obj;
    return idHi == that.idHi && idLo == that.idLo;
  }

  @Override
  public int hashCode() {
    // TODO: Use Objects.hashCode when switch to java17.
    return Arrays.hashCode(new Long[] {idHi, idLo});
  }

  @Override
  public String toString() {
    return "TraceId{traceId=" + toLowerBase16() + "}";
  }

  @Override
  public int compareTo(TraceId that) {
    if (idHi == that.idHi) {
      if (idLo == that.idLo) {
        return 0;
      }
      return idLo < that.idLo ? -1 : 1;
    }
    return idHi < that.idHi ? -1 : 1;
  }
}
