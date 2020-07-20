/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.flink.factory;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.FlinkClientBuilder;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.util.FlinkConfUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.ClusterDescriptor;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClient;
import org.apache.flink.shaded.curator.org.apache.curator.framework.CuratorFramework;
import org.apache.flink.shaded.curator.org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.flink.shaded.curator.org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.flink.shaded.curator.org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.flink.util.FlinkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

/**
 * Date: 2020/6/1
 * Company: www.dtstack.com
 * @author maqi
 */
public class SessionClientFactory extends AbstractClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SessionClientFactory.class);

    private ClusterDescriptor kubernetesClusterDescriptor;
    private ClusterSpecification clusterSpecification;
    private ClusterClient<String> clusterClient;
    private FlinkKubeClient flinkKubeClient;

    private FlinkConfig flinkConfig;
    private InterProcessMutex clusterClientLock;
    private String lockPath;
    private CuratorFramework zkClient;
    private Configuration flinkConfiguration;

    public SessionClientFactory(FlinkClientBuilder flinkClientBuilder) throws MalformedURLException {
        this.flinkConfig = flinkClientBuilder.getFlinkConfig();
        this.flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
        this.flinkKubeClient = flinkClientBuilder.getFlinkKubeClient();

        this.clusterSpecification = FlinkConfUtil.createClusterSpecification(flinkConfiguration, 0, null);
        this.kubernetesClusterDescriptor = createSessionClusterDescriptor();

        initZkClient();
        this.clusterClientLock = new InterProcessMutex(zkClient, lockPath);
    }

    private void initZkClient() {
        String zkAddress = flinkConfiguration.getValue(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM);
        if (StringUtils.isBlank(zkAddress)) {
            throw new RdosDefineException("zkAddress is error");
        }

        String sessionNodeName = flinkConfig.getCluster() + ConfigConstrant.SPLIT + flinkConfig.getNamespace();
        this.lockPath = String.format("/kubernetes_session/%s", sessionNodeName);

        this.zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkAddress).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(1000)
                .sessionTimeoutMs(1000).build();
        this.zkClient.start();
        LOG.warn("connector zk success...");
    }

    public boolean startFlinkSession() {
        try {
            if (this.clusterClientLock.acquire(5, TimeUnit.MINUTES)) {
                ClusterClient<String> retrieveClusterClient = initClusterClient();

                if (retrieveClusterClient != null) {
                    clusterClient = retrieveClusterClient;
                    LOG.info("retrieve flink client with session on Kubernetes success");
                    return true;
                }

                if (flinkConfig.getSessionStartAuto()) {
                    try {
                        clusterClient = kubernetesClusterDescriptor.deploySessionCluster(clusterSpecification).getClusterClient();
                        return true;
                    } catch (FlinkException e) {
                        LOG.info("Couldn't deploy session on Kubernetes cluster, {}", e);
                        throw e;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Couldn't deploy session on Kubernetes cluster:{}", e);
        } finally {
            if (this.clusterClientLock.isAcquiredInThisProcess()) {
                try {
                    this.clusterClientLock.release();
                } catch (Exception e) {
                    LOG.error("lockPath:{} release clusterClientLock error:{}", lockPath, e);
                }
            }
        }

        return false;
    }

    /**
     *   取消任务时，调用shutDownCluster
     */
    public void stopFlinkSession() {
        try {
            clusterClient.shutDownCluster();
        } catch (Exception ex) {
            LOG.info("[FlinkSessionStarter] Could not properly shutdown cluster client.", ex);
        }
    }

    @Override
    public ClusterClient getClusterClient() {
        return clusterClient;
    }

    /**
     * 获取ClusterClient
     */
    public ClusterClient<String> initClusterClient() {
        Configuration newConf = new Configuration(flinkConfiguration);
        if (!flinkConfig.getFlinkHighAvailability()) {
            setNoneHaModeConfig(newConf);
        }

        KubernetesClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf);
        String clusterId = acquireAppIdAndSetClusterId(newConf);

        ClusterClient<String> clusterClient = null;
        if (clusterId != null && flinkKubeClient.getInternalService(clusterId) != null) {
            ClusterClientProvider<String> clusterClientProvider = clusterDescriptor.retrieve(clusterId);
            clusterClient = clusterClientProvider.getClusterClient();

            LOG.warn("---init flink client with session on Kubernetes success----");

            return clusterClient;
        }

        LOG.info("No flink session, Couldn't retrieve Kubernetes cluster.");

        return null;
    }


    private String acquireAppIdAndSetClusterId(Configuration configuration) {
        try {
            String clusterId = configuration.get(KubernetesConfigOptions.CLUSTER_ID);
            return clusterId;
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }
    }

    public ClusterDescriptor<String> createSessionClusterDescriptor() throws MalformedURLException {
        Configuration newConf = new Configuration(flinkConfiguration);

        if (!flinkConfig.getFlinkHighAvailability()) {
            setNoneHaModeConfig(newConf);
        } else {
            //由engine管控的session clusterId不进行设置，默认使用appId作为clusterId
            newConf.removeConfig(HighAvailabilityOptions.HA_CLUSTER_ID);
        }

        KubernetesClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf);

        // plugin dependent on shipfile
        if (StringUtils.isNotBlank(flinkConfig.getPluginLoadMode()) && ConfigConstrant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(flinkConfig.getPluginLoadMode())) {
            newConf.setString(ConfigConstrant.FLINK_PLUGIN_LOAD_MODE, flinkConfig.getPluginLoadMode());
            newConf.setString("classloader.resolve-order", "parent-first");
        }

        return clusterDescriptor;
    }
}
