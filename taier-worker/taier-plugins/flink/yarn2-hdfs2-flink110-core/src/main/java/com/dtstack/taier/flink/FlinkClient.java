/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.flink;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.flink.plugininfo.SqlPluginInfo;
import com.dtstack.taier.pluginapi.pojo.CheckResult;
import com.dtstack.taier.base.filesystem.FilesystemManager;
import com.dtstack.taier.base.monitor.AcceptedApplicationMonitor;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.pluginapi.JarFileInfo;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.JobParam;
import com.dtstack.taier.pluginapi.client.AbstractClient;
import com.dtstack.taier.pluginapi.constrant.JobResultConstant;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.enums.RdosTaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.http.HttpClient;
import com.dtstack.taier.pluginapi.http.PoolHttpClient;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.util.DtStringUtil;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.pluginapi.util.UrlUtil;
import com.dtstack.taier.flink.base.enums.ClusterMode;
import com.dtstack.taier.flink.constrant.ConfigConstrant;
import com.dtstack.taier.flink.constrant.ExceptionInfoConstrant;
import com.dtstack.taier.flink.constrant.ErrorMessageConsts;
import com.dtstack.taier.flink.entity.TaskmanagerInfo;
import com.dtstack.taier.flink.factory.PerJobClientFactory;
import com.dtstack.taier.flink.parser.PrepareOperator;
import com.dtstack.taier.flink.plugininfo.SyncPluginInfo;
import com.dtstack.taier.flink.resource.FlinkPerJobResourceInfo;
import com.dtstack.taier.flink.resource.FlinkSessionResourceInfo;
import com.dtstack.taier.flink.util.ApplicationWSParser;
import com.dtstack.taier.flink.util.FileUtil;
import com.dtstack.taier.flink.util.FlinkConfUtil;
import com.dtstack.taier.flink.util.FlinkRestParseUtil;
import com.dtstack.taier.flink.util.FlinkUtil;
import com.dtstack.taier.flink.util.HadoopConf;
import com.dtstack.taier.flink.util.JobGraphBuildUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.ClientUtils;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.util.Preconditions;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 *
 * Date: 2017/2/20
 * Company: www.dtstack.com
 * @author xuchao
 */
