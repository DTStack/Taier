package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.commom.exception.ErrorCode;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.common.util.DtStringUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.execution.base.JarFileInfo;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobIdentifier;
import com.dtstack.rdos.engine.execution.base.JobParam;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.EJobType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.flink150.enums.Deploy;
import com.dtstack.rdos.engine.execution.flink150.enums.FlinkYarnMode;
import com.dtstack.rdos.engine.execution.flink150.parser.AddJarOperator;
import com.dtstack.rdos.engine.execution.flink150.util.FLinkConfUtil;
import com.dtstack.rdos.engine.execution.flink150.util.FlinkUtil;
import com.dtstack.rdos.engine.execution.flink150.util.HadoopConf;
import com.dtstack.rdos.engine.execution.flink150.util.KerberosUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import org.apache.flink.api.common.JobSubmissionResult;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.shaded.curator.org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.flink.util.Preconditions;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterClient;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.action.GetPropertyAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.security.AccessController.doPrivileged;

/**
 *
 * Date: 2017/2/20
 * Company: www.dtstack.com
 * @author xuchao
 */
public class FlinkClient extends AbsClient {

    private static final Logger logger = LoggerFactory.getLogger(FlinkClient.class);

    private static final String CLASS_FILE_NAME_PRESTR = "class_path";

    //FIXME key值需要根据客户端传输名称调整
    private static final String FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY = "allowNonRestoredState";

    public final static String FLINK_CP_URL_FORMAT = "/jobs/%s/checkpoints";

    private String tmpFileDirPath = "./tmp";

    //http://${addr}/proxy/${applicationId}/
    private static final String FLINK_URL_FORMAT = "http://%s/proxy/%s/";

    private static final String YARN_RM_WEB_KEY_PREFIX = "yarn.resourcemanager.webapp.address.";

    private static final Path tmpdir = Paths.get(doPrivileged(new GetPropertyAction("java.io.tmpdir")));

    private FlinkConfig flinkConfig;

    private FlinkPrometheusGatewayConfig prometheusGatewayConfig;

    private org.apache.hadoop.conf.Configuration hadoopConf;

    private YarnConfiguration yarnConf;

    private FlinkClientBuilder flinkClientBuilder;

    private SyncPluginInfo syncPluginInfo;

    private SqlPluginInfo sqlPluginInfo;

    private ClusterClient flinkClient;

    private Map<String, List<String>> cacheFile = Maps.newConcurrentMap();

    /**客户端是否处于可用状态*/
    private AtomicBoolean isClientOn = new AtomicBoolean(false);

    private YarnClient yarnClient;

    private ExecutorService yarnMonitorES;

    private ClusterClientCache clusterClientCache;

    private Properties flinkExtProp;

    private FlinkYarnSessionStarter flinkYarnSessionStarter;

    public static ThreadLocal<JobClient> jobClientThreadLocal = new ThreadLocal<>();

    public FlinkClient(){
        this.restartStrategy = new FlinkRestartStrategy();
    }

