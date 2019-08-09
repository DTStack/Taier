package com.dtstack.rdos.engine.execution.flink140;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.common.util.DtStringUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JarFileInfo;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobIdentifier;
import com.dtstack.rdos.engine.execution.base.JobParam;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.EJobType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.restart.IRestartStrategy;
import com.dtstack.rdos.engine.execution.flink140.enums.Deploy;
import com.dtstack.rdos.engine.execution.flink140.enums.FlinkYarnMode;
import com.dtstack.rdos.engine.execution.flink140.parser.AddJarOperator;
import com.dtstack.rdos.engine.execution.flink140.util.FLinkConfUtil;
import com.dtstack.rdos.engine.execution.flink140.util.FlinkUtil;
import com.dtstack.rdos.engine.execution.flink140.util.HadoopConf;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobSubmissionResult;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.client.program.ProgramMissingJobException;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.util.Preconditions;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterClient;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * Date: 2017/2/20
 * Company: www.dtstack.com
 * @author xuchao
 */
public class FlinkClient extends AbsClient {

    private static final Logger logger = LoggerFactory.getLogger(FlinkClient.class);

    //FIXME key值需要根据客户端传输名称调整
    private static final String FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY = "allowNonRestoredState";

    private String tmpFileDirPath = "./tmp";

    //http://${addr}/proxy/${applicationId}/
    private static final String FLINK_URL_FORMAT = "http://%s/proxy/%s/";

    private static final String YARN_RM_WEB_KEY_PREFIX = "yarn.resourcemanager.webapp.address.";

    private FlinkConfig flinkConfig;

    private org.apache.hadoop.conf.Configuration hadoopConf;

    private org.apache.hadoop.conf.Configuration yarnConf;

    private FlinkClientBuilder flinkClientBuilder;

    private SyncPluginInfo syncPluginInfo;

    private SqlPluginInfo sqlPluginInfo;

    private ClusterClient client;

    private Map<String, List<String>> cacheFile = Maps.newConcurrentMap();

    /**客户端是否处于可用状态*/
    private AtomicBoolean isClientOn = new AtomicBoolean(false);

    private ExecutorService yarnMonitorES;

    private boolean yarnCluster;

    private FlinkYarnMode flinkYarnMode;

    public static ThreadLocal<JobClient> jobClientThreadLocal = new ThreadLocal<>();

    public FlinkClient(){
        restartStrategy = new FlinkRestartStrategy();
    }

