package com.dtstack.engine.flink;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.http.HttpClient;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.util.*;
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.JobParam;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.constrant.ExceptionInfoConstrant;
import com.dtstack.engine.flink.entity.TaskmanagerInfo;
import com.dtstack.engine.flink.enums.FlinkYarnMode;
import com.dtstack.engine.flink.factory.PerJobClientFactory;
import com.dtstack.engine.flink.parser.PrepareOperator;
import com.dtstack.engine.flink.plugininfo.SqlPluginInfo;
import com.dtstack.engine.flink.plugininfo.SyncPluginInfo;
import com.dtstack.engine.flink.resource.FlinkPerJobResourceInfo;
import com.dtstack.engine.flink.resource.FlinkYarnSeesionResourceInfo;
import com.dtstack.engine.flink.util.FileUtil;
import com.dtstack.engine.flink.util.FlinkConfUtil;
import com.dtstack.engine.flink.util.FlinkRestParseUtil;
import com.dtstack.engine.flink.util.FlinkUtil;
import com.dtstack.engine.flink.util.HadoopConf;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.worker.enums.ClassLoaderType;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
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
import org.apache.flink.configuration.HistoryServerOptions;
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
import sun.security.action.GetPropertyAction;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.security.AccessController.doPrivileged;

/**
 *
 * Date: 2017/2/20
 * Company: www.dtstack.com
 * @author xuchao
 */
