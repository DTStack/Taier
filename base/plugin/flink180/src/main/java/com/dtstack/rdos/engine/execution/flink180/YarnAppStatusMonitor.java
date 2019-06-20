package com.dtstack.rdos.engine.execution.flink180;

import org.apache.hadoop.service.Service;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 用于检测 flink-application 切换的问题
 * Date: 2018/3/26
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class YarnAppStatusMonitor implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(YarnAppStatusMonitor.class);

    private static final Integer CHECK_INTERVAL = 20 * 1000;

    private AtomicBoolean run = new AtomicBoolean(true);

    private FlinkClient flinkClient;

    private YarnClient yarnClient;

    private ApplicationId applicationId;

    public YarnAppStatusMonitor(FlinkClient flinkClient, YarnClient yarnClient) {
        this.flinkClient = flinkClient;
        this.yarnClient = yarnClient;
    }

    @Override
    public void run() {
        while (run.get()) {
            if (flinkClient.isClientOn()) {
                if (yarnClient.isInState(Service.STATE.STARTED)) {

                    applicationId = (ApplicationId) flinkClient.getFlinkClient().getClusterId();

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

    public void setRun(boolean run){
        this.run = new AtomicBoolean(run);
    }

}
