package com.dtstack.rdos.engine.execution.flink120;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobIdentifier;
import com.dtstack.rdos.engine.execution.base.JobParam;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.EJobType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.flink120.util.FlinkUtil;
import com.dtstack.rdos.engine.execution.flink120.util.PluginSourceUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobSubmissionResult;
import org.apache.flink.api.common.accumulators.AccumulatorHelper;
import org.apache.flink.client.deployment.StandaloneClusterDescriptor;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.client.program.ProgramMissingJobException;
import org.apache.flink.client.program.ProgramParametrizationException;
import org.apache.flink.client.program.StandaloneClusterClient;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Preconditions;
import org.apache.http.HttpStatus;
import org.apache.http.conn.HttpHostConnectException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
public class FlinkClient extends AbsClient {

    private static final Logger logger = LoggerFactory.getLogger(FlinkClient.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String sqlPluginDirName = "sqlplugin";

    private static final String syncPluginDirName = "syncplugin";

    /**同步数据插件jar名称*/
    private static final String syncJarFileName = "flinkx.jar";

    public static final String FLINK_ENGINE_JOBID_KEY = "engineJobId";

    public static final String FLINK_JOB_FROMSAVEPOINT_KEY = "isRestoration";

    //FIXME key值需要根据客户端传输名称调整
    public static final String FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY = "allowNonRestoredState";
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
    public static String sp = File.separator;

    public final static String JOBEXCEPTION = "/jobs/%s/exceptions";

    public String tmpFileDirPath = "./tmp";

    private String jobMgrHost;

    private int jobMgrPort;

    private ClusterClient client;

    //默认使用异步提交
    private boolean isDetact = true;

    // 同步模块在flink集群加载插件
    private String remoteFlinkSyncPluginRoot;

    // 同步模块的monitorAddress, 用于获取错误记录数等信息
    private String monitorAddress;

    @Override
    public void init(Properties prop) throws Exception {

        FlinkConfig flinkConfig = objectMapper.readValue(objectMapper.writeValueAsBytes(prop), FlinkConfig.class);
        tmpFileDirPath = flinkConfig.getJarTmpDir();

        Preconditions.checkNotNull(tmpFileDirPath, "you need to set tmp file path for jar download.");
        Preconditions.checkState(flinkConfig.getFlinkJobMgrUrl() != null || flinkConfig.getFlinkZkNamespace() != null,
                "flink client can not init for host and zkNamespace is null at the same time.");

        String sqlPluginDir = getSqlPluginDir(flinkConfig.getFlinkPluginRoot());
        File sqlPluginDirFile = new File(sqlPluginDir);

        if(!sqlPluginDirFile.exists() || !sqlPluginDirFile.isDirectory()){
            throw new RdosException("not exists flink sql dir:" + sqlPluginDir + ", please check it!!!");
        }

        String remoteSqlPluginDir = getSqlPluginDir(flinkConfig.getRemotePluginRootDir());

        PluginSourceUtil.setSourceJarRootDir(sqlPluginDir);
        PluginSourceUtil.setRemoteSourceJarRootDir(remoteSqlPluginDir);

        if(flinkConfig.getFlinkZkNamespace() != null){//优先使用zk
            initClusterClientByZK(flinkConfig.getFlinkZkNamespace(), flinkConfig.getFlinkZkAddress(), flinkConfig.getFlinkClusterId());
        }else{
            initClusterClientByURL(flinkConfig.getFlinkJobMgrUrl());
        }

        String localSyncPluginDir = getSyncPluginDir(flinkConfig.getFlinkPluginRoot());
        FlinkUtil.setLocalSyncFileDir(localSyncPluginDir);

        String syncPluginDir = getSyncPluginDir(flinkConfig.getRemotePluginRootDir());
        this.remoteFlinkSyncPluginRoot = syncPluginDir;
        this.monitorAddress = flinkConfig.getMonitorAddress();

    }


    /**
     * 直接指定jobmanager host:port方式
     * @return
     * @throws Exception
     */
    public void initClusterClientByURL(String jobMgrURL){

        String[] splitInfo = jobMgrURL.split(":");
        if(splitInfo.length < 2){
            throw new RdosException("the config of engineUrl is wrong. " +
                    "setting value is :" + jobMgrURL +", please check it!");
        }

        this.jobMgrHost = splitInfo[0].trim();
        this.jobMgrPort = Integer.parseInt(splitInfo[1].trim());

        Configuration config = new Configuration();
        config.setString(ConfigConstants.JOB_MANAGER_IPC_ADDRESS_KEY, jobMgrHost);
        config.setInteger(ConfigConstants.JOB_MANAGER_IPC_PORT_KEY, jobMgrPort);

        StandaloneClusterDescriptor descriptor = new StandaloneClusterDescriptor(config);
        StandaloneClusterClient clusterClient = descriptor.retrieve(null);
        clusterClient.setDetached(isDetact);
        client = clusterClient;
    }

    /**
     * 根据yarn方式获取ClusterClient
     */
    public void initClusterClientByYarn(){
        //YarnClusterClient clusterClient =
    }


    /**
     * 根据zk获取clusterclient
     * @param zkNamespace
     */
    public void initClusterClientByZK(String zkNamespace, String zkAddress, String clusterId){
        Configuration config = new Configuration();
        config.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.ZOOKEEPER.toString());
        config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM, zkAddress);