public class FlinkClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(FlinkClient.class);

    //FIXME key值需要根据客户端传输名称调整
    private static final String FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY = "allowNonRestoredState";

    public final static String FLINK_CP_URL_FORMAT = "/jobs/%s/checkpoints";

    private static int MAX_RETRY_NUMBER = 2;

    private String tmpFileDirPath = "./tmp";

    private static final String USER_DIR = System.getProperty("user.dir");

    private static final String DIR = "/keytab/";
    private static final String APPLICATION_REST_API_TMP = "%s/ws/v1/cluster/apps/%s";
    private static final String CONTAINER_LOG_URL_TMP = "%s/node/containerlogs/%s/%s";
    private static final String TASK_MANAGERS_KEY = "taskmanagers";

    private static final Path tmpdir = Paths.get(doPrivileged(new GetPropertyAction("java.io.tmpdir")));

    private Properties flinkExtProp;

    private FlinkConfig flinkConfig;

    private HadoopConf hadoopConf;

    private FlinkClientBuilder flinkClientBuilder;

    private SyncPluginInfo syncPluginInfo;

    private SqlPluginInfo sqlPluginInfo;

    private Map<String, List<String>> cacheFile = Maps.newConcurrentMap();

    private FlinkClusterClientManager flinkClusterClientManager;

    private String jobHistory;

    @Override
    public void init(Properties prop) throws Exception {
        this.flinkExtProp = prop;

        String propStr = PublicUtil.objToString(prop);
        flinkConfig = PublicUtil.jsonStrToObject(propStr, FlinkConfig.class);

        tmpFileDirPath = flinkConfig.getJarTmpDir();
        Preconditions.checkNotNull(tmpFileDirPath, "you need to set tmp file path for jar download.");

        syncPluginInfo = SyncPluginInfo.create(flinkConfig);
        sqlPluginInfo = SqlPluginInfo.create(flinkConfig);

        hadoopConf = FlinkClientBuilder.initHadoopConf(flinkConfig);
        flinkClientBuilder = FlinkClientBuilder.create(flinkConfig, hadoopConf.getConfiguration(), hadoopConf.getYarnConfiguration());
        flinkClientBuilder.initFlinkGlobalConfiguration(flinkExtProp);

        flinkClusterClientManager = FlinkClusterClientManager.createWithInit(flinkClientBuilder);

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
                return jobResult;
            },hadoopConf.getYarnConfiguration());
        } catch (Exception e) {
            logger.error("can not submit a job process SubmitJobWithType error," ,e);
            return JobResult.createErrorResult(e);
        }
    }

    private JobResult submitJobWithJar(JobClient jobClient) {
        List<URL> classPaths = Lists.newArrayList();
        List<String> programArgList = Lists.newArrayList();
        return submitJobWithJar(jobClient, classPaths, programArgList);
    }

    private JobResult submitJobWithJar(JobClient jobClient, List<URL> classPaths, List<String> programArgList) {
        if (flinkConfig.isOpenKerberos()){
            FileUtil.downloadKafkaKeyTab(jobClient.getConfProperties(), flinkConfig);
            flinkClientBuilder.setSecurityConfig();
        }

        if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
            if(existsJobOnFlink(jobClient.getEngineTaskId())){
                return JobResult.createSuccessResult(jobClient.getEngineTaskId());
            }
        }

        JobParam jobParam = new JobParam(jobClient);
        String jarPath = jobParam.getJarPath();
        if(jarPath == null){
            logger.error("can not submit a job without jar path, please check it");
            return JobResult.createErrorResult("can not submit a job without jar path, please check it");
        }

        String args = jobParam.getClassArgs();
        if(StringUtils.isNotBlank(args)){
            programArgList.addAll(Arrays.asList(args.split("\\s+")));
        }
        //如果jar包里面未指定mainclass,需要设置该参数
        String entryPointClass = jobParam.getMainClass();

        FlinkYarnMode taskRunMode = FlinkUtil.getTaskRunMode(jobClient.getConfProperties(), jobClient.getComputeType());
        SavepointRestoreSettings spSettings = buildSavepointSetting(jobClient);
        String[] programArgs = programArgList.toArray(new String[programArgList.size()]);

        PackagedProgram packagedProgram = null;
        JobGraph jobGraph = null;
        Pair<String, String> runResult;

        File jarFile = null;
        try {
            if (FlinkYarnMode.isPerJob(taskRunMode)) {
                // perjob模式延后创建PackagedProgram
                jarFile = FlinkUtil.downloadJar(jarPath, tmpFileDirPath, hadoopConf.getConfiguration());
                ClusterSpecification clusterSpecification = FlinkConfUtil.createClusterSpecification(flinkClientBuilder.getFlinkConfiguration(), jobClient.getJobPriority(), jobClient.getConfProperties());
                clusterSpecification.setConfiguration(flinkClientBuilder.getFlinkConfiguration());
                clusterSpecification.setClasspaths(classPaths);
                clusterSpecification.setEntryPointClass(entryPointClass);
                clusterSpecification.setJarFile(jarFile);
                clusterSpecification.setSpSetting(spSettings);
                clusterSpecification.setProgramArgs(programArgs);
                clusterSpecification.setCreateProgramDelay(true);
                clusterSpecification.setYarnConfiguration(getYarnConf(jobClient.getPluginInfo()));
                clusterSpecification.setClassLoaderType(ClassLoaderType.getClassLoaderType(jobClient.getJobType()));

                runResult = runJobByPerJob(clusterSpecification, jobClient);
                packagedProgram = clusterSpecification.getProgram();
            } else {
                Integer runParallelism = FlinkUtil.getJobParallelism(jobClient.getConfProperties());
                packagedProgram = FlinkUtil.buildProgram(jarPath, tmpFileDirPath, classPaths, jobClient.getJobType(), entryPointClass, programArgs, spSettings, hadoopConf.getConfiguration(), flinkClientBuilder.getFlinkConfiguration());
                jobGraph = PackagedProgramUtils.createJobGraph(packagedProgram, flinkClientBuilder.getFlinkConfiguration(), runParallelism, false);

                //只有当程序本身没有指定并行度的时候该参数才生效
                clearClassPathShipfileLoadMode(packagedProgram);
                logger.info("--------job:{} run by session mode-----.", jobClient.getTaskId());

                runResult = runJobByYarnSession(jobGraph);
            }
            return JobResult.createSuccessResult(runResult.getFirst(), runResult.getSecond());
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
        logger.info("--------job:{} run by PerJob mode-----.", jobClient.getTaskId());
        YarnClusterDescriptor descriptor = PerJobClientFactory.getPerJobClientFactory().createPerJobClusterDescriptor(jobClient);
        ClusterClient<ApplicationId> clusterClient = descriptor.deployJobCluster(clusterSpecification, new JobGraph(),true).getClusterClient();

        String applicationId = clusterClient.getClusterId().toString();
        String flinkJobId = clusterSpecification.getJobGraph().getJobID().toString();

        delFilesFromDir(tmpdir, applicationId);

        flinkClusterClientManager.addClient(applicationId, clusterClient);

        return Pair.create(flinkJobId, applicationId);
    }

    /**
     * yarnSession模式运行任务
     */
    private Pair<String, String> runJobByYarnSession(JobGraph jobGraph) throws Exception {
        try {
            ClusterClient clusterClient = flinkClusterClientManager.getClusterClient();
            JobExecutionResult jobExecutionResult = ClientUtils.submitJob(clusterClient, jobGraph, flinkConfig.getSubmitTimeout(), TimeUnit.MINUTES);

            logger.info("Program execution finished");
            logger.info("Job with JobID " + jobExecutionResult.getJobID() + " has finished.");

            return Pair.create(jobExecutionResult.getJobID().toString(), null);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains(ExceptionInfoConstrant.FLINK_UNALE_TO_GET_CLUSTERCLIENT_STATUS_EXCEPTION)) {
                if (flinkClusterClientManager.getIsClientOn()) {
                    flinkClusterClientManager.setIsClientOn(false);
                }
            }
            throw e;
        } finally {
            delFilesFromDir(tmpdir, "flink-jobgraph");
        }
    }

    private YarnConfiguration getYarnConf(String pluginInfo){
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(pluginInfo).getAsJsonObject().getAsJsonObject("yarnConf");
        for (Map.Entry<String, JsonElement> keyVal : json.entrySet()) {
            conf.set(keyVal.getKey(),keyVal.getValue().getAsString());
        }

        return new YarnConfiguration(conf);
    }

    private void delFilesFromDir(Path dir ,String fileName){
        File[] jobGraphFile = dir.toFile().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(fileName);
            }
        });

        if (jobGraphFile.length != 0) {
            for (int i=0; i < jobGraphFile.length; i++) {
                jobGraphFile[i].delete();
            }
        }
    }

    private SavepointRestoreSettings buildSavepointSetting(JobClient jobClient){

        if(jobClient.getExternalPath() == null){
            return SavepointRestoreSettings.none();
        }

        String externalPath = jobClient.getExternalPath();
        boolean allowNonRestoredState = false;
        if(jobClient.getConfProperties().containsKey(FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY)){
            String allowNonRestored = (String) jobClient.getConfProperties().get(FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY);
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
            throw new RdosDefineException("need to set compute type.");
        }

        switch (computeType){
            case BATCH:
                return submitSqlJobForBatch(jobClient);
            case STREAM:
                return submitSqlJobForStream(jobClient);

        }

        throw new RdosDefineException("not support for compute type :" + computeType);
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
            //构建args
            List<String> args = sqlPluginInfo.buildExeArgs(jobClient);
            List<String> attachJarLists = cacheFile.get(jobClient.getTaskId());

            if(!CollectionUtils.isEmpty(attachJarLists)){
                args.add("-addjar");
                String attachJarStr = PublicUtil.objToString(attachJarLists);
                args.add(URLEncoder.encode(attachJarStr, Charsets.UTF_8.name()));
            }

            JarFileInfo coreJarInfo = sqlPluginInfo.createCoreJarInfo();
            jobClient.setCoreJarInfo(coreJarInfo);

            return submitJobWithJar(jobClient, Lists.newArrayList(), args);
        } catch (Exception e) {
            return JobResult.createErrorResult(e);
        }
    }

    private JobResult submitSqlJobForBatch(JobClient jobClient) {
        throw new RdosDefineException("not support for flink batch sql now!!!");
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        String appId = jobIdentifier.getApplicationId();
        try {
            RdosTaskStatus rdosTaskStatus = getJobStatus(jobIdentifier);
            //NOTFOUND 视为已经job已经结束
            if (RdosTaskStatus.NOTFOUND != rdosTaskStatus && !RdosTaskStatus.getStoppedStatus().contains(rdosTaskStatus.getStatus())) {
                ClusterClient targetClusterClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
                JobID jobId = new JobID(org.apache.flink.util.StringUtils.hexStringToByte(jobIdentifier.getEngineJobId()));

                if (StringUtils.isEmpty(appId)) {
                    // yarn session job cancel
                    targetClusterClient.cancel(jobId);
                } else {
                    // per job cancel
                    CompletableFuture completableFuture = targetClusterClient.cancelWithSavepoint(jobId, null);
                    Object ask = completableFuture.get(2, TimeUnit.MINUTES);
                    logger.info("flink job savepoint path {}", ask.toString());
                }
            }
        } catch (Exception e) {
            logger.error("cancelWithSavepoint error,will use yarn kill applicaiton", e);

            if (StringUtils.isEmpty(appId)) {
                return JobResult.createErrorResult(e);
            }

            try {
                ApplicationId applicationId = ConverterUtils.toApplicationId(appId);
                flinkClientBuilder.getYarnClient().killApplication(applicationId);
            } catch (Exception killExecption) {
                return JobResult.createErrorResult(e);
            }
        }

        JobResult jobResult = JobResult.newInstance(false);
        jobResult.setData(JobResult.JOB_ID_KEY, jobIdentifier.getEngineJobId());
        return jobResult;
    }

    /**
     * 直接调用rest api直接返回
     * @param jobIdentifier
     * @return
     */
    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        String applicationId = jobIdentifier.getApplicationId();

        if(!Strings.isNullOrEmpty(applicationId)){
            return getPerJobStatus(applicationId);
        }

        if(Strings.isNullOrEmpty(jobId)){
            return null;
        }

        String reqUrl = getReqUrl() + "/jobs/" + jobId;
        String response = null;
        try{
            response = PoolHttpClient.get(reqUrl);
        } catch (RdosDefineException e){
            return RdosTaskStatus.NOTFOUND;
        } catch (IOException e) {
            return RdosTaskStatus.NOTFOUND;
        }

        if (response == null) {
            return RdosTaskStatus.NOTFOUND;
        }

        try{
            Map<String, Object> statusMap = PublicUtil.jsonStrToObject(response, Map.class);
            Object stateObj = statusMap.get("state");
            if(stateObj == null){
                return RdosTaskStatus.NOTFOUND;
            }

            String state = (String) stateObj;
            state = StringUtils.upperCase(state);
            return RdosTaskStatus.getTaskStatus(state);
        }catch (Exception e){
            logger.error("", e);
            return RdosTaskStatus.NOTFOUND;
        }

    }

    /**
     * per-job模式其实获取的任务状态是yarn-application状态
     * @param applicationId
     * @return
     */
    public RdosTaskStatus getPerJobStatus(String applicationId){
        ApplicationId appId = ConverterUtils.toApplicationId(applicationId);
        try {
            ApplicationReport report = flinkClientBuilder.getYarnClient().getApplicationReport(appId);
            YarnApplicationState applicationState = report.getYarnApplicationState();
            switch(applicationState) {
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
                    if(finalApplicationStatus == FinalApplicationStatus.FAILED){
                        return RdosTaskStatus.FAILED;
                    }else if(finalApplicationStatus == FinalApplicationStatus.SUCCEEDED){
                        return RdosTaskStatus.FINISHED;
                    }else if(finalApplicationStatus == FinalApplicationStatus.KILLED){
                        return RdosTaskStatus.KILLED;
                    }else if(finalApplicationStatus == FinalApplicationStatus.UNDEFINED){
                        return RdosTaskStatus.FAILED;
                    }else{
                        return RdosTaskStatus.RUNNING;
                    }

                case FAILED:
                    return RdosTaskStatus.FAILED;
                default:
                    throw new RdosDefineException("Unsupported application state");
            }
        } catch (YarnException | IOException e) {
            logger.error("", e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    public String getReqUrl(FlinkYarnMode flinkYarnMode) {
        if (FlinkYarnMode.PER_JOB == flinkYarnMode){
            return "${monitor}";
        } else {
            return getReqUrl();
        }
    }

    public String getReqUrl(){
        return flinkClusterClientManager.getClusterClient().getWebInterfaceURL();
    }


    @Override
    public String getJobMaster(JobIdentifier jobIdentifier){
        ApplicationId applicationId = (ApplicationId) flinkClusterClientManager.getClusterClient(jobIdentifier).getClusterId();
        String url = null;
        try {
            url = flinkClientBuilder.getYarnClient().getApplicationReport(applicationId).getTrackingUrl();
            url = StringUtils.substringBefore(url.split("//")[1], "/");
        } catch (Exception e){
            logger.error("Getting URL failed" + e);
        }
        return url;
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
            return PoolHttpClient.get(reqUrl, null, MAX_RETRY_NUMBER);
        } catch (Exception e) {
            if(flinkClusterClientManager.getIsClientOn()){
                flinkClusterClientManager.setIsClientOn(false);
            }
            throw new RdosDefineException(ErrorCode.HTTP_CALL_ERROR, e);
        }
    }

    private String getMessageByHttp(String path, String reqURL) throws IOException {
        String reqUrl = String.format("%s%s", reqURL, path);
        return PoolHttpClient.get(reqUrl);
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();
        String applicationId = jobIdentifier.getApplicationId();

        RdosTaskStatus rdosTaskStatus = getJobStatus(jobIdentifier);
        String reqURL;

        //从jobhistory读取
        if(StringUtils.isNotBlank(applicationId) && (rdosTaskStatus.equals(RdosTaskStatus.FINISHED) || rdosTaskStatus.equals(RdosTaskStatus.CANCELED)
                || rdosTaskStatus.equals(RdosTaskStatus.FAILED) || rdosTaskStatus.equals(RdosTaskStatus.KILLED))){
            reqURL = getJobHistoryURL();
        }else{
            ClusterClient currClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
            reqURL = currClient.getWebInterfaceURL();
        }

        Map<String,String> retMap = Maps.newHashMap();

        try {
            String exceptPath = String.format(FlinkRestParseUtil.EXCEPTION_INFO, jobId);
            String except = getExceptionInfo(exceptPath, reqURL);
            retMap.put("exception", except);
            return FlinkRestParseUtil.parseEngineLog(retMap);
        } catch(RdosDefineException e){
            //http 请求失败时返回空日志
            logger.error("", e);
            return null;
        } catch (Exception e) {
            logger.error("", e);
            Map<String, String> map = new LinkedHashMap<>(8);
            map.put("jobId", jobId);
            map.put("reqURL", reqURL);
            map.put("exception", ExceptionInfoConstrant.FLINK_GET_LOG_ERROR_UNDO_RESTART_EXCEPTION);
            map.put("engineLogErr", ExceptionUtil.getErrorMessage(e));
            return new Gson().toJson(map);
        }
    }

    /**
     *  perjob模式下任务完成后进入jobHistory会有一定的时间
     */
    private String getExceptionInfo(String exceptPath, String reqURL) {
        String exceptionInfo = "";
        try {
            exceptionInfo = getMessageByHttp(exceptPath, reqURL);
            return exceptionInfo;
        } catch (Exception e) {
            logger.error("", e);
        }

        return exceptionInfo;
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {

        FlinkYarnMode taskRunMode = FlinkUtil.getTaskRunMode(jobClient.getConfProperties(), jobClient.getComputeType());
        boolean isPerJob = ComputeType.STREAM == jobClient.getComputeType() || FlinkYarnMode.isPerJob(taskRunMode);

        try {
            FlinkPerJobResourceInfo perJobResourceInfo = FlinkPerJobResourceInfo.FlinkPerJobResourceInfoBuilder()
                    .withYarnClient(flinkClientBuilder.getYarnClient())
                    .withQueueName(flinkConfig.getQueue())
                    .withYarnAccepterTaskNumber(flinkConfig.getYarnAccepterTaskNumber())
                    .build();
            perJobResourceInfo.getYarnSlots(flinkClientBuilder.getYarnClient(),
                    flinkConfig.getQueue(), flinkConfig.getYarnAccepterTaskNumber());
            JudgeResult judgeResult = perJobResourceInfo.judgeSlots(jobClient);

            if (!judgeResult.getResult() || isPerJob){
                return judgeResult;
            } else {
                if (!flinkClusterClientManager.getIsClientOn()) {
                    logger.warn("wait flink client recover...");
                    return JudgeResult.newInstance(false, "wait flink client recover");
                }
                FlinkYarnSeesionResourceInfo yarnSeesionResourceInfo = new FlinkYarnSeesionResourceInfo();
                String slotInfo = getMessageByHttp(FlinkRestParseUtil.SLOTS_INFO);
                yarnSeesionResourceInfo.getFlinkSessionSlots(slotInfo, flinkConfig.getFlinkSessionSlotCount());
                return yarnSeesionResourceInfo.judgeSlots(jobClient);
            }

        } catch (Exception e){
            logger.error("judgeSlots error:{}", e);
            return JudgeResult.newInstance(false, "judgeSlots error !");
        }
    }


    /**
     *  获取Flink任务执行时的container日志URL及字节大小
     * @return
     */
    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        String jobMaster = getJobMaster(jobIdentifier);
        String rootURL = UrlUtil.getHttpRootUrl(jobMaster);
        String amRootURl = String.format(APPLICATION_REST_API_TMP, rootURL, jobIdentifier.getApplicationId());

        List<String> result = Lists.newArrayList();
        try {
            String response = PoolHttpClient.get(amRootURl);
            if (!StringUtils.isEmpty(response)) {
                ApplicationWSParser applicationWsParser = new ApplicationWSParser(response);
                String amStatue = applicationWsParser.getParamContent(ApplicationWSParser.AM_STATUE);
                if (!StringUtils.equalsIgnoreCase(amStatue, "RUNNING")) {
                    logger.info("am statue is {} not running!", amStatue);
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
            logger.error("getRollingLogBaseInfo error", e);
        }

        return result;
    }

    private String buildContainerLogUrl(String containerHostPortFormat, String[] nameAndHost, String user) {
        logger.debug("buildContainerLogUrl name:{},host{},user{} ", nameAndHost[0], nameAndHost[1], user);
        String containerUlrPre = String.format(containerHostPortFormat, nameAndHost[1]);
        return String.format(CONTAINER_LOG_URL_TMP, containerUlrPre, nameAndHost[0], user);
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
            ApplicationWSParser.RollingBaseInfo amLogInfo = applicationWSParser.parseContainerLogBaseInfo(amContainerLogsURL, logPreURL);
            return Optional.ofNullable(JSONObject.toJSONString(amLogInfo));
        } catch (Exception e) {
            logger.error(" parse am Log error !", e);
        }
        return Optional.empty();
    }

    private List<String> parseContainersLog(ApplicationWSParser applicationWSParser, String user, String containerLogUrlFormat, String trackingUrl) throws IOException {
        List<String> taskmanagerInfoStr = Lists.newArrayList();
        try {
            List<TaskmanagerInfo> taskmanagerInfos = getContainersNameAndHost(trackingUrl);
            for (TaskmanagerInfo info : taskmanagerInfos) {
                String[] nameAndHost = parseContainerNameAndHost(info);
                String containerLogUrl = buildContainerLogUrl(containerLogUrlFormat, nameAndHost, user);
                String preUrl =  UrlUtil.getHttpRootUrl(containerLogUrl);
                logger.info("taskmanager container logs URL is: {},  preURL is :{}", containerLogUrl, preUrl);
                ApplicationWSParser.RollingBaseInfo rollingBaseInfo = applicationWSParser.parseContainerLogBaseInfo(containerLogUrl, preUrl);
                rollingBaseInfo.setOtherInfo(JSONObject.toJSONString(info));
                taskmanagerInfoStr.add(JSONObject.toJSONString(rollingBaseInfo));
            }
        } catch (Exception e) {
            logger.error(" parse taskmanager container log error !", e);
        }

        return taskmanagerInfoStr;
    }

    private List<TaskmanagerInfo> getContainersNameAndHost(String trackingUrl) throws IOException {
        List<TaskmanagerInfo> containersNameAndHost = Lists.newArrayList();
        try {
            String taskManagerUrl = trackingUrl + "/" + TASK_MANAGERS_KEY;
            String taskManagersInfo = HttpClient.get(taskManagerUrl);
            JSONObject response = JSONObject.parseObject(taskManagersInfo);
            JSONArray taskManagers = response.getJSONArray(TASK_MANAGERS_KEY);

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
        String sql = jobClient.getSql();
        List<String> sqlArr = DtStringUtil.splitIgnoreQuota(sql, ';');
        if(sqlArr.size() == 0){
            return;
        }

        List<String> sqlList = Lists.newArrayList(sqlArr);
        Iterator<String> sqlItera = sqlList.iterator();
        List<String> fileList = Lists.newArrayList();

        while (sqlItera.hasNext()) {
            String tmpSql = sqlItera.next();
            if (PrepareOperator.verificKeytab(tmpSql)) {
                sqlItera.remove();
                SFTPHandler handler = SFTPHandler.getInstance(flinkConfig.getSftpConf());
                String localDir = USER_DIR + DIR + jobClient.getTaskId();
                String localPath = handler.loadFromSftp(PrepareOperator.getFileName(tmpSql), flinkConfig.getRemoteDir(), localDir);
                logger.info("Download file to :" + localPath);
            } else if (PrepareOperator.verific(tmpSql)) {
                sqlItera.remove();
                JarFileInfo jarFileInfo = PrepareOperator.parseSql(tmpSql);
                String addFilePath = jarFileInfo.getJarPath();
                File tmpFile = null;
                try {
                    tmpFile = FlinkUtil.downloadJar(addFilePath, tmpFileDirPath, hadoopConf.getConfiguration(), flinkConfig.getSftpConf());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                fileList.add(tmpFile.getAbsolutePath());

                //更改路径为本地路径
                jarFileInfo.setJarPath(tmpFile.getAbsolutePath());

                if (jobClient.getJobType() == EJobType.SQL) {
                    jobClient.addAttachJarInfo(jarFileInfo);
                } else {
                    //非sql任务只允许提交一个附件包
                    jobClient.setCoreJarInfo(jarFileInfo);
                    break;
                }
            }
        }

        cacheFile.put(jobClient.getTaskId(), fileList);
        jobClient.setSql(String.join(";", sqlList));
    }

    @Override
    public void afterSubmitFunc(JobClient jobClient) {
        List<String> fileList = cacheFile.get(jobClient.getTaskId());
        if(CollectionUtils.isEmpty(fileList)){
            return;
        }

        //清理包含下载下来的临时jar文件
        for(String path : fileList){
            try{
                File file = new File(path);
                if(file.exists()){
                    file.delete();
                }

            }catch (Exception e1){
                logger.error("", e1);
            }
        }

        cacheFile.remove(jobClient.getTaskId());
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        String appId = jobIdentifier.getApplicationId();
        String jobId = jobIdentifier.getEngineJobId();

        RdosTaskStatus rdosTaskStatus = getJobStatus(jobIdentifier);

        String reqURL;
        if(rdosTaskStatus.equals(RdosTaskStatus.FINISHED) || rdosTaskStatus.equals(RdosTaskStatus.CANCELED)
                || rdosTaskStatus.equals(RdosTaskStatus.FAILED) || rdosTaskStatus.equals(RdosTaskStatus.KILLED)){
            reqURL = getJobHistoryURL();
        }else{
            ClusterClient currClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
            reqURL = currClient.getWebInterfaceURL();
        }

        try {
            return getMessageByHttp(String.format(FLINK_CP_URL_FORMAT, jobId), reqURL);
        } catch (IOException e) {
            logger.error("", e);
            return null;
        }
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

    private String getJobHistoryURL() {
        if (StringUtils.isNotBlank(jobHistory)) {
            return jobHistory;
        }
        String webAddress = flinkClientBuilder.getFlinkConfiguration().getValue(HistoryServerOptions.HISTORY_SERVER_WEB_ADDRESS);
        String port = flinkClientBuilder.getFlinkConfiguration().getValue(HistoryServerOptions.HISTORY_SERVER_WEB_PORT);
        if (StringUtils.isBlank(webAddress) || StringUtils.isBlank(port)) {
            throw new RdosDefineException("History Server webAddress:" + webAddress + " port:" + port);
        }
        jobHistory = String.format("http://%s:%s", webAddress, port);
        return jobHistory;
    }

    public static void main(String[] args) throws Exception {

        System.setProperty("HADOOP_USER_NAME", "admin");

        // input params json file path
        String filePath = args[0];
        File paramsFile = new File(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(paramsFile)));
        String request = reader.readLine();
        Map params =  PublicUtil.jsonStrToObject(request, Map.class);
        ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
        JobClient jobClient = new JobClient(paramAction);

        String pluginInfo = jobClient.getPluginInfo();
        Properties properties = PublicUtil.jsonStrToObject(pluginInfo, Properties.class);
        String md5plugin = MD5Util.getMd5String(pluginInfo);
        properties.setProperty("md5sum", md5plugin);

        FlinkClient client = new FlinkClient();
        client.init(properties);

        JobResult jobResult = client.submitJob(jobClient);
        String appId = jobResult.getData("extid");
        String jobId = jobResult.getData("jobid");
        System.out.println("submit success!, jobId: " + jobId + ", appId: " + appId);
        System.out.println(jobResult.getJsonStr());
        System.exit(0);
    }

}
