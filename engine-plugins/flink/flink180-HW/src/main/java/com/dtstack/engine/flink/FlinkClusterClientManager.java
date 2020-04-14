package com.dtstack.engine.flink;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.flink.enums.Deploy;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/8/27
 */
public class FlinkClusterClientManager {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkClusterClientManager.class);

    private FlinkClientBuilder flinkClientBuilder;

    private FlinkConfig flinkConfig;

    /**
     * 客户端是否处于可用状态
     */
    private AtomicBoolean isClientOn = new AtomicBoolean(false);

    /**
     * 常驻的yarnSessionClient，engine使用flink 1.8后，可以考虑废弃yarnSessionClient。
     */
    private ClusterClient clusterClient;

    private FlinkYarnSessionStarter flinkYarnSessionStarter;

    /**
     * 用于缓存连接perjob对应application的ClusterClient
     */
    private Cache<String, ClusterClient> perJobClientCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();

    private ExecutorService yarnMonitorES;

    private FlinkClusterClientManager() {
    }

    public static FlinkClusterClientManager createWithInit(FlinkClientBuilder flinkClientBuilder) throws Exception {
        LOG.warn("Start init FlinkClusterClientManager");
        FlinkClusterClientManager manager = new FlinkClusterClientManager();
        manager.flinkClientBuilder = flinkClientBuilder;
        manager.flinkConfig = flinkClientBuilder.getFlinkConfig();
        manager.initClusterClient();
        return manager;
    }

    public void initClusterClient() throws Exception {
        if (flinkConfig.getClusterMode().equals(Deploy.standalone.name())) {
            clusterClient = flinkClientBuilder.createStandalone();
        } else if (flinkConfig.getClusterMode().equals(Deploy.yarn.name())) {
            if (flinkYarnSessionStarter == null) {
                this.flinkYarnSessionStarter = new FlinkYarnSessionStarter(flinkClientBuilder);
                LOG.warn("Create FlinkYarnSessionStarter and start YarnSessionClientMonitor");
                this.startYarnSessionClientMonitor();
            }
            boolean clientOn = flinkYarnSessionStarter.startFlinkYarnSession();
            this.setIsClientOn(clientOn);
            clusterClient = flinkYarnSessionStarter.getClusterClient();
        }
    }

    private void startYarnSessionClientMonitor() throws Exception {
        yarnMonitorES = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("flink_yarn_monitor"));
        //启动守护线程---用于获取当前application状态和更新flink对应的application
        yarnMonitorES.submit(new YarnAppStatusMonitor(this, flinkClientBuilder, flinkYarnSessionStarter));
    }

    private ClusterClient getPerJobClient(JobIdentifier jobIdentifier){

        String applicationId = jobIdentifier.getApplicationId();
        String taskId = jobIdentifier.getTaskId();

        ClusterClient clusterClient;
        try {
            clusterClient = perJobClientCache.get(applicationId, () -> {
                JobClient jobClient = new JobClient();
                jobClient.setTaskId(taskId);
                AbstractYarnClusterDescriptor perJobYarnClusterDescriptor = flinkClientBuilder.createClusterDescriptorByMode(jobClient, true);
                return perJobYarnClusterDescriptor.retrieve(ConverterUtils.toApplicationId(applicationId));
            });

        } catch (ExecutionException e) {
            throw new RuntimeException("get yarn cluster client exception:", e);
        }

        return clusterClient;
    }

    /**
     * Get YarnSession ClusterClient
     */
    public ClusterClient getClusterClient() {
        return getClusterClient(null);
    }

    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {
        if (jobIdentifier == null || StringUtils.isBlank(jobIdentifier.getApplicationId())) {
            if (!isClientOn.get()) {
                throw new RdosDefineException("No flink session found on yarn cluster. getClusterClient failed...");
            }
            return clusterClient;
        } else {
            return getPerJobClient(jobIdentifier);
        }
    }

    public void addClient(String applicationId, ClusterClient<ApplicationId> clusterClient) {
        perJobClientCache.put(applicationId, clusterClient);
    }

    public boolean getIsClientOn() {
        return isClientOn.get();
    }

    public void setIsClientOn(boolean isClientOn) {
        this.isClientOn.set(isClientOn);
    }
}