        if(zkNamespace != null){//不设置默认值"/flink"
            config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_ROOT, zkNamespace);
        }

        if(clusterId != null){//不设置默认值"/default"
            config.setString(HighAvailabilityOptions.HA_CLUSTER_ID, clusterId);
        }

        StandaloneClusterDescriptor descriptor = new StandaloneClusterDescriptor(config);
        StandaloneClusterClient clusterClient = descriptor.retrieve(null);
        clusterClient.setDetached(isDetact);

        //初始化的时候需要设置,否则提交job会出错,update config of jobMgrhost, jobMgrprt
        InetSocketAddress address = clusterClient.getJobManagerAddress();
        config.setString(ConfigConstants.JOB_MANAGER_IPC_ADDRESS_KEY, address.getAddress().getHostAddress());
        config.setInteger(ConfigConstants.JOB_MANAGER_IPC_PORT_KEY, address.getPort());

        client = clusterClient;
    }

    public String getSqlPluginDir(String pluginRoot){
        return pluginRoot + sp + sqlPluginDirName;
    }

    public String getSyncPluginDir(String pluginRoot){
        return pluginRoot + sp + syncPluginDirName;
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

    /***
     * 提交 job-jar 到 cluster 的方式, jobname 需要在job-jar里面指定
     * @param jobClient
     * @return
     */
    private JobResult submitJobWithJar(JobClient jobClient) {

        JobParam jobParam = new JobParam(jobClient);

        String jarPath = jobParam.getJarPath();
        if(jarPath == null){
            logger.error("can not submit a job without jarpath, please check it");
            JobResult jobResult = JobResult.createErrorResult("can not submit a job without jarpath, please check it");
            return jobResult;
        }

        PackagedProgram packagedProgram = null;

        String entryPointClass = jobParam.getMainClass();//如果jar包里面未指定mainclass,需要设置该参数

        List<String> programArgList = new ArrayList<>();
        String args = jobParam.getClassArgs();

        if(StringUtils.isNotBlank(args)){
            programArgList.addAll(Arrays.asList(args.split("\\s+")));
        }

        if(StringUtils.isNotEmpty(monitorAddress)) {
            programArgList.add("-monitor");
            programArgList.add(monitorAddress);
        }

        List<URL> classpaths = remoteFlinkSyncPluginRoot != null ? FlinkUtil.getUserClassPath(programArgList, remoteFlinkSyncPluginRoot) : new ArrayList<>();

        SavepointRestoreSettings spSettings = buildSavepointSetting(jobClient);
        try{
            String[] programArgs = programArgList.toArray(new String[programArgList.size()]);
            packagedProgram = FlinkUtil.buildProgram(jarPath, tmpFileDirPath, classpaths, entryPointClass, programArgs, spSettings);
        }catch (Exception e){
            JobResult jobResult = JobResult.createErrorResult(e);
            logger.error("", e);
            return jobResult;
        }

        //只有当程序本身没有指定并行度的时候该参数才生效
        Integer runParallelism = FlinkUtil.getJobParallelism(jobClient.getConfProperties());
        JobSubmissionResult result = null;

        try {
            //packagedProgram.getPlanWithoutJars().getPlan().setJobName(jobClient.getJobName());
            result = client.run(packagedProgram, runParallelism);
        }catch (ProgramParametrizationException e){
            logger.error("", e);
            return JobResult.createErrorResult(e);
        }catch (ProgramMissingJobException e){
            logger.error("", e);
            return JobResult.createErrorResult(e);
        }catch (ProgramInvocationException e){
            logger.error("", e);
            return JobResult.createErrorResult(e);
        }finally {
            packagedProgram.deleteExtractedLibraries();
        }

        if (result.isJobExecutionResult()) {//FIXME 非detact模式下提交,即同步等到jobfinish,暂时不提供
            logger.info("Program execution finished");
            JobExecutionResult execResult = result.getJobExecutionResult();
            logger.info("Job with JobID " + execResult.getJobID() + " has finished.");
            logger.info("Job Runtime: " + execResult.getNetRuntime() + " ms");
            Map<String, Object> accumulatorsResult = execResult.getAllAccumulatorResults();
            if (accumulatorsResult.size() > 0) {
                System.out.println("Accumulator Results: ");
                System.out.println(AccumulatorHelper.getResultsFormated(accumulatorsResult));
            }
        } else {
            logger.info("Job has been submitted with JobID " + result.getJobID());
        }

        return JobResult.createSuccessResult(result.getJobID().toString());
    }

    public SavepointRestoreSettings buildSavepointSetting(JobClient jobClient){

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
     * 区分出source, transformation, sink
     * FIXME 目前source 只支持kafka
     * 1: 添加数据源 -- 可能多个
     * 2: 注册udf--fuc/table -- 可能多个---必须附带jar(做校验)
     * 3: 调用sql
     * 4: 添加sink
     * @param jobClient
     * @return
     */
    private JobResult submitSqlJobForStream(JobClient jobClient) {
        throw new RuntimeException("not support for flink1.2 stream sql.");
    }

    private JobResult submitSqlJobForBatch(JobClient jobClient){
        throw new RdosException("not support flink sql job for batch type.");
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        JobID jobID = new JobID(org.apache.flink.util.StringUtils.hexStringToByte(jobId));
        try{
            client.cancel(jobID);
        }catch (Exception e){
            return JobResult.createErrorResult(e);
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

    	if(jobId == null || "".equals(jobId)){
    		return null;
    	}
    	
        String reqUrl = getReqUrl() + "/jobs/" + jobId;
        String response = null;
        try{
            response = PoolHttpClient.get(reqUrl);
        }catch (RdosException e){
            //FIXME 如果查询不到就返回完成,需要清除,因为有可能数据被flink清除了
            if((HttpStatus.SC_NOT_FOUND + "").equals(e.getErrorMessage())){
                return RdosTaskStatus.NOTFOUND;
            }

            return null;
        }catch (HttpHostConnectException e){
            return null;
        } catch (IOException e) {
            return null;
        }

        try{
            Map<String, Object> statusMap = objectMapper.readValue(response, Map.class);
            Object stateObj = statusMap.get("state");
            if(stateObj == null){
                return null;
            }

            String state = (String) stateObj;
            state = org.apache.commons.lang3.StringUtils.upperCase(state);
            RdosTaskStatus status = RdosTaskStatus.getTaskStatus(state);
            return status;
        }catch (Exception e){
            logger.error("", e);
            return RdosTaskStatus.FINISHED;
        }

    }

    /**
     * 获取jobMgr-web地址
     * @return
     */
    private String getReqUrl(){
        return client.getWebInterfaceURL();
    }
    
    @Override
    public String getJobMaster(){
    	String url = getReqUrl();
    	return url.split("//")[1];
    }

    private StreamExecutionEnvironment getStreamExeEnv(Properties confProperties) throws IOException {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(FlinkUtil.getEnvParallelism(confProperties));
        if(FlinkUtil.getMaxEnvParallelism(confProperties)>0){
            env.setMaxParallelism(FlinkUtil.getMaxEnvParallelism(confProperties));
        }
        if(FlinkUtil.getBufferTimeoutMillis(confProperties)>0){
            env.setBufferTimeout(FlinkUtil.getBufferTimeoutMillis(confProperties));
        }
        return env;
    }


	public String getSavepointPath(String engineTaskId){
        String reqUrl = getReqUrl() + "/jobs/" + engineTaskId + "/checkpoints";
        String response = null;
        try {
            response = PoolHttpClient.get(reqUrl);
        } catch (IOException e) {
            return null;
        }

        try{
            Map<String, Object> statusMap = objectMapper.readValue(response, Map.class);
            Object latestObj = statusMap.get("latest");
            if(latestObj == null){
                return null;
            }

            Map<String, Object> latestMap = (Map<String, Object>) latestObj;
            Object completeObj = latestMap.get("completed");
            if(completeObj == null){
                return null;
            }

            Map<String, Object> completeMap = (Map<String, Object>) completeObj;
            String path = (String) completeMap.get("external_path");
            return path;
        }catch (Exception e){
            logger.error("", e);
            return null;
        }
    }

    private JobResult submitSyncJob(JobClient jobClient) {
        throw new RuntimeException("no support for ");
    }

	@Override
	public String getMessageByHttp(String path) {
        String reqUrl = String.format("%s%s",getReqUrl(),path);
        try {
            return PoolHttpClient.get(reqUrl);
        } catch (IOException e) {
           return null;
        }
    }
}
