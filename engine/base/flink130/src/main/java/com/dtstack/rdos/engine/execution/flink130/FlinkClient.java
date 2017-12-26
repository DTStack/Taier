package com.dtstack.rdos.engine.execution.flink130;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.enumeration.Restoration;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchAddJarOperator;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchCreateResultOperator;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchCreateSourceOperator;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchExecutionOperator;
import com.dtstack.rdos.engine.execution.base.operator.stream.AddJarOperator;
import com.dtstack.rdos.engine.execution.base.operator.stream.CreateFunctionOperator;
import com.dtstack.rdos.engine.execution.base.operator.stream.CreateSourceOperator;
import com.dtstack.rdos.engine.execution.base.operator.stream.ExecutionOperator;
import com.dtstack.rdos.engine.execution.base.operator.stream.StreamCreateResultOperator;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.flink130.sink.batch.BatchSinkFactory;
import com.dtstack.rdos.engine.execution.flink130.sink.stream.StreamSinkFactory;
import com.dtstack.rdos.engine.execution.flink130.source.batch.BatchSourceFactory;
import com.dtstack.rdos.engine.execution.flink130.source.stream.StreamSourceFactory;
import com.dtstack.rdos.engine.execution.flink130.util.FlinkUtil;
import com.dtstack.rdos.engine.execution.flink130.util.HadoopConf;
import com.dtstack.rdos.engine.execution.flink130.util.PluginSourceUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobSubmissionResult;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.common.accumulators.AccumulatorHelper;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.ExecutionEnvironment;
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
import org.apache.flink.core.fs.Path;
import org.apache.flink.optimizer.DataStatistics;
import org.apache.flink.optimizer.Optimizer;
import org.apache.flink.optimizer.plan.OptimizedPlan;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sources.BatchTableSource;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.util.Preconditions;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterClient;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.http.HttpStatus;
import org.apache.http.conn.HttpHostConnectException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * Date: 2017/2/20
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
public class FlinkClient extends AbsClient {

