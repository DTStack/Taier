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

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.FlinkClientBuilder;
import com.dtstack.engine.flink.FlinkClusterClientManager;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.util.FLinkConfUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.shaded.curator.org.apache.curator.framework.CuratorFramework;
import org.apache.flink.shaded.curator.org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.flink.shaded.curator.org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.flink.shaded.curator.org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.flink.util.FlinkException;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Date: 2020/5/12
 * Company: www.dtstack.com
 * @author maqi
 */
public class YarnSessionClientFactory implements IClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(YarnSessionClientFactory.class);

    private AbstractYarnClusterDescriptor yarnSessionDescriptor;
    private ClusterSpecification yarnSessionSpecification;
    private ClusterClient<ApplicationId> clusterClient;
    private FlinkClientBuilder flinkClientBuilder;
    private FlinkConfig flinkConfig;
    private InterProcessMutex clusterClientLock;
    private String lockPath = null;
    private CuratorFramework zkClient;

    private boolean isDetached = true;
    private FlinkClusterClientManager flinkClusterClientManager;
    private ExecutorService yarnMonitorES;

    public YarnSessionClientFactory(FlinkClusterClientManager flinkClusterClientManager, FlinkClientBuilder flinkClientBuilder) throws MalformedURLException {
        this.flinkClientBuilder = flinkClientBuilder;
        this.flinkConfig = flinkClientBuilder.getFlinkConfig();
        this.flinkClusterClientManager = flinkClusterClientManager;

        String clusterId = flinkConfig.getCluster() + ConfigConstrant.SPLIT + flinkConfig.getQueue();

        this.yarnSessionDescriptor = flinkClientBuilder.createYarnSessionClusterDescriptor();
        this.yarnSessionDescriptor.setName(flinkConfig.getFlinkSessionName() + ConfigConstrant.SPLIT + clusterId);
        this.yarnSessionSpecification = FLinkConfUtil.createYarnSessionSpecification(flinkClientBuilder.getFlinkConfiguration());

        initZk();

        this.clusterClientLock = new InterProcessMutex(zkClient, lockPath);

        startYarnSessionClientMonitor();
    }


    private void startYarnSessionClientMonitor() {
        yarnMonitorES = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("flink_yarn_monitor"));

        //启动守护线程---用于获取当前application状态和更新flink对应的application
        yarnMonitorES.submit(new YarnAppStatusMonitor(flinkClusterClientManager, flinkClientBuilder, this));
    }

    private void initZk() {
        Configuration flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
        String zkAddress = flinkConfiguration.getValue(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM);
        if (StringUtils.isBlank(zkAddress)) {
            throw new RdosDefineException("zkAddress is error");
        }
        lockPath = String.format("/yarn_session/%s", flinkConfig.getCluster() + ConfigConstrant.SPLIT + flinkConfig.getQueue());

        this.zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkAddress).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(1000)
                .sessionTimeoutMs(1000).build();
        this.zkClient.start();
        LOG.warn("connector zk success...");
    }

    public boolean startFlinkYarnSession() {
        try {
            if (this.clusterClientLock.acquire(5, TimeUnit.MINUTES)) {
                ClusterClient<ApplicationId> retrieveClusterClient = null;
                try {
                    retrieveClusterClient = initYarnClusterClient();
                } catch (Exception e) {
                    LOG.error("{}", e);
                }

                if (retrieveClusterClient != null) {
                    clusterClient = retrieveClusterClient;
                    LOG.info("retrieve flink client with yarn session success");
                    return true;
                }

                if (flinkConfig.getYarnSessionStartAuto()) {
                    try {
                        clusterClient = yarnSessionDescriptor.deploySessionCluster(yarnSessionSpecification);
                        clusterClient.setDetached(true);
                        return true;
                    } catch (FlinkException e) {
                        LOG.info("Couldn't deploy Yarn session cluster, {}", e);
                        throw e;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Couldn't deploy Yarn session cluster:{}", e);
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
     * 根据yarn方式获取ClusterClient
     */
    public ClusterClient<ApplicationId> initYarnClusterClient() {
        Configuration newConf = new Configuration(flinkClientBuilder.getFlinkConfiguration());
        ApplicationId applicationId = acquireAppIdAndSetClusterId(newConf);

        if (!flinkConfig.getFlinkHighAvailability()) {
            flinkClientBuilder.setNoneHaModeConfig(newConf);
        }
        AbstractYarnClusterDescriptor clusterDescriptor = flinkClientBuilder.getClusterDescriptor(newConf, flinkClientBuilder.getYarnConf(), ".");

        ClusterClient<ApplicationId> clusterClient = null;
        try {
            clusterClient = clusterDescriptor.retrieve(applicationId);
        } catch (Exception e) {
            LOG.info("No flink session, Couldn't retrieve Yarn cluster.", e);
            throw new RdosDefineException("No flink session, Couldn't retrieve Yarn cluster.");
        }

        clusterClient.setDetached(isDetached);
        LOG.warn("---init flink client with yarn session success----");

        return clusterClient;
    }

    private ApplicationId acquireAppIdAndSetClusterId(Configuration configuration) {
        try {
            Set<String> set = new HashSet<>();
            set.add("Apache Flink");
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.RUNNING);
            enumSet.add(YarnApplicationState.ACCEPTED);
            List<ApplicationReport> reportList = flinkClientBuilder.getYarnClient().getApplications(set, enumSet);

            int maxMemory = -1;
            int maxCores = -1;
            ApplicationId applicationId = null;


            for (ApplicationReport report : reportList) {
                if (!report.getName().startsWith(flinkConfig.getFlinkSessionName())) {
                    continue;
                }

                if (!report.getYarnApplicationState().equals(YarnApplicationState.RUNNING)) {
                    continue;
                }

                if (!report.getQueue().endsWith(flinkConfig.getQueue())) {
                    continue;
                }

                int thisMemory = report.getApplicationResourceUsageReport().getNeededResources().getMemory();
                int thisCores = report.getApplicationResourceUsageReport().getNeededResources().getVirtualCores();
                if (thisMemory > maxMemory || thisMemory == maxMemory && thisCores > maxCores) {
                    maxMemory = thisMemory;
                    maxCores = thisCores;
                    applicationId = report.getApplicationId();
                    String clusterId = flinkConfig.getCluster() + ConfigConstrant.SPLIT + flinkConfig.getQueue();
                    //flinkClusterId不为空 且 yarnsession不是由engine来管控时，需要设置clusterId（兼容手动启动yarnsession的情况）
                    if (StringUtils.isBlank(configuration.getValue(HighAvailabilityOptions.HA_CLUSTER_ID)) || report.getName().endsWith(clusterId)) {
                        configuration.setString(HighAvailabilityOptions.HA_CLUSTER_ID, applicationId.toString());
                    }
                }

            }

            if (applicationId == null) {
                throw new RdosDefineException("No flink session found on yarn cluster.");
            }
            return applicationId;
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }
    }


    public void stopFlinkYarnSession() {
        try {
            clusterClient.shutdown();
        } catch (Exception ex) {
            LOG.info("[YarnSessionClientFactory] Could not properly shutdown cluster client.", ex);
        }
    }

    @Override
    public ClusterClient<ApplicationId> getClusterClient() {
        return clusterClient;
    }
}
