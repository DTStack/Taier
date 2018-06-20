package com.dtstack.rdos.engine.execution.flink150;

import org.apache.flink.runtime.clusterframework.ApplicationStatus;
import org.apache.flink.runtime.concurrent.ScheduledExecutorServiceAdapter;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterClient;
import org.apache.flink.yarn.cli.YarnApplicationStatusMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 用于检测 flink-application 切换的问题
 * Date: 2018/3/26
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class YarnAppStatusMonitor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(YarnAppStatusMonitor.class);

    private static final Integer CHECK_INTERVAL = 20 * 1000;

    private AtomicBoolean run = new AtomicBoolean(true);

    private FlinkClient flinkClient;

    private YarnApplicationStatusMonitor yarnApplicationStatusMonitor;

    public YarnAppStatusMonitor(FlinkClient flinkClient, ScheduledExecutorService executorService) {
        this.flinkClient = flinkClient;
        try {
            YarnClusterClient clusterClient = (YarnClusterClient) flinkClient.getClient();
            Field clusterDescriptorField = clusterClient.getClass().getDeclaredField("clusterDescriptor");
            clusterDescriptorField.setAccessible(true);
            AbstractYarnClusterDescriptor clusterDescriptor = (AbstractYarnClusterDescriptor) clusterDescriptorField.get(clusterClient);

            this.yarnApplicationStatusMonitor = new YarnApplicationStatusMonitor(
                    clusterDescriptor.getYarnClient(),
                    clusterClient.getApplicationId(),
                    new ScheduledExecutorServiceAdapter(executorService));
        } catch (Exception e) {
            LOG.error("-------Flink monitor init error---- {}", e);
        }
    }

    @Override
    public void run() {

        LOG.warn("start flink monitor thread");
        while (run.get()) {
            if (flinkClient.isClientOn()) {
                try {
                    final ApplicationStatus applicationStatus = yarnApplicationStatusMonitor.getApplicationStatusNow();

                    if (!ApplicationStatus.SUCCEEDED.equals(applicationStatus)) {
                        LOG.error("-------Flink session is down----");
                        //限制任务提交---直到恢复
                        flinkClient.setClientOn(false);
                    }

                } catch (Exception e) {
                    LOG.error("-------Flink session is down----");
                    //限制任务提交---直到恢复
                    flinkClient.setClientOn(false);
                }
            }

            if (!flinkClient.isClientOn()) {
                retry();
            }

            try {
                Thread.sleep(CHECK_INTERVAL);
            } catch (InterruptedException e) {
                LOG.error("", e);
            }
        }
    }


    private void retry() {
        //重试
        try {
            LOG.warn("--retry flink client with yarn session----");
            flinkClient.initClient();
        } catch (Exception e) {
            LOG.error("", e);
        }
    }


    public void setRun(boolean run) {
        this.run = new AtomicBoolean(run);
    }
}
