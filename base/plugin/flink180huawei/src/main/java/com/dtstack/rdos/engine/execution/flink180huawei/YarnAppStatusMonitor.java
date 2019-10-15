package com.dtstack.rdos.engine.execution.flink180huawei;

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

    private FlinkClusterClientManager clusterClientManager;

    private YarnClient yarnClient;

    private FlinkYarnSessionStarter flinkYarnSessionStarter;

    private YarnApplicationState lastAppState;

    private long startTime = System.currentTimeMillis();

    public YarnAppStatusMonitor(FlinkClusterClientManager clusterClientManager, YarnClient yarnClient, FlinkYarnSessionStarter flinkYarnSessionStarter) {
        this.clusterClientManager = clusterClientManager;
        this.yarnClient = yarnClient;
        this.flinkYarnSessionStarter = flinkYarnSessionStarter;
        this.lastAppState = YarnApplicationState.NEW;
    }

    @Override
    public void run() {
        while (run.get()) {
            if (clusterClientManager.getIsClientOn()) {
                if (yarnClient.isInState(Service.STATE.STARTED)) {

                    ApplicationId applicationId = (ApplicationId) clusterClientManager.getClusterClient().getClusterId();

                    final ApplicationReport applicationReport;

                    try {
                        applicationReport = yarnClient.getApplicationReport(applicationId);
                    } catch (Exception e) {
                        LOG.error("Could not retrieve the Yarn application report for {}.", applicationId);
                        return;
                    }

                    YarnApplicationState appState = applicationReport.getYarnApplicationState();

                    switch(appState) {
                        case FAILED:
                        case KILLED:
                        case FINISHED:
                            flinkYarnSessionStarter.stopFlinkYarnSession();
                            LOG.error("-------Flink session is down----");
                            //限制任务提交---直到恢复
                            clusterClientManager.setIsClientOn(false);
                            break;
                        case RUNNING:
                            if (lastAppState != appState) {
                                LOG.info("YARN application has been deployed successfully.");
                            }
                            break;
                        default:
                            if (appState != lastAppState) {
                                LOG.info("Deploying cluster, current state " + appState);
                            }
                            if (System.currentTimeMillis() - startTime > 60000) {
                                LOG.info("Deployment took more than 60 seconds. Please check if the requested resources are available in the YARN cluster");
                            }
                    }
                    lastAppState = appState;
                } else {
                    LOG.error("Yarn client is no longer in state STARTED. Stopping the Yarn application status monitor.");
                    clusterClientManager.setIsClientOn(false);
                }
            }

            if (!clusterClientManager.getIsClientOn()) {
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
            startTime = System.currentTimeMillis();
            this.lastAppState = YarnApplicationState.NEW;
            clusterClientManager.initYarnSessionClient();
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    public void setRun(boolean run){
        this.run = new AtomicBoolean(run);
    }

}
