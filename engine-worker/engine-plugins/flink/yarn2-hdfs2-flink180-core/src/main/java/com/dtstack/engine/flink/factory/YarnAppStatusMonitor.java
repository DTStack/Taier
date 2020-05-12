package com.dtstack.engine.flink.factory;

import com.dtstack.engine.flink.FlinkClientBuilder;
import com.dtstack.engine.flink.FlinkClusterClientManager;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
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

public class YarnAppStatusMonitor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(YarnAppStatusMonitor.class);

    /**
     * 检查时间不需要太频繁，默认10s/次
     */
    private static final Integer CHECK_INTERVAL = 10 * 1000;

    private static final Integer RETRY_WAIT = 10 * 1000;

    private AtomicBoolean run = new AtomicBoolean(true);

    private FlinkClusterClientManager clusterClientManager;

    private FlinkClientBuilder clientBuilder;

    private YarnSessionClientFactory yarnSessionClientFactory;

    private YarnApplicationState lastAppState;

    private String attemptId;

    private long startTime = System.currentTimeMillis();

    public YarnAppStatusMonitor(FlinkClusterClientManager clusterClientManager, FlinkClientBuilder clientBuilder, YarnSessionClientFactory yarnSessionClientFactory) {
        this.clusterClientManager = clusterClientManager;
        this.clientBuilder = clientBuilder;
        this.yarnSessionClientFactory = yarnSessionClientFactory;
        this.lastAppState = YarnApplicationState.NEW;
    }

    @Override
    public void run() {
        while (run.get()) {
            try {
                if (clusterClientManager.getIsClientOn()) {
                    if (clientBuilder.getYarnClient().isInState(Service.STATE.STARTED)) {
                        ApplicationId applicationId = (ApplicationId) clusterClientManager.getClusterClient().getClusterId();
                        ApplicationReport applicationReport = clientBuilder.getYarnClient().getApplicationReport(applicationId);
                        YarnApplicationState appState = applicationReport.getYarnApplicationState();
                        switch (appState) {
                            case FAILED:
                            case KILLED:
                            case FINISHED:
                                LOG.error("-------Flink yarn-session appState:{}, prepare to stop Flink yarn-session client ----", appState.toString());
                                clusterClientManager.setIsClientOn(false);
                                break;
                            case RUNNING:
                                if (lastAppState != appState) {
                                    LOG.info("YARN application has been deployed successfully.");
                                }
                                if (isDifferentAttemptId(applicationReport)) {
                                    LOG.error("AttemptId has changed, prepare to stop Flink yarn-session client.");
                                    clusterClientManager.setIsClientOn(false);
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
                        LOG.error("Yarn client is no longer in state STARTED, prepare to stop Flink yarn-session client.");
                        clusterClientManager.setIsClientOn(false);
                    }
                } else {
                    //retry时有一段等待时间，确保session正常运行。
                    retry();
                }
            } catch (Throwable t) {
                LOG.error("YarnAppStatusMonitor check error:{}", t);
                clusterClientManager.setIsClientOn(false);
            } finally {
                try {
                    Thread.sleep(CHECK_INTERVAL);
                } catch (Exception e) {
                    LOG.error("", e);
                }
            }
        }
    }

    private void retry() {
        //重试
        try {
            if (yarnSessionClientFactory.getClusterClient() != null) {
                LOG.error("------- Flink yarn-session client shutdown ----");
                yarnSessionClientFactory.stopFlinkYarnSession();
            }
            LOG.warn("-- retry Flink yarn-session client ----");
            startTime = System.currentTimeMillis();
            this.lastAppState = YarnApplicationState.NEW;
            clusterClientManager.initClusterClient();

            Thread.sleep(RETRY_WAIT);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    public void setRun(boolean run) {
        this.run = new AtomicBoolean(run);
    }

    private boolean isDifferentAttemptId(ApplicationReport applicationReport) {
        String appId = applicationReport.getCurrentApplicationAttemptId().getApplicationId().toString();
        String attemptIdStr = String.valueOf(applicationReport.getCurrentApplicationAttemptId().getAttemptId());
        String currentAttemptId = appId + attemptIdStr;
        if (attemptId == null) {
            attemptId = currentAttemptId;
            return false;
        }
        if (!attemptId.equals(currentAttemptId)) {
            attemptId = currentAttemptId;
            return true;
        }
        return false;
    }

}