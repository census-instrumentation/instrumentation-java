/*
 * Copyright 2018, OpenCensus Authors
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

package io.opencensus.contrib.monitoredresource.util;

import static com.google.common.base.MoreObjects.firstNonNull;

import io.opencensus.resource.Resource;
import java.util.Map;
import javax.annotation.concurrent.Immutable;

/**
 * {@link MonitoredResource} represents an auto-detected monitored resource used by application for
 * exporting stats. It has a {@code ResourceType} associated with a mapping from resource labels to
 * values.
 *
 * @since 0.13
 * @deprecated use {@link Resource}.
 */
@Immutable
@Deprecated
public abstract class MonitoredResource {

  MonitoredResource() {}

  /**
   * Returns the {@link ResourceType} of this {@link MonitoredResource}.
   *
   * @return the {@code ResourceType}.
   * @since 0.13
   */
  public abstract ResourceType getResourceType();

  // TODO(songya): consider using a tagged union match() approach (that will introduce
  // dependency on opencensus-api).

  /**
   * {@link MonitoredResource} for AWS EC2 instance.
   *
   * @since 0.13
   * @deprecated use {@link Resource}.
   */
  @Immutable
  @Deprecated
  public static final class AwsEc2InstanceMonitoredResource extends MonitoredResource {
    private final Map<String, String> awsInstanceLabels;

    @Override
    public ResourceType getResourceType() {
      return ResourceType.AWS_EC2_INSTANCE;
    }

    /**
     * Returns the AWS account ID.
     *
     * @return the AWS account ID.
     * @since 0.13
     */
    public String getAccount() {
      return firstNonNull(awsInstanceLabels.get(AwsEc2InstanceResource.ACCOUNT_ID_KEY), "");
    }

    /**
     * Returns the AWS EC2 instance ID.
     *
     * @return the AWS EC2 instance ID.
     * @since 0.13
     */
    public String getInstanceId() {
      return firstNonNull(awsInstanceLabels.get(AwsEc2InstanceResource.INSTANCE_ID_KEY), "");
    }

    /**
     * Returns the AWS region.
     *
     * @return the AWS region.
     * @since 0.13
     */
    public String getRegion() {
      return firstNonNull(awsInstanceLabels.get(AwsEc2InstanceResource.REGION_KEY), "");
    }

    /**
     * Returns an {@link AwsEc2InstanceMonitoredResource}.
     *
     * @param account the AWS account ID.
     * @param instanceId the AWS EC2 instance ID.
     * @param region the AWS region.
     * @return an {@code AwsEc2InstanceMonitoredResource}.
     * @since 0.15
     */
    public static AwsEc2InstanceMonitoredResource create(
        String account, String instanceId, String region) {
      return new AwsEc2InstanceMonitoredResource(
          AwsEc2InstanceResource.create(account, region, instanceId).getLabels());
    }

    static AwsEc2InstanceMonitoredResource autoDetectResource() {
      return new AwsEc2InstanceMonitoredResource(AwsEc2InstanceResource.detect().getLabels());
    }

    private AwsEc2InstanceMonitoredResource(Map<String, String> awsInstanceLabels) {
      this.awsInstanceLabels = awsInstanceLabels;
    }
  }

  /**
   * {@link MonitoredResource} for GCP GCE instance.
   *
   * @since 0.13
   * @deprecated use {@link Resource}.
   */
  @Immutable
  @Deprecated
  public static final class GcpGceInstanceMonitoredResource extends MonitoredResource {
    private final Map<String, String> gcpInstanceLabels;

    @Override
    public ResourceType getResourceType() {
      return ResourceType.GCP_GCE_INSTANCE;
    }

    /**
     * Returns the GCP account number for the instance.
     *
     * @return the GCP account number for the instance.
     * @since 0.13
     */
    public String getAccount() {
      return firstNonNull(gcpInstanceLabels.get(GcpGceInstanceResource.PROJECT_ID_KEY), "");
    }

    /**
     * Returns the GCP GCE instance ID.
     *
     * @return the GCP GCE instance ID.
     * @since 0.13
     */
    public String getInstanceId() {
      return firstNonNull(gcpInstanceLabels.get(GcpGceInstanceResource.INSTANCE_ID_KEY), "");
    }

    /**
     * Returns the GCP zone.
     *
     * @return the GCP zone.
     * @since 0.13
     */
    public String getZone() {
      return firstNonNull(gcpInstanceLabels.get(GcpGceInstanceResource.ZONE_KEY), "");
    }

