package com.dtstack.taier.flink;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.base.filesystem.FilesystemManager;
import com.dtstack.taier.base.util.HadoopUtils;
import com.dtstack.taier.base.util.HttpClientUtil;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.flink.base.enums.ClusterMode;
import com.dtstack.taier.flink.client.AbstractClientManager;
import com.dtstack.taier.flink.client.ClientManagerBuilder;
import com.dtstack.taier.flink.config.FlinkConfig;
import com.dtstack.taier.flink.config.PluginConfig;
import com.dtstack.taier.flink.constant.ConfigConstant;
import com.dtstack.taier.flink.constant.ErrorMessageConstant;
import com.dtstack.taier.flink.info.resource.FlinkPerJobResourceInfo;
import com.dtstack.taier.flink.info.resource.FlinkSessionResourceInfo;
import com.dtstack.taier.flink.info.resource.TaskManagerInfo;
import com.dtstack.taier.flink.perjob.client.PerJobClientManager;
import com.dtstack.taier.flink.util.*;
import com.dtstack.taier.pluginapi.JarFileInfo;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.client.AbstractClient;
import com.dtstack.taier.pluginapi.constrant.JobResultConstant;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.http.PoolHttpClient;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.util.DtStringUtil;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.pluginapi.util.UrlUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.ClientUtils;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.ResourceManagerOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @program: engine-plugins
 * @author: xiuzhu
 * @create: 2021/07/15
 */

public class FlinkClient extends AbstractClient {

    static final Logger LOG = LoggerFactory.getLogger(FlinkClient.class);

    private FlinkConfig flinkConfig;

    private Properties flinkExtProp;

    private PluginConfig pluginConfig;

    /** client that submit the job */
    private AbstractClientManager clientManager;

    private FilesystemManager filesystemManager;

    private final Map<String, List<String>> cacheFile = Maps.newConcurrentMap();

    private final static Predicate<TaskStatus> IS_END_STATUS =
            status -> TaskStatus.getStoppedStatus().contains(status.getStatus())
                    || TaskStatus.NOTFOUND.equals(status);


    @Override
    public void init(Properties pluginInfo) throws Exception {
        LOG.info("==> init Flink client");
        flinkExtProp = pluginInfo;
        flinkConfig = PublicUtil.jsonStrToObject(
                PublicUtil.objToString(flinkExtProp),
                FlinkConfig.class);

        pluginConfig = PluginConfig.newInstance(flinkConfig);
        FlinkUtil.fillFlinkxToClassLoader(pluginConfig);

        clientManager = ClientManagerBuilder
                .newInstance(flinkConfig, flinkExtProp)
                .build();
        filesystemManager = new FilesystemManager(
                clientManager.getHadoopConfig().getCoreConfiguration(),
                flinkConfig.getSftpConf());
        LOG.info("<== init Flink client");
    }

