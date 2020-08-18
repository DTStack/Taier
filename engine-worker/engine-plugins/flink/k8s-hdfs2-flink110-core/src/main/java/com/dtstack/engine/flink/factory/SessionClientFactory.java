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
import org.apache.flink.client.deployment.ClusterRetrieveException;
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
import java.util.Objects;
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
    private Configuration flinkConfiguration;

    private FlinkClientBuilder flinkClientBuilder;
    private static volatile SessionClientFactory sessionClientFactory;

    public SessionClientFactory(FlinkClientBuilder flinkClientBuilder) {

        this.flinkClientBuilder = flinkClientBuilder;


        this.flinkConfig = flinkClientBuilder.getFlinkConfig();
        this.flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
        this.flinkKubeClient = flinkClientBuilder.getFlinkKubeClient();

        this.kubernetesClusterDescriptor = createSessionClusterDescriptor();

        initClusterClientLock();
    }

    private void initClusterClientLock() {
        String zkAddress = flinkConfiguration.getValue(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM);
        if (StringUtils.isBlank(zkAddress)) {
            throw new RdosDefineException("zkAddress is error");
        }

        String sessionNodeName = flinkConfig.getCluster() + ConfigConstrant.SPLIT + flinkConfig.getNamespace();
        String lockPath = String.format("/kubernetes_session/%s", sessionNodeName);

        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkAddress).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(1000)
                .sessionTimeoutMs(1000).build();
        zkClient.start();
        LOG.warn("connector zk success...");

        this.clusterClientLock = new InterProcessMutex(zkClient, lockPath);
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
                    LOG.error("release clusterClientLock error:{}", e);
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
        Configuration flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
        FlinkConfig flinkConfig = flinkClientBuilder.getFlinkConfig();
        String clusterId = flinkConfiguration.get(KubernetesConfigOptions.CLUSTER_ID);
        ClusterDescriptor kubernetesClusterDescriptor = createSessionClusterDescriptor();

        ClusterClient<String> retrieveClusterClient = retrieveClusterClient(clusterId);

        if (Objects.nonNull(retrieveClusterClient)) {
            return retrieveClusterClient;
        }

        ClusterSpecification clusterSpecification = FlinkConfUtil.createClusterSpecification(flinkConfiguration, 0, null);
        if (flinkConfig.getSessionStartAuto()) {
            try {
                ClusterClient<String> clusterClient = kubernetesClusterDescriptor.deploySessionCluster(clusterSpecification).getClusterClient();
                return clusterClient;
            } catch (FlinkException e) {
                LOG.info("Couldn't deploy session on Kubernetes cluster, {}", e);
                throw new RdosDefineException(e);
            }
        }

        throw new RdosDefineException("Get clusterClient error");
    }

    public ClusterClient retrieveClusterClient(String clusterId) {
        try {
            Configuration flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
            ClusterDescriptor kubernetesClusterDescriptor = createSessionClusterDescriptor();
            ClusterClientProvider<String> clusterClientProvider = kubernetesClusterDescriptor.retrieve(clusterId);
            if (Objects.isNull(clusterClientProvider)) {
                return null;
            }
            ClusterClient<String> clusterClient = clusterClientProvider.getClusterClient();
            return clusterClient;
        } catch (Exception e) {
            throw new RdosDefineException(e);
        }
    }

    /**
     * 获取ClusterClient
     */
    public ClusterClient<String> initClusterClient() {
        Configuration newConf = new Configuration(flinkConfiguration);
        if (!flinkConfig.getFlinkHighAvailability()) {
            setNoneHaModeConfig(newConf);
        } else {
            Long currentTime = System.currentTimeMillis();
            String kubernetesClusterId = newConf.getString(KubernetesConfigOptions.CLUSTER_ID);
            String HAClusterId = String.format("%s-%s", kubernetesClusterId, currentTime);

            String HAStoragePath = newConf.get(HighAvailabilityOptions.HA_STORAGE_PATH);
            String newHAStoragePath = String.format("%s/%s", HAStoragePath, HAClusterId);
            newConf.setString(HighAvailabilityOptions.HA_STORAGE_PATH, newHAStoragePath);

            String HAZookeeperRoot = newConf.get(HighAvailabilityOptions.HA_ZOOKEEPER_ROOT);
            String newHAZookeeperRoot = String.format("%s/%s", HAZookeeperRoot, HAClusterId);
            newConf.set(HighAvailabilityOptions.HA_ZOOKEEPER_ROOT, newHAZookeeperRoot);
        }

        KubernetesClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf);
        String clusterId = newConf.get(KubernetesConfigOptions.CLUSTER_ID);

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

    public ClusterDescriptor<String> createSessionClusterDescriptor() {
        Configuration newConf = flinkClientBuilder.getFlinkConfiguration();

        if (!flinkClientBuilder.getFlinkConfig().getFlinkHighAvailability()) {
            setNoneHaModeConfig(newConf);
        } else {
            //由engine管控的session clusterId不进行设置，默认使用appId作为clusterId
            newConf.removeConfig(HighAvailabilityOptions.HA_CLUSTER_ID);
        }


        // plugin dependent on shipfile
        if (StringUtils.isNotBlank(flinkConfig.getPluginLoadMode()) && ConfigConstrant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(flinkConfig.getPluginLoadMode())) {
            newConf.setString(ConfigConstrant.FLINK_PLUGIN_LOAD_MODE, flinkConfig.getPluginLoadMode());
            newConf.setString("classloader.resolve-order", "parent-first");
        }

        KubernetesClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf);
        return clusterDescriptor;
    }

    public static SessionClientFactoryBuilder sessionClientFactoryBuilder() {
        return new SessionClientFactoryBuilder();
    }

    public static class SessionClientFactoryBuilder {

        private FlinkClientBuilder flinkClientBuilder;

        public SessionClientFactoryBuilder withFlinkClientBuilder(FlinkClientBuilder flinkClientBuilder) {
            this.flinkClientBuilder = flinkClientBuilder;
            return this;
        }

        public SessionClientFactory build() {
            if (Objects.isNull(sessionClientFactory)) {
                synchronized (SessionClientFactory.class) {
                    if (Objects.isNull(sessionClientFactory)) {
                        sessionClientFactory = new SessionClientFactory(flinkClientBuilder);
                    }
                }
            }
            return sessionClientFactory;
        }

    }

}
