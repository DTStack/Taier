/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taiga.flink.factory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.base.filesystem.FilesystemManager;
import com.dtstack.taiga.base.util.KerberosUtils;
import com.dtstack.taiga.flink.FlinkClientBuilder;
import com.dtstack.taiga.flink.FlinkConfig;
import com.dtstack.taiga.flink.constrant.ConfigConstrant;
import com.dtstack.taiga.flink.constrant.ErrorMessageConsts;
import com.dtstack.taiga.flink.entity.SessionCheckInterval;
import com.dtstack.taiga.flink.entity.SessionHealthCheckedInfo;
import com.dtstack.taiga.flink.util.FileUtil;
import com.dtstack.taiga.flink.util.FlinkConfUtil;
import com.dtstack.taiga.flink.util.FlinkUtil;
import com.dtstack.taiga.pluginapi.CustomThreadFactory;
import com.dtstack.taiga.pluginapi.JobIdentifier;
import com.dtstack.taiga.pluginapi.constrant.ConfigConstant;
import com.dtstack.taiga.pluginapi.enums.RdosTaskStatus;
import com.dtstack.taiga.pluginapi.exception.PluginDefineException;
import com.dtstack.taiga.pluginapi.http.PoolHttpClient;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.ClientUtils;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.client.program.ProgramMissingJobException;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.SecurityOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.shaded.curator.org.apache.curator.framework.CuratorFramework;
import org.apache.flink.shaded.curator.org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.flink.shaded.curator.org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.flink.shaded.curator.org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.flink.shaded.curator.org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.flink.util.FlinkException;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Date: 2020/5/29
 * Company: www.dtstack.com
 * @author maqi
 */
