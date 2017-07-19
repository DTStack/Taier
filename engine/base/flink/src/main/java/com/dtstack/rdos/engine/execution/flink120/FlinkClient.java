package com.dtstack.rdos.engine.execution.flink120;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.enumeration.Restoration;
import com.dtstack.rdos.engine.execution.base.operator.*;
import com.dtstack.rdos.engine.execution.base.operator.stream.AddJarOperator;
import com.dtstack.rdos.engine.execution.base.operator.stream.CreateFunctionOperator;
import com.dtstack.rdos.engine.execution.base.operator.stream.CreateResultOperator;
import com.dtstack.rdos.engine.execution.base.operator.stream.CreateSourceOperator;
import com.dtstack.rdos.engine.execution.base.operator.stream.ExecutionOperator;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.execution.flink120.sink.SinkFactory;
import com.dtstack.rdos.engine.execution.flink120.source.IStreamSourceGener;
import com.dtstack.rdos.engine.execution.flink120.source.SourceFactory;
import com.dtstack.rdos.engine.execution.flink120.util.FlinkUtil;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobSubmissionResult;
import org.apache.flink.api.common.accumulators.AccumulatorHelper;
import org.apache.flink.client.deployment.StandaloneClusterDescriptor;
import org.apache.flink.client.program.*;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.util.Preconditions;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
public class FlinkClient extends AbsClient {