    /**
     * Returns a {@link GcpGceInstanceMonitoredResource}.
     *
     * @param account the GCP account number.
     * @param instanceId the GCP GCE instance ID.
     * @param zone the GCP zone.
     * @return a {@code GcpGceInstanceMonitoredResource}.
     * @since 0.15
     */
    public static GcpGceInstanceMonitoredResource create(
        String account, String instanceId, String zone) {
      return new GcpGceInstanceMonitoredResource(
          GcpGceInstanceResource.create(account, zone, instanceId).getLabels());
    }

    static GcpGceInstanceMonitoredResource autoDetectResource() {
      return new GcpGceInstanceMonitoredResource(GcpGceInstanceResource.detect().getLabels());
    }

    private GcpGceInstanceMonitoredResource(Map<String, String> gcpInstanceLabels) {
      this.gcpInstanceLabels = gcpInstanceLabels;
    }
  }

  /**
   * {@link MonitoredResource} for GCP GKE container.
   *
   * @since 0.13
   * @deprecated use {@link Resource}.
   */
  @Immutable
  @Deprecated
  public static final class GcpGkeContainerMonitoredResource extends MonitoredResource {
    private final Map<String, String> k8sContainerLabels;
    private final Map<String, String> gcpInstanceLabels;

    @Override
    public ResourceType getResourceType() {
      return ResourceType.GCP_GKE_CONTAINER;
    }

    /**
     * Returns the GCP account number for the instance.
     *
     * @return the GCP account number for the instance.
     * @since 0.13
     */
    public String getAccount() {
      return firstNonNull(gcpInstanceLabels.get(GcpGceInstanceResource.PROJECT_ID_KEY), "");
    }

    /**
     * Returns the GCP GKE cluster name.
     *
     * @return the GCP GKE cluster name.
     * @since 0.13
     */
    public String getClusterName() {
      return firstNonNull(k8sContainerLabels.get(K8sContainerResource.CLUSTER_NAME_KEY), "");
    }

    /**
     * Returns the GCP GKE container name.
     *
     * @return the GCP GKE container name.
     * @since 0.13
     */
    public String getContainerName() {
      return firstNonNull(k8sContainerLabels.get(K8sContainerResource.CONTAINER_NAME_KEY), "");
    }

    /**
     * Returns the GCP GKE namespace ID.
     *
     * @return the GCP GKE namespace ID.
     * @since 0.13
     */
    public String getNamespaceId() {
      return firstNonNull(k8sContainerLabels.get(K8sContainerResource.NAMESPACE_NAME_KEY), "");
    }

    /**
     * Returns the GCP GKE instance ID.
     *
     * @return the GCP GKE instance ID.
     * @since 0.13
     */
    public String getInstanceId() {
      return firstNonNull(gcpInstanceLabels.get(GcpGceInstanceResource.INSTANCE_ID_KEY), "");
    }

    /**
     * Returns the GCP GKE Pod ID.
     *
     * @return the GCP GKE Pod ID.
     * @since 0.13
     */
    public String getPodId() {
      return firstNonNull(k8sContainerLabels.get(K8sContainerResource.POD_NAME_KEY), "");
    }

    /**
     * Returns the GCP zone.
     *
     * @return the GCP zone.
     * @since 0.13
     */
    public String getZone() {
      return firstNonNull(gcpInstanceLabels.get(GcpGceInstanceResource.ZONE_KEY), "");
    }

    /**
     * Returns a {@link GcpGkeContainerMonitoredResource}.
     *
     * @param account the GCP account number.
     * @param clusterName the GCP GKE cluster name.
     * @param containerName the GCP GKE container name.
     * @param namespaceId the GCP GKE namespace ID.
     * @param instanceId the GCP GKE instance ID.
     * @param podId the GCP GKE Pod ID.
     * @param zone the GCP zone.
     * @return a {@code GcpGkeContainerMonitoredResource}.
     * @since 0.15
     */
    public static GcpGkeContainerMonitoredResource create(
        String account,
        String clusterName,
        String containerName,
        String namespaceId,
        String instanceId,
        String podId,
        String zone) {
      return new GcpGkeContainerMonitoredResource(
          K8sContainerResource.create(clusterName, namespaceId, podId, containerName).getLabels(),
          GcpGceInstanceResource.create(account, zone, instanceId).getLabels());
    }

    static GcpGkeContainerMonitoredResource autoDetectResource() {
      return new GcpGkeContainerMonitoredResource(
          K8sContainerResource.detect().getLabels(), GcpGceInstanceResource.detect().getLabels());
    }

    private GcpGkeContainerMonitoredResource(
        Map<String, String> k8sContainerLabels, Map<String, String> gcpInstanceLabels) {

      this.k8sContainerLabels = k8sContainerLabels;
      this.gcpInstanceLabels = gcpInstanceLabels;
    }
  }
}
