package com.dtstack.engine.flink;

import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.http.PoolHttpClient;
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

    private static final Integer CHECK_INTERVAL = 2 * 1000;

    private volatile AtomicBoolean run = new AtomicBoolean(true);

    private static int MAX_RETRY_NUMBER = 2;

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
            try{
                if (clusterClientManager.getIsClientOn()) {
                    if (yarnClient.isInState(Service.STATE.STARTED)) {
                        ApplicationId applicationId = (ApplicationId) clusterClientManager.getClusterClient().getClusterId();
                        ApplicationReport applicationReport  = yarnClient.getApplicationReport(applicationId);
                        YarnApplicationState appState = applicationReport.getYarnApplicationState();
                        switch(appState) {
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

                    if(!isTaskManagerRunning()){
                        LOG.error("TaskManager has no slots, prepare to stop Flink yarn-session client.");
                        clusterClientManager.setIsClientOn(false);
                    }
                }else {
                    retry();
                }
            }catch (Throwable t){
                LOG.error("YarnAppStatusMonitor check error:{}",t);
                clusterClientManager.setIsClientOn(false);
            }finally {
                try{
                    Thread.sleep(CHECK_INTERVAL);
                }catch(Exception e){
                    LOG.error("",e);
                }
            }
        }
    }

    private void retry() {
        //重试
        try {
            if (flinkYarnSessionStarter.getClusterClient() != null) {
                LOG.error("------- Flink yarn-session client shutdown ----");
                flinkYarnSessionStarter.stopFlinkYarnSession();
            }
            LOG.warn("-- retry Flink yarn-session client ----");
            startTime = System.currentTimeMillis();
            this.lastAppState = YarnApplicationState.NEW;
            clusterClientManager.initClusterClient();
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    public void setRun(boolean run){
        this.run = new AtomicBoolean(run);
    }

    private boolean isTaskManagerRunning(){
        String sessionUrl = clusterClientManager.getClusterClient().getWebInterfaceURL();
        try {
            String reqUrl = String.format("%s%s", sessionUrl, FlinkRestParseUtil.SLOTS_INFO);
            PoolHttpClient.get(reqUrl, MAX_RETRY_NUMBER);
            return true;
        } catch (Exception e){
            LOG.error("isTaskManagerRunning error, applicationId={}, ex={}", clusterClientManager.getClusterClient().getClusterId(), ExceptionUtil.getErrorMessage(e));
            return false;
        }
    }

}
