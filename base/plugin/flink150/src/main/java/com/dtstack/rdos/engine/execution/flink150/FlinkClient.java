package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobParam;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
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
import com.dtstack.rdos.engine.execution.flink150.enums.Deploy;
import com.dtstack.rdos.engine.execution.flink150.sink.batch.BatchSinkFactory;
import com.dtstack.rdos.engine.execution.flink150.sink.stream.StreamSinkFactory;
import com.dtstack.rdos.engine.execution.flink150.source.batch.BatchSourceFactory;
import com.dtstack.rdos.engine.execution.flink150.source.stream.StreamSourceFactory;
import com.dtstack.rdos.engine.execution.flink150.util.FlinkUtil;
import com.dtstack.rdos.engine.execution.flink150.util.HadoopConf;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobSubmissionResult;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.optimizer.DataStatistics;
import org.apache.flink.optimizer.Optimizer;
import org.apache.flink.optimizer.plan.OptimizedPlan;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sources.BatchTableSource;
import org.apache.flink.util.Preconditions;
import org.apache.flink.yarn.YarnClusterClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.http.HttpStatus;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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

    private static final int failureRate = 3;

    private static final int failureInterval = 6; //min

    private static final int delayInterval = 10; //sec

    //FIXME key值需要根据客户端传输名称调整
    private static final String FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY = "allowNonRestoredState";

    private String tmpFileDirPath = "./tmp";

    //http://${addr}/proxy/${applicationId}/
    private static final String FLINK_URL_FORMAT = "http://%s/proxy/%s/";

    private static final String YARN_RM_WEB_KEY_PREFIX = "yarn.resourcemanager.webapp.address.";

    private FlinkConfig flinkConfig;

    private org.apache.hadoop.conf.Configuration hadoopConf;

    private YarnConfiguration yarnConf;

    private FlinkClientBuilder flinkClientBuilder;

    private SyncPluginInfo syncPluginInfo;

    private SqlPluginInfo sqlPluginInfo;

    private ClusterClient client;

    /**客户端是否处于可用状态*/
    private AtomicBoolean isClientOn = new AtomicBoolean(false);

    private ScheduledExecutorService yarnMonitorES;

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
        if (flinkConfig.getClusterMode().equals(Deploy.yarn.name())){
            yarnMonitorES = Executors.newSingleThreadScheduledExecutor();

            //启动守护线程---用于获取当前application状态和更新flink对应的application
            yarnMonitorES.submit(new YarnAppStatusMonitor(this, yarnMonitorES));
        }
    }

    public void initClient(){
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
    public JobResult submitJobWithJar(JobClient jobClient) {
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
            JobResult jobResult = JobResult.createErrorResult(e);
            logger.error("", e);
            return jobResult;
        }

        //只有当程序本身没有指定并行度的时候该参数才生效
        Integer runParallelism = FlinkUtil.getJobParallelism(jobClient.getConfProperties());
        JobSubmissionResult result = null;

        try {
            result = client.run(packagedProgram, runParallelism);
        }catch (Exception e){
            logger.error("", e);
            return JobResult.createErrorResult(e);
        }finally {
            packagedProgram.deleteExtractedLibraries();
        }

        if (result.isJobExecutionResult()) {
            logger.info("Program execution finished");
            JobExecutionResult execResult = result.getJobExecutionResult();
            logger.info("Job with JobID " + execResult.getJobID() + " has finished.");
            logger.info("Job Runtime: " + execResult.getNetRuntime() + " ms");
            Map<String, Object> accumulatorsResult = execResult.getAllAccumulatorResults();
            if (accumulatorsResult.size() > 0) {
                System.out.println("Accumulator Results: ");
                //System.out.println(AccumulatorHelper.getResultsFormated(accumulatorsResult));
            }
        } else {
            logger.info("Job has been submitted with JobID " + result.getJobID());
        }

        return JobResult.createSuccessResult(result.getJobID().toString());
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
     * 1: 不再对操作顺序做限制
     * 2：不再限制输入源数量
     * 3：不再限制输出源数量
     * @param jobClient
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private JobResult submitSqlJobForStream(JobClient jobClient) throws IOException, ClassNotFoundException{

    	Properties confProperties = jobClient.getConfProperties();
        StreamExecutionEnvironment env = getStreamExeEnv(confProperties);
        FlinkUtil.setStreamTimeCharacteristic(env, confProperties);
        FlinkUtil.openCheckpoint(env, confProperties);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.getTableEnvironment(env);

        URLClassLoader classLoader = null;
        List<String> jarPathList = new ArrayList<>();
        List<URL> jarURList = Lists.newArrayList();
        Set<String> classPathSet = Sets.newHashSet();

        try {
            for(Operator operator : jobClient.getOperators()){
                if(operator instanceof AddJarOperator){

                    AddJarOperator addJarOperator = (AddJarOperator) operator;
                    String addFilePath = addJarOperator.getJarPath();
                    File tmpFile = FlinkUtil.downloadJar(addFilePath, tmpFileDirPath, hadoopConf);
                    jarURList.add(tmpFile.toURI().toURL());
                    jarPathList.add(tmpFile.getAbsolutePath());

                }else if(operator instanceof CreateSourceOperator){//添加数据源,注册指定table

                    CreateSourceOperator sourceOperator = (CreateSourceOperator) operator;
                    Table table = StreamSourceFactory.getStreamSource(sourceOperator, env, tableEnv, sqlPluginInfo);
                    tableEnv.registerTable(sourceOperator.getName(), table);

                    String sourceType = sourceOperator.getType() + StreamSourceFactory.SUFFIX_JAR;
                    String remoteJarPath = sqlPluginInfo.getRemoteJarFilePath(sourceType);
                    classPathSet.add(remoteJarPath);

                }else if(operator instanceof CreateFunctionOperator){//注册自定义func

                    CreateFunctionOperator tmpOperator = (CreateFunctionOperator) operator;
                    //需要把字节码加载进来
                    if(classLoader == null){
                        classLoader = FlinkUtil.createNewClassLoader(jarURList, this.getClass().getClassLoader());
                    }

                    classLoader.loadClass(tmpOperator.getClassName());
                    FlinkUtil.registerUDF(tmpOperator.getType(), tmpOperator.getClassName(), tmpOperator.getName(),
                            tableEnv, classLoader);

                }else if(operator instanceof ExecutionOperator){

                    String sql = operator.getSql();
                    if(sql.toLowerCase().contains("insert")){
                        tableEnv.sqlUpdate(sql);
                    }else{
                        tableEnv.sql(sql);
                    }

                }else if(operator instanceof StreamCreateResultOperator){

                    StreamCreateResultOperator resultOperator = (StreamCreateResultOperator) operator;
                    TableSink tableSink = StreamSinkFactory.getTableSink(resultOperator, sqlPluginInfo);
                    //只需要注册到tableEnv即可 不再主动写入到sink中,所有操作均在sql中完成
                    TypeInformation[] flinkTypes = FlinkUtil.transformTypes(resultOperator.getFieldTypes());
                    tableEnv.registerTableSink(resultOperator.getName(), resultOperator.getFields(), flinkTypes, tableSink);

                    String sinkType = resultOperator.getType() + StreamSinkFactory.SUFFIX_JAR;
                    String remoteJarPath = sqlPluginInfo.getRemoteJarFilePath(sinkType);
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
                URL url = new URL(SqlPluginInfo.getFileURLFormat(remoteJarPth));
                classPathList.add(url);
            }

            jobGraph.setClasspaths(classPathList);
            JobSubmissionResult submissionResult = client.runDetached(jobGraph, client.getClass().getClassLoader());
            return JobResult.createSuccessResult(submissionResult.getJobID().toString());
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
                    File tmpFile = FlinkUtil.downloadJar(addFilePath, tmpFileDirPath, hadoopConf);
                    jarURList.add(tmpFile.toURI().toURL());
                    jarPathList.add(tmpFile.getAbsolutePath());

                }else if(operator instanceof BatchCreateSourceOperator){
                    if(currStep > 1){
                        throw new RdosException("sql job order setting err. cause of BatchCreateSourceOperator");
                    }

                    currStep = 1;

                    BatchCreateSourceOperator sourceOperator = (BatchCreateSourceOperator) operator;
                    BatchTableSource tableSource = BatchSourceFactory.getBatchSource(sourceOperator, sqlPluginInfo);
                    tableEnv.registerTableSource(sourceOperator.getName(), tableSource);

                    String sourceType = sourceOperator.getType() + BatchSourceFactory.SUFFIX_JAR;
                    String remoteJarPath = sqlPluginInfo.getRemoteJarFilePath(sourceType);
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
                    TableSink tableSink = BatchSinkFactory.getTableSink(sinkOperator, sqlPluginInfo);
                    resultTable.writeToSink(tableSink);

                    String sinkType = sinkOperator.getType() + BatchSinkFactory.SUFFIX_JAR;
                    String remoteJarPath = sqlPluginInfo.getRemoteJarFilePath(sinkType);
                    classPathSet.add(remoteJarPath);
                }
            }


            Plan plan = env.createProgramPlan();
            Configuration configuration = client.getFlinkConfiguration();
            Optimizer pc = new Optimizer(new DataStatistics(), configuration);
            OptimizedPlan op = pc.compile(plan);

            List<URL> classPathList = Lists.newArrayList();
            for(String jarPath : classPathSet){
                URL jarURL = new URL(SqlPluginInfo.getFileURLFormat(jarPath));
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
    	if(Strings.isNullOrEmpty(jobId)){
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
            logger.error("", e);
            return RdosTaskStatus.FINISHED;
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
            String addr = hadoopConf.get(key);
            String appId = ((YarnClusterClient) client).getApplicationId().toString();
            url = String.format(FLINK_URL_FORMAT, addr, appId);
        }catch (Exception e){
            url = client.getWebInterfaceURL();
        }

        logger.info("get req url=" + url);
        return url;
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


    @Override
    public JobResult submitSyncJob(JobClient jobClient) {
        //使用flink作为数据同步调用的其实是提交mr--job
        AddJarOperator addjarOperator = syncPluginInfo.createAddJarOperator();
        jobClient.addOperator(addjarOperator);

        List<String> programArgList = syncPluginInfo.createSyncPluginArgs(jobClient, this);
        List<URL> classPaths = syncPluginInfo.getClassPaths(programArgList);

        return submitJobWithJar(jobClient, classPaths, programArgList);
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

        if(!isClientOn.get()){
            return null;
        }

        String slotInfo = getMessageByHttp(FlinkStandaloneRestParseUtil.SLOTS_INFO);
        FlinkResourceInfo resourceInfo = FlinkStandaloneRestParseUtil.getAvailSlots(slotInfo);
        if(resourceInfo == null){
            logger.error("---flink cluster maybe down.----");
            resourceInfo = new FlinkResourceInfo();
        }

        return resourceInfo;
    }

    private boolean existsJobOnFlink(String engineJobId){
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