    @Override
    public void init(Properties prop) throws Exception {

        String propStr = PublicUtil.objToString(prop);
        flinkConfig = PublicUtil.jsonStrToObject (propStr, FlinkConfig.class);
        tmpFileDirPath = flinkConfig.getJarTmpDir();
        Preconditions.checkNotNull(tmpFileDirPath, "you need to set tmp file path for jar download.");

        syncPluginInfo = SyncPluginInfo.create(flinkConfig);
        sqlPluginInfo = SqlPluginInfo.create(flinkConfig);

        initHadoopConf(flinkConfig);
        flinkClientBuilder = FlinkClientBuilder.create(hadoopConf, yarnConf);
        initClient();

        yarnCluster = flinkConfig.getClusterMode().equals(Deploy.yarn.name());
        if (yarnCluster){
            //设置plugin参数
            flinkYarnMode = FlinkYarnMode.mode(flinkConfig.getFlinkYarnMode());
            FlinkResourceInfo.flinkYarnMode = flinkYarnMode;
            FlinkResourceInfo.queue = flinkConfig.getQueue();
            FlinkResourceInfo.elasticCapacity = flinkConfig.getElasticCapacity();
            FlinkResourceInfo.yarnAccepterTaskNumber = flinkConfig.getYarnAccepterTaskNumber();
            FlinkResourceInfo.yarnClient = flinkClientBuilder.getYarnClient();

            yarnMonitorES = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(), new CustomThreadFactory("flink_yarn_monitor"));
            //启动守护线程---用于获取当前application状态和更新flink对应的application
            yarnMonitorES.submit(new YarnAppStatusMonitor(this));
        }
    }

    protected void initClient(){
        client = flinkClientBuilder.create(flinkConfig);
        setClientOn(true);
    }

    private void initHadoopConf(FlinkConfig flinkConfig){
        HadoopConf customerConf = new HadoopConf();
        customerConf.initHadoopConf(flinkConfig.getHadoopConf());
        customerConf.initYarnConf(flinkConfig.getYarnConf());

        hadoopConf = customerConf.getConfiguration();
        yarnConf = customerConf.getYarnConfiguration();
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        EJobType jobType = jobClient.getJobType();
        JobResult jobResult = null;
        if(EJobType.MR.equals(jobType)){
            jobResult = submitJobWithJar(jobClient);
        }else if(EJobType.SQL.equals(jobType)){
            jobResult = submitSqlJob(jobClient);
        }else if(EJobType.SYNC.equals(jobType)){
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

        String entryPointClass = jobParam.getMainClass();//如果jar包里面未指定mainclass,需要设置该参数
        String args = jobParam.getClassArgs();
        if(StringUtils.isNotBlank(args)){
            programArgList.addAll(Arrays.asList(args.split("\\s+")));
        }

        SavepointRestoreSettings spSettings = buildSavepointSetting(jobClient);
        PackagedProgram packagedProgram = null;
        try{
            String[] programArgs = programArgList.toArray(new String[programArgList.size()]);
            packagedProgram = FlinkUtil.buildProgram(jarPath, tmpFileDirPath, classPaths, entryPointClass,
                    programArgs, spSettings, hadoopConf);
        }catch (Throwable e){
            logger.error("", e);
            return JobResult.createErrorResult(e);
        }

        //只有当程序本身没有指定并行度的时候该参数才生效
        Integer runParallelism = FlinkUtil.getJobParallelism(jobClient.getConfProperties());


        jobClientThreadLocal.set(jobClient);
        try {
            String taskId = runJob(packagedProgram, runParallelism);
            return JobResult.createSuccessResult(taskId);
        }catch (Exception e){
            logger.error("", e);
            return JobResult.createErrorResult(e);
        }finally {
            if (packagedProgram != null){
                packagedProgram.deleteExtractedLibraries();
            }
            jobClientThreadLocal.remove();
        }
    }

    public String runJob(PackagedProgram program, int parallelism) throws Exception {
        JobClient jobClient = jobClientThreadLocal.get();
        if (FlinkYarnMode.isPerJob(flinkYarnMode) && ComputeType.STREAM == jobClient.getComputeType()){
            ClusterSpecification clusterSpecification = FLinkConfUtil.createClusterSpecification(flinkClientBuilder.getFlinkConfiguration(), jobClient.getJobPriority());
            AbstractYarnClusterDescriptor descriptor = flinkClientBuilder.createPerJobClusterDescriptor(flinkConfig, jobClient.getTaskId());
            descriptor.setName(jobClient.getJobName());
            YarnClusterClient cluster = null;
            try {
                cluster = descriptor.deploySessionCluster(clusterSpecification);
                cluster.setDetached(true);
                cluster.run(program, parallelism);

            } catch (RuntimeException e){
                logger.info("Couldn't deploy Yarn session cluster", e.getMessage());
                throw new Exception("Couldn't deploy Yarn session cluster" + e.getMessage());
            } catch (ProgramInvocationException | ProgramMissingJobException e) {
                logger.info("Job run failure!", e);
                throw new Exception("Job run failure!" + e.getMessage());
            }
            finally {
                try {
                    cluster.shutdown();
                } catch (Exception e) {
                    logger.info("Could not properly shut down the cluster.", e);
                    throw new Exception("Could not properly shut down the cluster." + e.getMessage());
                }
            }
            return cluster.getApplicationId().toString();
        } else{
            JobSubmissionResult result = client.run(program, parallelism);
            if (result.isJobExecutionResult()) {
                logger.info("Program execution finished");
                JobExecutionResult execResult = result.getJobExecutionResult();
                logger.info("Job with JobID " + execResult.getJobID() + " has finished.");
                logger.info("Job Runtime: " + execResult.getNetRuntime() + " ms");
            } else {
                logger.info("Job has been submitted with JobID " + result.getJobID());
            }
            return result.getJobID().toString();
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
            throw new RdosException("need to set compute type.");
        }

        switch (computeType){
            case BATCH:
                return submitSqlJobForBatch(jobClient);
            case STREAM:
                return submitSqlJobForStream(jobClient);

        }

        throw new RdosException("not support for compute type :" + computeType);
    }

    /**
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
            logger.info("", e);
            return JobResult.createErrorResult(e);
        }
    }

    private JobResult submitSqlJobForBatch(JobClient jobClient) {
        throw new RdosException("not support for flink batch sql now!!!");

    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();

        if (jobId.startsWith("application")){
            try {
                ApplicationId appId = ConverterUtils.toApplicationId(jobId);
                flinkClientBuilder.getYarnClient().killApplication(appId);
            } catch (Exception e) {
                return JobResult.createErrorResult(e);
            }
        } else {
            JobID jobID = new JobID(org.apache.flink.util.StringUtils.hexStringToByte(jobId));
            try{
                client.cancel(jobID);
            }catch (Exception e){
                return JobResult.createErrorResult(e);
            }
        }

        JobResult jobResult = JobResult.newInstance(false);
        jobResult.setData("jobid", jobId);
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

    	if(Strings.isNullOrEmpty(jobId)){
    		return null;
    	}

    	if (jobId.startsWith("application_")){
            ApplicationId appId = ConverterUtils.toApplicationId(jobId);
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
                        }else{
                            return RdosTaskStatus.RUNNING;
                        }

                    case FAILED:
                        return RdosTaskStatus.FAILED;
                    default:
                        throw new RdosException("Unsupported application state");
                }
            } catch (YarnException | IOException e) {
                logger.error("", e);
                return RdosTaskStatus.NOTFOUND;
            }
        }

        String reqUrl = getReqUrl() + "/jobs/" + jobId;
        String response = null;
        try{
            response = PoolHttpClient.get(reqUrl);
        } catch (RdosException e){
            //如果查询不到有可能数据被flink清除了
            if((HttpStatus.SC_NOT_FOUND + "").equals(e.getErrorMessage())){
                return RdosTaskStatus.NOTFOUND;
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }

        try{
            Map<String, Object> statusMap = PublicUtil.jsonStrToObject(response, Map.class);
            Object stateObj = statusMap.get("state");
            if(stateObj == null){
                return null;
            }

            String state = (String) stateObj;
            state = StringUtils.upperCase(state);
            RdosTaskStatus status = RdosTaskStatus.getTaskStatus(state);
            return status;
        }catch (Exception e){
            //flink-session被kill的情况下，如果找不到application或appid不对，则为失败
            //其他情况置为not find
            ApplicationId appId = ((YarnClusterClient) client).getApplicationId();
            ApplicationId yarnAppId = null;
            try {
                yarnAppId = flinkClientBuilder.acquireApplicationId(flinkConfig);
            } catch (RdosException appIdNotFindEx) {
            }
            if (yarnAppId!=null&& appId.toString().equals(yarnAppId.toString())){
                logger.error("", e);
                return RdosTaskStatus.NOTFOUND;
            } else {
                return RdosTaskStatus.FAILED;
            }

        }

    }

    /**
     * 获取jobMgr-web地址
     * @return
     */
    public String getReqUrl(){
        String url = "";
        try{
            Field yarnClientField = ((YarnClusterClient) client).getClass().getDeclaredField("yarnClient");
            yarnClientField.setAccessible(true);
            Object yarnClientObj = yarnClientField.get(client);

            Field rmClientField = yarnClientObj.getClass().getDeclaredField("rmClient");
            rmClientField.setAccessible(true);
            Object rmClient = rmClientField.get(yarnClientObj);

            Field hField = rmClient.getClass().getSuperclass().getDeclaredField("h");
            hField.setAccessible(true);
            //获取指定对象中此字段的值
            Object h = hField.get(rmClient);

            Field currentProxyField = h.getClass().getDeclaredField("currentProxy");
            currentProxyField.setAccessible(true);
            Object currentProxy = currentProxyField.get(h);

            Field proxyInfoField = currentProxy.getClass().getDeclaredField("proxyInfo");
            proxyInfoField.setAccessible(true);
            String proxyInfoKey = (String) proxyInfoField.get(currentProxy);

            String key = YARN_RM_WEB_KEY_PREFIX + proxyInfoKey;
            String addr = yarnConf.get(key);

            if(addr == null) {
                addr = yarnConf.get("yarn.resourcemanager.webapp.address");
            }

            String appId = ((YarnClusterClient) client).getApplicationId().toString();
            url = String.format(FLINK_URL_FORMAT, addr, appId);
        }catch (Exception e){
            url = client.getWebInterfaceURL();
        }

        logger.info("get req url=" + url);
        return url;
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier){
    	String url = getReqUrl();
    	return url.split("//")[1];
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
        String reqUrl = String.format("%s%s", getReqUrl(), path);
        try {
            return PoolHttpClient.get(reqUrl);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();

        if (jobId.startsWith("application_")){
            ApplicationId applicationId = ConverterUtils.toApplicationId(jobId);

            YarnLog yarnLog = new YarnLog();
            try {
                final ApplicationReport appReport = flinkClientBuilder.getYarnClient().getApplicationReport(applicationId);
                String msgInfo = appReport.getDiagnostics();
                yarnLog.addAppLog(jobId, msgInfo);
            } catch (Exception e) {
                logger.error("", e);
                yarnLog.addAppLog(jobId, "get log from yarn err:" + e.getMessage());
            }

            return yarnLog.toString();
        }

        String exceptPath = String.format(FlinkRestParseUtil.EXCEPTION_INFO, jobId);
        String except = getMessageByHttp(exceptPath);
        String jobPath = String.format(FlinkRestParseUtil.JOB_INFO, jobId);
        String jobInfo = getMessageByHttp(jobPath);
        String accuPath = String.format(FlinkRestParseUtil.JOB_ACCUMULATOR_INFO, jobId);
        String accuInfo = getMessageByHttp(accuPath);
        Map<String,String> retMap = new HashMap<>();
        retMap.put("except", except);
        retMap.put("jobInfo", jobInfo);
        retMap.put("accuInfo", accuInfo);

        try {
            return FlinkRestParseUtil.parseEngineLog(retMap);
        } catch (Throwable e) {
            logger.error("", e);
            try {
                return PublicUtil.objToString(retMap);
            } catch (IOException e1) {
                return "get engine message error," + e.getMessage();
            }
        }
    }

    @Override
    public EngineResourceInfo getAvailSlots() {

        if(!isClientOn.get()){
            return null;
        }

        String slotInfo = getMessageByHttp(FlinkRestParseUtil.SLOTS_INFO);
        FlinkResourceInfo resourceInfo = FlinkRestParseUtil.getAvailSlots(slotInfo);
        if(resourceInfo == null){
            logger.error("---flink cluster maybe down.----");
            resourceInfo = new FlinkResourceInfo();
        }

        return resourceInfo;
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

        while (sqlItera.hasNext()){
            String tmpSql = sqlItera.next();
            if(AddJarOperator.verific(tmpSql)){
                sqlItera.remove();
                JarFileInfo jarFileInfo = AddJarOperator.parseSql(tmpSql);
                String addFilePath = jarFileInfo.getJarPath();
                File tmpFile = null;
                try {
                    tmpFile = FlinkUtil.downloadJar(addFilePath, tmpFileDirPath, hadoopConf);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                fileList.add(tmpFile.getAbsolutePath());

                //更改路径为本地路径
                jarFileInfo.setJarPath(tmpFile.getAbsolutePath());

                if(jobClient.getJobType() == EJobType.SQL){
                    jobClient.addAttachJarInfo(jarFileInfo);
                }else{
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

    public void setClientOn(boolean isClientOn){
        this.isClientOn.set(isClientOn);
    }

    public boolean isClientOn(){
        return isClientOn.get();
    }

    public ClusterClient getClient() {
        return client;
    }
}
