package com.dtstack.rdos.engine.execution.flink150.util;

import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.execution.base.JobIdentifier;
import com.dtstack.rdos.engine.execution.flink150.ClusterClientCache;
import com.dtstack.rdos.engine.execution.flink150.FlinkClientBuilder;
import com.dtstack.rdos.engine.execution.flink150.FlinkConfig;
import com.dtstack.rdos.engine.execution.flink150.FlinkYarnSessionStarter;
import com.dtstack.rdos.engine.execution.flink150.YarnAppStatusMonitor;
import com.dtstack.rdos.engine.execution.flink150.enums.Deploy;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;

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


    public static FlinkClusterClientManager createWhithInit(FlinkClientBuilder flinkClientBuilder) throws Exception {
        FlinkClusterClientManager manager = new FlinkClusterClientManager(flinkClientBuilder);
        manager.initClient();
        return manager;
    }

    public FlinkClusterClientManager(FlinkClientBuilder flinkClientBuilder) throws Exception {
        this.flinkClientBuilder = flinkClientBuilder;
        this.flinkConfig = flinkClientBuilder.getFlinkConfig();

        if (flinkClientBuilder.getYarnClient() != null) {
            Configuration flinkConfig = new Configuration(flinkClientBuilder.getFlinkConfiguration());
            AbstractYarnClusterDescriptor perJobYarnClusterDescriptor = flinkClientBuilder.getClusterDescriptor(flinkConfig, yarnConf, ".", true);
            perJobClientCache = new ClusterClientCache(perJobYarnClusterDescriptor);
            yarnMonitorES = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(), new CustomThreadFactory("flink_yarn_monitor"));
            //启动守护线程---用于获取当前application状态和更新flink对应的application
            yarnMonitorES.submit(new YarnAppStatusMonitor(this, flinkClientBuilder.getYarnClient(), flinkYarnSessionStarter));
        }
    }

    private void initClient() throws Exception {
        if (flinkConfig.getClusterMode().equals(Deploy.standalone.name())) {
            flinkYarnSessionClient = flinkClientBuilder.createStandalone(flinkConfig);
        } else if (flinkConfig.getClusterMode().equals(Deploy.yarn.name())) {
            if (flinkYarnSessionStarter == null) {
                this.flinkYarnSessionStarter = new FlinkYarnSessionStarter(flinkClientBuilder, flinkConfig, prometheusGatewayConfig);
            }
            flinkYarnSessionStarter.startFlinkYarnSession();
            flinkYarnSessionClient = flinkYarnSessionStarter.getClusterClient();
        }
        setClientOn(true);
    }

    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {
        if (StringUtils.isBlank(jobIdentifier.getApplicationId())) {
            return flinkYarnSessionClient;
        } else {
            return perJobClientCache.getClusterClient(jobIdentifier);
        }
    }

}