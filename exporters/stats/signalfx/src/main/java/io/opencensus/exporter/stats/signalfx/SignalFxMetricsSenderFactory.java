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

package io.opencensus.exporter.stats.signalfx;

import com.signalfx.endpoint.SignalFxEndpoint;
import com.signalfx.metrics.auth.StaticAuthToken;
import com.signalfx.metrics.connection.HttpDataPointProtobufReceiverFactory;
import com.signalfx.metrics.connection.HttpEventProtobufReceiverFactory;
import com.signalfx.metrics.errorhandler.OnSendErrorHandler;
import com.signalfx.metrics.flush.AggregateMetricSender;
import java.net.URL;
import java.util.Collections;

/** Interface for creators of {@link AggregateMetricSender}. */
public interface SignalFxMetricsSenderFactory {

  /**
   * Creates a new SignalFx metrics sender instance.
   *
   * @param url The SignalFx ingest endpoint URL.
   * @param token The SignalFx ingest token.
   * @param errorHandler An {@link OnSendErrorHandler} through which errors when sending data to
   *     SignalFx will be communicated.
   * @return The created {@link AggregateMetricSender} instance.
   */
  AggregateMetricSender create(URL url, String token, OnSendErrorHandler errorHandler);

  /** The default, concrete implementation of this interface. */
  SignalFxMetricsSenderFactory DEFAULT =
      new SignalFxMetricsSenderFactory() {
        @Override
        public AggregateMetricSender create(
            URL url, String token, OnSendErrorHandler errorHandler) {
          SignalFxEndpoint endpoint =
              new SignalFxEndpoint(url.getProtocol(), url.getHost(), url.getPort());
          return new AggregateMetricSender(
              null,
              new HttpDataPointProtobufReceiverFactory(endpoint).setVersion(2),
              new HttpEventProtobufReceiverFactory(endpoint),
              new StaticAuthToken(token),
              Collections.singleton(errorHandler));
        }
      };
}