    private static final Logger logger = LoggerFactory.getLogger(FlinkClient.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String FLINK_ENGINE_JOBID_KEY = "engineJobId";

    public static final String FLINK_JOB_FROMSAVEPOINT_KEY = "isRestoration";

    private static final String sqlPluginDirName = "sqlplugin";

    private static final String syncPluginDirName = "syncplugin";

    private static final String YARN_CLUSTER_MODE = "yarn";

    private static final String STANDALONE_CLUSTER_MODE = "standalone";

    /**同步数据插件jar名称*/
    private static final String syncJarFileName = "flinkx.jar";

    private static final int failureRate = 3;

    private static final int failureInterval = 6; //min

    private static final int delayInterval = 10; //sec

    //FIXME key值需要根据客户端传输名称调整
    public static final String FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY = "allowNonRestoredState";

    public static String sp = File.separator;

    public String tmpFileDirPath = "./tmp";

    private String jobMgrHost;

    private int jobMgrPort;

    private ClusterClient client;

    //默认使用异步提交
    private boolean isDetact = true;

    // 同步模块在flink集群加载插件
    private String flinkRemoteSyncPluginRoot;

    // 同步模块的monitorAddress, 用于获取错误记录数等信息
    private String monitorAddress;

    private org.apache.hadoop.conf.Configuration hadoopConf;

    @Override
    public void init(Properties prop) throws Exception {

        FlinkConfig flinkConfig = objectMapper.readValue(objectMapper.writeValueAsBytes(prop), FlinkConfig.class);
        String clusterMode = flinkConfig.getClusterMode();
        if(StringUtils.isEmpty(clusterMode)) {
            clusterMode = STANDALONE_CLUSTER_MODE;
        }

        tmpFileDirPath = flinkConfig.getJarTmpDir();
        String localSqlPluginDir = getSqlPluginDir(flinkConfig.getFlinkPluginRoot());
        File sqlPluginDirFile = new File(localSqlPluginDir);

        if(!sqlPluginDirFile.exists() || !sqlPluginDirFile.isDirectory()){
            throw new RdosException("not exists flink sql dir:" + localSqlPluginDir + ", please check it!!!");
        }

        String remoteSqlPluginDir = getSqlPluginDir(flinkConfig.getRemotePluginRootDir());
        PluginSourceUtil.setSourceJarRootDir(localSqlPluginDir);
        PluginSourceUtil.setRemoteSourceJarRootDir(remoteSqlPluginDir);

        Preconditions.checkNotNull(tmpFileDirPath, "you need to set tmp file path for jar download.");
        Preconditions.checkState(flinkConfig.getFlinkJobMgrUrl() != null || flinkConfig.getFlinkZkNamespace() != null,
                "flink client can not init for host and zkNamespace is null at the same time.");

        if(clusterMode.equals(STANDALONE_CLUSTER_MODE)) {
            if(flinkConfig.getFlinkZkNamespace() != null){//优先使用zk
                Preconditions.checkNotNull(flinkConfig.getFlinkHighAvailabilityStorageDir(), "you need to set high availability storage dir...");
                initClusterClientByZK(flinkConfig.getFlinkZkNamespace(), flinkConfig.getFlinkZkAddress(), flinkConfig.getFlinkClusterId(),flinkConfig.getFlinkHighAvailabilityStorageDir());
            }else{
                initClusterClientByURL(flinkConfig.getFlinkJobMgrUrl());
            }
        } else if (clusterMode.equals(YARN_CLUSTER_MODE)) {
            initYarnClusterClient(flinkConfig);
        } else {
            throw new RdosException("Unsupported clusterMode: " + clusterMode);
        }

        String localSyncPluginDir =  getSyncPluginDir(flinkConfig.getFlinkPluginRoot());
        FlinkUtil.setLocalSyncFileDir(localSyncPluginDir);

        String remoteSyncPluginDir = getSyncPluginDir(flinkConfig.getRemotePluginRootDir());
        this.flinkRemoteSyncPluginRoot = remoteSyncPluginDir;
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
    public void initYarnClusterClient(FlinkConfig flinkConfig){

        hadoopConf = HadoopConf.getYarnConfiguration();
        AbstractYarnClusterDescriptor clusterDescriptor = new YarnClusterDescriptor();
        try {
            Field confField = AbstractYarnClusterDescriptor.class.getDeclaredField("conf");
            confField.setAccessible(true);
            haYarnConf();
            confField.set(clusterDescriptor, hadoopConf);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RdosException(e.getMessage());
        }

        Configuration config = new Configuration();
        config.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.ZOOKEEPER.toString());
        config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM, flinkConfig.getFlinkZkAddress());
        config.setString(HighAvailabilityOptions.HA_STORAGE_PATH, flinkConfig.getFlinkHighAvailabilityStorageDir());

        if(System.getenv("HADOOP_CONF_DIR") != null) {
            config.setString(ConfigConstants.PATH_HADOOP_CONFIG, System.getenv("HADOOP_CONF_DIR"));
        }

        if(flinkConfig.getFlinkZkNamespace() != null){//不设置默认值"/flink"
            config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_ROOT, flinkConfig.getFlinkZkNamespace());
        }

        if(flinkConfig.getFlinkClusterId() != null){//不设置默认值"/default"
            config.setString(HighAvailabilityOptions.HA_CLUSTER_ID, flinkConfig.getFlinkClusterId());
        }

        YarnClient yarnClient = YarnClient.createYarnClient();
        yarnClient.init(hadoopConf);
        yarnClient.start();
        String applicationId = null;

        try {
            Set<String> set = new HashSet<>();
            set.add("Apache Flink");
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.RUNNING);
            List<ApplicationReport> reportList = yarnClient.getApplications(set, enumSet);

            int maxMemory = -1;
            int maxCores = -1;
            for(ApplicationReport report : reportList) {
                if(!report.getName().startsWith("Flink session")){
                    continue;
                }

                int thisMemory = report.getApplicationResourceUsageReport().getNeededResources().getMemory();
                int thisCores = report.getApplicationResourceUsageReport().getNeededResources().getVirtualCores();
                if(thisMemory > maxMemory || thisMemory == maxMemory && thisCores > maxCores) {
                    maxMemory = thisMemory;
                    maxCores = thisCores;
                    applicationId = report.getApplicationId().toString();
                }

            }

