package com.dtstack.rdos.engine.execution.flink120;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.HttpClient;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.ESourceType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.operator.*;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.pojo.PropertyConstant;
import com.dtstack.rdos.engine.execution.flink120.source.IStreamSourceGener;
import com.dtstack.rdos.engine.execution.flink120.util.FlinkUtil;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.BooleanUtils;
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
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.instance.ActorGateway;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.runtime.messages.JobManagerMessages;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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

    public String tmpFileDirPath;

    private String jobMgrHost;

    private int jobMgrPort;

    private ClusterClient client;

    //默认使用异步提交
    private boolean isDetact = true;

    /**
     * FIXME 注意 StandaloneClusterClient 是否适用于Yarn方式
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
     * 根据zk获取cluster
     * @param zkNamespace
     */
    public void initClusterClientByZK(String zkNamespace, String root, String clusterId){
        Configuration config = new Configuration();
        config.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.ZOOKEEPER.toString());
        config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM, zkNamespace);

        if(root != null){//不设置默认值"/flink"
            config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_ROOT, root);
        }

        if(clusterId != null){//不设置默认值"/default"
            config.setString(HighAvailabilityOptions.HA_CLUSTER_ID, clusterId);
        }

        StandaloneClusterDescriptor descriptor = new StandaloneClusterDescriptor(config);
        StandaloneClusterClient clusterClient = descriptor.retrieve(null);
        clusterClient.setDetached(isDetact);

        //初始化的时候需要设置,否则提交job会出错,update config of jobMgrhost, jobMgrprt
        InetSocketAddress address = clusterClient.getJobManagerAddress();
        config.setString(ConfigConstants.JOB_MANAGER_IPC_ADDRESS_KEY, address.getHostName());
        config.setInteger(ConfigConstants.JOB_MANAGER_IPC_PORT_KEY, address.getPort());

        client = clusterClient;
    }

    public void init(Properties prop) {

        String jobMgrURL = prop.getProperty(PropertyConstant.FLINK_JOBMGR_URL_KEY);
        String zkNamespace = prop.getProperty(PropertyConstant.FLINK_ZKNAMESPACE_KEY);
        tmpFileDirPath = prop.getProperty(PropertyConstant.FILE_TMP_PATH_KEY);

        Preconditions.checkNotNull(tmpFileDirPath, "you need to set tmp file path for jar download.");
        Preconditions.checkState(jobMgrURL != null || zkNamespace != null,
                "flink client can not init for host and zkNamespace is null at the same time.");

        if(zkNamespace != null){//优先使用zk
            String zkRoot = prop.getProperty(PropertyConstant.FLINK_ZK_ROOT_KEY);
            String clusterId = prop.getProperty(PropertyConstant.FLINK_ZK_CLUSTERID_KEY);
            initClusterClientByZK(zkNamespace, zkRoot, clusterId);
        }else{
            initClusterClientByURL(jobMgrURL);
        }

    }

    /***
     * 提交 job-jar 到 cluster 的方式, jobname 需要在job-jar里面指定
     * @param properties
     * @return
     */
    public JobResult submitJobWithJar(Properties properties) {

        Object jarPath = properties.get(PropertyConstant.FLINK_JOB_JAR_PATH_KEY);
        if(jarPath == null){
            logger.error("can not submit a job without jarpath, please check it");
            JobResult jobResult = JobResult.newInstance(true);
            jobResult.setData("errMsg", "can not submit a job without jarpath, please check it");
            return jobResult;
        }

        PackagedProgram packagedProgram = null;

        String entryPointClass = properties.getProperty(PropertyConstant.FLINK_JOB_JAR_MAINCLASS_KEY);//如果jar包里面未指定mainclass,需要设置该参数
        String[] programArgs = new String[0];//FIXME 该参数设置暂时未设置
        List<URL> classpaths = new ArrayList<>();//FIXME 该参数设置暂时未设置
        SavepointRestoreSettings spSettings = buildSavepointSetting(properties);

        try{
            packagedProgram = FlinkUtil.buildProgram((String) jarPath, tmpFileDirPath, classpaths, entryPointClass, programArgs, spSettings);
        }catch (Exception e){
            JobResult jobResult = JobResult.newInstance(true);
            jobResult.setData("errMsg", e.getMessage());
            logger.error("", e);
            return jobResult;
        }

        //只有当程序本身没有指定并行度的时候该参数才生效
        String parallelismStr = properties.getProperty(PropertyConstant.FLINK_JOB_PARALLELISM_KEY);
        Integer runParallelism = parallelismStr == null ? 1 : Integer.valueOf(parallelismStr);
        JobSubmissionResult result = null;

        try {
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

        JobResult jobResult = JobResult.newInstance(false);
        jobResult.setData(jobResult.JOB_ID_KEY, result.getJobID().toString());

        return jobResult;
    }

    public SavepointRestoreSettings buildSavepointSetting(Properties properties){

        if(properties == null){
            return SavepointRestoreSettings.none();
        }

        if(properties.contains(PropertyConstant.FLINK_JOB_FROMSAVEPOINT_KEY)){ //有指定savepoint
            String savepointPath = properties.getProperty(PropertyConstant.FLINK_JOB_FROMSAVEPOINT_KEY);
            String stateStr = properties.getProperty(PropertyConstant.FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY);
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
                return  submitSqlJobForStream(jobClient);

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

        StreamExecutionEnvironment env = getStreamExeEnv(jobClient);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.getTableEnvironment(env);
        Table resultTable = null; //FIXME 注意现在只能使用一个result

        int currStep = 0;
        ParamsOperator paramsOperator = null;
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
                IStreamSourceGener sourceGener = FlinkUtil.getStreamSourceGener(sourceOperator.getType());
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
                    classLoader = FlinkUtil.loadJar(jarURList);
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
                FlinkUtil.writeToSink(resultOperator, resultTable);

            }else if(operator instanceof ParamsOperator){
                paramsOperator = (ParamsOperator) operator;

            }else{
                throw new RdosException("not support operator of " + operator.getClass().getName());
            }
        }

        try {
            //这里getStreamGraph() 和 getJobGraph()均是创建新的对象,方法命名让人疑惑.
            StreamGraph streamGraph = env.getStreamGraph();
            streamGraph.setJobName(jobClient.getJobName());
            JobGraph jobGraph = streamGraph.getJobGraph();
            if(paramsOperator != null){
                SavepointRestoreSettings spRestoreSetting = buildSavepointSetting(paramsOperator.getProperties());
                jobGraph.setSavepointRestoreSettings(spRestoreSetting);
            }

            for(String jarFile : jarPathList){
                URI jarFileUri = new File(jarFile).getAbsoluteFile().toURI();
                jobGraph.addJar(new Path(jarFileUri));
            }

            JobResult jobResult = JobResult.newInstance(false);
            if(isDetact){
                JobSubmissionResult submissionResult = client.runDetached(jobGraph, client.getClass().getClassLoader());
                jobResult.setData(jobResult.JOB_ID_KEY, submissionResult.getJobID().toString());
            }else{
                JobExecutionResult jobExecutionResult = client.run(jobGraph, client.getClass().getClassLoader());
                jobResult.setData(jobResult.JOB_ID_KEY, jobExecutionResult.getJobID().toString());
            }

            return jobResult;

        } catch (Exception e) {
            return JobResult.createErrorResult(e);
        }
    }

    private JobResult submitSqlJobForBatch(JobClient jobClient){
        throw new RdosException("not support flink sql job for batch type.");
    }

    public JobResult cancleJob(String jobId) {

        JobID jobID = new JobID(StringUtils.hexStringToByte(jobId));
        FiniteDuration clientTimeout = AkkaUtils.getDefaultClientTimeout();

        ActorGateway jobManager = null;
        Object rc = null;
        try {
            jobManager = client.getJobManagerGateway();
            Future<Object> response = jobManager.ask(new JobManagerMessages.CancelJob(jobID), clientTimeout);
            rc = Await.result(response, clientTimeout);
        } catch (Exception e) {
            logger.error("cancle job exception.", e);
            return  JobResult.createErrorResult(e);
        }

        if (rc instanceof JobManagerMessages.CancellationSuccess) {
            logger.info("cancle job {} success.", jobId);
        } else if (rc instanceof JobManagerMessages.CancellationFailure) {
            return JobResult.createErrorResult("Canceling the job with ID " + jobId + " failed. cause:"
                    + ((JobManagerMessages.CancellationFailure) rc).cause());
        } else {
            return  JobResult.createErrorResult("Unexpected response: " + rc);
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
        String reqUrl = getReqUrl() + "/jobs/" + jobId;
        String response = HttpClient.get(reqUrl);
        if(response == null){
            return null;
        }

        try{
            JsonObject jsonObject = objectMapper.readValue(response, JsonObject.class);
            String state = jsonObject.get("state").getAsString();
            if(state == null){
                return null;
            }

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
        String response = HttpClient.get(reqUrl);
        return response;
    }

    /**
     * 获取jobMgr-web地址
     * @return
     */
    private String getReqUrl(){
        return client.getWebInterfaceURL();
    }


    private StreamExecutionEnvironment getStreamExeEnv(JobClient jobClient) throws IOException {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        boolean setParallelism = false;

        for(Operator operator : jobClient.getOperators()){
            if(operator instanceof ParamsOperator){
                ParamsOperator paramsOperator = (ParamsOperator) operator;
                FlinkUtil.openCheckpoint(env, paramsOperator.getProperties());
                setParallelism = FlinkUtil.setEnvParallelism(env, paramsOperator.getProperties());
            }
        }

        if(!setParallelism){//默认的并行度是1
            env.setParallelism(1);
        }

        return env;
    }

}