    @Override
    public void init(Properties prop) throws Exception {
            this.flinkExtProp = prop;

            String propStr = PublicUtil.objToString(prop);
            flinkConfig = PublicUtil.jsonStrToObject(propStr, FlinkConfig.class);
            prometheusGatewayConfig = PublicUtil.jsonStrToObject(propStr, FlinkPrometheusGatewayConfig.class);

            tmpFileDirPath = flinkConfig.getJarTmpDir();
            Preconditions.checkNotNull(tmpFileDirPath, "you need to set tmp file path for jar download.");

            syncPluginInfo = SyncPluginInfo.create(flinkConfig);
            sqlPluginInfo = SqlPluginInfo.create(flinkConfig);

            initHadoopConf(flinkConfig);
            flinkClientBuilder = FlinkClientBuilder.create(hadoopConf, yarnConf);

            boolean yarnCluster = flinkConfig.getClusterMode().equals(Deploy.yarn.name());
            if (yarnCluster){
                initYarnClient();
            }

            flinkClientBuilder.initFLinkConf(flinkConfig, flinkExtProp);

            initClient();

            if(yarnCluster){
                Configuration flinkConfig = new Configuration(flinkClientBuilder.getFlinkConfiguration());
                AbstractYarnClusterDescriptor perJobYarnClusterDescriptor = flinkClientBuilder.getClusterDescriptor(flinkConfig, yarnConf, ".", true);
                clusterClientCache = new ClusterClientCache(perJobYarnClusterDescriptor);
                yarnMonitorES = new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(), new CustomThreadFactory("flink_yarn_monitor"));
                //启动守护线程---用于获取当前application状态和更新flink对应的application
                yarnMonitorES.submit(new YarnAppStatusMonitor(this, yarnClient, flinkYarnSessionStarter));
            }
    }

    private void initYarnClient() {
        if (flinkConfig.isSecurity()){
            initSecurity();
        }
        yarnClient = YarnClient.createYarnClient();
        yarnClient.init(yarnConf);
        yarnClient.start();

        flinkClientBuilder.setYarnClient(yarnClient);
    }

    private void initSecurity() {
        String userPrincipal = flinkConfig.getFlinkPrincipal();
        String userKeytabPath = flinkConfig.getFlinkKeytabPath();
        String krb5ConfPath = flinkConfig.getFlinkKrb5ConfPath();
        String zkPrincipal = flinkConfig.getZkPrincipal();
        String zkKeytabPath = flinkConfig.getZkKeytabPath();
        String zkLoginName = flinkConfig.getZkLoginName();
        hadoopConf.set("username.client.keytab.file", zkKeytabPath);
        hadoopConf.set("username.client.kerberos.principal", zkPrincipal);

        try {
            KerberosUtils.setJaasConf(zkLoginName, zkPrincipal, zkKeytabPath);
            KerberosUtils.setZookeeperServerPrincipal("zookeeper.server.principal", flinkConfig.getZkPrincipal());
            KerberosUtils.login(userPrincipal, userKeytabPath, krb5ConfPath, hadoopConf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initClient() throws Exception {
        if(flinkConfig.getClusterMode().equals(Deploy.standalone.name())) {
            flinkClient = flinkClientBuilder.createStandalone(flinkConfig);
        } else if (flinkConfig.getClusterMode().equals(Deploy.yarn.name())) {
            if (flinkYarnSessionStarter == null) {
                this.flinkYarnSessionStarter = new FlinkYarnSessionStarter(flinkClientBuilder, flinkConfig, prometheusGatewayConfig);
            }
            flinkYarnSessionStarter.startFlinkYarnSession();
            flinkClient = flinkYarnSessionStarter.getClusterClient();
        }
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

        //如果jar包里面未指定mainclass,需要设置该参数
        String entryPointClass = jobParam.getMainClass();

        String args = jobParam.getClassArgs();
        if(StringUtils.isNotBlank(args)){
            programArgList.addAll(Arrays.asList(args.split("\\s+")));
        }

        FlinkYarnMode taskRunMode = FlinkUtil.getTaskRunMode(jobClient.getConfProperties(),jobClient.getComputeType());

        SavepointRestoreSettings spSettings = buildSavepointSetting(jobClient);
        PackagedProgram packagedProgram = null;
        String[] programArgs = programArgList.toArray(new String[programArgList.size()]);
        File jarFile = null;
        try{
            if(FlinkYarnMode.isPerJob(taskRunMode)){
                // perjob模式延后创建PackagedProgram
                jarFile = FlinkUtil.downloadJar(jarPath, tmpFileDirPath,hadoopConf);
            } else {
                packagedProgram = FlinkUtil.buildProgram(jarPath, tmpFileDirPath, classPaths, entryPointClass,
                        programArgs, spSettings, hadoopConf);
            }
        }catch (Throwable e){
            return JobResult.createErrorResult(e);
        }

        //只有当程序本身没有指定并行度的时候该参数才生效
        Integer runParallelism = FlinkUtil.getJobParallelism(jobClient.getConfProperties());

        jobClientThreadLocal.set(jobClient);
        try {
            Pair<String, String> runResult;
            if(FlinkYarnMode.isPerJob(taskRunMode)){
                ClusterSpecification clusterSpecification = FLinkConfUtil.createClusterSpecification(flinkClientBuilder.getFlinkConfiguration(), jobClient.getJobPriority());
                clusterSpecification.setConfiguration(flinkClientBuilder.getFlinkConfiguration());
                clusterSpecification.setParallelism(runParallelism);
                clusterSpecification.setClasspaths(classPaths);
                clusterSpecification.setEntryPointClass(entryPointClass);
                clusterSpecification.setJarFile(jarFile);
                clusterSpecification.setSpSetting(spSettings);
                clusterSpecification.setProgramArgs(programArgs);
                clusterSpecification.setCreateProgramDelay(true);
                clusterSpecification.setYarnConfiguration(getYarnConf(jobClient.getPluginInfo()));

                runResult = runJobByPerJob(clusterSpecification);

                packagedProgram = clusterSpecification.getProgram();
            } else {
                runResult = runJobByYarnSession(packagedProgram,runParallelism);
            }

            return JobResult.createSuccessResult(runResult.getFirst(), runResult.getSecond());
        }catch (Exception e){
            return JobResult.createErrorResult(e);
        }finally {
            if (packagedProgram!=null){
                packagedProgram.deleteExtractedLibraries();
            }
            jobClientThreadLocal.remove();
        }
    }

    /**
     * perjob模式提交任务
     */
    private Pair<String, String> runJobByPerJob(ClusterSpecification clusterSpecification) throws Exception{
        JobClient jobClient = jobClientThreadLocal.get();

        AbstractYarnClusterDescriptor descriptor = flinkClientBuilder.createClusterDescriptorByMode(null, flinkConfig, prometheusGatewayConfig, jobClient, true);
        descriptor.setName(jobClient.getJobName());
        ClusterClient<ApplicationId> clusterClient = descriptor.deployJobCluster(clusterSpecification, new JobGraph(),true);

        String applicationId = clusterClient.getClusterId().toString();
        String flinkJobId = clusterSpecification.getJobGraph().getJobID().toString();

        delFilesFromDir(tmpdir, applicationId);

        clusterClientCache.put(applicationId, clusterClient);

        return Pair.create(flinkJobId, applicationId);
    }

    /**
     * yarnSession模式运行任务
     */
    private Pair<String, String> runJobByYarnSession(PackagedProgram program, int parallelism) throws Exception {
        JobSubmissionResult result = flinkClient.run(program, parallelism);
        if (result.isJobExecutionResult()) {
            logger.info("Program execution finished");
            JobExecutionResult execResult = result.getJobExecutionResult();
            logger.info("Job with JobID " + execResult.getJobID() + " has finished.");
            logger.info("Job Runtime: " + execResult.getNetRuntime() + " ms");
        } else {
            logger.info("Job has been submitted with JobID " + result.getJobID());
        }
        delFilesFromDir(tmpdir, "flink-jobgraph");

        return Pair.create(result.getJobID().toString(), null);
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
        throw new RdosException("not support for flink batch sql now!!!");
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();
        String applicationId = jobIdentifier.getApplicationId();

        try{
            ClusterClient targetClusterClient;
            if(!Strings.isNullOrEmpty(applicationId)){
                targetClusterClient = clusterClientCache.getClusterClient(jobIdentifier);
            }else{
                targetClusterClient = flinkClient;
            }

            JobID jobID = new JobID(org.apache.flink.util.StringUtils.hexStringToByte(jobId));
            targetClusterClient.cancel(jobID);
        }catch (Exception e){
            logger.error("", e);
            return JobResult.createErrorResult(e);
        }

        JobResult jobResult = JobResult.newInstance(false);
        jobResult.setData(JobResult.JOB_ID_KEY, jobId);
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

        String reqUrl = getReqUrl(flinkClient) + "/jobs/" + jobId;
        String response = null;
        try{
            response = PoolHttpClient.get(reqUrl);
        } catch (RdosException e){
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
            ApplicationReport report = yarnClient.getApplicationReport(appId);
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
                    throw new RdosException("Unsupported application state");
            }
        } catch (YarnException | IOException e) {
            logger.error("", e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    public String getReqUrl(FlinkYarnMode flinkYarnMode) {
        if (FlinkYarnMode.PER_JOB == flinkYarnMode){
            return "${monitor}";
        }else if (FlinkYarnMode.NEW == flinkYarnMode) {
            return getReqUrl(flinkClient);
        } else {
            return getLegacyReqUrl(yarnClient, flinkClient.getClusterId().toString());
        }
    }

    public String getReqUrl(ClusterClient clusterClient){

        boolean isYarnClusterClient = clusterClient instanceof YarnClusterClient;
        if(!isYarnClusterClient){
            return clusterClient.getWebInterfaceURL();
        }

        try{
            YarnClusterClient yarnClusterClient = (YarnClusterClient) clusterClient;
            Field clusterDescField = YarnClusterClient.class.getDeclaredField("clusterDescriptor");
            clusterDescField.setAccessible(true);
            AbstractYarnClusterDescriptor clusterDesc = (AbstractYarnClusterDescriptor) clusterDescField.get(yarnClusterClient);
            YarnClient currYarnClient = clusterDesc.getYarnClient();
            clusterDescField.setAccessible(false);

            return getLegacyReqUrl(currYarnClient, clusterClient.getClusterId().toString());
        }catch (Exception e){
            logger.error("", e);
            return clusterClient.getWebInterfaceURL();
        }
    }


    /**
     * 获取jobMgr-web地址
     * @return
     */
    private String getLegacyReqUrl(YarnClient currYarnClient, String appId){
        String url = "";
        try{
            Field rmClientField = currYarnClient.getClass().getDeclaredField("rmClient");
            rmClientField.setAccessible(true);
            Object rmClient = rmClientField.get(currYarnClient);

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

            YarnApplicationState yarnApplicationState = yarnClient.getApplicationReport((ApplicationId) flinkClient.getClusterId()).getYarnApplicationState();
            if (YarnApplicationState.RUNNING != yarnApplicationState){
                logger.error("curr flink application {} state is not running!", appId);
            }

            url = String.format(FLINK_URL_FORMAT, addr, appId);
        }catch (Exception e){
            logger.error("Getting URL failed" + e);
        }

        logger.info("get req url=" + url);
        return url;
    }

    @Override
    public String getJobMaster(){
    	String url = getReqUrl(flinkClient);
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
        try {
            String reqUrl = String.format("%s%s", getReqUrl(flinkClient), path);
            return PoolHttpClient.get(reqUrl);
        } catch (Exception e) {
            throw new RdosException(ErrorCode.HTTP_CALL_ERROR, e);
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
            reqURL = flinkConfig.getFlinkJobHistory();
        }else{
            ClusterClient currClient;
            if(StringUtils.isNotBlank(applicationId)){
                currClient = clusterClientCache.getClusterClient(jobIdentifier);
            }else{
                currClient = flinkClient;
            }

            reqURL = currClient.getWebInterfaceURL();
        }

        Map<String,String> retMap = Maps.newHashMap();

        try {
            String exceptPath = String.format(FlinkRestParseUtil.EXCEPTION_INFO, jobId);
            String except = getExceptionInfo(exceptPath, reqURL);
            String jobPath = String.format(FlinkRestParseUtil.JOB_INFO, jobId);
            String jobInfo = getMessageByHttp(jobPath, reqURL);
            String accuPath = String.format(FlinkRestParseUtil.JOB_ACCUMULATOR_INFO, jobId);
            String accuInfo = getMessageByHttp(accuPath, reqURL);
            retMap.put("except", except);
            retMap.put("jobInfo", jobInfo);
            retMap.put("accuInfo", accuInfo);
            return FlinkRestParseUtil.parseEngineLog(retMap);
        } catch (Exception e) {
            logger.error("", e);
            return ExceptionUtil.getTaskLogError();
        }
    }

    /**
     *  perjob模式下任务完成后进入jobHistory会有一定的时间
     */
    private String getExceptionInfo(String exceptPath,String reqURL){
        String exceptionInfo = "";
        int i = 0;
        while (i < 10){
            try {
                Thread.sleep(500);
                exceptionInfo = getMessageByHttp(exceptPath, reqURL);
                return exceptionInfo;
            } catch (RdosException e){
                if (!e.getErrorMessage().contains("404")){
                    throw e;
                }
            } catch (Exception ignore){

            }finally {
                i++;
            }

        }

        return exceptionInfo;
    }

    @Override
    public EngineResourceInfo getAvailSlots() {

        if(!isClientOn.get()){
            return null;
        }

        String slotInfo = getMessageByHttp(FlinkRestParseUtil.SLOTS_INFO);
        FlinkResourceInfo resourceInfo = FlinkRestParseUtil.getAvailSlots(slotInfo, yarnClient);
        if(resourceInfo == null){
            logger.error("---flink cluster maybe down.----");
            resourceInfo = new FlinkResourceInfo();
        }

        return resourceInfo;
    }

    private float getQueueRemainCapacity(float coefficient, List<QueueInfo> queueInfos){
        float capacity = 0;
        for (QueueInfo queueInfo : queueInfos){
            if (CollectionUtils.isNotEmpty(queueInfo.getChildQueues())) {
                float subCoefficient = queueInfo.getCapacity() * coefficient;
                capacity = getQueueRemainCapacity(subCoefficient, queueInfo.getChildQueues());
            }
            if (flinkConfig.getQueue().equals(queueInfo.getQueueName())){
                capacity = coefficient * queueInfo.getCapacity() * (1 - queueInfo.getCurrentCapacity());
            }
            if (capacity>0){
                return capacity;
            }
        }
        return capacity;
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

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        String appId = jobIdentifier.getApplicationId();
        String jobId = jobIdentifier.getEngineJobId();

        RdosTaskStatus rdosTaskStatus = getJobStatus(jobIdentifier);

        String reqURL;
        if(rdosTaskStatus.equals(RdosTaskStatus.FINISHED) || rdosTaskStatus.equals(RdosTaskStatus.CANCELED)
                || rdosTaskStatus.equals(RdosTaskStatus.FAILED) || rdosTaskStatus.equals(RdosTaskStatus.KILLED)){
            reqURL = flinkConfig.getFlinkJobHistory();
        }else{
            ClusterClient currClient;
            if(StringUtils.isNotBlank(appId)){
                currClient = clusterClientCache.getClusterClient(jobIdentifier);
            }else{
                currClient = flinkClient;
            }

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

    public void setClientOn(boolean isClientOn){
        this.isClientOn.set(isClientOn);
    }

    public boolean isClientOn(){
        return isClientOn.get();
    }

    public ClusterClient getFlinkClient() {
        return flinkClient;
    }

}
