package com.dtstack.engine.flink;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.base.monitor.AcceptedApplicationMonitor;
import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.JobParam;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.http.HttpClient;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.util.*;
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
import com.dtstack.engine.flink.util.*;
import com.dtstack.engine.worker.enums.ClassLoaderType;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobSubmissionResult;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.configuration.HistoryServerOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.util.Preconditions;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public class FlinkClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(FlinkClient.class);

    private String tmpFileDirPath = "./tmp";

    private Properties flinkExtProp;

    private FlinkConfig flinkConfig;

    private HadoopConf hadoopConf;

    private FlinkClientBuilder flinkClientBuilder;

    private SyncPluginInfo syncPluginInfo;

    private SqlPluginInfo sqlPluginInfo;

    private Map<String, List<String>> cacheFile = Maps.newConcurrentMap();

    private FlinkClusterClientManager flinkClusterClientManager;

    private String jobHistory;

    private static final String JOBMANAGER_DIR = "jobmanager.archive.fs.dir";

    private String jobmanagerDir;


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
        flinkClientBuilder = new FlinkClientBuilder(flinkConfig, hadoopConf.getConfiguration(), hadoopConf.getYarnConfiguration());
        flinkClientBuilder.initFlinkGlobalConfiguration(flinkExtProp);

        flinkClusterClientManager = new FlinkClusterClientManager(flinkClientBuilder);

        if (flinkConfig.getMonitorAcceptedApp()) {
            AcceptedApplicationMonitor.start(hadoopConf.getYarnConfiguration(), flinkConfig.getQueue(), flinkConfig);
        }

        jobmanagerDir = prop.getProperty(JOBMANAGER_DIR);
    }


    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        try {
            return KerberosUtils.login(flinkConfig, () -> {
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
            }, hadoopConf.getYarnConfiguration());
        } catch (Exception e) {
            logger.error("can not submit a job process SubmitJobWithType error,", e);
            return JobResult.createErrorResult(e);
        }
    }

    private JobResult submitJobWithJar(JobClient jobClient) {
        List<URL> classPaths = Lists.newArrayList();
        List<String> programArgList = Lists.newArrayList();
        return submitJobWithJar(jobClient, classPaths, programArgList);
    }

    private JobResult submitJobWithJar(JobClient jobClient, List<URL> classPaths, List<String> programArgList) {
        if (flinkConfig.isOpenKerberos()) {
            FileUtil.downloadKafkaKeyTab(jobClient, flinkConfig);
        }

        if (StringUtils.isNotBlank(jobClient.getEngineTaskId())) {
            if (existsJobOnFlink(jobClient.getEngineTaskId())) {
                return JobResult.createSuccessResult(jobClient.getEngineTaskId());
            }
        }

        JobParam jobParam = new JobParam(jobClient);
        String jarPath = jobParam.getJarPath();
        if (jarPath == null) {
            logger.error("can not submit a job without jar path, please check it");
            return JobResult.createErrorResult("can not submit a job without jar path, please check it");
        }

        String args = jobParam.getClassArgs();
        if (StringUtils.isNotBlank(args)) {
            programArgList.addAll(Arrays.asList(args.split("\\s+")));
        }
        //如果jar包里面未指定mainclass,需要设置该参数
        String entryPointClass = jobParam.getMainClass();

        FlinkYarnMode taskRunMode = FlinkUtil.getTaskRunMode(jobClient.getConfProperties(), jobClient.getComputeType());
        SavepointRestoreSettings spSettings = buildSavepointSetting(jobClient);
        String[] programArgs = programArgList.toArray(new String[programArgList.size()]);

        PackagedProgram packagedProgram = null;
        File jarFile = null;
        Pair<String, String> runResult;

        try {
            if (FlinkYarnMode.isPerJob(taskRunMode)) {
                // perjob模式延后创建PackagedProgram
                jarFile = FlinkUtil.downloadJar(jarPath, tmpFileDirPath, hadoopConf.getConfiguration(), null);
                ClusterSpecification clusterSpecification = FlinkConfUtil.createClusterSpecification(flinkClientBuilder.getFlinkConfiguration(), jobClient.getApplicationPriority(), jobClient.getConfProperties());
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
                packagedProgram = FlinkUtil.buildProgram(jarPath, tmpFileDirPath, classPaths, jobClient.getJobType(), entryPointClass, programArgs, spSettings, hadoopConf.getConfiguration());
                //只有当程序本身没有指定并行度的时候该参数才生效
                Integer runParallelism = FlinkUtil.getJobParallelism(jobClient.getConfProperties());
                clearClassPathShipfileLoadMode(packagedProgram);
                logger.info("--------job:{} run by session mode-----.", jobClient.getTaskId());

                runResult = runJobByYarnSession(packagedProgram, runParallelism);
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
    private Pair<String, String> runJobByPerJob(ClusterSpecification clusterSpecification, JobClient jobClient) throws Exception {
        logger.info("--------job:{} run by PerJob mode-----.", jobClient.getTaskId());
        PerJobClientFactory perJobClientFactory = flinkClusterClientManager.getPerJobClientFactory();
        try (
                AbstractYarnClusterDescriptor descriptor = perJobClientFactory.createPerJobClusterDescriptor(jobClient);
        ) {
            descriptor.close();
            perJobClientFactory.deleteTaskIfExist(jobClient);
            ClusterClient<ApplicationId> clusterClient = descriptor.deployJobCluster(clusterSpecification, new JobGraph(), true);

            String applicationId = clusterClient.getClusterId().toString();
            String flinkJobId = clusterSpecification.getJobGraph().getJobID().toString();
            delFilesFromDir(ConfigConstrant.IO_TMPDIR, applicationId);

            flinkClusterClientManager.addClient(applicationId, clusterClient);

            return Pair.create(flinkJobId, applicationId);
        }
    }

    /**
     * yarnSession模式运行任务
     */
    private Pair<String, String> runJobByYarnSession(PackagedProgram program, int parallelism) throws Exception {
        try {
            ClusterClient clusterClient = flinkClusterClientManager.getClusterClient();
            JobSubmissionResult result = clusterClient.run(program, parallelism);

            if (result.isJobExecutionResult()) {
                logger.info("Program execution finished");
                JobExecutionResult execResult = result.getJobExecutionResult();
                logger.info("Job with JobID " + execResult.getJobID() + " has finished.");

            } else {
                logger.info("Job has been submitted with JobID " + result.getJobID());
            }

            return Pair.create(result.getJobID().toString(), null);
        } catch (Exception e) {
            //累加失败次数
            if (flinkClusterClientManager.getSessionClientFactory() != null) {
                flinkClusterClientManager.getSessionClientFactory().getSessionHealthCheckedInfo().incrSubmitError();
            }
            throw e;
        } finally {
            delFilesFromDir(ConfigConstrant.IO_TMPDIR, "flink-jobgraph");
        }
    }

    private YarnConfiguration getYarnConf(String pluginInfo) {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(pluginInfo).getAsJsonObject().getAsJsonObject("yarnConf");
        for (Map.Entry<String, JsonElement> keyVal : json.entrySet()) {
            conf.set(keyVal.getKey(), keyVal.getValue().getAsString());
        }

        return new YarnConfiguration(conf);
    }

    private void delFilesFromDir(Path dir, String fileName) {
        File[] jobGraphFile = dir.toFile().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(fileName);
            }
        });

        if (jobGraphFile.length != 0) {
            for (int i = 0; i < jobGraphFile.length; i++) {
                jobGraphFile[i].delete();
            }
        }
    }

    private SavepointRestoreSettings buildSavepointSetting(JobClient jobClient) {
        if (jobClient.getExternalPath() == null) {
            return SavepointRestoreSettings.none();
        }

        String externalPath = jobClient.getExternalPath();
        boolean allowNonRestoredState = false;
        if (jobClient.getConfProperties().containsKey(ConfigConstrant.FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY)) {
            String allowNonRestored = (String) jobClient.getConfProperties().get(ConfigConstrant.FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY);
            allowNonRestoredState = BooleanUtils.toBoolean(allowNonRestored);
        }

        return SavepointRestoreSettings.forPath(externalPath, allowNonRestoredState);
    }

    private JobResult submitSqlJob(JobClient jobClient) {

        if (StringUtils.isNotBlank(jobClient.getEngineTaskId())) {
            if (existsJobOnFlink(jobClient.getEngineTaskId())) {
                return JobResult.createSuccessResult(jobClient.getEngineTaskId());
            }
        }

        ComputeType computeType = jobClient.getComputeType();
        if (computeType == null) {
            throw new RdosDefineException("need to set compute type.");
        }

        switch (computeType) {
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
     *
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

            if (!CollectionUtils.isEmpty(attachJarLists)) {
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
        try {
            return KerberosUtils.login(flinkConfig, () -> {
                String appId = jobIdentifier.getApplicationId();
                RdosTaskStatus rdosTaskStatus = null;
                try {
                    rdosTaskStatus = getJobStatus(jobIdentifier);

                    if (rdosTaskStatus != null && !RdosTaskStatus.getStoppedStatus().contains(rdosTaskStatus.getStatus())) {
                        ClusterClient targetClusterClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
                        JobID jobId = new JobID(org.apache.flink.util.StringUtils.hexStringToByte(jobIdentifier.getEngineJobId()));

                        if (StringUtils.isEmpty(appId)) {
                            // yarn session job cancel
                            targetClusterClient.cancelWithSavepoint(jobId, null);
                        } else {
                            // per job cancel
                            if(jobIdentifier.isForceCancel()){
                                return killApplication(jobIdentifier);
                            }
                            String savepoint = targetClusterClient.cancelWithSavepoint(jobId, null);
                            logger.info("jobId: {},flink job savepoint path {}", jobId, savepoint);
                        }
                    }
                    return JobResult.createSuccessResult(jobIdentifier.getEngineJobId());
                } catch (Exception e) {

                    if (rdosTaskStatus != null){
                        logger.warn("current jobId is : {} and jobStatus is: {}", jobIdentifier.getTaskId(), rdosTaskStatus.name());
                    }

                    logger.error("jobId:{} engineJobId:{} applicationId:{} cancelJob error, try to cancel with yarnClient.", jobIdentifier.getTaskId(), jobIdentifier.getEngineJobId(), jobIdentifier.getApplicationId(), e);
                    return JobResult.createErrorResult(e);
                }
            }, hadoopConf.getYarnConfiguration());
        } catch (Exception exception) {
            logger.error("jobId:{} engineJobId:{} applicationId:{} cancelJob error:", jobIdentifier.getTaskId(), jobIdentifier.getEngineJobId(), jobIdentifier.getApplicationId(), exception);
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
            logger.info("jobId:{} engineJobId:{} applicationId:{} yarnClient kill application success.", jobIdentifier.getTaskId(), jobIdentifier.getEngineJobId(), jobIdentifier.getApplicationId());
            return JobResult.createSuccessResult(jobIdentifier.getEngineJobId());
        } catch (Exception killException) {
            logger.error("jobId:{} engineJobId:{} applicationId:{} yarnClient kill application error:", jobIdentifier.getTaskId(), jobIdentifier.getEngineJobId(), jobIdentifier.getApplicationId(), killException);
            return JobResult.createErrorResult(killException);
        }
    }

    /**
     * 直接调用rest api直接返回
     *
     * @param jobIdentifier
     * @return
     */
    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        String applicationId = jobIdentifier.getApplicationId();

        if (StringUtils.isBlank(jobId)) {
            logger.warn("jobIdentifier:{} is blank.", jobIdentifier);
            return RdosTaskStatus.NOTFOUND;
        }

        ClusterClient clusterClient = null;
        try {
            clusterClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
        } catch (Exception e) {
            logger.error("Get clusterClient error: {}", e.getMessage());
        }

        String response = null;
        if (clusterClient != null) {
            try {
                String webInterfaceURL = clusterClient.getWebInterfaceURL();
                String jobUrl = String.format("%s/jobs/%s", webInterfaceURL, jobId);
                response = PoolHttpClient.get(jobUrl);
            } catch (IOException e) {
                logger.error("request job status error: {}", e.getMessage());
            }
        }

        if (StringUtils.isEmpty(response)) {
            try {
                String jobHistoryURL = getJobHistoryURL();
                String jobUrl = String.format("%s/jobs/%s", jobHistoryURL, jobId);
                response = PoolHttpClient.get(jobUrl);
            } catch (IOException e) {
                logger.error("request job status error from jobHistory: {}", e.getMessage());
            }
        }

        if (StringUtils.isEmpty(response)) {
            if (StringUtils.isNotEmpty(applicationId)) {
                return getPerJobStatus(applicationId);
            }
            return RdosTaskStatus.NOTFOUND;
        }

        try {
            Map<String, Object> statusMap = PublicUtil.jsonStrToObject(response, Map.class);
            Object stateObj = statusMap.get("state");
            if (stateObj == null) {
                return RdosTaskStatus.NOTFOUND;
            }

            String state = (String) stateObj;
            state = StringUtils.upperCase(state);
            return RdosTaskStatus.getTaskStatus(state);
        } catch (Exception e) {
            logger.error("", e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    /**
     * per-job模式其实获取的任务状态是yarn-application状态
     *
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
                            throw new RdosDefineException("Unsupported application state");
                    }
                } catch (YarnException | IOException e) {
                    logger.error("", e);
                    return RdosTaskStatus.NOTFOUND;
                }
            }, hadoopConf.getYarnConfiguration());
        } catch (Exception e) {
            logger.error("", e);
            //防止因为kerberos 认证不过出现notfound最后变为failed
            return RdosTaskStatus.RUNNING;
        }
    }

    public String getReqUrl(FlinkYarnMode flinkYarnMode) {
        if (FlinkYarnMode.PER_JOB == flinkYarnMode) {
            return "${monitor}";
        } else {
            return getReqUrl();
        }
    }

    public String getReqUrl() {
        return flinkClusterClientManager.getClusterClient().getWebInterfaceURL();
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
            throw new RdosDefineException(ErrorCode.HTTP_CALL_ERROR, e);
        }
    }

    private String getMessageByHttp(String path, String reqURL) {
        try {
            String reqUrl = String.format("%s%s", reqURL, path);
            return PoolHttpClient.get(reqUrl);
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();
        String applicationId = jobIdentifier.getApplicationId();

        RdosTaskStatus rdosTaskStatus = getJobStatus(jobIdentifier);
        String reqURL;

        if(StringUtils.isNotBlank(applicationId) && (rdosTaskStatus.equals(RdosTaskStatus.FINISHED) || rdosTaskStatus.equals(RdosTaskStatus.CANCELED)
                || rdosTaskStatus.equals(RdosTaskStatus.FAILED) || rdosTaskStatus.equals(RdosTaskStatus.KILLED))){
            //perjob从jobhistory读取
            reqURL = getJobHistoryURL();
        }else{
            //session
            ClusterClient currClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
            reqURL = currClient.getWebInterfaceURL();
        }

        try {
            String except = getExceptionInfo(jobId, reqURL);
            return FlinkRestParseUtil.parseEngineLog(except);
        } catch (Exception e) {
            logger.error("", e);
            Map<String, String> map = new LinkedHashMap<>(8);
            map.put("jobId", jobId);
            map.put("exception", ExceptionInfoConstrant.FLINK_GET_LOG_ERROR_UNDO_RESTART_EXCEPTION);
            map.put("reqURL", reqURL);
            map.put("engineLogErr", ExceptionUtil.getErrorMessage(e));
            return new Gson().toJson(map);
        }
    }

    /**
     *  perjob模式下任务完成后进入jobHistory会有一定的时间
     */
    private String getExceptionInfo(String jobId, String reqURL) {
        String exceptPath = String.format(FlinkRestParseUtil.EXCEPTION_INFO, jobId);
        String exceptionInfo = getMessageByHttp(exceptPath, reqURL);

        if (exceptionInfo == null) {
            exceptionInfo = getExceptionFromHdfsCompleted(jobId);
        }

        return exceptionInfo;
    }

    private String getExceptionFromHdfsCompleted(String jobId) {
        try {
            String exceptPath = jobmanagerDir + ConfigConstrant.SP + jobId;
            JsonObject exceptJson = FileUtil.readJsonFromHdfs(exceptPath, hadoopConf.getConfiguration());
            JsonArray jsonArray = exceptJson.get("archive").getAsJsonArray();

            for (JsonElement ele: jsonArray) {
                JsonObject obj = ele.getAsJsonObject();
                if (obj.get("path").getAsString().endsWith("exceptions")) {
                    String exception = obj.get("json").getAsString();
                    return exception;
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {

        FlinkYarnMode taskRunMode = FlinkUtil.getTaskRunMode(jobClient.getConfProperties(), jobClient.getComputeType());
        boolean isPerJob = ComputeType.STREAM == jobClient.getComputeType() || FlinkYarnMode.isPerJob(taskRunMode);

        try {
            JudgeResult judgeResult = KerberosUtils.login(flinkConfig, () -> {
                FlinkPerJobResourceInfo perJobResourceInfo = FlinkPerJobResourceInfo.FlinkPerJobResourceInfoBuilder()
                        .withYarnClient(flinkClientBuilder.getYarnClient())
                        .withQueueName(flinkConfig.getQueue())
                        .withYarnAccepterTaskNumber(flinkConfig.getYarnAccepterTaskNumber())
                        .build();

                return perJobResourceInfo.judgeSlots(jobClient);
            }, hadoopConf.getYarnConfiguration());

            if (!judgeResult.available() || isPerJob) {
                return judgeResult;
            } else {
                if (flinkClusterClientManager.getSessionClientFactory()!=null&&
                        !flinkClusterClientManager.getSessionClientFactory().getSessionHealthCheckedInfo().isRunning()) {
                    logger.warn("wait flink session client recover...");
                    return JudgeResult.notOk("wait flink session client recover");
                }
                FlinkYarnSeesionResourceInfo yarnSeesionResourceInfo = new FlinkYarnSeesionResourceInfo();
                String slotInfo = getMessageByHttp(FlinkRestParseUtil.SLOTS_INFO);
                yarnSeesionResourceInfo.getFlinkSessionSlots(slotInfo, flinkConfig.getFlinkSessionSlotCount());
                return yarnSeesionResourceInfo.judgeSlots(jobClient);
            }
        } catch (Exception e) {
            logger.error("judgeSlots error:{}", e);
            return JudgeResult.notOk("judgeSlots error");
        }
    }

    /**
     * 获取Flink任务执行时的container日志URL及字节大小
     *
     * @return
     */
    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
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
        return String.format(ConfigConstrant.YARN_CONTAINER_LOG_URL_FORMAT, containerUlrPre, nameAndHost[0], user);
    }

    /**
     * parse am log
     *
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
            logger.error(" parse am Log error !", e);
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
                String preUrl = UrlUtil.getHttpRootUrl(containerLogUrl);
                logger.info("taskmanager container logs URL is: {},  preURL is :{}", containerLogUrl, preUrl);
                ApplicationWSParser.RollingBaseInfo rollingBaseInfo = applicationWSParser.parseContainerLogBaseInfo(containerLogUrl, preUrl, ConfigConstrant.TASKMANAGER_COMPONEN, yarnConfig);
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
            String taskManagerUrl = trackingUrl + "/" + ConfigConstrant.TASKMANAGERS_KEY;
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
        logger.info("Job[{}] submit before", jobClient.getTaskId());

        String sql = jobClient.getSql();
        List<String> sqlArr = DtStringUtil.splitIgnoreQuota(sql, ';');
        if (sqlArr.size() == 0) {
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
                String localDir = ConfigConstrant.LOCAL_KEYTAB_DIR_PARENT + ConfigConstrant.SP + jobClient.getTaskId();

                if (!new File(localDir).exists()) {
                    new File(localDir).mkdirs();
                }

                String keytabFileName = PrepareOperator.getFileName(tmpSql);
                File keytabFile = new File(keytabFileName);
                keytabFileName = keytabFile.getName();
                String remoteDir = keytabFile.getParent();

                String localPath = handler.loadFromSftp(keytabFileName, remoteDir, localDir);
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
        String newSql = String.join(";", sqlList);
        jobClient.setSql(newSql);
    }

    @Override
    public void afterSubmitFunc(JobClient jobClient) {
        List<String> fileList = cacheFile.get(jobClient.getTaskId());

        if (null != fileList) {
            //清理包含下载下来的临时jar文件
            for (String path : fileList) {
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                } catch (Exception e1) {
                    logger.error("", e1);
                }
            }
        }


        cacheFile.remove(jobClient.getTaskId());

        String localDirStr = ConfigConstrant.LOCAL_KEYTAB_DIR_PARENT + ConfigConstrant.SP + jobClient.getTaskId();
        File localDir = new File(localDirStr);
        if (localDir.exists()) {
            try {
                FileUtils.deleteDirectory(localDir);
            } catch (IOException e) {
                logger.error("Delete dir failed: " + e);
            }
        }
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        String appId = jobIdentifier.getApplicationId();
        String jobId = jobIdentifier.getEngineJobId();

        RdosTaskStatus rdosTaskStatus = getJobStatus(jobIdentifier);

        String reqURL;
        if (rdosTaskStatus.equals(RdosTaskStatus.FINISHED) || rdosTaskStatus.equals(RdosTaskStatus.CANCELED)
                || rdosTaskStatus.equals(RdosTaskStatus.FAILED) || rdosTaskStatus.equals(RdosTaskStatus.KILLED)) {
            reqURL = getJobHistoryURL();
        } else {
            ClusterClient currClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
            reqURL = currClient.getWebInterfaceURL();
        }

        return getMessageByHttp(String.format(ConfigConstrant.FLINK_CP_URL_FORMAT, jobId), reqURL);
    }

    private boolean existsJobOnFlink(String engineJobId) {
        RdosTaskStatus taskStatus = getJobStatus(JobIdentifier.createInstance(engineJobId, null, null));
        if (taskStatus == null) {
            return false;
        }

        if (taskStatus == RdosTaskStatus.RUNNING) {
            return true;
        }

        return false;
    }

    /**
     * shipfile模式下，插件包在flinksession启动时,已经全部上传
     *
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
        Map params = PublicUtil.jsonStrToObject(request, Map.class);
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
        logger.info("submit success!, jobId: " + jobId + ", appId: " + appId);
        logger.info(jobResult.getJsonStr());
        System.exit(0);
    }

}
