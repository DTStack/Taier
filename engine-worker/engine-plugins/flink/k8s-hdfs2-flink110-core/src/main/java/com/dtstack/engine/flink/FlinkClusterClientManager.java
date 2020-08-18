package com.dtstack.engine.flink;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.flink.factory.PerJobClientFactory;
import com.dtstack.engine.flink.factory.SessionClientFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.client.program.ClusterClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/04/03
 */
public class FlinkClusterClientManager {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkClusterClientManager.class);

    private FlinkClientBuilder flinkClientBuilder;

    /**
     * 客户端是否处于可用状态
     */
    private AtomicBoolean isClientOn = new AtomicBoolean(false);

    private ClusterClient<String> clusterClient;

    private SessionClientFactory sessionClientFactory;

    private PerJobClientFactory perJobClientFactory;

    /**
     * 用于缓存连接perjob对应application的ClusterClient
     */
    private Cache<String, ClusterClient<String>> perJobClientCache = CacheBuilder.newBuilder()
            .removalListener(new ClusterClientRemovalListener())
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    private FlinkClusterClientManager() {
    }

    public static FlinkClusterClientManager createWithInit(FlinkClientBuilder flinkClientBuilder) throws Exception {
        LOG.warn("Start init FlinkClusterClientManager");
        FlinkClusterClientManager manager = new FlinkClusterClientManager();
        manager.flinkClientBuilder = flinkClientBuilder;
        manager.initClusterClient();
        return manager;
    }

    public void initClusterClient() throws Exception {
        if (sessionClientFactory == null) {
            this.sessionClientFactory = new SessionClientFactory(flinkClientBuilder);
            LOG.warn("create FlinkSessionStarter.");
        }
        boolean clientOn = sessionClientFactory.startFlinkSession();
        this.setIsClientOn(clientOn);
        clusterClient = sessionClientFactory.getClusterClient();
    }


    /**
     * Get KubernetesSession ClusterClient
     */
    public ClusterClient getClusterClient() {
        return getClusterClient(null);
    }

    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {
        if (jobIdentifier == null || StringUtils.isBlank(jobIdentifier.getApplicationId()) || jobIdentifier.getApplicationId().contains("flinksession")) {
            if (!isClientOn.get()) {
                throw new RdosDefineException("No flink session found cluster on Kubernetes. getClusterClient failed...");
            }
            return getSessionJobClient();
        } else {
            return getPerJobClient(jobIdentifier);
        }
    }

    private ClusterClient getSessionJobClient() {

        return null;
    }

    private ClusterClient getPerJobClient(JobIdentifier jobIdentifier) {

        String clusterId = jobIdentifier.getApplicationId();
        String taskId = jobIdentifier.getTaskId();

        ClusterClient clusterClient;
        try {
            clusterClient = perJobClientCache.get(clusterId, () -> {
                JobClient jobClient = new JobClient();
                jobClient.setTaskId(taskId);

                PerJobClientFactory perJobClientFactory = PerJobClientFactory.getPerJobClientFactory();
                return perJobClientFactory.retrieveClusterClient(clusterId);
            });

        } catch (Exception e) {
            throw new RuntimeException("get cluster on Kubernetes client exception:", e);
        }

        return clusterClient;
    }

    public void addClient(String applicationId, ClusterClient<String> clusterClient) {
        perJobClientCache.put(applicationId, clusterClient);
    }

    public boolean getIsClientOn() {
        return isClientOn.get();
    }

    public void setIsClientOn(boolean isClientOn) {
        this.isClientOn.set(isClientOn);
    }

    /**
     * 创建一个监听器，在缓存被移除的时候，得到这个通知
     */
    private class ClusterClientRemovalListener implements RemovalListener<String, ClusterClient> {

        @Override
        public void onRemoval(RemovalNotification<String, ClusterClient> notification) {
            LOG.info("key={},value={},reason={}", notification.getKey(), notification.getValue(), notification.getCause());
            if (notification.getValue() != null) {
                try {
                    if (notification.getValue() != FlinkClusterClientManager.this.clusterClient) {
                        notification.getValue().shutDownCluster();
                    }
                } catch (Exception ex) {
                    LOG.info("[ClusterClientCache] Could not properly shutdown cluster client.", ex);
                }
            }
        }
    }

    public SessionClientFactory getSessionClientFactory() {
        return sessionClientFactory;
    }

    public PerJobClientFactory getPerJobClientFactory() {
        return perJobClientFactory;
    }
}