    @Override
    protected void beforeSubmitFunc(JobClient jobClient) {
        LOG.info("Job[{}] submit before", jobClient.getJobId());
        String sql = jobClient.getSql();
        List<String> sqlArr = DtStringUtil.splitIgnoreQuota(sql, ';');
        if(sqlArr.size() == 0){
            return;
        }

        List<String> sqlList = Lists.newArrayList(sqlArr);
        Iterator<String> sqlIterator = sqlList.iterator();
        List<String> fileList = Lists.newArrayList();

        String taskWorkspace = FlinkUtil.getTaskWorkspace(jobClient.getJobId());
        while (sqlIterator.hasNext()) {
            String tmpSql = sqlIterator.next();
            // handle add jar statements and comment statements on the same line
            tmpSql = FileParserHelper.handleSql(tmpSql);
            if (FileParserHelper.verifyResource(tmpSql)) {
                sqlIterator.remove();
                String localResourceDir = taskWorkspace + ConfigConstant.SP + "resource";

                if (!new File(localResourceDir).exists()) {
                    boolean succeed = new File(localResourceDir).mkdirs();
                    if(!succeed){
                        throw new PluginDefineException(
                                jobClient.getJobType() + " failed to create directory of " + localResourceDir);
                    }
                }

                File resourceFile = FileParserHelper.getResourceFile(tmpSql);
                String resourceFileName = FileParserHelper.getResourceFileName(tmpSql);

                String remoteFile = resourceFile.getAbsolutePath();
                String localFile = localResourceDir + ConfigConstant.SP + resourceFileName;
                //download file and close
                File downloadFile = filesystemManager.downloadFile(remoteFile, localFile);
                LOG.info("Download Resource File : " + downloadFile.getAbsolutePath());
            } else if (FileParserHelper.verifyJar(tmpSql)) {
                sqlIterator.remove();
                JarFileInfo jarFileInfo = FileParserHelper.parseJarFile(tmpSql);
                String addFilePath = jarFileInfo.getJarPath();

                String tmpJarDir = taskWorkspace + ConfigConstant.SP + "jar";
                if (!new File(tmpJarDir).exists()) {
                    boolean succeed = new File(tmpJarDir).mkdirs();
                    if(!succeed){
                        throw new PluginDefineException(
                                jobClient.getJobType() + " failed to create directory of " + tmpJarDir);
                    }
                }

                File jarFile;
                try {
                    jarFile = FlinkUtil.downloadJar(addFilePath, tmpJarDir, filesystemManager, false);
                    LOG.info("Download Resource File : " + jarFile.getAbsolutePath());
                } catch (Exception e) {
                    throw new PluginDefineException(
                            jobClient.getJobId() + " failed",
                            e);
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
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        try {
            return KerberosUtils.login(flinkConfig,()->{
                try {
                    JobResult jobResult;
                    switch (jobClient.getJobType()){
                        case MR:{
                            jobResult = submitJarJob(jobClient);
                            break;
                        }
                        case SQL:{
                            jobResult = submitSqlJob(jobClient);
                            break;
                        }
                        case SYNC:{
                            jobResult = submitSyncJob(jobClient);
                            break;
                        }
                        default:{
                            throw new PluginDefineException("FlinkTask:" + jobClient.getJobId() + " use no support jobType of " + jobClient.getJobType());
                        }
                    }
                    if (jobResult != null) {
                        LOG.info("taskId: {}, submit job success, result: {}", jobClient.getJobId(), jobResult);
                    }
                    return jobResult;
                } catch (Exception e) {
                    throw new PluginDefineException(e);
                }
            }, clientManager.getHadoopConfig().getYarnConfiguration());
        } catch (Exception e) {
            String errMsg = jobClient.getJobId() + " submit job error";
            throw new PluginDefineException(errMsg, e);
        }
    }

    private JobResult submitSqlJob(JobClient jobClient) throws IOException {
        if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
            if(existsJobOnFlink(jobClient.getEngineTaskId())){
                return JobResult.createSuccessResult(jobClient.getEngineTaskId());
            }
        }
        switch (jobClient.getComputeType()){
            case BATCH:
                return submitSqlJobForBatch(jobClient);
            case STREAM:
                return submitSqlJobForStream(jobClient);
            default:
                throw new UnsupportedOperationException(
                        jobClient.getJobId() + " Unsupported compute type : " + jobClient.getComputeType());
        }
    }

    private JobResult submitSqlJobForBatch(JobClient jobClient) {
        throw new UnsupportedOperationException(jobClient.getJobId() + ": Batch flink sql is not supported");
    }

    private JobResult submitSqlJobForStream(JobClient jobClient) throws IOException {
        String taskWorkspace = FlinkUtil.getTaskWorkspace(jobClient.getJobId());
        List<String> attachJarLists = cacheFile.get(taskWorkspace);
        List<String> programArgs = pluginConfig.buildProgramArgs(jobClient);
        List<URL> attachJarUrls = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(attachJarLists)){
            programArgs.add("-addjar");
            programArgs.add(URLEncoder.encode(
                    PublicUtil.objToString(attachJarLists),
                    Charsets.UTF_8.name()));

            attachJarUrls = attachJarLists.stream().map(k -> {
                try {
                    return new File(k).toURL();
                } catch (MalformedURLException e) {
                    throw new PluginDefineException(
                            jobClient.getJobId() + " failed",
                            e);
                }
            }).collect(Collectors.toList());
        }

        jobClient.setCoreJarInfo(pluginConfig.getCoreJarInfo());
        return submitJobWithJar(jobClient,
                attachJarUrls,
                programArgs);
    }

    private JobResult submitSyncJob(JobClient jobClient) throws IOException {
        jobClient.setCoreJarInfo(pluginConfig.getCoreJarInfo());
        return submitJobWithJar(jobClient,
                Lists.newArrayList(),
                pluginConfig.buildProgramArgs(jobClient));
    }

    private JobResult submitJarJob(JobClient jobClient) {
        return submitJobWithJar(jobClient,
                Lists.newArrayList(),
                Lists.newArrayList());
    }

    private JobResult submitJobWithJar(JobClient jobClient, List<URL> classPaths, List<String> programArgList) {

        JarFileInfo jarFileInfo = jobClient.getCoreJarInfo();
        Preconditions.checkNotNull(jarFileInfo, "submit need to add jar operator.");
        String jarPath = jarFileInfo.getJarPath();
        //如果jar包里面未指定mainclass,需要设置该参数
        String entryPointClass = jarFileInfo.getMainClass();
        Preconditions.checkNotNull(jarPath, "core jar path is null.");
        String args = jobClient.getClassArgs();
        if(StringUtils.isNotBlank(args)){
            programArgList.addAll(DtStringUtil.splitIgnoreQuota(args, ' '));
        }

        SavepointRestoreSettings savepointRestoreSettings = FlinkUtil.buildSavepointSetting(jobClient);
        String[] programArgs = programArgList.toArray(new String[programArgList.size()]);

        PackagedProgram packagedProgram = null;
        JobGraph jobGraph;
        Pair<String, String> runResult;

        try {
            ClusterMode clusterMode = ClusterMode.getClusteMode(flinkConfig.getClusterMode());
            if (ClusterMode.isPerjob(clusterMode)) {
                // perjob模式延后创建PackagedProgram
                ClusterSpecification clusterSpecification = FlinkUtil.createClusterSpecification(
                        clientManager.getFlinkConfiguration(),
                        jobClient.getApplicationPriority(),
                        jobClient.getConfProperties(),
                        flinkExtProp);
                clusterSpecification.setClassPaths(classPaths);
                clusterSpecification.setEntryPointClass(entryPointClass);
                clusterSpecification.setJarFile(new File(jarPath));
                clusterSpecification.setSpSetting(savepointRestoreSettings);
                clusterSpecification.setProgramArgs(programArgs);
                clusterSpecification.setCreateProgramDelay(true);
                clusterSpecification.setYarnConfiguration(clientManager.getHadoopConfig().getYarnConfiguration());

                LOG.info("--------taskId: {} run by PerJob mode-----", jobClient.getJobId());
                runResult = runJobByPerJob(clusterSpecification, jobClient);
                jobGraph = clusterSpecification.getJobGraph();
                packagedProgram = clusterSpecification.getProgram();
            } else {
                packagedProgram = FlinkUtil.buildProgram(jarPath,
                        classPaths,
                        jobClient.getJobType(),
                        entryPointClass,
                        programArgs, savepointRestoreSettings, clientManager.getFlinkConfiguration(), filesystemManager);
                jobGraph = PackagedProgramUtils.createJobGraph(
                        packagedProgram,
                        clientManager.getFlinkConfiguration(),
                        FlinkUtil.getJobParallelism(jobClient.getConfProperties()),
                        false);
                //只有当程序本身没有指定并行度的时候该参数才生效
                clearClassPathShipFileLoadMode(packagedProgram);

                LOG.info("--------taskId: {} run by Session mode-----", jobClient.getJobId());
                runResult = runJobBySession(jobGraph);
            }

            JobResult jobResult = JobResult.createSuccessResult(runResult.getSecond(),runResult.getFirst());
            // set jobgraph
            jobResult.setExtraData(JobResultConstant.JOB_GRAPH, JobGraphBuildUtil.buildLatencyMarker(jobGraph));
            // set checkpointInterval
            long checkpointInterval = jobGraph.getCheckpointingSettings().getCheckpointCoordinatorConfiguration().getCheckpointInterval();
            checkpointInterval = checkpointInterval >= Long.MAX_VALUE ? 0 : checkpointInterval;
            jobResult.setExtraData(JobResultConstant.FLINK_CHECKPOINT, String.valueOf(checkpointInterval));

            // set job archive path
            jobResult.setExtraData(JobResultConstant.ARCHIVE, clientManager.getFlinkConfiguration().get(JobManagerOptions.ARCHIVE_DIR));

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
     * judge whether the same engineJobId job in running status
     */
    private boolean existsJobOnFlink(String engineJobId){
        TaskStatus taskStatus = getJobStatus(JobIdentifier.createInstance(engineJobId, null, null));
        if(taskStatus == null){
            return false;
        }
        return taskStatus == TaskStatus.RUNNING;
    }


    /**
     * perjob模式提交任务,提交完毕后会缓存ClusterClient
     */
    private Pair<String, String> runJobByPerJob(ClusterSpecification clusterSpecification, JobClient jobClient) throws Exception{
        PerJobClientManager clientManager = (PerJobClientManager) this.clientManager;
        try (
                YarnClusterDescriptor descriptor = clientManager.createPerJobClusterDescriptor(jobClient);
        ) {
            clientManager.deleteTaskIfExist(jobClient);
            ClusterClient<ApplicationId> clusterClient = descriptor
                    .deployJobCluster(clusterSpecification, new JobGraph(),true)
                    .getClusterClient();

            String applicationId = clusterClient.getClusterId().toString();
            String flinkJobId = clusterSpecification.getJobGraph().getJobID().toString();

            clientManager.dealWithDeployCluster(applicationId, clusterClient);
            return Pair.create(flinkJobId, applicationId);
        }
    }

    /**
     * Session模式运行任务
     */
    private Pair<String, String> runJobBySession(JobGraph jobGraph) throws Exception {
        try {
            ClusterClient clusterClient = clientManager.getClusterClient(null);
            JobExecutionResult jobExecutionResult = ClientUtils.submitJob(
                    clusterClient,
                    jobGraph,
                    flinkConfig.getSubmitTimeout(),
                    TimeUnit.MINUTES);
            return Pair.create(jobExecutionResult.getJobID().toString(), null);
        } catch (Exception e) {
            clientManager.dealWithClientError();
            throw new PluginDefineException(e);
        }
    }

    @Override
    protected void afterSubmitFunc(JobClient jobClient) {
        try {
            String taskWorkspace = FlinkUtil.getTaskWorkspace(jobClient.getJobId());
            cacheFile.remove(taskWorkspace);
            File localDir = new File(taskWorkspace);
            if (localDir.exists()){
                FileUtils.deleteDirectory(localDir);
            }
        } catch (Exception e) {
            // 文件删除失败不影响任务执行结束
            LOG.error(jobClient.getJobId() + " clear job dir failed: " + e);
        }
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
                        .withYarnClient(clientManager.getYarnClient())
                        .withQueueName(flinkConfig.getQueue())
                        .withYarnAccepterTaskNumber(flinkConfig.getYarnAccepterTaskNumber())
                        .withProperties(flinkExtProp)
                        .build();

                return perJobResourceInfo.judgeSlots(jobClient);
            }, clientManager.getHadoopConfig().getYarnConfiguration());

            if (judgeResult.available() && !isPerJob){
                judgeResult = judgeSessionSlot(jobClient, false);
            }
        } catch (Exception e){
            LOG.error("taskId:{} judgeSlots error: ", jobClient.getJobId(), e);
            judgeResult = JudgeResult.exception("judgeSlots error:" + ExceptionUtil.getErrorMessage(e));
        }
        LOG.info("taskId:{}, judgeResult: {}, reason: {}", jobClient.getJobId(), judgeResult.getResult(), judgeResult.getReason());
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
            clientManager.getClusterClient(null);
        } catch (Exception e) {
            LOG.warn("taskId: {}, wait flink session client recover: ", jobClient.getJobId(), e);
            return JudgeResult.notOk(ErrorMessageConstant.WAIT_SESSION_RECOVER);
        }
        String slotInfo = null;
        try {
            slotInfo = getMessageByHttp(FlinkUtil.SLOTS_INFO);
        } catch (Exception e) {
            LOG.error("taskId: {}, Connection to jobmanager failed, ", jobClient.getJobId(), e);
            return JudgeResult.notOk("Connection to jobmanager failed");
        }