            if(StringUtils.isEmpty(applicationId)) {
                logger.error("No flink session found on yarn cluster.");
                throw new RdosException("No flink session found on yarn cluster.");
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RdosException(e.getMessage());
        }

        yarnClient.stop();

        clusterDescriptor.setFlinkConfiguration(config);
        YarnClusterClient clusterClient = clusterDescriptor.retrieve(applicationId);
        clusterClient.setDetached(isDetact);

        client = clusterClient;

    }


    /**
     * 根据zk获取clusterclient
     * @param zkNamespace
     */
    public void initClusterClientByZK(String zkNamespace, String zkAddress, String clusterId,String flinkHighAvailabilityStorageDir){

        Configuration config = new Configuration();
        config.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.ZOOKEEPER.toString());
        config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM, zkAddress);
        config.setString(HighAvailabilityOptions.HA_STORAGE_PATH, flinkHighAvailabilityStorageDir);
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

    /***
     * 提交 job-jar 到 cluster 的方式, jobname 需要在job-jar里面指定
     * @param jobClient
     * @return
     */
    @Override
    public JobResult submitJobWithJar(JobClient jobClient) {

        if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
            if(existsJobOnFlink(jobClient.getEngineTaskId())){
                return JobResult.createSuccessResult(jobClient.getEngineTaskId());
            }
        }

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
            programArgList.addAll(Arrays.asList(args.split("\\s+")));
        }

        if(StringUtils.isNotEmpty(monitorAddress)) {
            programArgList.add("-monitor");
            programArgList.add(monitorAddress);
        }

        List<URL> classpaths = flinkRemoteSyncPluginRoot != null ? FlinkUtil.getUserClassPath(programArgList, flinkRemoteSyncPluginRoot) : new ArrayList<>();

        SavepointRestoreSettings spSettings = buildSavepointSetting(jobClient);
        try{
            String[] programArgs = programArgList.toArray(new String[programArgList.size()]);
            packagedProgram = FlinkUtil.buildProgram((String) jarPath, tmpFileDirPath, classpaths, entryPointClass, programArgs, spSettings);
        }catch (Throwable e){
            JobResult jobResult = JobResult.createErrorResult(e);
            e.printStackTrace();
            logger.error("", e);
            return jobResult;
        }

        //只有当程序本身没有指定并行度的时候该参数才生效
        Integer runParallelism = FlinkUtil.getJobParallelism(jobClient.getConfProperties());
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

        JobResult jobResult = JobResult.createSuccessResult(result.getJobID().toString());

        return jobResult;
    }

    public Properties adaptToJarSubmit(JobClient jobClient){
        Properties properties = new Properties();
        for(Operator operator : jobClient.getOperators()){
            if(operator instanceof AddJarOperator){
                AddJarOperator addjarOperator = (AddJarOperator) operator;
                properties.setProperty(JOB_JAR_PATH_KEY, addjarOperator.getJarPath());

                if(addjarOperator.getMainClass() != null){
                    properties.setProperty(JOB_MAIN_CLASS_KEY, addjarOperator.getMainClass());
                }
                break;
            }else if(operator instanceof BatchAddJarOperator){
                BatchAddJarOperator addjarOperator = (BatchAddJarOperator) operator;
                properties.setProperty(JOB_JAR_PATH_KEY, addjarOperator.getJarPath());
                break;
            }
        }

        if(!properties.containsKey(JOB_JAR_PATH_KEY)){
            throw new RdosException("submit type of MR need to add jar operator.");
        }

        properties.setProperty(JOB_APP_NAME_KEY, jobClient.getJobName());

        if(jobClient.getClassArgs() != null){
            properties.setProperty(JOB_EXE_ARGS, jobClient.getClassArgs());
        }

        return properties;
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

    @Override
    public JobResult submitSqlJob(JobClient jobClient) throws IOException, ClassNotFoundException {

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
     * 区分出source, transformation, sink
     * FIXME 目前source 只支持kafka
     * 1: 添加数据源 -- 可能多个
     * 2: 注册udf--fuc/table -- 可能多个---必须附带jar(做校验)
     * 3: 调用sql
     * 4: 添加sink
     * @param jobClient
     * @return
     */
    private JobResult submitSqlJobForStream(JobClient jobClient) throws IOException, ClassNotFoundException{

    	Properties confProperties = jobClient.getConfProperties();
        StreamExecutionEnvironment env = getStreamExeEnv(confProperties);
        FlinkUtil.openCheckpoint(env, confProperties);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.getTableEnvironment(env);

        Table resultTable = null; //FIXME 注意现在只能使用一个result
        URLClassLoader classLoader = null;

        List<String> jarPathList = new ArrayList<>();
        List<URL> jarURList = Lists.newArrayList();
        Set<String> classPathSet = Sets.newHashSet();

        int currStep = 0;

        try {
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
                        throw new RdosException("sql job order setting err. cause of StreamCreateSourceOperator");
                    }

                    currStep = 1;
                    CreateSourceOperator sourceOperator = (CreateSourceOperator) operator;
                    StreamTableSource tableSource = StreamSourceFactory.getStreamSource(sourceOperator);
                    tableEnv.registerTableSource(sourceOperator.getName(), tableSource);

                    String sourceType = sourceOperator.getType() + StreamSourceFactory.SUFFIX_JAR;
                    String remoteJarPath = PluginSourceUtil.getRemoteJarFilePath(sourceType);
                    classPathSet.add(remoteJarPath);

                }else if(operator instanceof CreateFunctionOperator){//注册自定义func
                    if(currStep > 2){
                        throw new RdosException("sql job order setting err. cause of CreateFunctionOperator");
                    }

                    currStep = 2;
                    CreateFunctionOperator tmpOperator = (CreateFunctionOperator) operator;
                    //需要把字节码加载进来
                    if(classLoader == null){
                        classLoader = FlinkUtil.createNewClassLoader(jarURList, this.getClass().getClassLoader());
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

                }else if(operator instanceof StreamCreateResultOperator){
                    if(currStep > 4){
                        throw new RdosException("sql job order setting err. cause of StreamCreateResultOperator");
                    }

                    currStep = 4;
                    StreamCreateResultOperator resultOperator = (StreamCreateResultOperator) operator;
                    TableSink tableSink = StreamSinkFactory.getTableSink(resultOperator);
                    resultTable.writeToSink(tableSink);

                    String sinkType = resultOperator.getType() + StreamSinkFactory.SUFFIX_JAR;
                    String remoteJarPath = PluginSourceUtil.getRemoteJarFilePath(sinkType);
                    classPathSet.add(remoteJarPath);

                }else{
                    throw new RdosException("not support operator of " + operator.getClass().getName());
                }
            }

            //这里getStreamGraph() 和 getJobGraph()均是创建新的对象,方法命名让人疑惑.
            StreamGraph streamGraph = env.getStreamGraph();
            streamGraph.setJobName(jobClient.getJobName());
            JobGraph jobGraph = streamGraph.getJobGraph();

            SavepointRestoreSettings spRestoreSetting = buildSavepointSetting(jobClient);
            jobGraph.setSavepointRestoreSettings(spRestoreSetting);
            for(String jarFile : jarPathList){
                URI jarFileUri = new File(jarFile).getAbsoluteFile().toURI();
                jobGraph.addJar(new Path(jarFileUri));
            }

            List<URL> classPathList = Lists.newArrayList();
            for(String remoteJarPth : classPathSet){
                URL url = new URL(PluginSourceUtil.getFileURLFormat(remoteJarPth));
                classPathList.add(url);
            }

            jobGraph.setClasspaths(classPathList);
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
            logger.info("", e);
            return JobResult.createErrorResult(e);
        }finally {
            //如果包含了下载下来的临时jar文件则清理
            for(String path : jarPathList){
                try{
                    File file = new File(path);
                    if(file.exists()){
                        file.delete();
                    }

                }catch (Exception e1){
                    logger.error("", e1);
                }
            }
        }
    }

    private JobResult submitSqlJobForBatch(JobClient jobClient) throws FileNotFoundException, MalformedURLException, ClassNotFoundException {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tableEnv = BatchTableEnvironment.getTableEnvironment(env);
        Table resultTable = null;

        URLClassLoader classLoader = null;
        int currStep = 0;
        List<String> jarPathList = new ArrayList<>();
        List<URL> jarURList = Lists.newArrayList();
        Set<String> classPathSet = Sets.newHashSet();

        try{
            for(Operator operator : jobClient.getOperators()){

                if(operator instanceof BatchAddJarOperator){
                    if(currStep > 0){
                        throw new RdosException("sql job order setting err. cause of AddJarOperator");
                    }

                    BatchAddJarOperator addJarOperator = (BatchAddJarOperator) operator;
                    String addFilePath = addJarOperator.getJarPath();
                    File tmpFile = FlinkUtil.downloadJar(addFilePath, tmpFileDirPath);
                    jarURList.add(tmpFile.toURI().toURL());
                    jarPathList.add(tmpFile.getAbsolutePath());

                }else if(operator instanceof BatchCreateSourceOperator){
                    if(currStep > 1){
                        throw new RdosException("sql job order setting err. cause of BatchCreateSourceOperator");
                    }

                    currStep = 1;

                    BatchCreateSourceOperator sourceOperator = (BatchCreateSourceOperator) operator;
                    BatchTableSource tableSource = BatchSourceFactory.getBatchSource(sourceOperator);
                    tableEnv.registerTableSource(sourceOperator.getName(), tableSource);

                    String sourceType = sourceOperator.getType() + BatchSourceFactory.SUFFIX_JAR;
                    String remoteJarPath = PluginSourceUtil.getRemoteJarFilePath(sourceType);
                    classPathSet.add(remoteJarPath);

                }else if(operator instanceof CreateFunctionOperator){//注册自定义func
                    if(currStep > 2){
                        throw new RdosException("sql job order setting err. cause of CreateFunctionOperator");
                    }

                    currStep = 2;
                    CreateFunctionOperator tmpOperator = (CreateFunctionOperator) operator;
                    //需要把字节码加载进来
                    if(classLoader == null){
                        classLoader = FlinkUtil.createNewClassLoader(jarURList, this.getClass().getClassLoader());
                    }

                    classLoader.loadClass(tmpOperator.getClassName());
                    FlinkUtil.registerUDF(tmpOperator.getType(), tmpOperator.getClassName(), tmpOperator.getName(),
                            tableEnv, classLoader);

                }else if(operator instanceof BatchExecutionOperator){

                    if(currStep > 3){
                        throw new RdosException("sql job order setting err. cause of ExecutionOperator");
                    }

                    currStep = 3;
                    BatchExecutionOperator exeOperator = (BatchExecutionOperator) operator;
                    resultTable = tableEnv.sql(exeOperator.getSql());

                }else if(operator instanceof BatchCreateResultOperator){

                    if(currStep > 4){
                        throw new RdosException("sql job order setting err. cause of StreamCreateResultOperator");
                    }

                    currStep = 4;

                    BatchCreateResultOperator sinkOperator = (BatchCreateResultOperator) operator;
                    TableSink tableSink = BatchSinkFactory.getTableSink(sinkOperator);
                    resultTable.writeToSink(tableSink);

                    String sinkType = sinkOperator.getType() + BatchSinkFactory.SUFFIX_JAR;
                    String remoteJarPath = PluginSourceUtil.getRemoteJarFilePath(sinkType);
                    classPathSet.add(remoteJarPath);
                }
            }


            Plan plan = env.createProgramPlan();
            Configuration configuration = client.getFlinkConfiguration();
            Optimizer pc = new Optimizer(new DataStatistics(), configuration);
            OptimizedPlan op = pc.compile(plan);

            List<URL> classPathList = Lists.newArrayList();
            for(String jarPath : classPathSet){
                URL jarURL = new URL(PluginSourceUtil.getFileURLFormat(jarPath));
                classPathList.add(jarURL);
            }

            JobSubmissionResult submissionResult = client.run(op, jarURList, classPathList, this.getClass().getClassLoader());
            return JobResult.createSuccessResult(submissionResult.getJobID().toString());
        }catch (Exception e){
            logger.error("", e);
            return JobResult.createErrorResult(e);
        }

    }

    @Override
    public JobResult cancelJob(String jobId) {
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
    @Override
    public RdosTaskStatus getJobStatus(String jobId) {
    	if(jobId == null || "".equals(jobId)){
    		return null;
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
        } catch (HttpHostConnectException e){
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

        if(FlinkUtil.getMaxEnvParallelism(confProperties) > 0){
            env.setMaxParallelism(FlinkUtil.getMaxEnvParallelism(confProperties));
        }

        if(FlinkUtil.getBufferTimeoutMillis(confProperties) > 0){
            env.setBufferTimeout(FlinkUtil.getBufferTimeoutMillis(confProperties));
        }

        env.setRestartStrategy(RestartStrategies.failureRateRestart(
                failureRate, // 一个时间段内的最大失败次数
                Time.of(failureInterval, TimeUnit.MINUTES), // 衡量失败次数的是时间段
                Time.of(delayInterval, TimeUnit.SECONDS) // 间隔
        ));

        return env;
    }

	public String getSavepointPath(String engineTaskId){
        String reqUrl = getReqUrl() + "/jobs/" + engineTaskId + "/checkpoints";
        String response = null;
        try{
            response = PoolHttpClient.get(reqUrl);
        }catch (Exception e){
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


    @Override
    public JobResult submitSyncJob(JobClient jobClient) {
        //使用flink作为数据同步调用的其实是提交mr--job
        //需要构造出add jar
        AddJarOperator addjarOperator = new AddJarOperator();
        addjarOperator.setJarPath(syncJarFileName);
        jobClient.addOperator(addjarOperator);

        return submitJobWithJar(jobClient);
    }


    /**
     * 处理yarn HA的配置项
     */
    private void haYarnConf() {
        Iterator<Map.Entry<String, String>> iterator = hadoopConf.iterator();
        while(iterator.hasNext()) {
            Map.Entry<String,String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if(key.startsWith("yarn.resourcemanager.hostname.")) {
                String rm = key.substring("yarn.resourcemanager.hostname.".length());
                String addressKey = "yarn.resourcemanager.address." + rm;
                if(hadoopConf.get(addressKey) == null) {
                    hadoopConf.set(addressKey, value + ":" + YarnConfiguration.DEFAULT_RM_PORT);
                }
            }
        }
    }

	@Override
	public String getMessageByHttp(String path) {
        String reqUrl = String.format("%s%s", getReqUrl(), path);
        try {
            return PoolHttpClient.get(reqUrl);
        } catch (IOException e) {
            logger.error("", e);
            return null;
        }
    }

    @Override
    public String getJobLog(String jobId) {
        String exceptPath = String.format(FlinkStandaloneRestParseUtil.EXCEPTION_INFO, jobId);
        String except = getMessageByHttp(exceptPath);
        String jobPath = String.format(FlinkStandaloneRestParseUtil.JOB_INFO, jobId);
        String jobInfo = getMessageByHttp(jobPath);
        String accuPath = String.format(FlinkStandaloneRestParseUtil.JOB_ACCUMULATOR_INFO, jobId);
        String accuInfo = getMessageByHttp(accuPath);
        Map<String,String> retMap = new HashMap<>();
        retMap.put("except", except);
        retMap.put("jobInfo", jobInfo);
        retMap.put("accuInfo", accuInfo);

        try {
            return FlinkStandaloneRestParseUtil.parseEngineLog(retMap);
        } catch (IOException e) {
            logger.error("", e);
            return "get engine message error," + e.getMessage();
        }
    }

    @Override
    public EngineResourceInfo getAvailSlots() {
        String slotInfo = getMessageByHttp(FlinkStandaloneRestParseUtil.SLOTS_INFO);
        FlinkResourceInfo resourceInfo = FlinkStandaloneRestParseUtil.getAvailSlots(slotInfo);
        if(resourceInfo == null){
            logger.error("---flink cluster maybe down.----");
            resourceInfo = new FlinkResourceInfo();
        }

        return resourceInfo;
    }

    public boolean existsJobOnFlink(String engineJobId){
        RdosTaskStatus taskStatus = getJobStatus(engineJobId);
        if(taskStatus == null){
            return false;
        }

        if(taskStatus == RdosTaskStatus.RESTARTING
                || taskStatus == RdosTaskStatus.RUNNING){
            return true;
        }

        return false;
    }
}
