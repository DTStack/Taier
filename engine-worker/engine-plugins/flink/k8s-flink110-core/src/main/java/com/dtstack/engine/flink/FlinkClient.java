package com.dtstack.engine.flink;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.base.filesystem.FilesystemManager;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.pojo.JobStatusFrequency;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.common.util.PublicUtil;
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
import com.dtstack.engine.flink.enums.FlinkMode;
import com.dtstack.engine.flink.factory.PerJobClientFactory;
import com.dtstack.engine.flink.parser.AddJarOperator;
import com.dtstack.engine.flink.plugininfo.SqlPluginInfo;
import com.dtstack.engine.flink.plugininfo.SyncPluginInfo;
import com.dtstack.engine.flink.storage.AbstractStorage;
import com.dtstack.engine.flink.util.*;
import com.dtstack.engine.flink.resource.FlinkK8sSeesionResourceInfo;
import com.dtstack.engine.common.client.AbstractClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.fabric8.kubernetes.api.model.ResourceQuota;
import io.fabric8.kubernetes.api.model.ResourceQuotaList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.cache.DistributedCache;
import org.apache.flink.client.ClientUtils;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.function.FunctionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.action.GetPropertyAction;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.security.AccessController.doPrivileged;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/04/03
 */
public class FlinkClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(FlinkClient.class);

    private FlinkConfig flinkConfig;

    private FlinkClientBuilder flinkClientBuilder;

    private SyncPluginInfo syncPluginInfo;

    private SqlPluginInfo sqlPluginInfo;

    private Map<String, List<String>> cacheFile = Maps.newConcurrentMap();

    private KubernetesClient kubernetesClient;

    private FlinkClusterClientManager flinkClusterClientManager;

    private AbstractStorage storage;

    private FilesystemManager filesystemManager;

    private final static Predicate<RdosTaskStatus> IS_END_STATUS = status -> RdosTaskStatus.getStoppedStatus().contains(status.getStatus()) || RdosTaskStatus.NOTFOUND.equals(status);

    @Override
    public void init(Properties prop) throws Exception {

        String propStr = PublicUtil.objToString(prop);
        flinkConfig = PublicUtil.jsonStrToObject(propStr, FlinkConfig.class);

        storage = loadStorage();
        storage.init(prop);

        filesystemManager = new FilesystemManager(storage.getStorageConfig(), flinkConfig.getSftpConf());

        FlinkUtil.downloadK8sConfig(prop, flinkConfig, filesystemManager);

        flinkClientBuilder = new FlinkClientBuilder(flinkConfig, storage, prop);
        kubernetesClient = flinkClientBuilder.getKubernetesClient();

        syncPluginInfo = SyncPluginInfo.create(flinkConfig);
        sqlPluginInfo = SqlPluginInfo.create(flinkConfig);

        flinkClusterClientManager = FlinkClusterClientManager.createWithInit(flinkClientBuilder);
    }

    private AbstractStorage loadStorage() {
        final ServiceLoader<AbstractStorage> loader = ServiceLoader.load(AbstractStorage.class);
        final Iterator<AbstractStorage> factories = loader.iterator();
        while (factories.hasNext()) {
            try {
                AbstractStorage abstractStorage = factories.next();
                Preconditions.checkNotNull(abstractStorage, "abstractStorage is null");
                return abstractStorage;
            } catch (Exception e) {
                throw new RdosDefineException(e);
            }
        }
        throw new RdosDefineException("Unsupported storage type!");
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
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
    }

    private JobResult submitJobWithJar(JobClient jobClient) {
        List<URL> classPaths = Lists.newArrayList();
        List<String> programArgList = Lists.newArrayList();
        return submitJobWithJar(jobClient, classPaths, programArgList);
    }

    private JobResult submitJobWithJar(JobClient jobClient, List<URL> classPaths, List<String> programArgList) {
        JobParam jobParam = new JobParam(jobClient);
        String jarPath = jobParam.getJarPath();
        if (jarPath == null) {
            logger.error("can not submit a job without jar path, please check it");
            return JobResult.createErrorResult("can not submit a job without jar path, please check it");
        }

        FlinkMode taskRunMode = FlinkUtil.getTaskRunMode(jobClient.getConfProperties(), jobClient.getComputeType());
        Configuration tmpConfiguration = new Configuration(flinkClientBuilder.getFlinkConfiguration());
        ClusterClient clusterClient = null;
        String monitorUrl = "";
        logger.info("clusterClient monitorUrl is {}, run mode is {}", monitorUrl, taskRunMode.name());
        try {
            if (FlinkMode.isPerJob(taskRunMode)) {
                PerJobClientFactory perJobClientFactory = flinkClusterClientManager.getPerJobClientFactory();
                clusterClient = perJobClientFactory.getClusterClient(jobClient);

                flinkClusterClientManager.addClient(clusterClient.getClusterId().toString(), clusterClient);

            } else {
                clusterClient = flinkClusterClientManager.getClusterClient(null);
            }

            Preconditions.checkNotNull(clusterClient, "clusterClient is null");
            monitorUrl = clusterClient.getWebInterfaceURL();
            logger.info("clusterClient monitorUrl is {},run mode is {}", monitorUrl, taskRunMode.name());
        } catch (Exception e) {
            logger.error("create clusterClient or getSession clusterClient error", e);
            throw new RdosDefineException(e);
        }

        PackagedProgram packagedProgram = null;
        JobGraph jobGraph = null;
        SavepointRestoreSettings spSettings = buildSavepointSetting(jobClient);
        String entryPointClass = jobParam.getMainClass();
        EJobType jobType = jobClient.getJobType();
        String[] programArgs = dealProgramArgs(programArgList, jobParam, monitorUrl, jobType);

        try {
            Integer runParallelism = FlinkUtil.getJobParallelism(jobClient.getConfProperties());
            packagedProgram = FlinkUtil.buildProgram(jarPath, classPaths, jobClient.getJobType(), entryPointClass, programArgs, spSettings, tmpConfiguration, filesystemManager);
            jobGraph = PackagedProgramUtils.createJobGraph(packagedProgram, tmpConfiguration, runParallelism, false);

            fillJobGraphClassPath(jobGraph);
            Pair<String, String> runResult = submitFlinkJob(clusterClient, jobGraph, taskRunMode);
            return JobResult.createSuccessResult(runResult.getFirst(), runResult.getSecond(), JobGraphBuildUtil.buildLatencyMarker(jobGraph));
        } catch (Exception e) {
            if (FlinkMode.isPerJob(taskRunMode)) {
                String clusterId = clusterClient.getClusterId().toString();
                flinkClientBuilder.getFlinkKubeClient().stopAndCleanupCluster(clusterId);
            }
            return JobResult.createErrorResult(e);
        } finally {
            if (packagedProgram != null) {
                packagedProgram.deleteExtractedLibraries();
            }
        }
    }

    /**
     *
     *  sync job rely on monitorUrl parameters to transmit metric information
     * @param programArgList
     * @param jobParam
     * @param monitorUrl
     * @return
     */
    private String[] dealProgramArgs(List<String> programArgList, JobParam jobParam, String monitorUrl, EJobType jobType) {
        String args = jobParam.getClassArgs();
        if (StringUtils.isNotBlank(args)) {
            programArgList.addAll(Arrays.asList(args.split("\\s+")));
        }
        String[] programArgs = programArgList.toArray(new String[programArgList.size()]);

        if (EJobType.SYNC.equals(jobType)) {
            for (int i = 0; i < args.length(); i++) {
                if ("-monitor".equals(programArgs[i])) {
                    programArgs[i + 1] = monitorUrl;
                    break;
                }
            }
        }
        return programArgs;
    }

    private Pair<String, String> submitFlinkJob(ClusterClient clusterClient, JobGraph jobGraph, FlinkMode taskRunMode) throws Exception {
        try {
            JobExecutionResult jobExecutionResult = ClientUtils.submitJob(clusterClient, jobGraph, flinkConfig.getSubmitTimeout(), TimeUnit.MINUTES);
            logger.info("Program execution finished");
            logger.info("Job with JobID " + jobExecutionResult.getJobID() + " has finished.");
            return Pair.create(jobExecutionResult.getJobID().toString(), clusterClient.getClusterId().toString());
        } catch (Exception e) {
            logger.error("submit job error: ", e);
            throw new RdosDefineException(e);
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
        String clusterId = jobIdentifier.getApplicationId();
        logger.info("cancel job clusterId is: {}", clusterId);

        ClusterClient targetClusterClient = flinkClusterClientManager.getClusterClient(jobIdentifier);

        // session mode
        Boolean isSession = StringUtils.isBlank(clusterId) || clusterId.contains("flinksession");
        try {
            RdosTaskStatus rdosTaskStatus = processJobStatus(jobIdentifier);
            if (!RdosTaskStatus.getStoppedStatus().contains(rdosTaskStatus.getStatus())) {
                JobID jobID = new JobID(org.apache.flink.util.StringUtils.hexStringToByte(jobIdentifier.getEngineJobId()));

                String savepointPath = "";

                if (jobIdentifier.isForceCancel()) {
                    flinkClientBuilder.getFlinkKubeClient().stopAndCleanupCluster(clusterId);
                } else {
                    savepointPath = targetClusterClient.stopWithSavepoint(jobID, true, null)
                            .get(jobIdentifier.getTimeout(), TimeUnit.MILLISECONDS).toString();
                }
                logger.info("Job[{}] Savepoint completed. Path:{}", jobID.toString(), savepointPath);
            }
        } catch (Exception e) {
            logger.error("Stop job error:", e);
            if (isSession) {
                logger.error("", e);
                return JobResult.createErrorResult(e);
            }
        }

        JobResult jobResult = JobResult.newInstance(false);
        jobResult.setData(JobResult.JOB_ID_KEY, jobIdentifier.getEngineJobId());
        return jobResult;
    }

    /**
     * 直接调用rest api直接返回
     *
     * @param jobIdentifier
     * @return
     */
    @Override
    public RdosTaskStatus processJobStatus(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        String clusterId = jobIdentifier.getApplicationId();

        if (StringUtils.isBlank(jobId)) {
            logger.warn("jobIdentifier:{} is blank.", jobIdentifier);
            return RdosTaskStatus.NOTFOUND;
        }
        if (!flinkClientBuilder.getFlinkKubeClient().getInternalService(clusterId).isPresent()) {
            return RdosTaskStatus.CANCELED;
        }

        String jobUrlPath = String.format(ConfigConstrant.JOB_URL_FORMAT, jobId);

        ClusterClient clusterClient = null;
        try {
            clusterClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
        } catch (Exception e) {
            logger.error("Get clusterClient error:", e);
        }

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
                response = storage.getMessageFromJobArchive(jobId, jobUrlPath);
            } catch (Exception e) {
                if (urlException != null) {
                    logger.error("Get job status error from webInterface: {}", urlException.getMessage());
                }
                logger.error("Get job status error from jobArchive: {}", e.getMessage());
            }
        }

        try {
            if (response == null) {
                throw new RdosDefineException("Get status response is null");
            }

            Map<String, Object> statusMap = PublicUtil.jsonStrToObject(response, Map.class);
            Object stateObj = statusMap.get("state");
            if (stateObj != null) {
                String state = (String) stateObj;
                state = StringUtils.upperCase(state);
                RdosTaskStatus rdosTaskStatus =  RdosTaskStatus.getTaskStatus(state);
                Boolean isFlinkSessionTask = clusterId.startsWith(ConfigConstrant.FLINK_SESSION_PREFIX);
                if (RdosTaskStatus.isStopped(rdosTaskStatus.getStatus()) && !isFlinkSessionTask) {
                    if (flinkClientBuilder.getFlinkKubeClient().getInternalService(clusterId).isPresent()) {
                        flinkClientBuilder.getFlinkKubeClient().stopAndCleanupCluster(clusterId);
                    }
                }
                return rdosTaskStatus;
            }
        } catch (Exception e) {
            logger.error("Get job status error. {}", e.getMessage());
        }
        return RdosTaskStatus.NOTFOUND;
    }

    @Override
    protected void handleJobStatus(JobIdentifier jobIdentifier, RdosTaskStatus status) {
        String jobId = jobIdentifier.getEngineJobId();
        String clusterId = jobIdentifier.getApplicationId();
        JobStatusFrequency statusFrequency = jobStatusMap.computeIfAbsent(jobId,
                k -> new JobStatusFrequency(status.getStatus()));
        if (!statusFrequency.getStatus().equals(status.getStatus())) {
            synchronized (jobId.intern()) {
                statusFrequency.resetJobStatus(status.getStatus());
            }
        }
        Double waitTime = flinkClientBuilder.getFlinkConfiguration().getDouble(ConfigConstrant.NOTFOUND_WAITTIME_key, ConfigConstrant.NOTFOUND_WAITTIME_DEFAULT);
        boolean isClear = (System.currentTimeMillis() - statusFrequency.getCreateTime()) >= waitTime;
        boolean isNotfound = statusFrequency.getStatus() == RdosTaskStatus.NOTFOUND.getStatus().intValue();

        if (isNotfound && isClear) {
            try {
                flinkClientBuilder.getFlinkKubeClient().stopAndCleanupCluster(clusterId);
            } catch (Exception e) {}
            jobStatusMap.remove(jobId);
        }
    }

    public String getReqUrl(FlinkMode flinkMode) {
        if (FlinkMode.PER_JOB == flinkMode) {
            return "${monitor}";
        } else {
            return flinkClusterClientManager.getClusterClient(null).getWebInterfaceURL();
        }
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        return flinkClusterClientManager.getClusterClient(jobIdentifier).getWebInterfaceURL();
    }

    private JobResult submitSyncJob(JobClient jobClient) {
        //使用flink作为数据同步调用的其实是提交mr--job
        JarFileInfo coreJar = syncPluginInfo.createAddJarInfo();
        jobClient.setCoreJarInfo(coreJar);

        List<String> programArgList = syncPluginInfo.createSyncPluginArgs(jobClient, this);
        List<URL> classPaths = syncPluginInfo.getClassPaths(programArgList);

        return submitJobWithJar(jobClient, classPaths, programArgList);
    }

    private String getMessageByHttp(String path, String reqURL) throws IOException {
        String reqUrl = String.format("%s%s", reqURL, path);
        return PoolHttpClient.get(reqUrl);
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {

        String engineJobId = jobIdentifier.getEngineJobId();

        String exceptionUrlPath = String.format(ConfigConstrant.JOB_EXCEPTIONS_URL_FORMAT, engineJobId);
        String accumulatorUrlPath = String.format(ConfigConstrant.JOB_ACCUMULATOR_URL_FORMAT, engineJobId);
        String exceptMessage = "";
        String accumulator = "";
        Map<String, String> retMap = Maps.newHashMap();

        try {
            RdosTaskStatus taskStatus = processJobStatus(jobIdentifier);
            Boolean isEndStatus = IS_END_STATUS.test(taskStatus);
            Boolean isPerjob = EDeployMode.PERJOB.getType().equals(jobIdentifier.getDeployMode());
            if (isPerjob && isEndStatus) {
                exceptMessage = storage.getMessageFromJobArchive(engineJobId, exceptionUrlPath);
                accumulator = storage.getMessageFromJobArchive(engineJobId, accumulatorUrlPath);
            } else {
                ClusterClient currClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
                String reqURL = currClient.getWebInterfaceURL();
                exceptMessage = getMessageByHttp(exceptionUrlPath, reqURL);
                accumulator = getMessageByHttp(accumulatorUrlPath, reqURL);
            }

            retMap.put("exception", exceptMessage);
            retMap.put("accuInfo", accumulator);
            return FlinkRestParseUtil.parseEngineLog(retMap);
        } catch (Exception e) {
            logger.error("Get job log error, {}", e.getMessage());
            Map<String, String> map = new LinkedHashMap<>(8);
            map.put("jobId", engineJobId);
            map.put("exception", ExceptionInfoConstrant.FLINK_GET_LOG_ERROR_UNDO_RESTART_EXCEPTION);
            map.put("engineLogErr", ExceptionUtil.getErrorMessage(e));
            return new Gson().toJson(map);
        }
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {

        try {
            FlinkK8sSeesionResourceInfo seesionResourceInfo = FlinkK8sSeesionResourceInfo.FlinkK8sSeesionResourceInfoBuilder()
                    .withKubernetesClient(kubernetesClient)
                    .withNamespace(flinkConfig.getNamespace())
                    .withAllowPendingPodSize(0)
                    .build();
            ResourceQuotaList resourceQuotas = kubernetesClient.resourceQuotas().inNamespace(flinkConfig.getNamespace()).list();
            if (resourceQuotas != null && resourceQuotas.getItems().size() > 0) {
                ResourceQuota resourceQuota = resourceQuotas.getItems().get(0);
                return seesionResourceInfo.judgeSlotsInNamespace(jobClient, resourceQuota);
            } else {
                seesionResourceInfo.getResource(kubernetesClient);
                return seesionResourceInfo.judgeSlots(jobClient);
            }
        } catch (Exception e) {
            logger.error("jobId:{} judgeSlots error:", jobClient.getTaskId(), e);
            return JudgeResult.exception("judgeSlots error:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    @Override
    public void beforeSubmitFunc(JobClient jobClient) {
        String sql = jobClient.getSql();
        List<String> sqlArr = DtStringUtil.splitIgnoreQuota(sql, ';');
        if (sqlArr.size() == 0) {
            return;
        }

        List<String> sqlList = Lists.newArrayList(sqlArr);

        Iterator<String> sqlItera = sqlList.iterator();
        List<String> fileList = Lists.newArrayList();
        List<String> sftpFiles = Lists.newArrayList();

        String taskWorkspace = String.format("%s/%s_%s", ConfigConstrant.TMP_DIR, jobClient.getTaskId(), Thread.currentThread().getId());
        while (sqlItera.hasNext()) {
            String tmpSql = sqlItera.next();
            // handle add jar statements and comment statements on the same line
            tmpSql = AddJarOperator.handleSql(tmpSql);
            if (AddJarOperator.verific(tmpSql)) {
                sqlItera.remove();
                JarFileInfo jarFileInfo = AddJarOperator.parseSql(tmpSql);
                String addFilePath = jarFileInfo.getJarPath();
                sftpFiles.add(addFilePath);

                String tmpJarDir = taskWorkspace + ConfigConstrant.SP + "jar";
                if (!new File(tmpJarDir).exists()) {
                    new File(tmpJarDir).mkdirs();
                }

                File tmpFile = null;
                try {
                    tmpFile = FlinkUtil.downloadJar(addFilePath, tmpJarDir, filesystemManager, false);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
                if (tmpFile == null) {
                    throw new RuntimeException("JAR file does not exist: " + addFilePath);
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
        if (CollectionUtils.isNotEmpty(sftpFiles)) {
            String sftpFileStr = String.join(";", sftpFiles);
            Properties confProps = jobClient.getConfProperties();
            confProps.setProperty(ConfigConstrant.KEY_SFTPFILES_PATH, sftpFileStr);
        }

        cacheFile.put(taskWorkspace, fileList);
        jobClient.setSql(String.join(";", sqlList));
        try {
            FlinkConfig flinkConfig = PublicUtil.jsonStrToObject(jobClient.getPluginInfo(), FlinkConfig.class);
            Properties prop = PublicUtil.stringToProperties(jobClient.getPluginInfo());
            FlinkUtil.downloadK8sConfig(prop, flinkConfig, filesystemManager);
        } catch (IOException e) {
            throw new RuntimeException("k8s config file download fail");
        }
    }

    @Override
    public void afterSubmitFunc(JobClient jobClient) {
        String taskWorkspace = String.format("%s/%s_%s", ConfigConstrant.TMP_DIR, jobClient.getTaskId(), Thread.currentThread().getId());
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
        String checkpointMsg = "";
        String engineJobId = jobIdentifier.getEngineJobId();
        if (StringUtils.isEmpty(engineJobId)) {
            logger.warn("{} getCheckpoints is null, because engineJobId is empty", jobIdentifier.getTaskId());
            return checkpointMsg;
        }

        try {
            String checkpointUrlPath = String.format(ConfigConstrant.FLINK_CP_URL_FORMAT, engineJobId);
            RdosTaskStatus taskStatus = processJobStatus(jobIdentifier);
            Boolean isEndStatus = IS_END_STATUS.test(taskStatus);
            Boolean isPerjob = EDeployMode.PERJOB.getType().equals(jobIdentifier.getDeployMode());
            if (isPerjob && isEndStatus) {
                checkpointMsg = storage.getMessageFromJobArchive(engineJobId, checkpointUrlPath);
            } else {
                ClusterClient currClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
                String reqURL = currClient.getWebInterfaceURL();
                checkpointMsg = getMessageByHttp(checkpointUrlPath, reqURL);
            }
        } catch (Exception e) {
            logger.error("Get checkpoint error, {}", e.getMessage());
        }
        return checkpointMsg;
    }

    public void fillJobGraphClassPath(JobGraph jobGraph) {
        Map<String, DistributedCache.DistributedCacheEntry> jobCacheFileConfig = jobGraph.getUserArtifacts();
        List<URL> classPath = jobCacheFileConfig.entrySet().stream()
                .filter(tmp -> tmp.getKey().startsWith("class_path"))
                .map(FunctionUtils.uncheckedFunction(tmp -> new URL("file:" + tmp.getValue().filePath)))
                .collect(Collectors.toList());

        jobGraph.getUserArtifacts().clear();
        jobGraph.setClasspaths(classPath);
    }

    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        List<String> resrult = new ArrayList<>();
        try {
            ClusterClient currClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
            String webInterfaceUrl = currClient.getWebInterfaceURL();

            String jobmanagerLogInfo = getJobmanagerLogInfo(webInterfaceUrl);
            resrult.add(jobmanagerLogInfo);

            List<String> taskmanagerLogInfos = getTaskmanagersLogInfo(webInterfaceUrl);
            resrult.addAll(taskmanagerLogInfos);
        } catch (Exception e) {
            logger.error("getRollingLogBaseInfo error {}", e);
        }

        return resrult;
    }

    private String getJobmanagerLogInfo(String webInterfaceUrl) throws IOException {
        JSONObject jobmanager = new JSONObject();
        jobmanager.put("typeName", ConfigConstrant.JOBMANAGER_COMPONENT);
        String jobmanagerUrl = String.format(ConfigConstrant.JOBMANAGER_LOG_URL_FORMAT, webInterfaceUrl);
        String jobmanagerMsg = PoolHttpClient.get(jobmanagerUrl);

        JSONObject logInfo = new JSONObject();
        logInfo.put("name", ConfigConstrant.JOBMANAGER_LOG_NAME);
        Integer totalBytes = jobmanagerMsg.length();
        logInfo.put("totalBytes", String.valueOf(totalBytes));
        logInfo.put("url", jobmanagerUrl);

        List<JSONObject> logInfos = new ArrayList<>();
        logInfos.add(logInfo);
        jobmanager.put("logs", logInfos);

        return JSONObject.toJSONString(jobmanager);
    }

    private List<String> getTaskmanagersLogInfo(String webInterfaceUrl) throws IOException {
        List<String> taskmanagerLogs = new ArrayList<>();

        String taskmanagersUrl = String.format(ConfigConstrant.TASKMANAGERS_URL_FORMAT, webInterfaceUrl);
        String taskmanagersMsg = PoolHttpClient.get(taskmanagersUrl);
        JSONObject taskmanagers = JSONObject.parseObject(taskmanagersMsg);
        if (!taskmanagers.containsKey(ConfigConstrant.TASKMANAGERS_KEY)) {
            logger.error("Get the taskmanagers but does not include the taskmanagers field! " + taskmanagersMsg);
            throw new RdosDefineException("Does not include the taskmanagers field.");
        }
        JSONArray taskmanagersInfo = taskmanagers.getJSONArray(ConfigConstrant.TASKMANAGERS_KEY);
        for(Object taskmanager : taskmanagersInfo) {
            JSONObject logInfo = new JSONObject();

            JSONObject taskmanagerJson = (JSONObject)taskmanager;
            String taskmanagerId = taskmanagerJson.getString("id");
            String logUrl = String.format(ConfigConstrant.TASKMANAGER_LOG_URL_FORMAT, webInterfaceUrl, taskmanagerId);

            JSONObject taskmanagerLogInfo = new JSONObject();
            taskmanagerLogInfo.put("url", logUrl);
            taskmanagerLogInfo.put("name", ConfigConstrant.TASKMANAGER_LOG_NAME);
            Integer totalBytes = PoolHttpClient.get(logUrl).length();
            taskmanagerLogInfo.put("totalBytes", String.valueOf(totalBytes));

            List<JSONObject> logInfos = new ArrayList<>();
            logInfos.add(taskmanagerLogInfo);

            logInfo.put("typeName", ConfigConstrant.TASKMANAGER_COMPONENT);
            logInfo.put("otherInfo", JSONObject.toJSONString(taskmanagerJson));
            logInfo.put("logs", logInfos);

            taskmanagerLogs.add(JSONObject.toJSONString(logInfo));
        }

        return taskmanagerLogs;
    }

    @Override
    public CheckResult grammarCheck(JobClient jobClient) {

        CheckResult checkResult = CheckResult.success();
        String taskId = jobClient.getTaskId();
        try {
            // 1. before download jar
            beforeSubmitFunc(jobClient);

            // 2. flink sql args
            String taskWorkspace = String.format("%s/%s_%s", ConfigConstrant.TMP_DIR, jobClient.getTaskId(), Thread.currentThread().getId());
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
                        throw new RdosDefineException(e);
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
