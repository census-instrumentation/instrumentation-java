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

package io.opencensus.stats;

import io.opencensus.stats.Measure.MeasureDouble;
import io.opencensus.stats.Measure.MeasureLong;
import io.opencensus.tags.TagKey.TagKeyString;

/**
 * Constants for collecting rpc stats.
 */
public final class RpcMeasureConstants {

  // Rpc tag keys.
  public static final TagKeyString RPC_STATUS = TagKeyString.create("canonical_status");
  public static final TagKeyString RPC_CLIENT_METHOD = TagKeyString.create("method");
  public static final TagKeyString RPC_SERVER_METHOD = TagKeyString.create("method");

  // Constants used to define the following Measures.
  private static final String BYTE = "By";
  private static final String COUNT = "1";
  private static final String MILLISECOND = "ms";

  // RPC client Measures.
  public static final MeasureLong RPC_CLIENT_ERROR_COUNT =
      Measure.MeasureLong.create(
          "grpc.io/client/error_count",
          "RPC Errors",
          COUNT);
  public static final MeasureDouble RPC_CLIENT_REQUEST_BYTES =
      Measure.MeasureDouble.create(
          "grpc.io/client/request_bytes",
          "Request bytes",
          BYTE);
  public static final MeasureDouble RPC_CLIENT_RESPONSE_BYTES =
      Measure.MeasureDouble.create(
          "grpc.io/client/response_bytes",
          "Response bytes",
          BYTE);
  public static final MeasureDouble RPC_CLIENT_ROUNDTRIP_LATENCY =
      Measure.MeasureDouble.create(
          "grpc.io/client/roundtrip_latency",
          "RPC roundtrip latency msec",
          MILLISECOND);
  public static final MeasureDouble RPC_CLIENT_SERVER_ELAPSED_TIME =
      Measure.MeasureDouble.create(
          "grpc.io/client/server_elapsed_time",
          "Server elapsed time in msecs",
          MILLISECOND);
  public static final MeasureDouble RPC_CLIENT_UNCOMPRESSED_REQUEST_BYTES =
      Measure.MeasureDouble.create(
          "grpc.io/client/uncompressed_request_bytes",
          "Uncompressed Request bytes",
          BYTE);
  public static final MeasureDouble RPC_CLIENT_UNCOMPRESSED_RESPONSE_BYTES =
      Measure.MeasureDouble.create(
          "grpc.io/client/uncompressed_response_bytes",
          "Uncompressed Response bytes",
          BYTE);
  public static final MeasureLong RPC_CLIENT_STARTED_COUNT =
      Measure.MeasureLong.create(
          "grpc.io/client/started_count",
          "Number of client RPCs (streams) started",
          COUNT);
  public static final MeasureLong RPC_CLIENT_FINISHED_COUNT =
      Measure.MeasureLong.create(
          "grpc.io/client/finished_count",
          "Number of client RPCs (streams) finished",
          COUNT);
  public static final MeasureLong RPC_CLIENT_REQUEST_COUNT =
      Measure.MeasureLong.create(
          "grpc.io/client/request_count",
          "Number of client RPC request messages",
          COUNT);
  public static final MeasureLong RPC_CLIENT_RESPONSE_COUNT =
      Measure.MeasureLong.create(
          "grpc.io/client/response_count",
          "Number of client RPC response messages",
          COUNT);


  // RPC server Measures.
  public static final MeasureLong RPC_SERVER_ERROR_COUNT =
      Measure.MeasureLong.create(
          "grpc.io/server/error_count",
          "RPC Errors",
          COUNT);
  public static final MeasureDouble RPC_SERVER_REQUEST_BYTES =
      Measure.MeasureDouble.create(
          "grpc.io/server/request_bytes",
          "Request bytes",
          BYTE);
  public static final MeasureDouble RPC_SERVER_RESPONSE_BYTES =
      Measure.MeasureDouble.create(
          "grpc.io/server/response_bytes",
          "Response bytes",
          BYTE);
  public static final MeasureDouble RPC_SERVER_SERVER_ELAPSED_TIME =
      Measure.MeasureDouble.create(
          "grpc.io/server/server_elapsed_time",
          "Server elapsed time in msecs",
          MILLISECOND);
  public static final MeasureDouble RPC_SERVER_SERVER_LATENCY =
      Measure.MeasureDouble.create(
          "grpc.io/server/server_latency",
          "Latency in msecs",
          MILLISECOND);
  public static final MeasureDouble RPC_SERVER_UNCOMPRESSED_REQUEST_BYTES =
      Measure.MeasureDouble.create(
          "grpc.io/server/uncompressed_request_bytes",
          "Uncompressed Request bytes",
          BYTE);
  public static final MeasureDouble RPC_SERVER_UNCOMPRESSED_RESPONSE_BYTES =
      Measure.MeasureDouble.create(
          "grpc.io/server/uncompressed_response_bytes",
          "Uncompressed Response bytes",
          BYTE);
  public static final MeasureLong RPC_SERVER_STARTED_COUNT =
      Measure.MeasureLong.create(
          "grpc.io/server/started_count",
          "Number of server RPCs (streams) started",
          COUNT);
  public static final MeasureLong RPC_SERVER_FINISHED_COUNT =
      Measure.MeasureLong.create(
          "grpc.io/server/finished_count",
          "Number of server RPCs (streams) finished",
          COUNT);
  public static final MeasureLong RPC_SERVER_REQUEST_COUNT =
      Measure.MeasureLong.create(
          "grpc.io/server/request_count",
          "Number of server RPC request messages",
          COUNT);
  public static final MeasureLong RPC_SERVER_RESPONSE_COUNT =
      Measure.MeasureLong.create(
          "grpc.io/server/response_count",
          "Number of server RPC response messages",
          COUNT);

  // Visible for testing.
  RpcMeasureConstants() {
    throw new AssertionError();
  }
}
