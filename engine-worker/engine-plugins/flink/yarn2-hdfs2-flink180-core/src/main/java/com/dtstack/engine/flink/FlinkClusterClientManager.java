package com.dtstack.engine.flink;

import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.flink.enums.ClusterMode;
import com.dtstack.engine.flink.factory.PerJobClientFactory;
import com.dtstack.engine.flink.factory.StandaloneClientFactory;
import com.dtstack.engine.flink.factory.SessionClientFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.util.IOUtils;
import org.apache.flink.util.Preconditions;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/8/27
 */
public class FlinkClusterClientManager {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkClusterClientManager.class);

    private FlinkClientBuilder flinkClientBuilder;

    private FlinkConfig flinkConfig;

    private SessionClientFactory sessionClientFactory;

    private PerJobClientFactory perJobClientFactory;

    /**
     * 常驻的yarnSessionClient，engine使用flink 1.8后，可以考虑废弃yarnSessionClient。
     */
    private volatile ClusterClient clusterClient;

    /**
     * 用于缓存连接perjob对应application的ClusterClient
     */
    private Cache<String, ClusterClient> perJobClientCache = CacheBuilder.newBuilder()
            .removalListener(new ClusterClientRemovalListener())
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    public FlinkClusterClientManager(FlinkClientBuilder flinkClientBuilder) throws Exception {
        LOG.warn("Start init FlinkClusterClientManager");
        this.flinkClientBuilder = flinkClientBuilder;
        this.flinkConfig = flinkClientBuilder.getFlinkConfig();
        this.perJobClientFactory = PerJobClientFactory.createPerJobClientFactory(flinkClientBuilder);
        if (!flinkConfig.getClusterMode().equalsIgnoreCase(ClusterMode.PER_JOB.name())) {
            this.clusterClient = KerberosUtils.login(flinkConfig, () -> this.initClusterClient(), flinkClientBuilder.getYarnConf());
        }
    }

    public ClusterClient initClusterClient() {
        if (flinkConfig.getClusterMode().equalsIgnoreCase(ClusterMode.STANDALONE.name())) {
            return new StandaloneClientFactory(flinkClientBuilder.getFlinkConfiguration(), flinkConfig).getClusterClient();
        } else if (flinkConfig.getClusterMode().equalsIgnoreCase(ClusterMode.SESSION.name())) {
            if (null == sessionClientFactory) {
                try {
                    sessionClientFactory = new SessionClientFactory(this, flinkClientBuilder);
                    LOG.warn("Create FlinkYarnSessionStarter and start YarnSessionClientMonitor");
                } catch (MalformedURLException e) {
                    LOG.error("Create FlinkYarnSessionStarter and start YarnSessionClientMonitor error", e);
                    throw new RdosDefineException(e);
                }
            }
            this.clusterClient = sessionClientFactory.startAndGetSessionClusterClient();
            return clusterClient;
        }
        return null;
    }

    /**
     * Get YarnSession ClusterClient
     */
    public ClusterClient getClusterClient() {
        return getClusterClient(null);
    }

    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {
        if (jobIdentifier == null || StringUtils.isBlank(jobIdentifier.getApplicationId())) {
            if (sessionClientFactory != null && !sessionClientFactory.getSessionHealthCheckedInfo().isRunning()) {
                throw new RdosDefineException("No flink session found on yarn cluster. getClusterClient failed...");
            }
            return clusterClient;
        } else {
            return getPerJobClient(jobIdentifier);
        }
    }

    private ClusterClient getPerJobClient(JobIdentifier jobIdentifier) {

        String applicationId = jobIdentifier.getApplicationId();
        String taskId = jobIdentifier.getTaskId();

        ClusterClient clusterClient = null;
        try {
            clusterClient = KerberosUtils.login(flinkConfig, () -> {
                try {
                    return perJobClientCache.get(applicationId, () -> {
                        JobClient jobClient = new JobClient();
                        jobClient.setTaskId(taskId);
                        jobClient.setJobName("taskId-" + taskId);
                        try (
                                AbstractYarnClusterDescriptor perJobYarnClusterDescriptor = perJobClientFactory.createPerJobClusterDescriptor(jobClient);
                        ) {
                            return perJobYarnClusterDescriptor.retrieve(ConverterUtils.toApplicationId(applicationId));
                        }
                    });
                } catch (ExecutionException e) {
                    throw new RdosDefineException(e);
                }
            }, flinkClientBuilder.getYarnConf());
        } catch (Exception e) {
            LOG.error("job[{}] get perJobClient exception:{}", taskId, e.getMessage());
        }

        Preconditions.checkNotNull(clusterClient, "Get perJobClient is null!");
        return clusterClient;
    }

    public void addClient(String applicationId, ClusterClient<ApplicationId> clusterClient) {
        perJobClientCache.put(applicationId, clusterClient);
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
                        notification.getValue().shutdown();
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