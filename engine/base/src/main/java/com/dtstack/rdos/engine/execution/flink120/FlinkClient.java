package com.dtstack.rdos.engine.execution.flink120;

import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.operator.*;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.pojo.PropertyConstant;
import com.dtstack.rdos.engine.execution.flink120.source.IStreamSourceGener;
import com.dtstack.rdos.engine.execution.flink120.util.FlinkUtil;
import com.dtstack.rdos.engine.execution.exception.RdosException;
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
import org.apache.flink.runtime.messages.JobManagerMessages;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class FlinkClient extends AbsClient {

    private static final Logger logger = LoggerFactory.getLogger(FlinkClient.class);

    private String jobMgrHost;

    private int jobMgrPort;

    private int restPort;

    private ClusterClient client;

    //默认使用异步提交
    private boolean isDetact = true;

    /**
     * 注意 StandaloneClusterClient 是否适用于Yarn方式
     * FIXME
     * @return
     * @throws Exception
     */
    public void initClusterClient(String host, int port){

        this.jobMgrHost = host;
        this.jobMgrPort = port;

        Configuration config = new Configuration();
        config.setString(ConfigConstants.JOB_MANAGER_IPC_ADDRESS_KEY, host);
        config.setInteger(ConfigConstants.JOB_MANAGER_IPC_PORT_KEY, port);

        StandaloneClusterDescriptor descriptor = new StandaloneClusterDescriptor(config);
        StandaloneClusterClient clusterClient = descriptor.retrieve(null);
        clusterClient.setDetached(isDetact);
        client = clusterClient;
    }

    /**
     * 根据zk获取cluster
     * FIXME 未测试过
     * @param zkNamespace
     */
    public void initClusterClient(String zkNamespace){
        Configuration config = new Configuration();
        config.setString(HighAvailabilityOptions.HA_CLUSTER_ID, zkNamespace);

        StandaloneClusterDescriptor descriptor = new StandaloneClusterDescriptor(config);
        StandaloneClusterClient clusterClient = descriptor.retrieve(null);
        clusterClient.setDetached(isDetact);
        client = clusterClient;
    }

    public void init(Properties prop) {

        Object host = prop.get("host");
        Object port = prop.get("port");
        Object zkNamespace = prop.get("zkNamespace");

        Preconditions.checkState(host != null || zkNamespace != null,
                "flink client can not init for host and zkNamespace is null at the same time.");

        if(zkNamespace != null){//优先使用zk
            initClusterClient((String) zkNamespace);
        }else{
            Preconditions.checkState(port != null,
                    "flink client can not init for specil host but port is null.");

            Integer portVal = Integer.valueOf((String)port);
            initClusterClient((String)host, portVal);
        }

    }

    /***
     * 提交 job-jar 到cluster, jobname 需要在job-jar里面指定
     * FIXME 该提交功能也需要支持指定savepoint 方式启动
     * @param properties
     * @return
     */
    public JobResult submitJobWithJar(Properties properties) {

        Object jarPath = properties.get("jarpath");
        if(jarPath == null){
            logger.error("can not submit a job without jarpath, please check it");
            JobResult jobResult = JobResult.newInstance(true);
            jobResult.setData("errMsg", "can not submit a job without jarpath, please check it");
            return jobResult;
        }

        PackagedProgram packagedProgram = null;

        String entryPointClass = properties.getProperty("class");//如果jar包里面未指定mainclass,需要设置该参数
        String[] programArgs = new String[0];//FIXME 该参数设置暂时未设置
        List<URL> classpaths = new ArrayList<>();//FIXME 该参数设置暂时未设置
        SavepointRestoreSettings spSettings = buildSavepointSetting(properties);

        try{
            //FIXME 参数设置细化
            packagedProgram = FlinkUtil.buildProgram((String) jarPath, classpaths, entryPointClass, programArgs, spSettings);
        }catch (Exception e){
            JobResult jobResult = JobResult.newInstance(true);
            jobResult.setData("errMsg", e.getMessage());
            logger.error("", e);
            return jobResult;
        }

        Integer runParallelism = properties.get("parallelism") == null ? 1 : (Integer)properties.get("parallelism");
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
        jobResult.setData("jobid", result.getJobID().toString());

        return jobResult;
    }

    public SavepointRestoreSettings buildSavepointSetting(Properties properties){

        if(properties == null){
            return SavepointRestoreSettings.none();
        }

        if(properties.contains("fromSavepoint")){ //有指定savepoint
            String savepointPath = properties.getProperty("fromSavepoint");
            String stateStr = properties.getProperty("allowNonRestoredState");
            boolean allowNonRestoredState = BooleanUtils.toBoolean(stateStr);
            return SavepointRestoreSettings.forPath(savepointPath, allowNonRestoredState);
        }else{
            return SavepointRestoreSettings.none();
        }
    }


    /**
     * FIXME flink sql提交需要使用remoteEnv
     * 区分出source, transformation, sink
     * FIXME 目前source 只支持kafka
     * FIXME 注意流程的顺序--校验顺序
     * 1: 添加数据源 -- 可能多个
     * 2: 注册udf--fuc/table -- 可能多个---必须附带jar(做校验)
     * 3: 调用sql
     * 4: 添加sink
     * @param jobClient
     * @return
     */
    public JobResult submitSqlJob(JobClient jobClient) throws IOException {

        StreamExecutionEnvironment env = getRemoteStreamExeEnv(jobClient);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.getTableEnvironment(env);
        Table resultTable = null; //FIXME 注意现在只能使用一个result

        int currStep = 0;
        ParamsOperator paramsOperator = null;
        List<String> jarPathList = new ArrayList<>();

        for(Operator operator : jobClient.getOperators()){
            if(operator instanceof CreateSourceOperator){//添加数据源,注册指定table
                if(currStep > 1){
                    throw new RdosException("sql job order setting err. cause of CreateSourceOperator");
                }

                currStep = 1;
                IStreamSourceGener sourceGener = FlinkUtil.getStreamSourceGener(FlinkUtil.ESourceType.KAFKA09);
                CreateSourceOperator tmpOperator = (CreateSourceOperator) operator;
                StreamTableSource tableSource = (StreamTableSource) sourceGener.genStreamSource
                        (tmpOperator.getProperties(), tmpOperator.getFields(), tmpOperator.getFieldTypes());

                tableEnv.registerTableSource(tmpOperator.getName(), tableSource);

            }else if(operator instanceof CreateFunctionOperator){//注册自定义func
                if(currStep > 2){
                    throw new RdosException("sql job order setting err. cause of CreateFunctionOperator");
                }

                currStep = 2;
                CreateFunctionOperator tmpOperator = (CreateFunctionOperator) operator;
                FlinkUtil.registerUDF(tmpOperator.getType(), tmpOperator.getClassName(), tmpOperator.getName(), tableEnv);

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

            }else if(operator instanceof AddJarOperator){
                AddJarOperator addJarOperator = (AddJarOperator) operator;
                jarPathList.add(addJarOperator.getJarPath());

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

            JobExecutionResult exeResult = client.run(jobGraph, client.getClass().getClassLoader());
            JobResult jobResult = JobResult.newInstance(false);
            jobResult.setData("jobId", exeResult.getJobID().toString());
            return jobResult;

        } catch (Exception e) {
            return JobResult.createErrorResult(e);
        }

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
     * 考虑直接调用rest api直接返回
     * @param jobId
     * @return
     */
    public String getJobStatus(String jobId) {
        return null;
    }


    private StreamExecutionEnvironment getRemoteStreamExeEnv(JobClient jobClient) throws IOException {
        AddJarOperator addJarOperator = null;
        for(Operator operator : jobClient.getOperators()){
            if(operator instanceof  AddJarOperator){
                addJarOperator = (AddJarOperator) operator;
                break;
            }
        }

        StreamExecutionEnvironment env = null;
        if(addJarOperator != null){
            File jarFile = FlinkUtil.downloadJar(addJarOperator.getJarPath());
            env = StreamExecutionEnvironment.createRemoteEnvironment(jobMgrHost, jobMgrPort, jarFile.getAbsolutePath());
        }else{
            env = StreamExecutionEnvironment.createRemoteEnvironment(jobMgrHost, jobMgrPort);
        }

        for(Operator operator : jobClient.getOperators()){
            if(operator instanceof ParamsOperator){
                ParamsOperator paramsOperator = (ParamsOperator) operator;
                openCheckpoint(env, paramsOperator.getProperties());
            }
        }

        return env;
    }

    /**
     * 开启checkpoint
     * @param env
     * @throws IOException
     */
    public static void openCheckpoint(StreamExecutionEnvironment env, Properties properties) throws IOException {

        if(properties == null){
            return;
        }

        //设置了时间间隔才表明开启了checkpoint
        if(properties.getProperty(PropertyConstant.FLINK_CHECKPOINT_INTERVAL) == null){
            return;
        }else{
            Long interval = Long.valueOf(properties.getProperty(PropertyConstant.FLINK_CHECKPOINT_INTERVAL));
            //start checkpoint every ${interval}
            env.enableCheckpointing(interval);
        }

        String checkMode = properties.getProperty(PropertyConstant.FLINK_CHECKPOINT_MODE);
        if(checkMode != null){
            if(checkMode.equalsIgnoreCase("EXACTLY_ONCE")){
                env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
            }else if(checkMode.equalsIgnoreCase("AT_LEAST_ONCE")){
                env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
            }else{
                throw new RdosException("not support of FLINK_CHECKPOINT_MODE :" + checkMode);
            }
        }

        String checkpointTimeoutStr = properties.getProperty(PropertyConstant.FLINK_CHECKPOINT_TIMEOUT);
        if(checkpointTimeoutStr != null){
            Long checkpointTimeout = Long.valueOf(checkpointTimeoutStr);
            //checkpoints have to complete within one min,or are discard
            env.getCheckpointConfig().setCheckpointTimeout(checkpointTimeout);
        }

        String maxConcurrCheckpointsStr = properties.getProperty(PropertyConstant.FLINK_MAXCONCURRENTCHECKPOINTS);
        if(maxConcurrCheckpointsStr != null){
            Integer maxConcurrCheckpoints = Integer.valueOf(maxConcurrCheckpointsStr);
            //allow only one checkpoint to be int porgress at the same time
            env.getCheckpointConfig().setMaxConcurrentCheckpoints(maxConcurrCheckpoints);
        }

        String cleanupModeStr = properties.getProperty(PropertyConstant.FLINK_CHECKPOINT_CLEANUPMODE);
        if(cleanupModeStr != null){//设置在cancle job情况下checkpoint是否被保存
            if("true".equalsIgnoreCase(cleanupModeStr)){
                env.getCheckpointConfig().enableExternalizedCheckpoints(
                        CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);
            }else if("false".equalsIgnoreCase(cleanupModeStr)){
                env.getCheckpointConfig().enableExternalizedCheckpoints(
                        CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
            }else{
                throw new RdosException("not support value of cleanup mode :" + cleanupModeStr);
            }
        }

        String backendPath = properties.getProperty(PropertyConstant.FLINK_CHECKPOINT_DATAURI);
        if(backendPath != null){
            //set checkpoint save path on file system, 根据实际的需求设定文件路径,hdfs://, file://
            env.setStateBackend(new FsStateBackend(backendPath));
        }

    }

}
