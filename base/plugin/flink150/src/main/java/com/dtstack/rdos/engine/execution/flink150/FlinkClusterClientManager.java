package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.execution.base.JobIdentifier;
import com.dtstack.rdos.engine.execution.flink150.enums.Deploy;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;

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

    private FlinkClientBuilder flinkClientBuilder;

    private FlinkConfig flinkConfig;

    /**
     * 客户端是否处于可用状态
     */
    private AtomicBoolean isClientOn = new AtomicBoolean(false);

    /**
     * 常驻的yarnSessionClient，engine使用flink 1.8后，可以考虑废弃yarnSessionClient。
     */
    private ClusterClient flinkYarnSessionClient;

    private FlinkYarnSessionStarter flinkYarnSessionStarter;

    private ClusterClientCache perJobClientCache;

    private ExecutorService yarnMonitorES;

    private FlinkClusterClientManager() {
    }

    public static FlinkClusterClientManager createWithInit(FlinkClientBuilder flinkClientBuilder) throws Exception {
        FlinkClusterClientManager manager = new FlinkClusterClientManager();
        manager.flinkClientBuilder = flinkClientBuilder;
        manager.flinkConfig = flinkClientBuilder.getFlinkConfig();
        manager.initYarnSessionClient();
        manager.initPerJobClusterClient();
        return manager;
    }

    public void initYarnSessionClient() throws Exception {
        if (flinkConfig.getClusterMode().equals(Deploy.standalone.name())) {
            flinkYarnSessionClient = flinkClientBuilder.createStandalone();
        } else if (flinkConfig.getClusterMode().equals(Deploy.yarn.name())) {
            if (flinkYarnSessionStarter == null) {
                this.flinkYarnSessionStarter = new FlinkYarnSessionStarter(flinkClientBuilder, flinkConfig);
                this.startYarnSessionClientMonitor();
            }
            flinkYarnSessionStarter.startFlinkYarnSession();
            flinkYarnSessionClient = flinkYarnSessionStarter.getClusterClient();
            this.setIsClientOn(true);
        }
    }

    private void startYarnSessionClientMonitor() throws Exception {
        yarnMonitorES = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("flink_yarn_monitor"));
        //启动守护线程---用于获取当前application状态和更新flink对应的application
        yarnMonitorES.submit(new YarnAppStatusMonitor(this, flinkClientBuilder.getYarnClient(), flinkYarnSessionStarter));
    }

    private void initPerJobClusterClient() throws Exception {
        AbstractYarnClusterDescriptor perJobYarnClusterDescriptor = flinkClientBuilder.createClusterDescriptorByMode(null, null, true);
        perJobClientCache = new ClusterClientCache(perJobYarnClusterDescriptor);
    }

    /**
     * Get YarnSession ClusterClient
     */
    public ClusterClient getClusterClient() {
        return getClusterClient(null);
    }

    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {
        if (jobIdentifier == null || StringUtils.isBlank(jobIdentifier.getApplicationId())) {
            return flinkYarnSessionClient;
        } else {
            return perJobClientCache.getClusterClient(jobIdentifier);
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