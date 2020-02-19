package com.dtstack.engine.flink;

import com.dtstack.engine.common.exception.RdosDefineException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/5/30
 */
public class FlinkYarnSessionStarter {

    private static final Logger logger = LoggerFactory.getLogger(FlinkYarnSessionStarter.class);

    private String lockPath = null;
    private CuratorFramework zkClient;

    private AbstractYarnClusterDescriptor yarnSessionDescriptor;
    private ClusterSpecification yarnSessionSpecification;
    private ClusterClient<ApplicationId> clusterClient;
    private FlinkClientBuilder flinkClientBuilder;
    private FlinkConfig flinkConfig;
    private InterProcessMutex clusterClientLock;

    public FlinkYarnSessionStarter(FlinkClientBuilder flinkClientBuilder, FlinkConfig flinkConfig) throws MalformedURLException {
        this.flinkClientBuilder = flinkClientBuilder;
        this.flinkConfig = flinkConfig;

        String clusterId = flinkConfig.getCluster() + ConfigConstrant.SPLIT + flinkConfig.getQueue();

        this.yarnSessionDescriptor = flinkClientBuilder.createClusterDescriptorByMode(null, false);
        this.yarnSessionDescriptor.setName(flinkConfig.getFlinkSessionName() + ConfigConstrant.SPLIT + clusterId);
        this.yarnSessionSpecification = FLinkConfUtil.createYarnSessionSpecification(flinkClientBuilder.getFlinkConfiguration());

        initZk();
        this.clusterClientLock = new InterProcessMutex(zkClient, lockPath);
    }

    public boolean startFlinkYarnSession() {
        try {
            if (this.clusterClientLock.acquire(5, TimeUnit.MINUTES)){
                ClusterClient<ApplicationId> retrieveClusterClient = null;
                try {
                    retrieveClusterClient = flinkClientBuilder.initYarnClusterClient();
                } catch (Exception e) {
                    logger.error("{}", e);
                }

                if (retrieveClusterClient != null) {
                    clusterClient = retrieveClusterClient;
                    logger.info("retrieve flink client with yarn session success");
                    return true;
                }

                if (flinkConfig.getYarnSessionStartAuto()) {
                    try {
                        clusterClient = yarnSessionDescriptor.deploySessionCluster(yarnSessionSpecification);
                        clusterClient.setDetached(true);
                        return true;
                    } catch (FlinkException e) {
                        logger.info("Couldn't deploy Yarn session cluster, {}", e);
                        throw e;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Couldn't deploy Yarn session cluster:{}",e);
        } finally {
            if (this.clusterClientLock.isAcquiredInThisProcess()) {
                try {
                    this.clusterClientLock.release();
                } catch (Exception e) {
                    logger.error("lockPath:{} release clusterClientLock error:{}", lockPath, e);
                }
            }
        }

        return false;
    }

    public void stopFlinkYarnSession() {
        try {
            clusterClient.shutdown();
        } catch (Exception ex) {
            logger.info("[FlinkYarnSessionStarter] Could not properly shutdown cluster client.", ex);
        }
    }

    public ClusterClient<ApplicationId> getClusterClient() {
        return clusterClient;
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
        logger.warn("connector zk success...");
    }

}