public class FlinkClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(FlinkClient.class);

    private Properties flinkExtProp;

    private FlinkConfig flinkConfig;

    private HadoopConf hadoopConf;

    private FlinkClientBuilder flinkClientBuilder;

    private SyncPluginInfo syncPluginInfo;

    private SqlPluginInfo sqlPluginInfo;

    private Map<String, List<String>> cacheFile = Maps.newConcurrentMap();

    private FlinkClusterClientManager flinkClusterClientManager;

    private FilesystemManager filesystemManager;

    private final static Predicate<RdosTaskStatus> IS_END_STATUS = status -> RdosTaskStatus.getStoppedStatus().contains(status.getStatus()) || RdosTaskStatus.NOTFOUND.equals(status);

    @Override
    public void init(Properties prop) throws Exception {
        this.flinkExtProp = prop;

        String propStr = PublicUtil.objToString(prop);
        flinkConfig = PublicUtil.jsonStrToObject(propStr, FlinkConfig.class);

        syncPluginInfo = SyncPluginInfo.create(flinkConfig);
        sqlPluginInfo = SqlPluginInfo.create(flinkConfig);

        hadoopConf = FlinkClientBuilder.initHadoopConf(flinkConfig);
        flinkClientBuilder = new FlinkClientBuilder(flinkConfig, hadoopConf.getConfiguration(), hadoopConf.getYarnConfiguration());
        flinkClientBuilder.initFlinkGlobalConfiguration(flinkExtProp);

        flinkClusterClientManager = new FlinkClusterClientManager(flinkClientBuilder);

        filesystemManager = new FilesystemManager(hadoopConf.getYarnConfiguration(), flinkConfig.getSftpConf());

        if (flinkConfig.getMonitorAcceptedApp()) {
            AcceptedApplicationMonitor.start(hadoopConf.getYarnConfiguration(), flinkConfig.getQueue(), flinkConfig);
        }
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        try {
            return KerberosUtils.login(flinkConfig,()->{
                EJobType jobType = jobClient.getJobType();
                JobResult jobResult = null;
                if (EJobType.MR.equals(jobType)) {
                    jobResult = submitJobWithJar(jobClient);
                } else if (EJobType.SQL.equals(jobType)) {
                    jobResult = submitSqlJob(jobClient);
                } else if (EJobType.SYNC.equals(jobType)) {
                    jobResult = submitSyncJob(jobClient);
                }
                if (jobResult != null) {
                    logger.info("taskId: {}, job submit success, result: {}", jobClient.getJobId(), jobResult.toString());
                }
                return jobResult;
            },hadoopConf.getYarnConfiguration());
        } catch (Exception e) {
            logger.error("taskId: {}, can not submit a job process SubmitJobWithType error: ", jobClient.getJobId(), e);
            return JobResult.createErrorResult(e);
        }
    }

    private JobResult submitJobWithJar(JobClient jobClient) {
        List<URL> classPaths = Lists.newArrayList();
        List<String> programArgList = Lists.newArrayList();
        return submitJobWithJar(jobClient, classPaths, programArgList);
    }

    private JobResult submitJobWithJar(JobClient jobClient, List<URL> classPaths, List<String> programArgList) {

        JobParam jobParam = new JobParam(jobClient);
        String jarPath = jobParam.getJarPath();
        if(jarPath == null){
            logger.error("can not submit a job without jar path, please check it");
            return JobResult.createErrorResult("can not submit a job without jar path, please check it");
        }

        String args = jobParam.getClassArgs();
        if(StringUtils.isNotBlank(args)){
            programArgList.addAll(DtStringUtil.splitIgnoreQuota(args, ' '));
        }
        //如果jar包里面未指定mainclass,需要设置该参数
        String entryPointClass = jobParam.getMainClass();

        SavepointRestoreSettings spSettings = buildSavepointSetting(jobClient);
        String[] programArgs = programArgList.toArray(new String[programArgList.size()]);

        PackagedProgram packagedProgram = null;
        JobGraph jobGraph = null;
        Pair<String, String> runResult;


        try {
            ClusterMode clusterMode = ClusterMode.getClusteMode(flinkConfig.getClusterMode());
            if (ClusterMode.isPerjob(clusterMode)) {
                // perjob模式延后创建PackagedProgram
                ClusterSpecification clusterSpecification = FlinkConfUtil.createClusterSpecification(flinkClientBuilder.getFlinkConfiguration(), jobClient.getApplicationPriority(), jobClient.getConfProperties());
                clusterSpecification.setClasspaths(classPaths);
                clusterSpecification.setEntryPointClass(entryPointClass);
                clusterSpecification.setJarFile(new File(jarPath));
                clusterSpecification.setSpSetting(spSettings);
                clusterSpecification.setProgramArgs(programArgs);
                clusterSpecification.setCreateProgramDelay(true);
                clusterSpecification.setYarnConfiguration(hadoopConf.getYarnConfiguration());

                logger.info("--------taskId: {} run by PerJob mode-----", jobClient.getJobId());
                runResult = runJobByPerJob(clusterSpecification, jobClient);
                jobGraph = clusterSpecification.getJobGraph();
                packagedProgram = clusterSpecification.getProgram();
            } else {
                Integer runParallelism = FlinkUtil.getJobParallelism(jobClient.getConfProperties());
                packagedProgram = FlinkUtil.buildProgram(jarPath, classPaths, jobClient.getJobType(), entryPointClass, programArgs, spSettings, flinkClientBuilder.getFlinkConfiguration(), filesystemManager);
                jobGraph = PackagedProgramUtils.createJobGraph(packagedProgram, flinkClientBuilder.getFlinkConfiguration(), runParallelism, false);

                //只有当程序本身没有指定并行度的时候该参数才生效
                clearClassPathShipfileLoadMode(packagedProgram);

                logger.info("--------taskId: {} run by Session mode-----", jobClient.getJobId());
                runResult = runJobBySession(jobGraph);
            }

            JobResult jobResult = JobResult.createSuccessResult(runResult.getFirst(), runResult.getSecond());
            jobResult.setExtraData(JobResultConstant.JOB_GRAPH, JobGraphBuildUtil.buildLatencyMarker(jobGraph));
            long checkpointInterval = jobGraph.getCheckpointingSettings().getCheckpointCoordinatorConfiguration().getCheckpointInterval();
            if (checkpointInterval >= Long.MAX_VALUE) {
                checkpointInterval = 0;
            }
            jobResult.setExtraData(JobResultConstant.FLINK_CHECKPOINT, String.valueOf(checkpointInterval));
            return jobResult;
        } catch (Throwable e) {
            return JobResult.createErrorResult(e);
        } finally {
            if (packagedProgram != null) {
                packagedProgram.deleteExtractedLibraries();
            }
        }
    }

    /**
     * perjob模式提交任务
     */
    private Pair<String, String> runJobByPerJob(ClusterSpecification clusterSpecification, JobClient jobClient) throws Exception{
        PerJobClientFactory perJobClientFactory = (PerJobClientFactory) flinkClusterClientManager.getClientFactory();
        try (
                YarnClusterDescriptor descriptor = perJobClientFactory.createPerJobClusterDescriptor(jobClient);
        ) {
            perJobClientFactory.deleteTaskIfExist(jobClient);
            ClusterClient<ApplicationId> clusterClient = descriptor.deployJobCluster(clusterSpecification, new JobGraph(),true).getClusterClient();

            String applicationId = clusterClient.getClusterId().toString();
            String flinkJobId = clusterSpecification.getJobGraph().getJobID().toString();

            perJobClientFactory.dealWithDeployCluster(applicationId, clusterClient);
            return Pair.create(flinkJobId, applicationId);
        }
    }

    /**
     * Session模式运行任务
     */
    private Pair<String, String> runJobBySession(JobGraph jobGraph) throws Exception {
        try {
            ClusterClient clusterClient = flinkClusterClientManager.getClusterClient(null);
            JobExecutionResult jobExecutionResult = ClientUtils.submitJob(clusterClient, jobGraph, flinkConfig.getSubmitTimeout(), TimeUnit.MINUTES);

            return Pair.create(jobExecutionResult.getJobID().toString(), null);
        } catch (Exception e) {
            flinkClusterClientManager.dealWithClientError();
            throw new PluginDefineException(e);
        }
    }

    private SavepointRestoreSettings buildSavepointSetting(JobClient jobClient){

        if(jobClient.getExternalPath() == null){
            return SavepointRestoreSettings.none();
        }

        String externalPath = jobClient.getExternalPath();
        boolean allowNonRestoredState = false;
        if(jobClient.getConfProperties().containsKey(ConfigConstrant.FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY)){
            String allowNonRestored = (String) jobClient.getConfProperties().get(ConfigConstrant.FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY);
            allowNonRestoredState = BooleanUtils.toBoolean(allowNonRestored);
        }

        return SavepointRestoreSettings.forPath(externalPath, allowNonRestoredState);
    }

    private JobResult submitSqlJob(JobClient jobClient) {

        if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
            if(existsJobOnFlink(jobClient.getEngineTaskId())){
                return JobResult.createSuccessResult(jobClient.getEngineTaskId());
            }
        }

        ComputeType computeType = jobClient.getComputeType();
        if(computeType == null){
            throw new PluginDefineException("need to set compute type.");
        }

        switch (computeType){
            case BATCH:
                return submitSqlJobForBatch(jobClient);
            case STREAM:
                return submitSqlJobForStream(jobClient);

        }
        throw new PluginDefineException("not support for compute type :" + computeType);
    }

    /**
     * 1: 不再对操作顺序做限制
     * 2：不再限制输入源数量
     * 3：不再限制输出源数量
     * @param jobClient
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private JobResult submitSqlJobForStream(JobClient jobClient) {

        try {
            String taskWorkspace = String.format("%s/%s_%s", ConfigConstrant.TMP_DIR, jobClient.getJobId(), Thread.currentThread().getId());
            //构建args
            List<String> args = sqlPluginInfo.buildExeArgs(jobClient);
            List<String> attachJarLists = cacheFile.get(taskWorkspace);

            List<URL> attachJarUrls = Lists.newArrayList();
            if(!CollectionUtils.isEmpty(attachJarLists)){
                args.add("-addjar");
                String attachJarStr = PublicUtil.objToString(attachJarLists);
                args.add(URLEncoder.encode(attachJarStr, Charsets.UTF_8.name()));

                attachJarUrls = attachJarLists.stream().map(k -> {
                    try {
                        return new File(k).toURL();
                    } catch (MalformedURLException e) {
                        throw new PluginDefineException(e);
                    }
                }).collect(Collectors.toList());
            }

            JarFileInfo coreJarInfo = sqlPluginInfo.createCoreJarInfo();
            jobClient.setCoreJarInfo(coreJarInfo);

            return submitJobWithJar(jobClient, attachJarUrls, args);
        } catch (Exception e) {
            return JobResult.createErrorResult(e);
        }
    }

    private JobResult submitSqlJobForBatch(JobClient jobClient) {
        throw new PluginDefineException("not support for flink batch sql now!!!");
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        try {
            return KerberosUtils.login(flinkConfig, () -> {
                String engineJobId = jobIdentifier.getEngineJobId();
                String appId = jobIdentifier.getApplicationId();
                RdosTaskStatus rdosTaskStatus = null;
                try {
                    rdosTaskStatus = getJobStatus(jobIdentifier);

                    if (rdosTaskStatus != null && !RdosTaskStatus.getStoppedStatus().contains(rdosTaskStatus.getStatus())) {
                        ClusterClient targetClusterClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
                        JobID jobId = new JobID(org.apache.flink.util.StringUtils.hexStringToByte(jobIdentifier.getEngineJobId()));

                        Integer deployMode = jobIdentifier.getDeployMode();
                        if (EDeployMode.SESSION == EDeployMode.getByType(deployMode) ||
                                EDeployMode.STANDALONE == EDeployMode.getByType(deployMode)) {
                            // session job cancel
                            Object ack = targetClusterClient.cancel(jobId).get(jobIdentifier.getTimeout(), TimeUnit.MILLISECONDS);
                            logger.info("taskId: {}, job[{}] cancel success with ack : {}", jobIdentifier.getJobId(), engineJobId, ack.toString());
                        } else if (EDeployMode.PERJOB == EDeployMode.getByType(deployMode)) {
                            // perJob cancel
                            if(jobIdentifier.isForceCancel()){
                                return killApplication(jobIdentifier);
                            }
                            CompletableFuture completableFuture = targetClusterClient.cancelWithSavepoint(jobId, null);
                            Object ask = completableFuture.get(jobIdentifier.getTimeout(), TimeUnit.MILLISECONDS);
                            logger.info("taskId: {}, job[{}] cancelWithSavepoint success, savepoint path {}",
                                    jobIdentifier.getJobId(), engineJobId, ask.toString());
                        } else {
                            logger.warn("taskId: {}, job[{}] cancel failed Unexpected deployMode type: {}", jobIdentifier.getJobId(), engineJobId, deployMode);
                        }
                    }
                    return JobResult.createSuccessResult(jobIdentifier.getEngineJobId());
                } catch (Exception e) {
                    if (rdosTaskStatus != null){
                        logger.warn("taskId: {}, cancel job error jobStatus is: {}", jobIdentifier.getJobId(), rdosTaskStatus.name());
                    }

                    logger.error("taskId: {} engineJobId:{} applicationId:{} cancelJob error, try to cancel with yarnClient.", jobIdentifier.getJobId(), engineJobId, appId, e);
                    return JobResult.createErrorResult(e);
                }
            }, hadoopConf.getYarnConfiguration());
        } catch (Exception exception) {
            logger.error("taskId: {} engineJobId: {} applicationId: {} cancelJob error: ", jobIdentifier.getJobId(), jobIdentifier.getEngineJobId(), jobIdentifier.getApplicationId(), exception);
            return JobResult.createErrorResult(exception);
        }
    }

    /**
     * 强制关闭yarn application
     * @param jobIdentifier
     * @return
     */
    private JobResult killApplication(JobIdentifier jobIdentifier){
        try {
            ApplicationId applicationId = ConverterUtils.toApplicationId(jobIdentifier.getApplicationId());
            flinkClientBuilder.getYarnClient().killApplication(applicationId);
            logger.info("jobId:{} engineJobId:{} applicationId:{} yarnClient kill application success.", jobIdentifier.getJobId(), jobIdentifier.getEngineJobId(), jobIdentifier.getApplicationId());
            return JobResult.createSuccessResult(jobIdentifier.getEngineJobId());
        } catch (Exception killException) {
            logger.error("jobId:{} engineJobId:{} applicationId:{} yarnClient kill application error:", jobIdentifier.getJobId(), jobIdentifier.getEngineJobId(), jobIdentifier.getApplicationId(), killException);
            return JobResult.createErrorResult(killException);
        }
    }

    /**
     * 直接调用rest api直接返回
     * @param jobIdentifier
     * @return
     */
    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getJobId();
        String engineJobId = jobIdentifier.getEngineJobId();
        String applicationId = jobIdentifier.getApplicationId();

        if (StringUtils.isEmpty(engineJobId)) {
            logger.warn("{} getJobStatus is NOTFOUND, because engineJobId is empty.", jobId);
            return RdosTaskStatus.NOTFOUND;
        }

        ClusterClient clusterClient = null;
        try {
            clusterClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
        } catch (Exception e) {
            logger.error("taskId: {}, get clusterClient error:", jobId, e);
        }

        String jobUrlPath = String.format(ConfigConstrant.JOB_URL_FORMAT, engineJobId);
        String response = null;
        Exception urlException = null;
        if (clusterClient != null) {
            try {
                String webInterfaceURL = clusterClient.getWebInterfaceURL();
                String jobUrl = webInterfaceURL + jobUrlPath;
                response = PoolHttpClient.get(jobUrl);
            } catch (Exception e) {
                urlException = e;
            }
        }

        if (StringUtils.isEmpty(response)) {
            try {
                response = getMessageFromJobArchive(engineJobId, jobUrlPath);
            } catch (Exception e) {
                if (urlException != null) {
                    logger.error("taskId: {}, Get job status error from webInterface: ", jobId, urlException);
                }
                logger.error("taskId: {}, request job status error from jobArchive: ", jobId, e);
            }
        }

        if (StringUtils.isEmpty(response)) {
            if (StringUtils.isNotEmpty(applicationId)) {
                RdosTaskStatus rdosTaskStatus = getPerJobStatus(applicationId);
                logger.info("taskId: {}, try getPerJobStatus with yarnClient, status: {}", jobId, rdosTaskStatus.name());
                return rdosTaskStatus;
            }
            return RdosTaskStatus.NOTFOUND;
        }

        try{
            if (response == null) {
                throw new PluginDefineException("Get status response is null");
            }

            Map<String, Object> statusMap = PublicUtil.jsonStrToObject(response, Map.class);
            Object stateObj = statusMap.get("state");
            if(stateObj == null){
                return RdosTaskStatus.NOTFOUND;
            }

            String state = (String) stateObj;
            state = StringUtils.upperCase(state);
            return RdosTaskStatus.getTaskStatus(state);
        }catch (Exception e){
            logger.error("taskId: {}, getJobStatus error: ", jobId, e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    /**
     * per-job模式其实获取的任务状态是yarn-application状态
     * @param applicationId
     * @return
     */
    public RdosTaskStatus getPerJobStatus(String applicationId) {
        try {
            return KerberosUtils.login(flinkConfig, () -> {
                ApplicationId appId = ConverterUtils.toApplicationId(applicationId);
                try {
                    ApplicationReport report = flinkClientBuilder.getYarnClient().getApplicationReport(appId);
                    YarnApplicationState applicationState = report.getYarnApplicationState();
                    switch (applicationState) {
                        case KILLED:
                            return RdosTaskStatus.KILLED;
                        case NEW:
                        case NEW_SAVING:
                            return RdosTaskStatus.CREATED;
                        case SUBMITTED:
                            //FIXME 特殊逻辑,认为已提交到计算引擎的状态为等待资源状态
                            return RdosTaskStatus.WAITCOMPUTE;
                        case ACCEPTED:
                            return RdosTaskStatus.SCHEDULED;
                        case RUNNING:
                            return RdosTaskStatus.RUNNING;
                        case FINISHED:
                            //state 为finished状态下需要兼顾判断finalStatus.
                            FinalApplicationStatus finalApplicationStatus = report.getFinalApplicationStatus();
                            if (finalApplicationStatus == FinalApplicationStatus.FAILED) {
                                return RdosTaskStatus.FAILED;
                            } else if (finalApplicationStatus == FinalApplicationStatus.SUCCEEDED) {
                                return RdosTaskStatus.FINISHED;
                            } else if (finalApplicationStatus == FinalApplicationStatus.KILLED) {
                                return RdosTaskStatus.KILLED;
                            } else if (finalApplicationStatus == FinalApplicationStatus.UNDEFINED) {
                                return RdosTaskStatus.FAILED;
                            } else {
                                return RdosTaskStatus.RUNNING;
                            }

                        case FAILED:
                            return RdosTaskStatus.FAILED;
                        default:
                            throw new PluginDefineException("Unsupported application state");
                    }
                } catch (YarnException | IOException e) {
                    logger.error("appId: {}, getPerJobStatus with yarnClient error: ", applicationId, e);
                    return RdosTaskStatus.NOTFOUND;
                }
            }, hadoopConf.getYarnConfiguration());
        } catch (Exception e) {
            logger.error("appId: {}, getPerJobStatus with yarnClient error: ", applicationId, e);
            //防止因为kerberos 认证不过出现notfound最后变为failed
            return RdosTaskStatus.RUNNING;
        }
    }

    public String getReqUrl(ClusterMode clusterMode) {
        if (ClusterMode.isPerjob(clusterMode)){
            return "${monitor}";
        } else {
            return getReqUrl();
        }
    }

    public String getReqUrl(){
        return flinkClusterClientManager.getClusterClient(null).getWebInterfaceURL();
    }


    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        try {
            return KerberosUtils.login(flinkConfig, () -> {
                ApplicationId applicationId = (ApplicationId) flinkClusterClientManager.getClusterClient(jobIdentifier).getClusterId();

                String url = null;
                try {
                    url = flinkClientBuilder.getYarnClient().getApplicationReport(applicationId).getTrackingUrl();
                    url = StringUtils.substringBefore(url.split("//")[1], "/");
                } catch (Exception e) {
                    logger.error("Getting URL failed" + e);
                }
                return url;
            }, hadoopConf.getYarnConfiguration());
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    private JobResult submitSyncJob(JobClient jobClient) {
        //使用flink作为数据同步调用的其实是提交mr--job
        JarFileInfo coreJar = syncPluginInfo.createAddJarInfo();
        jobClient.setCoreJarInfo(coreJar);

        List<String> programArgList = syncPluginInfo.createSyncPluginArgs(jobClient, this);
        List<URL> classPaths = syncPluginInfo.getClassPaths(programArgList);

        return submitJobWithJar(jobClient, classPaths, programArgList);
    }


    @Override
    public String getMessageByHttp(String path) {
        try {
            String reqUrl = String.format("%s%s", getReqUrl(), path);
            return PoolHttpClient.get(reqUrl, null, ConfigConstrant.HTTP_MAX_RETRY);
        } catch (Exception e) {
            throw new PluginDefineException(e);
        }
    }

    private String getMessageByHttp(String path, String reqURL) {
        try {
            String reqUrl = String.format("%s%s", reqURL, path);
            return PoolHttpClient.get(reqUrl);
        } catch (Exception e) {
            logger.error("", e);
            throw new PluginDefineException(e);
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getJobId();
        String engineJobId = jobIdentifier.getEngineJobId();
        String exceptMessage = "";
        try {
            if (engineJobId == null) {
                logger.error("{} getJobLog is null, because engineJobId is empty", jobId);
                return handleJobLog("", "Get jogLog error, because engineJobId is null", "Job has not submitted to yarn, Please waiting moment.");
            }
            String exceptionUrlPath = String.format(ConfigConstrant.JOB_EXCEPTIONS_URL_FORMAT, engineJobId);
            RdosTaskStatus jobStatus = getJobStatus(jobIdentifier);
            Boolean isEndStatus = IS_END_STATUS.test(jobStatus);
            Boolean isPerjob = EDeployMode.PERJOB.getType().equals(jobIdentifier.getDeployMode());

            if (isPerjob && isEndStatus) {
                exceptMessage = getMessageFromJobArchive(engineJobId, exceptionUrlPath);
            } else {
                ClusterClient currClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
                String reqURL = currClient.getWebInterfaceURL();
                exceptMessage = getMessageByHttp(exceptionUrlPath, reqURL);
            }
            String jobLogContent = FlinkRestParseUtil.parseEngineLog(exceptMessage);
            logger.info("taskId: {}, job log content: {}", jobId, StringUtils.substring(jobLogContent, 0, 100));
            return jobLogContent;
        } catch (Exception e) {
            logger.error("Get job log error, {}", e.getMessage());
            return handleJobLog(engineJobId, ExceptionInfoConstrant.FLINK_GET_LOG_ERROR_UNDO_RESTART_EXCEPTION, ExceptionUtil.getErrorMessage(e));
        }
    }

    private String handleJobLog(String engineJobId, String exception, String exceptionErr) {
        Map<String, String> map = new LinkedHashMap<>(8);
        map.put("jobId", engineJobId);
        map.put("exception", exception);
        map.put("engineLogErr", exceptionErr);
        return new Gson().toJson(map);
    }

    public String getMessageFromJobArchive(String jobId, String urlPath) throws Exception {
        String archiveDir = flinkExtProp.getProperty(JobManagerOptions.ARCHIVE_DIR.key());
        String jobArchivePath = archiveDir + ConfigConstrant.SP + jobId;

        return KerberosUtils.login(flinkConfig, () -> {
            try {
                InputStream is = FileUtil.readStreamFromFile(jobArchivePath, hadoopConf.getConfiguration());
                JsonParser jsonParser = new JsonParser();
                try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    JsonObject jobArchiveAll = (JsonObject) jsonParser.parse(reader);
                    Preconditions.checkNotNull(jobArchiveAll, "jobArchive is null");

                    JsonArray jsonArray = jobArchiveAll.getAsJsonArray("archive");
                    for (JsonElement ele: jsonArray) {
                        JsonObject obj = ele.getAsJsonObject();
                        if (StringUtils.equals(obj.get("path").getAsString(), urlPath)) {
                            String exception = obj.get("json").getAsString();
                            return exception;
                        }
                    }
                }
                throw new PluginDefineException(String.format("Not found Message from jobArchive, jobId[%s], urlPath[%s]", jobId, urlPath));
            } catch (Exception e) {
                throw new PluginDefineException(e);
            }
        }, hadoopConf.getConfiguration());
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {

        ClusterMode clusterMode = ClusterMode.getClusteMode(flinkConfig.getClusterMode());

        boolean isPerJob = ClusterMode.isPerjob(clusterMode);
        JudgeResult judgeResult = JudgeResult.notOk(null);
        try {
            if(ClusterMode.isStandalone(clusterMode)){
                return judgeSessionSlot(jobClient, true);
            }
            judgeResult = KerberosUtils.login(flinkConfig, () -> {
                FlinkPerJobResourceInfo perJobResourceInfo = FlinkPerJobResourceInfo.FlinkPerJobResourceInfoBuilder()
                        .withYarnClient(flinkClientBuilder.getYarnClient())
                        .withQueueName(flinkConfig.getQueue())
                        .withYarnAccepterTaskNumber(flinkConfig.getYarnAccepterTaskNumber())
                        .build();

                return perJobResourceInfo.judgeSlots(jobClient);
            }, hadoopConf.getYarnConfiguration());

            if (judgeResult.available() && !isPerJob){
                judgeResult = judgeSessionSlot(jobClient, false);
            }
        } catch (Exception e){
            logger.error("taskId:{} judgeSlots error: ", jobClient.getJobId(), e);
            judgeResult = JudgeResult.exception("judgeSlots error:" + ExceptionUtil.getErrorMessage(e));
        }
        logger.info("taskId:{}, judgeResult: {}, reason: {}", jobClient.getJobId(), judgeResult.getResult(), judgeResult.getReason());
        return judgeResult;
    }

    /**
     *  judge slot of flink session
     * @param jobClient job description
     * @param standalone true if flink mode is standalone
     * @return judge result
     */
    public JudgeResult judgeSessionSlot(JobClient jobClient, boolean standalone){
        try {
            flinkClusterClientManager.getClusterClient(null);
        } catch (Exception e) {
            logger.warn("taskId: {}, wait flink session client recover: ", jobClient.getJobId(), e);
            return JudgeResult.notOk(ErrorMessageConsts.WAIT_SESSION_RECOVER);
        }
        String slotInfo = null;
        try {
            slotInfo = getMessageByHttp(FlinkRestParseUtil.SLOTS_INFO);
        } catch (Exception e) {
            logger.error("taskId: {}, Connection to jobmanager failed, ", jobClient.getJobId(), e);
            return JudgeResult.notOk("Connection to jobmanager failed");
        }

        FlinkSessionResourceInfo yarnSessionResourceInfo = new FlinkSessionResourceInfo(standalone);
        yarnSessionResourceInfo.getFlinkSessionSlots(slotInfo, flinkConfig.getFlinkSessionSlotCount());
        return yarnSessionResourceInfo.judgeSlots(jobClient);
    }

    /**
     *  获取Flink任务执行时的container日志URL及字节大小
     * @return
     */
    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getJobId();
        String jobMaster = getJobMaster(jobIdentifier);
        String rootURL = UrlUtil.getHttpRootUrl(jobMaster);
        String amRootURl = String.format(ConfigConstrant.YARN_APPLICATION_URL_FORMAT, rootURL, jobIdentifier.getApplicationId());

        List<String> result = Lists.newArrayList();
        try {
            YarnConfiguration yarnConfig = hadoopConf.getYarnConfiguration();
            String response = ApplicationWSParser.getDataFromYarnRest(yarnConfig, amRootURl);
            if (!StringUtils.isEmpty(response)) {
                ApplicationWSParser applicationWsParser = new ApplicationWSParser(response);
                String amStatue = applicationWsParser.getParamContent(ApplicationWSParser.AM_STATUE);
                if (!StringUtils.equalsIgnoreCase(amStatue, "RUNNING")) {
                    logger.info("taskId: {}, am statue is {} not running!", jobId, amStatue);
                    return result;
                }

                String amContainerLogsURL = applicationWsParser.getParamContent(ApplicationWSParser.AM_CONTAINER_LOGS_TAG);
                String user = applicationWsParser.getParamContent(ApplicationWSParser.AM_USER_TAG);
                String containerLogUrlFormat = UrlUtil.formatUrlHost(amContainerLogsURL);
                String trackingUrl = applicationWsParser.getParamContent(ApplicationWSParser.TRACKING_URL);

                // parse am log
                parseAmLog(applicationWsParser, amContainerLogsURL).ifPresent(result::add);
                // parse containers log
                List<String> containersLogs = parseContainersLog(applicationWsParser, user, containerLogUrlFormat, trackingUrl);
                result.addAll(containersLogs);
            }
        } catch (Exception e) {
            logger.error("taskId:{}, getRollingLogBaseInfo error: ", jobId, e);
        }
        logger.info("taskId: {}, getRollingLogBaseInfo: {}", jobId, result);
        return result;
    }

    private String buildContainerLogUrl(String containerHostPortFormat, String[] nameAndHost, String user) {
        logger.debug("buildContainerLogUrl name:{},host{},user{} ", nameAndHost[0], nameAndHost[1], user);
        String containerUlrPre = String.format(containerHostPortFormat, nameAndHost[1]);
        return String.format(ConfigConstrant.YARN_CONTAINER_LOG_URL_FORMAT, containerUlrPre, nameAndHost[0], user);
    }

    /**
     *  parse am log
     * @param applicationWSParser
     * @return
     */
    public Optional<String> parseAmLog(ApplicationWSParser applicationWSParser, String amContainerLogsURL) {
        try {
            String logPreURL = UrlUtil.getHttpRootUrl(amContainerLogsURL);
            logger.info("jobmanager container logs URL is: {}, logPreURL is {} :", amContainerLogsURL, logPreURL);
            YarnConfiguration yarnConfig = hadoopConf.getYarnConfiguration();
            ApplicationWSParser.RollingBaseInfo amLogInfo = applicationWSParser.parseContainerLogBaseInfo(amContainerLogsURL, logPreURL, ConfigConstrant.JOBMANAGER_COMPONEN, yarnConfig);
            return Optional.ofNullable(JSONObject.toJSONString(amLogInfo));
        } catch (Exception e) {
            logger.error("parse am Log error! ", e);
        }
        return Optional.empty();
    }

    private List<String> parseContainersLog(ApplicationWSParser applicationWSParser, String user, String containerLogUrlFormat, String trackingUrl) throws IOException {
        List<String> taskmanagerInfoStr = Lists.newArrayList();
        try {
            YarnConfiguration yarnConfig = hadoopConf.getYarnConfiguration();
            List<TaskmanagerInfo> taskmanagerInfos = getContainersNameAndHost(trackingUrl);
            for (TaskmanagerInfo info : taskmanagerInfos) {
                String[] nameAndHost = parseContainerNameAndHost(info);
                String containerLogUrl = buildContainerLogUrl(containerLogUrlFormat, nameAndHost, user);
                String preUrl =  UrlUtil.getHttpRootUrl(containerLogUrl);
                logger.info("taskmanager container logs URL is: {},  preURL is :{}", containerLogUrl, preUrl);
                ApplicationWSParser.RollingBaseInfo rollingBaseInfo = applicationWSParser.parseContainerLogBaseInfo(containerLogUrl, preUrl, ConfigConstrant.TASKMANAGER_COMPONEN, yarnConfig);
                rollingBaseInfo.setOtherInfo(JSONObject.toJSONString(info));
                taskmanagerInfoStr.add(JSONObject.toJSONString(rollingBaseInfo));
            }
        } catch (Exception e) {
            logger.error("parse taskmanager container log error! ", e);
        }

        return taskmanagerInfoStr;
    }

    private List<TaskmanagerInfo> getContainersNameAndHost(String trackingUrl) throws IOException {
        List<TaskmanagerInfo> containersNameAndHost = Lists.newArrayList();
        try {
            String taskManagerUrl = String.format(ConfigConstrant.TASKMANAGERS_URL_FORMAT, trackingUrl);
            String taskManagersInfo = HttpClient.get(taskManagerUrl);
            JSONObject response = JSONObject.parseObject(taskManagersInfo);
            JSONArray taskManagers = response.getJSONArray(ConfigConstrant.TASKMANAGERS_KEY);

            containersNameAndHost = IntStream.range(0, taskManagers.size())
                    .mapToObj(taskManagers::getJSONObject)
                    .map(jsonObject -> JSONObject.toJavaObject(jsonObject, TaskmanagerInfo.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("request task managers error !", e);
        }
        return containersNameAndHost;
    }

    private String[] parseContainerNameAndHost(TaskmanagerInfo taskmanagerInfo) {
        String containerName = taskmanagerInfo.getId();
        String akkaPath = taskmanagerInfo.getPath();
        String host = "";
        try {
            logger.info("parse akkaPath: {}", akkaPath);
            host = akkaPath.split("[@:]")[2];
        } catch (Exception e) {
            logger.error("parseContainersHost error ", e);
        }
        return new String[]{containerName, host};
    }

    @Override
    public void beforeSubmitFunc(JobClient jobClient) {
        logger.info("Job[{}] submit before", jobClient.getJobId());

        String sql = jobClient.getSql();
        List<String> sqlArr = DtStringUtil.splitIgnoreQuota(sql, ';');
        if(sqlArr.size() == 0){
            return;
        }

        List<String> sqlList = Lists.newArrayList(sqlArr);
        Iterator<String> sqlItera = sqlList.iterator();
        List<String> fileList = Lists.newArrayList();

        String taskWorkspace = FlinkUtil.getTaskWorkspace(jobClient.getJobId());
        while (sqlItera.hasNext()) {
            String tmpSql = sqlItera.next();
            // handle add jar statements and comment statements on the same line
            tmpSql = PrepareOperator.handleSql(tmpSql);
            if (PrepareOperator.verificResource(tmpSql)) {
                sqlItera.remove();
                String localResourceDir = taskWorkspace + ConfigConstrant.SP + "resource";

                if (!new File(localResourceDir).exists()) {
                    new File(localResourceDir).mkdirs();
                }

                File resourceFile = PrepareOperator.getResourceFile(tmpSql);
                String resourceFileName = PrepareOperator.getResourceFileName(tmpSql);

                String remoteFile = resourceFile.getAbsolutePath();
                String localFile = localResourceDir + ConfigConstrant.SP + resourceFileName;
                //download file and close
                File downloadFile = filesystemManager.downloadFile(remoteFile, localFile);
                logger.info("Download Resource File :" + downloadFile.getAbsolutePath());
            } else if (PrepareOperator.verificJar(tmpSql)) {
                sqlItera.remove();
                JarFileInfo jarFileInfo = PrepareOperator.parseJarFile(tmpSql);
                String addFilePath = jarFileInfo.getJarPath();

                String tmpJarDir = taskWorkspace + ConfigConstrant.SP + "jar";
                if (!new File(tmpJarDir).exists()) {
                    new File(tmpJarDir).mkdirs();
                }

                File jarFile = null;
                try {
                    jarFile = FlinkUtil.downloadJar(addFilePath, tmpJarDir, filesystemManager, false);
                } catch (Exception e) {
                    throw new PluginDefineException(e);
                }

                fileList.add(jarFile.getAbsolutePath());

                //更改路径为本地路径
                jarFileInfo.setJarPath(jarFile.getAbsolutePath());

                if (jobClient.getJobType() == EJobType.SQL) {
                    jobClient.addAttachJarInfo(jarFileInfo);
                } else {
                    //非sql任务只允许提交一个附件包
                    jobClient.setCoreJarInfo(jarFileInfo);
                    break;
                }
            }
        }

        cacheFile.put(taskWorkspace, fileList);
        String newSql = String.join(";", sqlList);
        jobClient.setSql(newSql);
    }

    @Override
    public void afterSubmitFunc(JobClient jobClient) {
        String taskWorkspace = FlinkUtil.getTaskWorkspace(jobClient.getJobId());
        cacheFile.remove(taskWorkspace);
        File localDir = new File(taskWorkspace);
        if (localDir.exists()){
            try {
                FileUtils.deleteDirectory(localDir);
            } catch (IOException e) {
                logger.error("Delete dir failed: " + e);
            }
        }
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getJobId();
        String checkpointMsg = "";
        String engineJobId = jobIdentifier.getEngineJobId();
        if (StringUtils.isEmpty(engineJobId)) {
            logger.warn("{} getCheckpoints is null, because engineJobId is empty", jobId);
            return checkpointMsg;
        }

        RdosTaskStatus taskStatus = RdosTaskStatus.NOTFOUND;
        try {
            String checkpointUrlPath = String.format(ConfigConstrant.JOB_CHECKPOINTS_URL_FORMAT, engineJobId);
            taskStatus = getJobStatus(jobIdentifier);
            Boolean isEndStatus = IS_END_STATUS.test(taskStatus);
            Boolean isPerjob = EDeployMode.PERJOB.getType().equals(jobIdentifier.getDeployMode());
            if (isPerjob && isEndStatus) {
                checkpointMsg = getMessageFromJobArchive(engineJobId, checkpointUrlPath);
            } else {
                ClusterClient currClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
                String reqURL = currClient.getWebInterfaceURL();
                checkpointMsg = getMessageByHttp(checkpointUrlPath, reqURL);
            }
        } catch (Exception e) {
            logger.error("taskId: {}, taskStatus is {}, Get checkpoint error: ", jobId, taskStatus.name(), e);
        }
        logger.info("taskId: {}, getCheckpoints: {}", jobId, StringUtils.substring(checkpointMsg, 0, 200));
        return checkpointMsg;
    }

    private boolean existsJobOnFlink(String engineJobId){
        RdosTaskStatus taskStatus = getJobStatus(JobIdentifier.createInstance(engineJobId, null, null));
        if(taskStatus == null){
            return false;
        }

        if(taskStatus == RdosTaskStatus.RUNNING){
            return true;
        }

        return false;
    }

    /**
     *  shipfile模式下，插件包在flinksession启动时,已经全部上传
     * @param packagedProgram
     */
    private void clearClassPathShipfileLoadMode(PackagedProgram packagedProgram) {

        if (ConfigConstrant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(flinkConfig.getPluginLoadMode())) {
            packagedProgram.getClasspaths().clear();
        }
    }

    @Override
    public CheckResult grammarCheck(JobClient jobClient) {

        CheckResult checkResult = CheckResult.success();
        String taskId = jobClient.getJobId();
        try {
            // 1. before download jar
            beforeSubmitFunc(jobClient);

            // 2. flink sql args
            String taskWorkspace = FlinkUtil.getTaskWorkspace(jobClient.getJobId());
            List<String> args = sqlPluginInfo.buildExeArgs(jobClient);
            List<String> attachJarLists = cacheFile.get(taskWorkspace);

            List<URL> attachJarUrls = Lists.newArrayList();
            if(!CollectionUtils.isEmpty(attachJarLists)){
                args.add("-addjar");
                String attachJarStr = PublicUtil.objToString(attachJarLists);
                args.add(URLEncoder.encode(attachJarStr, Charsets.UTF_8.name()));

                attachJarUrls = attachJarLists.stream().map(k -> {
                    try {
                        return new File(k).toURL();
                    } catch (MalformedURLException e) {
                        throw new PluginDefineException(e);
                    }
                }).collect(Collectors.toList());
            }

            JarFileInfo coreJarInfo = sqlPluginInfo.createCoreJarInfo();
            jobClient.setCoreJarInfo(coreJarInfo);

            // 3. build jobGraph
            String[] programArgs = args.toArray(new String[args.size()]);
            Configuration flinkConfig = flinkClientBuilder.getFlinkConfiguration();
            PackagedProgram program = PackagedProgram.newBuilder()
                    .setJarFile(new File(coreJarInfo.getJarPath()))
                    .setUserClassPaths(attachJarUrls)
                    .setConfiguration(flinkConfig)
                    .setArguments(programArgs)
                    .build();
            PackagedProgramUtils.createJobGraph(program, flinkConfig, 1, false);

            logger.info("TaskId: {}, GrammarCheck success!", taskId);
        } catch (Exception e) {
            logger.error("TaskId: {}, GrammarCheck error: ", taskId, e);
            checkResult = CheckResult.exception(ExceptionUtil.getErrorMessage(e));
        } finally {
            try {
                afterSubmitFunc(jobClient);
            } catch (Exception e) {
            }
        }
        return checkResult;
    }
}