        FlinkSessionResourceInfo yarnSessionResourceInfo = new FlinkSessionResourceInfo(standalone);
        Integer sessionSlotsLimit = standalone ? 0 :
                Integer.parseInt(flinkExtProp.getProperty(ResourceManagerOptions.MAX_SLOT_NUM.key()));
        yarnSessionResourceInfo.getFlinkSessionSlots(slotInfo, sessionSlotsLimit);
        return yarnSessionResourceInfo.judgeSlots(jobClient);
    }


    @Override
    protected TaskStatus processJobStatus(JobIdentifier jobIdentifier) {
        return super.processJobStatus(jobIdentifier);
    }

    /**
     * 直接调用rest api直接返回
     */
    @Override
    public TaskStatus getJobStatus(JobIdentifier jobIdentifier) {
        String taskId = jobIdentifier.getJobId();
        String engineJobId = jobIdentifier.getEngineJobId();
        String applicationId = jobIdentifier.getApplicationId();

        if (StringUtils.isEmpty(engineJobId)) {
            LOG.warn("{} getJobStatus is NOTFOUND, because engineJobId is empty.", taskId);
            return TaskStatus.NOTFOUND;
        }

        ClusterClient clusterClient = null;
        try {
            clusterClient = clientManager.getClusterClient(jobIdentifier);
        } catch (Exception e) {
            LOG.error("taskId: {}, get clusterClient error:", taskId, e);
        }

        String jobUrlPath = String.format(ConfigConstant.JOB_URL_FORMAT, engineJobId);
        String response = null;
        Exception urlException = null;
        if (clusterClient != null) {
            try {
                String jobUrl = clusterClient.getWebInterfaceURL() + jobUrlPath;
                response = PoolHttpClient.get(jobUrl);
            } catch (Exception e) {
                urlException = e;
            }
        }

        if (StringUtils.isEmpty(response)) {
            try {
                String archiveDir = jobIdentifier.getArchiveFsDir();
                if (StringUtils.isBlank(archiveDir)) {
                    archiveDir = clientManager.getFlinkConfiguration().get(JobManagerOptions.ARCHIVE_DIR);
                }
                response = getMessageFromJobArchive(engineJobId, jobUrlPath, archiveDir);
            } catch (Exception e) {
                if (urlException != null) {
                    LOG.error("taskId: {}, Get job status error from webInterface: ", taskId, urlException);
                }
                LOG.error("taskId: {}, request job status error from jobArchive: ", taskId, e);
            }
        }

        if (StringUtils.isEmpty(response)) {
            if (StringUtils.isNotEmpty(applicationId)) {
                TaskStatus TaskStatus = getPerJobStatus(applicationId);
                LOG.info("taskId: {}, try getPerJobStatus with yarnClient, status: {}", taskId, TaskStatus.name());
                return TaskStatus;
            }
            return TaskStatus.NOTFOUND;
        }

        try{

            Map<String, Object> statusMap = PublicUtil.jsonStrToObject(response, Map.class);
            Object stateObj = statusMap.get("state");
            if(stateObj == null){
                return TaskStatus.NOTFOUND;
            }

            String state = (String) stateObj;
            state = StringUtils.upperCase(state);
            return TaskStatus.getTaskStatus(state);
        }catch (Exception e){
            LOG.error("taskId: {}, getJobStatus error: ", taskId, e);
            return TaskStatus.NOTFOUND;
        }
    }

    /**
     * per-job模式其实获取的任务状态是yarn-application状态
     */
    public TaskStatus getPerJobStatus(String applicationId) {
        try {
            return KerberosUtils.login(flinkConfig, () -> {
                ApplicationId appId = ConverterUtils.toApplicationId(applicationId);
                try {
                    ApplicationReport report = clientManager.getYarnClient().getApplicationReport(appId);
                    YarnApplicationState applicationState = report.getYarnApplicationState();
                    switch (applicationState) {
                        case KILLED:
                            return TaskStatus.KILLED;
                        case NEW:
                        case NEW_SAVING:
                            return TaskStatus.CREATED;
                        case SUBMITTED:
                            //FIXME 特殊逻辑,认为已提交到计算引擎的状态为等待资源状态
                            return TaskStatus.WAITCOMPUTE;
                        case ACCEPTED:
                            return TaskStatus.SCHEDULED;
                        case RUNNING:
                            return TaskStatus.RUNNING;
                        case FINISHED:
                            //state 为finished状态下需要兼顾判断finalStatus.
                            FinalApplicationStatus finalApplicationStatus = report.getFinalApplicationStatus();
                            switch (finalApplicationStatus){
                                case FAILED:
                                case UNDEFINED: {
                                    return TaskStatus.FAILED;
                                }
                                case SUCCEEDED:{
                                    return TaskStatus.FINISHED;
                                }
                                case KILLED:{
                                    return TaskStatus.KILLED;
                                }
                                default:{
                                    return TaskStatus.RUNNING;
                                }
                            }
                        case FAILED:
                            return TaskStatus.FAILED;
                        default:
                            throw new PluginDefineException("Unsupported application state");
                    }
                } catch (YarnException | IOException e) {
                    LOG.error("appId: {}, getPerJobStatus with yarnClient error: ", applicationId, e);
                    return TaskStatus.NOTFOUND;
                }
            }, clientManager.getHadoopConfig().getYarnConfiguration());
        } catch (Exception e) {
            LOG.error("appId: {}, getPerJobStatus with yarnClient error: ", applicationId, e);
            //防止因为kerberos 认证不过出现notfound最后变为failed
            return TaskStatus.RUNNING;
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        String taskId = jobIdentifier.getJobId();
        String engineJobId = jobIdentifier.getEngineJobId();
        String exceptMessage;
        try {
            if (engineJobId == null) {
                LOG.error("{} getJobLog is null, because engineJobId is empty", taskId);
                return handleJobLog("", "Get jogLog error, because engineJobId is null", "Job has not submitted to yarn, Please waiting moment.");
            }
            String exceptionUrlPath = String.format(ConfigConstant.JOB_EXCEPTIONS_URL_FORMAT, engineJobId);
            TaskStatus jobStatus = getJobStatus(jobIdentifier);
            Boolean isEndStatus = IS_END_STATUS.test(jobStatus);
            Boolean isPerjob = EDeployMode.PERJOB.getType().equals(jobIdentifier.getDeployMode());

            if (isPerjob && isEndStatus) {
                String archiveDir = jobIdentifier.getArchiveFsDir();
                if (StringUtils.isBlank(archiveDir)) {
                    archiveDir = clientManager.getFlinkConfiguration().get(JobManagerOptions.ARCHIVE_DIR);
                }
                exceptMessage = getMessageFromJobArchive(engineJobId, exceptionUrlPath, archiveDir);
            } else {
                ClusterClient currClient = clientManager.getClusterClient(jobIdentifier);
                String reqURL = currClient.getWebInterfaceURL();
                exceptMessage = getMessageByHttp(exceptionUrlPath, reqURL);
            }
            String jobLogContent = FlinkUtil.parseEngineLog(exceptMessage);
            LOG.info("taskId: {}, job log content: {}", taskId, StringUtils.substring(jobLogContent, 0, 100));
            return jobLogContent;
        } catch (Exception e) {
            LOG.error("Get job log error, {}", e.getMessage());
            return handleJobLog(engineJobId, ErrorMessageConstant.FLINK_GET_LOG_ERROR_UNDO_RESTART_EXCEPTION, ExceptionUtil.getErrorMessage(e));
        }
    }

    public String getReqUrl(){
        return clientManager.getClusterClient(null).getWebInterfaceURL();
    }

    @Override
    public String getMessageByHttp(String path) {
        try {
            String reqUrl = String.format("%s%s", getReqUrl(), path);
            return doKerberosGet(reqUrl);
        } catch (Exception e) {
            throw new PluginDefineException(e);
        }
    }

    private String getMessageByHttp(String path, String reqURL) {
        try {
            String reqUrl = String.format("%s%s", reqURL, path);
            return doKerberosGet(reqUrl);
        } catch (Exception e) {
            throw new PluginDefineException(e);
        }
    }

    private String handleJobLog(String engineJobId, String exception, String exceptionErr) {
        Map<String, String> map = new LinkedHashMap<>(8);
        map.put("jobId", engineJobId);
        map.put("exception", exception);
        map.put("engineLogErr", exceptionErr);
        return new Gson().toJson(map);
    }

    /**
     *  获取Flink任务执行时的container日志URL及字节大小
     * @return logs
     */
    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        String taskId = jobIdentifier.getJobId();

        List<String> result = Lists.newArrayList();
        try {
            YarnConfiguration yarnConfig = clientManager.getHadoopConfig().getYarnConfiguration();

            String rmWebAddress = HadoopUtils.getRMWebAddress(yarnConfig, clientManager.getYarnClient());
            String amRootURl = String.format(ConfigConstant.YARN_APPLICATION_URL_FORMAT, rmWebAddress, jobIdentifier.getApplicationId());

            String response = ApplicationWSParser.getDataFromYarnRest(flinkConfig, yarnConfig, amRootURl);
            if (!StringUtils.isEmpty(response)) {
                ApplicationWSParser applicationWsParser = new ApplicationWSParser(response);
                String amStatue = applicationWsParser.getParamContent(ApplicationWSParser.AM_STATUE);
                if (!StringUtils.equalsIgnoreCase(amStatue, "RUNNING")) {
                    LOG.info("taskId: {}, am statue is {} not running!", taskId, amStatue);
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
            LOG.error("taskId:{}, getRollingLogBaseInfo error: ", taskId, e);
        }
        LOG.info("taskId: {}, getRollingLogBaseInfo: {}", taskId, result);
        return result;
    }

    /**
     *  parse am log
     */
    public Optional<String> parseAmLog(ApplicationWSParser applicationWSParser, String amContainerLogsURL) {
        try {
            String logPreURL = UrlUtil.getHttpRootUrl(amContainerLogsURL);
            LOG.info("jobmanager container logs URL is: {}, logPreURL is {} :", amContainerLogsURL, logPreURL);
            YarnConfiguration yarnConfig = clientManager.getHadoopConfig().getYarnConfiguration();
            ApplicationWSParser.RollingBaseInfo amLogInfo = applicationWSParser.parseContainerLogBaseInfo(flinkConfig, amContainerLogsURL, logPreURL, ConfigConstant.JOBMANAGER_COMPONEN, yarnConfig);
            return Optional.of(JSONObject.toJSONString(amLogInfo));
        } catch (Exception e) {
            LOG.error("parse am Log error! ", e);
        }
        return Optional.empty();
    }

    private List<String> parseContainersLog(ApplicationWSParser applicationWSParser, String user, String containerLogUrlFormat, String trackingUrl) throws IOException {
        List<String> taskManagerInfoStr = Lists.newArrayList();
        try {
            YarnConfiguration yarnConfig = clientManager.getHadoopConfig().getYarnConfiguration();
            List<TaskManagerInfo> taskmanagerInfos = getContainersNameAndHost(trackingUrl);
            for (TaskManagerInfo info : taskmanagerInfos) {
                String[] nameAndHost = parseContainerNameAndHost(info);
                String containerLogUrl = buildContainerLogUrl(containerLogUrlFormat, nameAndHost, user);
                String preUrl =  UrlUtil.getHttpRootUrl(containerLogUrl);
                LOG.info("taskManager container logs URL is: {},  preURL is :{}", containerLogUrl, preUrl);
                ApplicationWSParser.RollingBaseInfo rollingBaseInfo = applicationWSParser.parseContainerLogBaseInfo(flinkConfig, containerLogUrl, preUrl, ConfigConstant.TASKMANAGER_COMPONEN, yarnConfig);
                rollingBaseInfo.setOtherInfo(JSONObject.toJSONString(info));
                taskManagerInfoStr.add(JSONObject.toJSONString(rollingBaseInfo));
            }
        } catch (Exception e) {
            LOG.error("parse taskmanager container log error! ", e);
        }

        return taskManagerInfoStr;
    }

    private String buildContainerLogUrl(String containerHostPortFormat, String[] nameAndHost, String user) {
        LOG.debug("buildContainerLogUrl name:{},host{},user{} ", nameAndHost[0], nameAndHost[1], user);
        String containerUlrPre = String.format(containerHostPortFormat, nameAndHost[1]);
        return String.format(ConfigConstant.YARN_CONTAINER_LOG_URL_FORMAT, containerUlrPre, nameAndHost[0], user);
    }

    private List<TaskManagerInfo> getContainersNameAndHost(String trackingUrl) throws IOException {
        List<TaskManagerInfo> containersNameAndHost = Lists.newArrayList();
        try {
            String taskManagerUrl = String.format(ConfigConstant.TASKMANAGERS_URL_FORMAT, trackingUrl);
            String taskManagersInfo = doKerberosGet(taskManagerUrl);
            JSONObject response = JSONObject.parseObject(taskManagersInfo);
            JSONArray taskManagers = response.getJSONArray(ConfigConstant.TASKMANAGERS_KEY);

            containersNameAndHost = IntStream.range(0, taskManagers.size())
                    .mapToObj(taskManagers::getJSONObject)
                    .map(jsonObject -> JSONObject.toJavaObject(jsonObject, TaskManagerInfo.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("request task managers error !", e);
        }
        return containersNameAndHost;
    }

    private String[] parseContainerNameAndHost(TaskManagerInfo taskmanagerInfo) {
        String containerName = taskmanagerInfo.getId();
        String akkaPath = taskmanagerInfo.getPath();
        String host = "";
        try {
            LOG.info("parse akkaPath: {}", akkaPath);
            host = akkaPath.split("[@:]")[2];
        } catch (Exception e) {
            LOG.error("parseContainersHost error ", e);
        }
        return new String[]{containerName, host};
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {

        String taskId = jobIdentifier.getJobId();
        String engineJobId = jobIdentifier.getEngineJobId();
        String checkpointMsg = "";
        if (StringUtils.isEmpty(engineJobId)) {
            LOG.warn("{} getCheckpoints is null, because engineJobId is empty", jobIdentifier.getJobId());
            return checkpointMsg;
        }

        TaskStatus taskStatus = TaskStatus.NOTFOUND;
        try {
            String checkpointUrlPath = String.format(ConfigConstant.JOB_CHECKPOINTS_URL_FORMAT, engineJobId);
            taskStatus = getJobStatus(jobIdentifier);
            Boolean isEndStatus = IS_END_STATUS.test(taskStatus);
            Boolean isPerJob = EDeployMode.PERJOB.getType().equals(jobIdentifier.getDeployMode());
            if (isPerJob && isEndStatus) {
                String archiveDir = jobIdentifier.getArchiveFsDir();
                if (StringUtils.isBlank(archiveDir)) {
                    archiveDir = clientManager.getFlinkConfiguration().get(JobManagerOptions.ARCHIVE_DIR);
                }
                checkpointMsg = getMessageFromJobArchive(engineJobId, checkpointUrlPath, archiveDir);
            } else {
                ClusterClient currClient = clientManager.getClusterClient(jobIdentifier);
                String reqURL = currClient.getWebInterfaceURL();
                checkpointMsg = getMessageByHttp(checkpointUrlPath, reqURL);
            }
        } catch (Exception e) {
            LOG.error("taskId: {}, taskStatus is {}, Get checkpoint error: ", taskId, taskStatus.name(), e);
        }
        // todo: 为什么截取200个字符
        LOG.info("taskId: {}, getCheckpoints: {}", taskId, StringUtils.substring(checkpointMsg, 0, 200));
        return checkpointMsg;
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        try {
            return KerberosUtils.login(flinkConfig, () -> {
                String taskId = jobIdentifier.getJobId();
                String engineJobId = jobIdentifier.getEngineJobId();
                String appId = jobIdentifier.getApplicationId();
                TaskStatus TaskStatus = null;
                try {
                    TaskStatus = getJobStatus(jobIdentifier);

                    if (TaskStatus != null && !TaskStatus.getStoppedStatus().contains(TaskStatus.getStatus())) {
                        ClusterClient targetClusterClient = clientManager.getClusterClient(jobIdentifier);
                        JobID jobId = new JobID(org.apache.flink.util.StringUtils.hexStringToByte(jobIdentifier.getEngineJobId()));

                        EDeployMode deployMode = EDeployMode.getByType(jobIdentifier.getDeployMode());
                        switch (deployMode){
                            case SESSION:
                            case STANDALONE:{
                                // session job cancel
                                Object ack = targetClusterClient.cancel(jobId).get(jobIdentifier.getTimeout(), TimeUnit.MILLISECONDS);
                                LOG.info("taskId: {}, job[{}] cancel success with ack : {}", taskId, engineJobId, ack.toString());
                                break;
                            }
                            case PERJOB:{
                                // perJob cancel
                                if(jobIdentifier.isForceCancel()){
                                    return killApplication(jobIdentifier);
                                }
                                CompletableFuture completableFuture = targetClusterClient.cancelWithSavepoint(jobId, null);
                                Object ask = completableFuture.get(jobIdentifier.getTimeout(), TimeUnit.MILLISECONDS);
                                LOG.info("taskId: {}, job[{}] cancelWithSavepoint success, savepoint path {}",
                                        taskId, engineJobId, ask.toString());
                                break;
                            }
                            default:{
                                LOG.warn("taskId: {}, job[{}] cancel failed Unexpected deployMode type: {}", taskId, engineJobId, deployMode);
                            }
                        }
                    }
                    return JobResult.createSuccessResult(jobIdentifier.getEngineJobId());
                } catch (Exception e) {
                    if (TaskStatus != null){
                        LOG.warn("taskId: {}, cancel job error jobStatus is: {}", taskId, TaskStatus.name());
                    }

                    LOG.error("taskId: {} engineJobId:{} applicationId:{} cancelJob error, try to cancel with yarnClient.", taskId, engineJobId, appId, e);
                    return JobResult.createErrorResult(e);
                }
            }, clientManager.getHadoopConfig().getYarnConfiguration());
        } catch (Exception exception) {
            LOG.error("taskId: {} engineJobId: {} applicationId: {} cancelJob error: ", jobIdentifier.getJobId(), jobIdentifier.getEngineJobId(), jobIdentifier.getApplicationId(), exception);
            return JobResult.createErrorResult(exception);
        }
    }

    /**
     * force kill yarn application
     */
    private JobResult killApplication(JobIdentifier jobIdentifier){
        try {
            ApplicationId applicationId = ConverterUtils.toApplicationId(jobIdentifier.getApplicationId());
            clientManager.getYarnClient().killApplication(applicationId);
            LOG.info("jobId:{} engineJobId:{} applicationId:{} yarnClient kill application success.", jobIdentifier.getJobId(), jobIdentifier.getEngineJobId(), jobIdentifier.getApplicationId());
            return JobResult.createSuccessResult(jobIdentifier.getEngineJobId());
        } catch (Exception killException) {
            LOG.error("jobId:{} engineJobId:{} applicationId:{} yarnClient kill application error:", jobIdentifier.getJobId(), jobIdentifier.getEngineJobId(), jobIdentifier.getApplicationId(), killException);
            return JobResult.createErrorResult(killException);
        }
    }


    /**
     * find out jobManager location
     * example：
     * http://kudu1:8088/
     */
    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        try {
            return KerberosUtils.login(flinkConfig, () -> {
                ApplicationId applicationId = (ApplicationId) clientManager.getClusterClient(jobIdentifier).getClusterId();

                String url = null;
                try {
                    url = clientManager.getYarnClient().getApplicationReport(applicationId).getTrackingUrl();
                    url = StringUtils.substringBefore(url.split("//")[1], "/");
                } catch (Exception e) {
                    LOG.error(jobIdentifier.getJobId() + " Getting URL failed" + e);
                }
                return url;
            }, clientManager.getHadoopConfig().getYarnConfiguration());
        } catch (Exception e) {
            LOG.error("", e);
            return null;
        }
    }

    /**
     * should not load any plugin jar in shipFile mode since session is started
     */
    private void clearClassPathShipFileLoadMode(PackagedProgram packagedProgram) {
        if (ConfigConstant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(flinkConfig.getPluginLoadMode())) {
            packagedProgram.getClasspaths().clear();
        }
    }

    /**
     * download log file from hdfs, which placed in dir of jobmanager.archive.fs.dir
     * example:
     * {
     *     "archive":[
     *          {
     *              "path":"",
     *              "json":""
     *          },{
     *              "path":"",
     *              "json":""
     *          }
     *     ]
     * }
     */
    public String getMessageFromJobArchive(String jobId, String urlPath, String archiveDir) throws Exception {
        String jobArchivePath = archiveDir + ConfigConstant.SP + jobId;

        return KerberosUtils.login(flinkConfig, () -> {
            JsonParser jsonParser = new JsonParser();
            try (
                    InputStream is = FileUtil.readStreamFromFile(jobArchivePath,
                            clientManager.getHadoopConfig().getCoreConfiguration());
                    InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonObject jobArchiveAll = (JsonObject) jsonParser.parse(reader);
                Preconditions.checkNotNull(jobArchiveAll, jobId + "jobArchive is null");
                // todo: json解析映射应该更规范化
                JsonArray jsonArray = jobArchiveAll.getAsJsonArray("archive");
                for (JsonElement ele : jsonArray) {
                    JsonObject obj = ele.getAsJsonObject();
                    if (StringUtils.equals(obj.get("path").getAsString(), urlPath)) {
                        String exception = obj.get("json").getAsString();
                        if(StringUtils.isNotBlank(exception)){
                            return exception;
                        }
                    }
                }
                throw new PluginDefineException(String.format("Not found Message from jobArchive, jobId[%s], urlPath[%s]", jobId, urlPath));
            } catch (Exception e) {
                throw new PluginDefineException(e);
            }
        }, clientManager.getHadoopConfig().getCoreConfiguration());
    }



    private String doKerberosGet(String url) throws Exception {
        return KerberosUtils.login(
                flinkConfig, () -> {
                    String response = "";
                    try {
                        response = HttpClientUtil
                                .builder()
                                .setBaseConfig(flinkConfig)
                                .build()
                                .get(url);
                    } catch (Exception e) {
                        throw new PluginDefineException(e);
                    }
                    return response;
                }, clientManager.getHadoopConfig().getYarnConfiguration());
    }
}
