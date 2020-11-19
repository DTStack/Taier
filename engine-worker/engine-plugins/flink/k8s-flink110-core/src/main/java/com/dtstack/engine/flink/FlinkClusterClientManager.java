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
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
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
    private AtomicBoolean isClientOn = new AtomicBoolean(true);

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
        manager.perJobClientFactory = PerJobClientFactory.createPerJobClientFactory(flinkClientBuilder);
        manager.sessionClientFactory = SessionClientFactory.createSessionClientFactory(flinkClientBuilder);
        return manager;
    }

    /**
     * Get KubernetesSession ClusterClient
     */
    public ClusterClient getClusterClient() {
        return getClusterClient(null);
    }

    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {
        String applicationId = jobIdentifier == null? null : jobIdentifier.getApplicationId();
        Boolean isSession = StringUtils.isBlank(applicationId) || applicationId.contains("flinksession");
        if (jobIdentifier == null || isSession) {
            if (!isClientOn.get()) {
                throw new RdosDefineException("No flink session found cluster on Kubernetes. getClusterClient failed...");
            }
            return getSessionJobClient();
        } else {
            return getPerJobClient(jobIdentifier);
        }
    }

    private ClusterClient getSessionJobClient() {

        String sessionClusterId = sessionClientFactory.getSessionClusterId();

        ClusterClient clusterClient = null;
        try {
            clusterClient = sessionClientFactory.retrieveClusterClient(sessionClusterId, null);
        } catch (Exception e) {
            LOG.warn("Can not retrieve ClusterClient!");
        }

        if (null != clusterClient) {
            return clusterClient;
        }

        FlinkConfig flinkConfig = flinkClientBuilder.getFlinkConfig();
        if (flinkConfig.getSessionStartAuto()) {
            try {
                clusterClient = sessionClientFactory.getClusterClient(null);
            } catch (Exception e) {
                LOG.error("Create ClusterClient error! e:{}", e.getMessage());
            }
        }
        Preconditions.checkNotNull(clusterClient, "clusterClient is null");
        return clusterClient;
    }

    private ClusterClient getPerJobClient(JobIdentifier jobIdentifier) {

        String clusterId = jobIdentifier.getApplicationId();
        String taskId = jobIdentifier.getTaskId();

        try {
            ClusterClient clusterClient = perJobClientCache.get(clusterId, () -> {
                JobClient jobClient = new JobClient();
                jobClient.setTaskId(taskId);
                return perJobClientFactory.retrieveClusterClient(clusterId, jobClient);
            });
            return clusterClient;
        } catch (Exception e) {
            throw new RuntimeException("get cluster on Kubernetes client exception:", e);
        }
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
                    notification.getValue().close();
                } catch (Exception ex) {
                    LOG.warn("[ClusterClientCache] Could not properly shutdown cluster client.", ex);
                }
            }
        }
    }

    public SessionClientFactory getSessionClientFactory() {
        Preconditions.checkNotNull(sessionClientFactory, "sessionClientFactory is null");
        return sessionClientFactory;
    }

    public PerJobClientFactory getPerJobClientFactory() {
        Preconditions.checkNotNull(perJobClientFactory, "perJobClientFactory is null");
        return perJobClientFactory;
    }
}