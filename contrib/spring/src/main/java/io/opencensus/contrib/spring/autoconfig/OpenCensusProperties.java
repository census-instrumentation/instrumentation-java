/*
 * Copyright 2019, OpenCensus Authors
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

package io.opencensus.contrib.spring.autoconfig;

import static io.opencensus.contrib.spring.autoconfig.OpenCensusProperties.Trace.Propagation.TRACE_PROPAGATION_TRACE_CONTEXT;

import io.opencensus.trace.propagation.B3InjectionFormat;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Opencensus settings.
 *
 * @since 0.23.0
 */
@ConfigurationProperties("opencensus.spring")
public class OpenCensusProperties {

  private boolean enabled = true;
  private Trace trace = new Trace();

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Trace getTrace() {
    return this.trace;
  }

  public void setTrace(Trace trace) {
    this.trace = trace;
  }

  /**
   * Trace properties.
   *
   * @since 0.23
   */
  public static final class Trace {

    public enum Propagation {
      /**
       * Specifies Trace Context format for span context propagation.
       *
       * @since 0.23
       */
      TRACE_PROPAGATION_TRACE_CONTEXT,

      /**
       * Specifies B3 format for span context propagation. For more granular configuration of the B3
       * format, see {@link B3Properties} and https://github.com/openzipkin/b3-propagation.
       *
       * @since 0.23
       * @see B3Properties
       */
      TRACE_PROPAGATION_B3,
    }

    /**
     * B3 properties. This only applies if {@link Trace#getPropagation() propagation} is set to
     * {@link Propagation#TRACE_PROPAGATION_B3}. Otherwise, these parameters are ignored.
     *
     * @since 0.25
     * @see Propagation#TRACE_PROPAGATION_B3
     */
    public static final class B3Properties {

      private B3InjectionFormat[] injectionFormats =
          new B3InjectionFormat[] {B3InjectionFormat.MULTI};

      /**
       * Retrieve the propagation formats.
       *
       * @return the propagation formats that will be sent downstream
       */
      public B3InjectionFormat[] getInjectionFormats() {
        final B3InjectionFormat[] retval = new B3InjectionFormat[injectionFormats.length];
        System.arraycopy(injectionFormats, 0, retval, 0, injectionFormats.length);
        return retval;
      }

      /**
       * Specify the propagation formats.
       *
       * @param injectionFormats the propagation formats to send downstream
       */
      public void setInjectionFormats(final B3InjectionFormat[] injectionFormats) {
        Objects.requireNonNull(injectionFormats, "injectionFormats cannot be null");
        final B3InjectionFormat[] copy = new B3InjectionFormat[injectionFormats.length];
        System.arraycopy(injectionFormats, 0, copy, 0, injectionFormats.length);
        this.injectionFormats = copy;
      }
    }

    private Propagation propagation = TRACE_PROPAGATION_TRACE_CONTEXT;
    private boolean publicEndpoint = false;
    private B3Properties b3 = new B3Properties();

    public Propagation getPropagation() {
      return propagation;
    }

    public void setPropagation(Propagation propagation) {
      this.propagation = propagation;
    }

    public boolean isPublicEndpoint() {
      return publicEndpoint;
    }

    public void setPublicEndpoint(boolean publicEndpoint) {
      this.publicEndpoint = publicEndpoint;
    }

    public B3Properties getB3() {
      return b3;
    }

    public void setB3(final B3Properties b3) {
      this.b3 = b3;
    }
  }
}