    private static final Logger logger = LoggerFactory.getLogger(FlinkClient.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String FLINK_ENGINE_JOBID_KEY = "engineJobId";

    public static final String FLINK_JOB_FROMSAVEPOINT_KEY = "isRestoration";

    //FIXME key值需要根据客户端传输名称调整
    public static final String FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY = "allowNonRestoredState";
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
    public static final String FILE_TMP_PATH_KEY = "jarTmpDir";

    public String tmpFileDirPath;

    private String jobMgrHost;

    private int jobMgrPort;

    private ClusterClient client;

    //默认使用异步提交
    private boolean isDetact = true;

    // 同步模块在flink集群加载插件
    private String flinkPluginRoot;

    // 同步模块的monitorAddress, 用于获取错误记录数等信息
    private String monitorAddress;

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
        this.jobMgrPort = Integer.parseInt(splitInfo[1].trim())+1;

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

    public void init(Properties prop) throws Exception {
    	FlinkConfig flinkConfig = objectMapper.readValue(objectMapper.writeValueAsBytes(prop), FlinkConfig.class);
        tmpFileDirPath = flinkConfig.getJarTmpDir();

        Preconditions.checkNotNull(tmpFileDirPath, "you need to set tmp file path for jar download.");
        Preconditions.checkState(flinkConfig.getFlinkJobMgrUrl() != null || flinkConfig.getFlinkZkNamespace() != null,
                "flink client can not init for host and zkNamespace is null at the same time.");

        if(flinkConfig.getFlinkZkNamespace() != null){//优先使用zk
            initClusterClientByZK(flinkConfig.getFlinkZkNamespace(), flinkConfig.getFlinkZkAddress(), flinkConfig.getFlinkClusterId());
        }else{
            initClusterClientByURL(flinkConfig.getFlinkJobMgrUrl());
        }

        this.flinkPluginRoot = flinkConfig.getFlinkPluginRoot();
        this.monitorAddress = flinkConfig.getMonitorAddress();

    }

    /***
     * 提交 job-jar 到 cluster 的方式, jobname 需要在job-jar里面指定
     * @param jobClient
     * @return
     */
    public JobResult submitJobWithJar(JobClient jobClient) {

        Properties properties = adaptToJarSubmit(jobClient);

        Object jarPath = properties.get(JOB_JAR_PATH_KEY);
        if(jarPath == null){
            logger.error("can not submit a job without jarpath, please check it");
            JobResult jobResult = JobResult.createErrorResult("can not submit a job without jarpath, please check it");
            return jobResult;
        }

        PackagedProgram packagedProgram = null;

        String entryPointClass = properties.getProperty(JOB_MAIN_CLASS_KEY);//如果jar包里面未指定mainclass,需要设置该参数

        List<String> programArgList = new ArrayList<>();
        String args = properties.getProperty(JOB_EXE_ARGS);

        if(StringUtils.isNotBlank(args)){
            try {
                args = java.net.URLDecoder.decode(args, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            programArgList.addAll(Arrays.asList(args.split("\\s+")));
        }

        if(StringUtils.isNotEmpty(monitorAddress)) {
            programArgList.add("-monitor");
            programArgList.add(monitorAddress);
        }

        List<URL> classpaths = flinkPluginRoot != null ? FlinkUtil.getUserClassPath(programArgList, flinkPluginRoot) : new ArrayList<>();

        Properties spProp = getSpProperty(jobClient);
        SavepointRestoreSettings spSettings = buildSavepointSetting(spProp);
        try{
            packagedProgram = FlinkUtil.buildProgram((String) jarPath, tmpFileDirPath, classpaths, entryPointClass, programArgList.toArray(new String[programArgList.size()]), spSettings);
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
            //FIXME 作用?
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

        JobResult jobResult = JobResult.createSuccessResult(result.getJobID().toString());

        return jobResult;
    }

    public Properties adaptToJarSubmit(JobClient jobClient){

        AddJarOperator jarOperator = null;
        for(Operator operator : jobClient.getOperators()){
            if(operator instanceof AddJarOperator){
                jarOperator = (AddJarOperator) operator;
                break;
            }
        }

        if(jarOperator == null){
            throw new RdosException("submit type of MR need to add jar operator.");
        }

        Properties properties = new Properties();
        properties.setProperty(JOB_JAR_PATH_KEY, jarOperator.getJarPath());
        properties.setProperty(JOB_APP_NAME_KEY, jobClient.getJobName());

        if(jobClient.getClassArgs() != null){
            properties.setProperty(JOB_EXE_ARGS, jobClient.getClassArgs());
        }
        return properties;
    }

    public SavepointRestoreSettings buildSavepointSetting(Properties properties){

        if(properties == null){
            return SavepointRestoreSettings.none();
        }

        if(properties.contains(FLINK_JOB_FROMSAVEPOINT_KEY)){ //有指定savepoint
            String jobId = properties.getProperty(FLINK_ENGINE_JOBID_KEY);
            String savepointPath = getSavepointPath(jobId);
            if(savepointPath == null){
                throw new RdosException("can't get any savepoint path!");
            }

            String stateStr = properties.getProperty(FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY);
            boolean allowNonRestoredState = BooleanUtils.toBoolean(stateStr);
            return SavepointRestoreSettings.forPath(savepointPath, allowNonRestoredState);
        }else{
            return SavepointRestoreSettings.none();
        }
    }


    public JobResult submitSqlJob(JobClient jobClient) throws IOException, ClassNotFoundException {
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
    private JobResult submitSqlJobForStream(JobClient jobClient) throws IOException, ClassNotFoundException {
    	Properties confProperties = jobClient.getConfProperties();
        StreamExecutionEnvironment env = getStreamExeEnv(confProperties);
        FlinkUtil.openCheckpoint(env, confProperties);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.getTableEnvironment(env);
        Table resultTable = null; //FIXME 注意现在只能使用一个result
        int currStep = 0;
        List<String> jarPathList = new ArrayList<>();
        List<URL> jarURList = Lists.newArrayList();
        URLClassLoader classLoader = null;
        for(Operator operator : jobClient.getOperators()){
            if(operator instanceof AddJarOperator){
                if(currStep > 0){
                    throw new RdosException("sql job order setting err. cause of AddJarOperator");
                }

                AddJarOperator addJarOperator = (AddJarOperator) operator;
                String addFilePath = addJarOperator.getJarPath();
                File tmpFile = FlinkUtil.downloadJar(addFilePath, tmpFileDirPath);
                jarURList.add(tmpFile.toURI().toURL());
                jarPathList.add(tmpFile.getAbsolutePath());

            }else if(operator instanceof CreateSourceOperator){//添加数据源,注册指定table
                if(currStep > 1){
                    throw new RdosException("sql job order setting err. cause of CreateSourceOperator");
                }

                currStep = 1;
                CreateSourceOperator sourceOperator = (CreateSourceOperator) operator;
                IStreamSourceGener sourceGener = SourceFactory.getStreamSourceGener(sourceOperator.getType());
                StreamTableSource tableSource = (StreamTableSource) sourceGener.genStreamSource
                        (sourceOperator.getProperties(), sourceOperator.getFields(), sourceOperator.getFieldTypes());
                tableEnv.registerTableSource(sourceOperator.getName(), tableSource);

            }else if(operator instanceof CreateFunctionOperator){//注册自定义func
                if(currStep > 2){
                    throw new RdosException("sql job order setting err. cause of CreateFunctionOperator");
                }

                currStep = 2;
                CreateFunctionOperator tmpOperator = (CreateFunctionOperator) operator;
                //需要把字节码加载进来
                if(classLoader == null){
                    classLoader = FlinkUtil.loadJar(jarURList, this.getClass().getClassLoader());
                }

                classLoader.loadClass(tmpOperator.getClassName());
                FlinkUtil.registerUDF(tmpOperator.getType(), tmpOperator.getClassName(), tmpOperator.getName(),
                        tableEnv, classLoader);

            }else if(operator instanceof ExecutionOperator){
                if(currStep > 3){
                    throw new RdosException("sql job order setting err. cause of ExecutionOperator");
                }

                currStep = 3;
                resultTable = tableEnv.sql(((ExecutionOperator) operator).getSql());

            }else if(operator instanceof CreateResultOperator){
                if(currStep > 4){
                    throw new RdosException("sql job order setting err. cause of CreateResultOperator");
                }

                currStep = 4;
                CreateResultOperator resultOperator = (CreateResultOperator) operator;
                TableSink tableSink = SinkFactory.getTableSink(resultOperator);
                resultTable.writeToSink(tableSink);

            }else{
                throw new RdosException("not support operator of " + operator.getClass().getName());
            }
        }

        try {
            //这里getStreamGraph() 和 getJobGraph()均是创建新的对象,方法命名让人疑惑.
            StreamGraph streamGraph = env.getStreamGraph();
            streamGraph.setJobName(jobClient.getJobName());
            JobGraph jobGraph = streamGraph.getJobGraph();

            Properties spProp = getSpProperty(jobClient);
            SavepointRestoreSettings spRestoreSetting = buildSavepointSetting(spProp);
            jobGraph.setSavepointRestoreSettings(spRestoreSetting);
            for(String jarFile : jarPathList){
                URI jarFileUri = new File(jarFile).getAbsoluteFile().toURI();
                jobGraph.addJar(new Path(jarFileUri));
            }
            JobResult jobResult;
            if(isDetact){
                JobSubmissionResult submissionResult = client.runDetached(jobGraph, client.getClass().getClassLoader());
                jobResult = JobResult.createSuccessResult(submissionResult.getJobID().toString());
            }else{
                JobExecutionResult jobExecutionResult = client.run(jobGraph, client.getClass().getClassLoader());
                jobResult = JobResult.createSuccessResult(jobExecutionResult.getJobID().toString());
            }

            return jobResult;

        } catch (Exception e) {
            return JobResult.createErrorResult(e);
        }
    }

    private JobResult submitSqlJobForBatch(JobClient jobClient){
        throw new RdosException("not support flink sql job for batch type.");
    }

    @Override
    public JobResult cancelJob(ParamAction paramAction) {
        String jobId = paramAction.getEngineTaskId();

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
     * @param jobId
     * @return
     */
    public RdosTaskStatus getJobStatus(String jobId) {
    	if(jobId == null||"".equals(jobId)){
    		return null;
    	}
    	
        String reqUrl = getReqUrl() + "/jobs/" + jobId;
        String response = null;
        try{
            response = PoolHttpClient.get(reqUrl);
        }catch (Exception e){
            //FIXME 如果查询不到就返回完成,需要清除,因为有可能数据被flink清除了
            if(e instanceof RdosException && (HttpStatus.SC_NOT_FOUND + "").equals(((RdosException) e).getErrorMessage())){
                return RdosTaskStatus.NOTFOUND;
            }else{
                throw e;
            }
        }

        if(response == null){
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
            return RdosTaskStatus.getTaskStatus(state);
        }catch (Exception e){
            logger.error("", e);
            return null;
        }

    }

    @Override
    public String getJobDetail(String jobId) {
        String reqUrl = getReqUrl() + "/jobs/" + jobId;
        String response = PoolHttpClient.get(reqUrl);
        return response;
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

	@Override
	public JobResult immediatelySubmitJob(JobClient jobClient) {
		// TODO Auto-generated method stub
		return null;
	}


	public String getSavepointPath(String engineTaskId){
        String reqUrl = getReqUrl() + "/jobs/" + engineTaskId + "/checkpoints";
        String response = PoolHttpClient.get(reqUrl);
        if(response == null){
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

    public Properties getSpProperty(JobClient jobClient){
	    Properties properties = new Properties();

	    if(jobClient.getIsRestoration() == Restoration.NO){
            return properties;
        }

        properties.put(FLINK_JOB_FROMSAVEPOINT_KEY, jobClient.getIsRestoration().getVal());

	    if(jobClient.getEngineTaskId() != null){
	        properties.setProperty(FLINK_ENGINE_JOBID_KEY, jobClient.getEngineTaskId());
        }

        if(jobClient.getConfProperties().containsKey(FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY)){
            properties.put(FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY,
                    jobClient.getConfProperties().get(FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY));
        }

        return properties;
    }

    @Override
    public JobResult submitSyncJob(JobClient jobClient) {
        //使用flink作为数据同步调用的其实是提交mr--job
        return submitJobWithJar(jobClient);
    }

    // 加工传入给flinkx的命令行参数
    private String[] preProcessProgramArgs(final String[] programArgs) {

        return null;
    }

}
