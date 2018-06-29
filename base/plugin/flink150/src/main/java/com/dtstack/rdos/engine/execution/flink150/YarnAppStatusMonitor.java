package com.dtstack.rdos.engine.execution.flink150;

import org.apache.flink.runtime.concurrent.ScheduledExecutorServiceAdapter;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterClient;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 用于检测 flink-application 切换的问题
 * Date: 2018/3/26
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class YarnAppStatusMonitor implements Runnable, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(YarnAppStatusMonitor.class);

    private static final Integer CHECK_INTERVAL = 20 * 1000;

    private FlinkClient flinkClient;

    private YarnClient yarnClient;

    private ApplicationId applicationId;

    private ScheduledFuture<?> applicationStatusUpdateFuture;

    public YarnAppStatusMonitor(FlinkClient flinkClient, ScheduledExecutorService executorService) {
        this.flinkClient = flinkClient;
        try {
            YarnClusterClient clusterClient = (YarnClusterClient) flinkClient.getClient();
            applicationId = clusterClient.getApplicationId();
            Field clusterDescriptorField = clusterClient.getClass().getDeclaredField("clusterDescriptor");
            clusterDescriptorField.setAccessible(true);
            AbstractYarnClusterDescriptor clusterDescriptor = (AbstractYarnClusterDescriptor) clusterDescriptorField.get(clusterClient);
            yarnClient = clusterDescriptor.getYarnClient();
            LOG.warn("start flink monitor thread");
            applicationStatusUpdateFuture = new ScheduledExecutorServiceAdapter(executorService).scheduleWithFixedDelay(
                    this,
                    0L,
                    CHECK_INTERVAL,
                    TimeUnit.MILLISECONDS);


        } catch (Exception e) {
            LOG.error("-------Flink monitor init error---- {}", e);
        }
    }

    @Override
    public void run() {
        if (flinkClient.isClientOn()) {
            if (yarnClient.isInState(Service.STATE.STARTED)) {
                final ApplicationReport applicationReport;

                try {
                    applicationReport = yarnClient.getApplicationReport(applicationId);
                } catch (Exception e) {
                    LOG.error("Could not retrieve the Yarn application report for {}.", applicationId);
                    return;
                }

                YarnApplicationState yarnApplicationState = applicationReport.getYarnApplicationState();

                if (YarnApplicationState.RUNNING != yarnApplicationState) {
                    LOG.error("-------Flink session is down----");
                    //限制任务提交---直到恢复
                    flinkClient.setClientOn(false);
                }
            } else {
                LOG.error("Yarn client is no longer in state STARTED. Stopping the Yarn application status monitor.");
                flinkClient.setClientOn(false);
            }
        }

        if (!flinkClient.isClientOn()) {
            retry();
        }
    }

    @Override
    public void close() {
        applicationStatusUpdateFuture.cancel(false);
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

}
