package com.dtstack.taier.flink.session.check;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.base.filesystem.FilesystemManager;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.flink.config.FlinkConfig;
import com.dtstack.taier.flink.constant.ConfigConstant;
import com.dtstack.taier.flink.session.client.SessionClientManager;
import com.dtstack.taier.flink.util.FlinkUtil;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.http.PoolHttpClient;
import com.google.common.collect.Lists;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.client.ClientUtils;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.client.program.ProgramMissingJobException;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @program: engine-plugins
 * @author: lany
 * @create: 2021/07/11 21:11
 */
public class SessionStatusMonitor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SessionStatusMonitor.class);

    /**
     * 检查时间不需要太频繁，默认10s/次
     */
    private static final Integer CHECK_INTERVAL = 10 * 1000;

    private volatile Integer retryWait = 20 * 1000;

    private AtomicInteger retryNum = new AtomicInteger(0);

    private AtomicBoolean run = new AtomicBoolean(true);

    private SessionClientManager sessionClientDeployer;

    private SessionCheckInfo sessionCheckInfo;

    private YarnApplicationState lastAppState;

    private long startTime = System.currentTimeMillis();

    private FilesystemManager fileSystemManager;

    public SessionStatusMonitor(SessionClientManager sessionClient) {
        this.sessionClientDeployer = sessionClient;
        this.lastAppState = YarnApplicationState.NEW;
        this.sessionCheckInfo = new SessionCheckInfo(sessionClient.getFlinkConfig().getCheckSubmitJobGraphInterval(), sessionClient.getSessionHealthInfo());
        //查找本地路径
        this.fileSystemManager = new FilesystemManager(null, null);
    }

    @Override
    public void run() {
        while (run.get()) {
            try {
                KerberosUtils.login(sessionClientDeployer.getFlinkConfig(), () -> {
                    try {
                        if (sessionCheckInfo.sessionHealthInfo.getSessionState()) {
                            if (sessionClientDeployer.getYarnClient().isInState(Service.STATE.STARTED)) {
                                ApplicationId applicationId = sessionClientDeployer.getClusterClient().getClusterId();
                                ApplicationReport applicationReport = sessionClientDeployer.getYarnClient().getApplicationReport(applicationId);
                                YarnApplicationState appState = applicationReport.getYarnApplicationState();
                                switch (appState) {
                                    case FAILED:
                                    case KILLED:
                                    case FINISHED:
                                        LOG.error("-------Flink yarn-session appState:{}, prepare to stop Flink yarn-session client ----", appState.toString());
                                        sessionCheckInfo.sessionHealthInfo.unHealthy();
                                        break;
                                    case RUNNING:
                                        if (lastAppState != appState) {
                                            // 当 session 重启成功后 重置 retry次数
                                            retryNum.set(0);
                                            LOG.info("YARN application has been deployed successfully. reset retry_num to {} and retry_wait to {}.", retryNum.get(), retryWait);
                                        }
                                        if (sessionClientDeployer.getIsLeader().get() && sessionCheckInfo.doCheck()) {
                                            int checked = 0;
                                            boolean checkRs = checkJobGraphWithStatus();
                                            while (!checkRs) {
                                                if (checked++ >= 3) {
                                                    LOG.error("Health check  failed exceeded 3 times, prepare to stop Flink yarn-session client");
                                                    sessionCheckInfo.sessionHealthInfo.unHealthy();
                                                    break;
                                                } else {
                                                    try {
                                                        Thread.sleep(6L * CHECK_INTERVAL);
                                                    } catch (Exception e) {
                                                        LOG.error("", e);
                                                    }
                                                }
                                                checkRs = checkJobGraphWithStatus();
                                            }
                                            if (checkRs) {
                                                //健康，则重置
                                                sessionCheckInfo.sessionHealthInfo.healthy();
                                            }
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
                                sessionCheckInfo.sessionHealthInfo.unHealthy();
                            }
                        } else {
                            retry();
                            Thread.sleep(retryWait);
                        }
                    } catch (Throwable e) {
                        LOG.error("YarnAppStatusMonitor check error:", e);
                        sessionCheckInfo.sessionHealthInfo.unHealthy();
                    }
                    return null;
                }, sessionClientDeployer.getHadoopConfig().getYarnConfiguration());
            } catch (Throwable t) {
                LOG.error("YarnAppStatusMonitor check error:", t);
            } finally {
                try {
                    Thread.sleep(CHECK_INTERVAL);
                    sessionClientDeployer.flushRole();
                    LOG.info("Current SessionAppName is " + sessionClientDeployer.getSessionAppNameSuffix() + " and Current role is : " + (sessionClientDeployer.getIsLeader().get() ? "Leader" : "Follower"));
                } catch (Exception e) {
                    LOG.error("", e);
                }
            }
        }
    }

    private boolean checkJobGraphWithStatus() {
        boolean checkResult = false;
        try {
            JobExecutionResult executionResult = submitCheckedJobGraph();
            if (null != executionResult) {
                final long startTime = System.currentTimeMillis();
                TaskStatus lastAppState = TaskStatus.SUBMITTING;
                loop:
                while (true) {
                    TaskStatus jobStatus = TaskStatus.SUBMITTING;
                    try {
                        String reqUrl = sessionClientDeployer.getClusterClient().getWebInterfaceURL() + "/jobs/" + executionResult.getJobID().toString();
                        String response = PoolHttpClient.get(reqUrl);
                        if (response != null) {
                            JSONObject statusJson = JSON.parseObject(response);
                            String status = statusJson.getString("state");
                            jobStatus = TaskStatus.getTaskStatus(status.toUpperCase());
                        }
                    } catch (Exception e) {
                        LOG.warn("Fetch session check job status failed from http request. ", e);
                        // exists some miscalculation，should confirm again by cluster client.
                        try {
                            CompletableFuture<JobStatus> status = sessionClientDeployer.getClusterClient().getJobStatus(executionResult.getJobID());
                            JobStatus temp_status = status.get();
                            jobStatus = TaskStatus.getTaskStatus(temp_status.toString());
                        } catch (InterruptedException | ExecutionException ex) {
                            LOG.error("Fetch session check job status failed by cluster client.", ex);
                            jobStatus = TaskStatus.FAILED;
                        }
                    }
                    if (null == jobStatus) {
                        checkResult = false;
                        break;
                    }

                    LOG.info("JobID: {} status: {}", executionResult.getJobID(), jobStatus);
                    switch (jobStatus) {
                        case FAILED:
                            LOG.info("YARN Session Job is failed.");
                            checkResult = false;
                            break loop;
                        case FINISHED:
                            LOG.info("YARN Session Job has been finished successfully.");
                            checkResult = true;
                            break loop;
                        default:
                            if (jobStatus != lastAppState) {
                                LOG.info("Yarn Session Job, current state " + jobStatus);
                            }
                            long cost = System.currentTimeMillis() - startTime;
                            if (cost > 60000 && cost < 300000) {
                                LOG.info("Yarn Session Job took more than 60 seconds.");
                            } else if (cost > 300000) {
                                LOG.info("Yarn Session Job took more than 300 seconds.");
                                // cancel this job.
                                try {
                                    sessionClientDeployer.getClusterClient().cancel(executionResult.getJobID());
                                } catch (Exception e) {
                                    LOG.warn("Cancel Session Job Failed.", e);
                                }
                                checkResult = false;
                                break loop;
                            }

                    }
                    lastAppState = jobStatus;
                    Thread.sleep(3000);
                }
                deleteJobDir(executionResult.getJobID());
            }
        } catch (Exception e) {
            LOG.error("", e);
            checkResult = false;
        }
        return checkResult;
    }

    private void deleteJobDir(JobID jobId) {
        String checkpointsDir =
                sessionClientDeployer.getFlinkConfiguration().getString(
                        CheckpointingOptions.CHECKPOINTS_DIRECTORY);
        String archiveDir =
                sessionClientDeployer.getFlinkConfiguration().getString(
                        JobManagerOptions.ARCHIVE_DIR);
        String jobIdHex = jobId.toHexString();
        String checkpointsPath =
                new org.apache.flink.core.fs.Path(checkpointsDir, jobIdHex).toString();
        deleteHdfsDir(checkpointsPath);
        String archivePath = new org.apache.flink.core.fs.Path(archiveDir, jobIdHex).toString();
        deleteHdfsDir(archivePath);
    }

    private void deleteHdfsDir(String remotePath) {
        YarnConfiguration yarnConf = sessionClientDeployer.getHadoopConfig().getYarnConfiguration();
        try {
            FileSystem fs = FileSystem.get(yarnConf);
            Path appRemotePath = new Path(remotePath);
            if (fs.exists(appRemotePath)) {
                fs.delete(appRemotePath, true);
            }
        } catch (IOException e) {
            LOG.error("", e);
        }
    }

    private synchronized void retry() {

        int tempNum = retryNum.incrementAndGet();

        //if temp_num exceeded max retry num. leader monitor thread will retry every 5 times.
        if (tempNum > sessionClientDeployer.getFlinkConfig().getSessionRetryNum()
                && sessionClientDeployer.getIsLeader().get()
                && (tempNum % 5 != 0)) {
            return;
        }

        //重试
        try {
            LOG.warn("ThreadName : {} retry times is {}", Thread.currentThread().getName(), tempNum);
            LOG.warn("----{} retry Flink yarn-session client ----", tempNum);
            if (sessionClientDeployer.getIsLeader().get() && sessionClientDeployer.getFlinkConfig().getSessionStartAuto()) {
                stopFlinkYarnSession();
            }
            startTime = System.currentTimeMillis();
            this.lastAppState = YarnApplicationState.NEW;
            this.sessionClientDeployer.startAndGetSessionClusterClient();

        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    private void stopFlinkYarnSession() {
        if (sessionClientDeployer.getClusterClient() != null) {
            LOG.info("------- Flink yarn-session client shutdownCluster. ----");
            sessionClientDeployer.getClusterClient().shutDownCluster();
            LOG.info("------- Flink yarn-session client shutdownCluster over. ----");

            try {
                LOG.info("------- Flink yarn-session client shutdown ----");
                sessionClientDeployer.getClusterClient().close();
                LOG.info("------- Flink yarn-session client shutdown over. ----");
            } catch (Exception ex) {
                LOG.error("[SessionClientFactory] Could not properly shutdown cluster client.", ex);
            }
        }

        try {
            Configuration newConf = new Configuration(sessionClientDeployer.getFlinkConfiguration());
            ApplicationId applicationId = sessionClientDeployer.acquireAppIdAndSetClusterId(newConf);
            if (applicationId != null) {
                LOG.info("------- Flink yarn-session application kill. ----");
                sessionClientDeployer.getYarnClient().killApplication(applicationId);
                LOG.info("------- Flink yarn-session application kill over. ----");
            } else {
                LOG.info("------- Flink yarn-session compatible application not exist. ----");
            }
            YarnConfiguration yarnConf = sessionClientDeployer.getHadoopConfig().getYarnConfiguration();
            FileSystem fs = FileSystem.get(yarnConf);
            Path homeDir = fs.getHomeDirectory();
            Path appRemotePath = new Path(String.format("%s/.flink/%s", homeDir, sessionClientDeployer.getClusterId().toString()));
            if (fs.exists(appRemotePath)) {
                fs.delete(appRemotePath, true);
            }

        } catch (Exception ex) {
            LOG.error("[SessionClientFactory] Could not properly shutdown cluster client.", ex);
        }
    }

    public void setRun(boolean run) {
        this.run = new AtomicBoolean(run);
    }

    private JobExecutionResult submitCheckedJobGraph() throws Exception {
        List<URL> classPaths = Lists.newArrayList();
        FlinkConfig flinkConfig = sessionClientDeployer.getFlinkConfig();
        String jarPath = String.format("%s%s/%s", ConfigConstant.USER_DIR, flinkConfig.getSessionCheckJarPath(), ConfigConstant.SESSION_CHECK_JAR_NAME);
        LOG.debug("The session check jar is in : " + jarPath);
        String mainClass = ConfigConstant.SESSION_CHECK_MAIN_CLASS;
        String checkpoint = sessionClientDeployer.getFlinkConfiguration().getString(CheckpointingOptions.CHECKPOINTS_DIRECTORY);
        String[] programArgs = {checkpoint};

        PackagedProgram packagedProgram = FlinkUtil.buildProgram(jarPath, classPaths,
                null, mainClass, programArgs, SavepointRestoreSettings.none(), sessionClientDeployer.getFlinkConfiguration(), fileSystemManager);
        JobGraph jobGraph = PackagedProgramUtils.createJobGraph(packagedProgram, sessionClientDeployer.getFlinkConfiguration(), 1, false);
        JobExecutionResult result = ClientUtils.submitJob(sessionClientDeployer.getClusterClient(), jobGraph, 1, TimeUnit.MINUTES);

        if (null == result) {
            throw new ProgramMissingJobException("No JobSubmissionResult returned, please make sure you called " +
                    "ExecutionEnvironment.execute()");
        }
        LOG.info("Checked Program submitJob finished, Job with JobID:{} .", result.getJobID());
        return result.getJobExecutionResult();
    }


}