public class SessionClientFactory extends AbstractClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SessionClientFactory.class);
    private static final String SESSION_CHECK_LEADER_ELECTION = "/session-check-leader-election";
    private static final String FLINK_VERSION = "flink110";


    private ClusterSpecification yarnSessionSpecification;
    private volatile ClusterClient<ApplicationId> clusterClient;
    private volatile ApplicationId clusterId;
    private AtomicBoolean isLeader = new AtomicBoolean(false);
    private FlinkConfig flinkConfig;
    private CuratorFramework zkClient;
    private LeaderLatch leaderLatch;
    private Configuration flinkConfiguration;
    private String sessionAppNameSuffix;

    private AtomicBoolean startMonitor = new AtomicBoolean(false);
    private ExecutorService yarnMonitorES;
    private FlinkClientBuilder flinkClientBuilder;
    private SessionHealthCheckedInfo sessionHealthCheckedInfo = new SessionHealthCheckedInfo();


    public SessionClientFactory(FlinkClientBuilder flinkClientBuilder) {
        this.flinkConfig = flinkClientBuilder.getFlinkConfig();
        this.flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
        this.flinkClientBuilder = flinkClientBuilder;

        // add session  name
        this.sessionAppNameSuffix = String.format("%s_%s_%s",
                flinkConfig.getCluster(),
                flinkConfig.getQueue(),
                FLINK_VERSION);
        flinkConfiguration.setString(YarnConfigOptions.APPLICATION_NAME, flinkConfig.getFlinkSessionName() + ConfigConstrant.SPLIT + sessionAppNameSuffix);

        this.yarnSessionSpecification = FlinkConfUtil.createClusterSpecification(flinkConfiguration, 0, null);

        initZkClient();

        try {
            KerberosUtils.login(flinkConfig, () -> this.startAndGetSessionClusterClient(), flinkClientBuilder.getYarnConf());
        } catch (Exception e) {
            throw new PluginDefineException("init SessionClient startAndGetSessionClusterClient error.");
        }

    }

    private void initZkClient() {
        String zkAddress = flinkConfiguration.getValue(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM);
        if (StringUtils.isBlank(zkAddress)) {
            throw new PluginDefineException("zkAddress is error");
        }

        this.zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(flinkConfig.getZkConnectionTimeout())
                .sessionTimeoutMs(flinkConfig.getZkSessionTimeout()).build();
        this.zkClient.start();

        try {
            if(null == this.leaderLatch){
                this.leaderLatch = getLeaderLatch();
                this.leaderLatch.addListener(new LeaderLatchListener() {
                    @Override
                    public void isLeader() {
                        isLeader.set(true);
                        LOG.info(">>>My monitor role is Leader.");
                    }

                    @Override
                    public void notLeader() {
                        isLeader.set(false);
                        LOG.info(">>>My monitor role is Follower.");
                    }
                });
                this.leaderLatch.start();

                //这里需要sleep一下，避免leader还未选举完就走到下一步 默认5S
                Thread.sleep(flinkConfig.getMonitorElectionWaitTime());
            }
        } catch (Exception e) {
            LOG.error("join leader election failed.", e);
        }


        LOG.warn("connector zk success...");
    }

    public LeaderLatch getLeaderLatch() {

        String lockPath = String.format("%s/%s/%s",
                SESSION_CHECK_LEADER_ELECTION,
                this.sessionAppNameSuffix,
                flinkConfig.getFlinkSessionName());

        LOG.info("flink monitor election path is {}", lockPath);
        LeaderLatch ll = new LeaderLatch(this.zkClient, lockPath);
        return ll;
    }


    private void startYarnSessionClientMonitor() {

        String threadName = String.format("%s-%s",sessionAppNameSuffix, "flink_yarn_monitor");
        LOG.warn("ThreadName : [{}] start a yarn session client monitor [{}].", Thread.currentThread().getName(), threadName);
        yarnMonitorES = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory(threadName));

        //启动守护线程---用于获取当前application状态和更新flink对应的application
        yarnMonitorES.submit(new AppStatusMonitor(flinkClientBuilder, this));
    }

    public ClusterClient<ApplicationId> startAndGetSessionClusterClient() {
        boolean startRs = startFlinkYarnSession();
        LOG.info("FlinkYarnSession launched {}.", startRs ? "succeeded" : "failed");
        if (startRs) {
            this.sessionHealthCheckedInfo.reset();
        } else {
            this.sessionHealthCheckedInfo.unHealth();
        }
        if (startMonitor.compareAndSet(false, true)) {

            this.startYarnSessionClientMonitor();
        }
        return clusterClient;
    }

    private boolean startFlinkYarnSession() {
        try {
            ClusterClient<ApplicationId> retrieveClusterClient = null;
            try {
                retrieveClusterClient = initYarnClusterClient();
            } catch (Exception e) {
                LOG.error("", e);
            }

            if (retrieveClusterClient != null) {
                clusterClient = retrieveClusterClient;
                clusterId = clusterClient.getClusterId();
                LOG.info("retrieve flink client with yarn session success");
                return true;
            }

            LOG.info("Current role is [{}] and session start auto is {}", isLeader.get() ? "Leader" : "Follower", flinkConfig.getSessionStartAuto());
            if(isLeader.get()&& flinkConfig.getSessionStartAuto()){
                try {
                    try (
                            YarnClusterDescriptor yarnSessionDescriptor = createYarnSessionClusterDescriptor();
                    ) {
                        clusterClient = yarnSessionDescriptor.deploySessionCluster(yarnSessionSpecification).getClusterClient();
                        clusterId = clusterClient.getClusterId();
                    }
                    return true;
                } catch (FlinkException e) {
                    LOG.info("Couldn't deploy Yarn session cluster, ", e);
                    throw e;
                }
            }

        } catch (Exception e) {
            LOG.error("Couldn't deploy Yarn session cluster:{}", e);
        }
        return false;
    }

    @Override
    public ClusterClient<ApplicationId> getClusterClient(JobIdentifier jobIdentifier) {
        if (!sessionHealthCheckedInfo.isRunning()) {
            LOG.warn(ErrorMessageConsts.WAIT_SESSION_RECOVER);
            // TODO 抛出一个FlinkSessionUnhealthyException类型？
            throw new PluginDefineException(ErrorMessageConsts.WAIT_SESSION_RECOVER);
        }
        return clusterClient;
    }

    @Override
    public void dealWithClientError() {
        sessionHealthCheckedInfo.incrSubmitError();
    }

    public ClusterClient<ApplicationId> initYarnClusterClient() {
        Configuration newConf = new Configuration(flinkConfiguration);
        ApplicationId applicationId = acquireAppIdAndSetClusterId(newConf);
        if (applicationId == null) {
            throw new PluginDefineException("No flink session found on yarn cluster.");
        }

        if (!flinkConfig.getFlinkHighAvailability()) {
            setNoneHaModeConfig(newConf);
        }

        YarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, flinkClientBuilder.getYarnConf());

        ClusterClient<ApplicationId> clusterClient = null;
        try {
            ClusterClientProvider<ApplicationId> clusterClientProvider = clusterDescriptor.retrieve(applicationId);
            clusterClient = clusterClientProvider.getClusterClient();
        } catch (Exception e) {
            LOG.info("No flink session, Couldn't retrieve Yarn cluster.", e);
            throw new PluginDefineException("No flink session, Couldn't retrieve Yarn cluster.");
        }

        LOG.warn("---init flink client with yarn session success----");

        return clusterClient;
    }

    private ApplicationId acquireAppIdAndSetClusterId(Configuration configuration) {
        try {
            Set<String> set = new HashSet<>();
            set.add("Apache Flink");
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.RUNNING);
            enumSet.add(YarnApplicationState.ACCEPTED);

            YarnClient yarnClient = flinkClientBuilder.getYarnClient();
            if (null == yarnClient) {
                throw new PluginDefineException("getYarnClient error, Yarn Client is null!");
            }

            List<ApplicationReport> reportList = yarnClient.getApplications(set, enumSet);

            int maxMemory = -1;
            int maxCores = -1;
            ApplicationId applicationId = null;


            for (ApplicationReport report : reportList) {
                LOG.info("filter flink session application current reportName:{} queue:{} status:{}", report.getName(), report.getQueue(), report.getYarnApplicationState());
                if (!report.getYarnApplicationState().equals(YarnApplicationState.RUNNING)) {
                    continue;
                }
                if (!report.getName().startsWith(flinkConfig.getFlinkSessionName())) {
                    continue;
                }
                if (flinkConfig.getSessionStartAuto() && !report.getName().endsWith(sessionAppNameSuffix)) {
                    continue;
                }
                if (!report.getQueue().endsWith(flinkConfig.getQueue())) {
                    continue;
                }

                int thisMemory = report.getApplicationResourceUsageReport().getNeededResources().getMemory();
                int thisCores = report.getApplicationResourceUsageReport().getNeededResources().getVirtualCores();
                LOG.info("current flink session memory {},Cores{}", thisMemory, thisCores);
                if (thisMemory > maxMemory || thisMemory == maxMemory && thisCores > maxCores) {
                    maxMemory = thisMemory;
                    maxCores = thisCores;
                    applicationId = report.getApplicationId();
                    //flinkClusterId不为空 且 yarnsession不是由engine来管控时，需要设置clusterId（兼容手动启动yarnsession的情况）
                    if (StringUtils.isBlank(configuration.getValue(HighAvailabilityOptions.HA_CLUSTER_ID)) || report.getName().endsWith(sessionAppNameSuffix)) {
                        configuration.setString(HighAvailabilityOptions.HA_CLUSTER_ID, applicationId.toString());
                    }
                }

            }
            return applicationId;
        } catch (Exception e) {
            LOG.error("", e);
            throw new PluginDefineException(e);
        }
    }

    public YarnClusterDescriptor createYarnSessionClusterDescriptor() throws MalformedURLException {
        Configuration newConf = new Configuration(flinkConfiguration);

        String flinkJarPath = flinkConfig.getFlinkJarPath();
        String pluginLoadMode = flinkConfig.getPluginLoadMode();
        YarnConfiguration yarnConf = flinkClientBuilder.getYarnConf();

        FileUtil.checkFileExist(flinkJarPath);

        if (!flinkConfig.getFlinkHighAvailability()) {
            setNoneHaModeConfig(newConf);
        } else {
            //由engine管控的yarnsession clusterId不进行设置，默认使用appId作为clusterId
            newConf.removeConfig(HighAvailabilityOptions.HA_CLUSTER_ID);
        }

        List<File> keytabFiles = null;
        if (flinkConfig.isOpenKerberos()) {
            keytabFiles = getKeytabFilesAndSetSecurityConfig(newConf);
        }

        newConf = setHdfsFlinkJarPath(flinkConfig, newConf);

        YarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, yarnConf);

        if (StringUtils.isNotBlank(pluginLoadMode) && ConfigConstrant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(pluginLoadMode)) {
            newConf.setString(ConfigConstrant.FLINK_PLUGIN_LOAD_MODE, flinkConfig.getPluginLoadMode());

            String flinkPluginRoot = flinkConfig.getFlinkPluginRoot();
            if (StringUtils.isNotBlank(flinkPluginRoot)) {
                String syncPluginDir = flinkPluginRoot + ConfigConstrant.SP + ConfigConstrant.SYNCPLUGIN_DIR;
                File syncFile = new File(syncPluginDir);
                if (!syncFile.exists()) {
                    throw new PluginDefineException("syncPlugin path is null");
                }
                List<File> pluginPaths = Arrays.stream(syncFile.listFiles())
                        .filter(file -> !file.getName().endsWith("zip"))
                        .collect(Collectors.toList());
                clusterDescriptor.addShipFiles(pluginPaths);
            }
        }
        if(CollectionUtils.isNotEmpty(keytabFiles)){
            clusterDescriptor.addShipFiles(keytabFiles);
        }
        List<URL> classpaths = getFlinkJarFile(flinkJarPath, clusterDescriptor);
        clusterDescriptor.setProvidedUserJarFiles(classpaths);

        return clusterDescriptor;
    }

    private List<File> getKeytabFilesAndSetSecurityConfig(Configuration config) {
        Map<String, File> keytabs = new HashMap<>();
        String remoteDir = flinkConfig.getRemoteDir();

        // 任务提交keytab
        String clusterKeytabDirPath = ConfigConstant.LOCAL_KEYTAB_DIR_PARENT + remoteDir;
        File clusterKeytabDir = new File(clusterKeytabDirPath);
        File[] clusterKeytabFiles = clusterKeytabDir.listFiles();

        if (clusterKeytabFiles == null || clusterKeytabFiles.length == 0) {
            throw new PluginDefineException("not find keytab file from " + clusterKeytabDirPath);
        }
        for (File file : clusterKeytabFiles) {
            String fileName = file.getName();
            String keytabPath = file.getAbsolutePath();
            String keytabFileName = flinkConfig.getPrincipalFile();

            if (StringUtils.equals(fileName, keytabFileName)) {
                String principal = flinkConfig.getPrincipal();
                if (StringUtils.isEmpty(principal)) {
                    principal = KerberosUtils.getPrincipal(keytabPath);
                }
                config.setString(SecurityOptions.KERBEROS_LOGIN_KEYTAB, keytabPath);
                config.setString(SecurityOptions.KERBEROS_LOGIN_PRINCIPAL, principal);
                continue;
            }
            keytabs.put(file.getName(), file);
        }

        return keytabs.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
    }

    public SessionHealthCheckedInfo getSessionHealthCheckedInfo() {
        return sessionHealthCheckedInfo;
    }

    static class AppStatusMonitor implements Runnable {

        /**
         * 检查时间不需要太频繁，默认10s/次
         */
        private static final Integer CHECK_INTERVAL = 10 * 1000;

        private volatile Integer retry_wait = 20 * 1000;

        private AtomicInteger retry_num = new AtomicInteger(0);

        private AtomicBoolean run = new AtomicBoolean(true);

        private FlinkClientBuilder clientBuilder;

        private SessionClientFactory sessionClientFactory;

        private SessionCheckInterval sessionCheckInterval;

        private YarnApplicationState lastAppState;

        private long startTime = System.currentTimeMillis();

        private FilesystemManager filesystemManager;

        public AppStatusMonitor(FlinkClientBuilder clientBuilder, SessionClientFactory yarnSessionClientFactory) {
            this.clientBuilder = clientBuilder;
            this.sessionClientFactory = yarnSessionClientFactory;
            this.lastAppState = YarnApplicationState.NEW;
            this.sessionCheckInterval = new SessionCheckInterval(clientBuilder.getFlinkConfig().getCheckSubmitJobGraphInterval(), yarnSessionClientFactory.sessionHealthCheckedInfo);
            //查找本地路径
            this.filesystemManager = new FilesystemManager(null, null);
        }

        @Override
        public void run() {
            while (run.get()) {
                try {
                    KerberosUtils.login(clientBuilder.getFlinkConfig(), () -> {
                        try {
                            if (sessionCheckInterval.sessionHealthCheckedInfo.isRunning()) {
                                if (clientBuilder.getYarnClient().isInState(Service.STATE.STARTED)) {
                                    ApplicationId applicationId = (ApplicationId) sessionClientFactory.clusterClient.getClusterId();
                                    ApplicationReport applicationReport = clientBuilder.getYarnClient().getApplicationReport(applicationId);
                                    YarnApplicationState appState = applicationReport.getYarnApplicationState();
                                    switch (appState) {
                                        case FAILED:
                                        case KILLED:
                                        case FINISHED:
                                            LOG.error("-------Flink yarn-session appState:{}, prepare to stop Flink yarn-session client ----", appState.toString());
                                            sessionCheckInterval.sessionHealthCheckedInfo.unHealth();
                                            break;
                                        case RUNNING:
                                            if (lastAppState != appState) {
                                                // 当 session 重启成功后 重置 retry次数
                                                retry_num.set(0);
                                                LOG.info("YARN application has been deployed successfully. reset retry_num to {} and retry_wait to {}.", retry_num.get(), retry_wait);
                                            }
                                            if (sessionClientFactory.isLeader.get() && sessionCheckInterval.doCheck()) {
                                                int checked = 0;
                                                boolean checkRs = checkJobGraphWithStatus();
                                                while (!checkRs) {
                                                    if (checked++ >= 3) {
                                                        LOG.error("Health check  failed exceeded 3 times, prepare to stop Flink yarn-session client");
                                                        sessionCheckInterval.sessionHealthCheckedInfo.unHealth();
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
                                                    sessionCheckInterval.sessionHealthCheckedInfo.reset();
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
                                    sessionCheckInterval.sessionHealthCheckedInfo.unHealth();
                                }
                            } else {
                                retry();
                                Thread.sleep(retry_wait);
                            }
                        } catch (Throwable e) {
                            LOG.error("YarnAppStatusMonitor check error:", e);
                            sessionCheckInterval.sessionHealthCheckedInfo.unHealth();
                        }
                        return null;
                    }, clientBuilder.getYarnConf());
                } catch (Throwable t) {
                    LOG.error("YarnAppStatusMonitor check error:", t);
                } finally {
                    try {
                        Thread.sleep(CHECK_INTERVAL);
                        LOG.warn("Current ThreadName is " + Thread.currentThread().getName()+" SessionAppName is "+ sessionClientFactory.sessionAppNameSuffix +" and Current role is : "+ (sessionClientFactory.isLeader.get() ? "Leader": "Follower"));
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
                    RdosTaskStatus lastAppState = RdosTaskStatus.SUBMITTING;
                    loop:
                    while (true) {
                        RdosTaskStatus jobStatus = RdosTaskStatus.SUBMITTING;
                        try {
                            String reqUrl = sessionClientFactory.clusterClient.getWebInterfaceURL() + "/jobs/" + executionResult.getJobID().toString();
                            String response = PoolHttpClient.get(reqUrl);
                            if (response != null) {
                                JSONObject statusJson = JSON.parseObject(response);
                                String status = statusJson.getString("state");
                                jobStatus = RdosTaskStatus.getTaskStatus(status.toUpperCase());
                            }
                        } catch (Exception e) {
                            LOG.error("", e);
                            jobStatus = RdosTaskStatus.FAILED;
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
                                } else if (cost > 300000){
                                    LOG.info("Yarn Session Job took more than 300 seconds.");
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
                    sessionClientFactory.flinkConfiguration.getString(
                            CheckpointingOptions.CHECKPOINTS_DIRECTORY);
            String archiveDir =
                    sessionClientFactory.flinkConfiguration.getString(
                            JobManagerOptions.ARCHIVE_DIR);
            String jobIdHex = jobId.toHexString();
            String checkpointsPath =
                    new org.apache.flink.core.fs.Path(checkpointsDir, jobIdHex).toString();
            deleteHdfsDir(checkpointsPath);
            String archivePath = new org.apache.flink.core.fs.Path(archiveDir, jobIdHex).toString();
            deleteHdfsDir(archivePath);
        }

        private void deleteHdfsDir(String remotePath) {
            YarnConfiguration yarnConf = sessionClientFactory.flinkClientBuilder.getYarnConf();
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

            int temp_num = retry_num.incrementAndGet();

            //if temp_num exceeded max retry num. leader monitor thread will retry every 5 times.
            if (temp_num > sessionClientFactory.flinkConfig.getSessionRetryNum()
                    && sessionClientFactory.isLeader.get()
                    && (temp_num % 5 != 0)) {
                return;
            }

            //重试
            try {
                LOG.warn("ThreadName : {} retry times is {}", Thread.currentThread().getName(), temp_num);
                LOG.warn("---- retry Flink yarn-session client ----", temp_num);
                if(sessionClientFactory.isLeader.get() && sessionClientFactory.flinkConfig.getSessionStartAuto()){
                    stopFlinkYarnSession();
                }
                startTime = System.currentTimeMillis();
                this.lastAppState = YarnApplicationState.NEW;
                this.sessionClientFactory.startAndGetSessionClusterClient();

            } catch (Exception e) {
                LOG.error("", e);
            }
        }

        private void stopFlinkYarnSession() {
            if (sessionClientFactory.clusterClient != null) {
                LOG.info("------- Flink yarn-session client shutdownCluster. ----");
                sessionClientFactory.clusterClient.shutDownCluster();
                LOG.info("------- Flink yarn-session client shutdownCluster over. ----");

                try {
                    LOG.info("------- Flink yarn-session client shutdown ----");
                    sessionClientFactory.clusterClient.close();
                    LOG.info("------- Flink yarn-session client shutdown over. ----");
                } catch (Exception ex) {
                    LOG.error("[SessionClientFactory] Could not properly shutdown cluster client.", ex);
                }
            }

            try {
                Configuration newConf = new Configuration(sessionClientFactory.flinkConfiguration);
                ApplicationId applicationId = sessionClientFactory.acquireAppIdAndSetClusterId(newConf);
                if (applicationId != null){
                    LOG.info("------- Flink yarn-session application kill. ----");
                    clientBuilder.getYarnClient().killApplication(applicationId);
                    LOG.info("------- Flink yarn-session application kill over. ----");
                } else {
                    LOG.info("------- Flink yarn-session compatible application not exist. ----");
                }
                YarnConfiguration yarnConf = sessionClientFactory.flinkClientBuilder.getYarnConf();
                FileSystem fs = FileSystem.get(yarnConf);
                Path homeDir = fs.getHomeDirectory();
                Path appRemotePath = new Path(String.format("%s/.flink/%s", homeDir, sessionClientFactory.clusterId.toString()));
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
            FlinkConfig flinkConfig = clientBuilder.getFlinkConfig();
            String jarPath = String.format("%s%s/%s", ConfigConstrant.USER_DIR, flinkConfig.getSessionCheckJarPath(), ConfigConstrant.SESSION_CHECK_JAR_NAME);
            LOG.info("The session check jar is in : " + jarPath);
            String mainClass = ConfigConstrant.SESSION_CHECK_MAIN_CLASS;
            String checkpoint = sessionClientFactory.flinkConfiguration.getString(CheckpointingOptions.CHECKPOINTS_DIRECTORY);
            String[] programArgs = {checkpoint};

            PackagedProgram packagedProgram = FlinkUtil.buildProgram(jarPath, classPaths,
                    null, mainClass, programArgs, SavepointRestoreSettings.none(), sessionClientFactory.flinkConfiguration, filesystemManager);
            JobGraph jobGraph = PackagedProgramUtils.createJobGraph(packagedProgram, sessionClientFactory.flinkConfiguration, 1, false);
            JobExecutionResult result = ClientUtils.submitJob(sessionClientFactory.clusterClient, jobGraph, 1, TimeUnit.MINUTES);

            if (null == result) {
                throw new ProgramMissingJobException("No JobSubmissionResult returned, please make sure you called " +
                        "ExecutionEnvironment.execute()");
            }
            LOG.info("Checked Program submitJob finished, Job with JobID:{} .", result.getJobID());
            return result.getJobExecutionResult();
        }
    }

}
