package com.dtstack.rdos.engine.execution.sparkyarn;

import com.clearspring.analytics.util.Lists;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchAddJarOperator;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;
import org.codehaus.jackson.map.ObjectMapper;
import org.datanucleus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by softfly on 17/8/10.
 */
public class SparkYarnClient extends AbsClient {

    private static final Logger logger = LoggerFactory.getLogger(SparkYarnClient.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private SparkYarnConfig sparkYarnConfig;

    private Configuration yarnConf = new YarnConfiguration();

    private YarnClient yarnClient = YarnClient.createYarnClient();

    private static final String HADOOP_CONF_DIR_KEY = "HADOOP_CONF_DIR";

    private static final String XML_SUFFIX = ".xml";

    private static final String HDFS_PREFIX = "hdfs://";

    private static final String HTTP_PREFIX = "http://";

    private static final String KEY_PRE_STR = "spark.";

    private static final String PYTHON_RUNNER_CLASS = "org.apache.spark.deploy.PythonRunner";

    private static final String DEFAULT_SPARK_SQL_PROXY_JAR_PATH = "/user/spark/spark-0.0.1-SNAPSHOT.jar";

    private static final String DEFAULT_SPARK_SQL_PROXY_MAINCLASS = "com.dtstack.sql.main.SqlProxy";

    private List<String> webAppAddrList = Lists.newArrayList();

    private static final String CLUSTER_INFO_WS_FORMAT = "%s/ws/v1/cluster";

    /**如果请求 CLUSTER_INFO_WS_FORMAT 返回信息包含该特征则表示是alive*/
    private static final String ALIVE_WEB_FLAG = "clusterInfo";

    @Override
    public void init(Properties prop) throws Exception {

        String errorMessage = null;
        sparkYarnConfig = OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsBytes(prop), SparkYarnConfig.class);

        if(StringUtils.isEmpty(sparkYarnConfig.getSparkYarnArchive())){
            errorMessage = "you need to set sparkYarnArchive when used spark engine.";
        }

        if(StringUtils.isEmpty(sparkYarnConfig.getSparkSqlProxyPath())){
            logger.info("use default spark proxy jar with path:{}", DEFAULT_SPARK_SQL_PROXY_JAR_PATH);
            sparkYarnConfig.setSparkSqlProxyPath(DEFAULT_SPARK_SQL_PROXY_JAR_PATH);
        }

        if(StringUtils.isEmpty(sparkYarnConfig.getSparkSqlProxyMainClass())){
            logger.info("use default spark proxy jar with main class:{}", DEFAULT_SPARK_SQL_PROXY_MAINCLASS);
            sparkYarnConfig.setSparkSqlProxyMainClass(DEFAULT_SPARK_SQL_PROXY_MAINCLASS);
        }

        if(errorMessage != null){
            logger.error(errorMessage);
            throw new RdosException(errorMessage);
        }

        if(System.getenv(HADOOP_CONF_DIR_KEY) !=  null) {
            File[] xmlFileList = new File(System.getenv(HADOOP_CONF_DIR_KEY)).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if(name.endsWith(XML_SUFFIX)){
                        return true;
                    }
                    return false;
                }
            });

            if(xmlFileList != null) {
                for(File xmlFile : xmlFileList) {
                    yarnConf.addResource(xmlFile.toURI().toURL());
                }
            }
        }

        System.setProperty("SPARK_YARN_MODE", "true");
        yarnConf();
        yarnClient.init(yarnConf);
        yarnClient.start();

    }

    private SparkConf buildBasicSparkConf(){

        SparkConf sparkConf = new SparkConf();
        sparkConf.remove("spark.jars");
        sparkConf.remove("spark.files");
        sparkConf.set("spark.yarn.archive", sparkYarnConfig.getSparkYarnArchive());
        SparkConfig.initDefautlConf(sparkConf);
        return sparkConf;
    }

    /**
     * 通过提交的paramsOperator 设置sparkConf
     * FIXME 解析传递过来的参数是不带spark.前面缀的,如果参数和spark支持不一致的话是否需要转换
     * @param sparkConf
     * @param confProperties
     */
    private void fillExtSparkConf(SparkConf sparkConf, Properties confProperties){

        if(confProperties == null){
            return;
        }

        for(Map.Entry<Object, Object> param : confProperties.entrySet()){
            String key = (String) param.getKey();
            String val = (String) param.getValue();
            key = KEY_PRE_STR + key;
            sparkConf.set(key, val);
        }
    }

    @Override
    public JobResult submitJobWithJar(JobClient jobClient){
        Properties properties = adaptToJarSubmit(jobClient);

        String mainClass = properties.getProperty(JOB_MAIN_CLASS_KEY);
        //只支持hdfs
        String jarPath = properties.getProperty(JOB_JAR_PATH_KEY);
        String appName = properties.getProperty(JOB_APP_NAME_KEY);
        String exeArgsStr = properties.getProperty(JOB_EXE_ARGS);

        if(!jarPath.startsWith(HDFS_PREFIX)){
            throw new RdosException("spark jar path protocol must be " + HDFS_PREFIX);
        }

        if(Strings.isNullOrEmpty(appName)){
            throw new RdosException("spark jar must set app name!");
        }


        String[] appArgs = new String[]{};
        if(org.apache.commons.lang3.StringUtils.isNotBlank(exeArgsStr)){
            appArgs = exeArgsStr.split("\\s+");
        }

        List<String> argList = new ArrayList<>();
        argList.add("--jar");
        argList.add(jarPath);
        argList.add("--class");
        argList.add(mainClass);

        if(appArgs != null) {
            for(String appArg : appArgs) {
                argList.add("--arg");
                argList.add(appArg);
            }
        }

        ClientArguments clientArguments = new ClientArguments(argList.toArray(new String[argList.size()]));
        SparkConf sparkConf = buildBasicSparkConf();
        sparkConf.setAppName(appName);
        fillExtSparkConf(sparkConf, jobClient.getConfProperties());

        ApplicationId appId = null;

        try {
            appId = new Client(clientArguments, yarnConf, sparkConf).submitApplication();
            return JobResult.createSuccessResult(appId.toString());
        } catch(Exception ex) {
            logger.info("", ex);
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }

    }

    /**
     * FIXME spark yarn参数设置
     * @param jobClient
     * @return
     */
    @Override
    public JobResult submitPythonJob(JobClient jobClient){

        Properties properties = adaptToJarSubmit(jobClient);
        //.py .egg .zip 存储的hdfs路径
        String pyFilePath = properties.getProperty(JOB_JAR_PATH_KEY);
        String appName = properties.getProperty(JOB_APP_NAME_KEY);
        String exeArgsStr = properties.getProperty(JOB_EXE_ARGS);

        if(Strings.isNullOrEmpty(pyFilePath)){
            return JobResult.createErrorResult("exe python file can't be null.");
        }

        if(Strings.isNullOrEmpty(appName)){
            return JobResult.createErrorResult("an application name must be set in your configuration");
        }

        ApplicationId appId = null;

        List<String> argList = new ArrayList<>();
        argList.add("--primary-py-file");
        argList.add(pyFilePath);

        argList.add("--class");
        argList.add(PYTHON_RUNNER_CLASS);

        String[] appArgs = new String[]{};
        if(org.apache.commons.lang3.StringUtils.isNotBlank(exeArgsStr)){
            appArgs = exeArgsStr.split("\\s+");
        }

        for(String appArg : appArgs) {
            argList.add("--arg");
            argList.add(appArg);
        }

        String pythonExtPath = sparkYarnConfig.getSparkPythonExtLibPath();
        if(Strings.isNullOrEmpty(pythonExtPath)){
            return JobResult.createErrorResult("engine node.yml setting error, " +
                    "commit spark python job need to set param of sparkPythonExtLibPath.");
        }

        SparkConf sparkConf = buildBasicSparkConf();
        sparkConf.set("spark.submit.pyFiles", pythonExtPath);
        sparkConf.setAppName(appName);
        fillExtSparkConf(sparkConf, properties);

        try {
            ClientArguments clientArguments = new ClientArguments(argList.toArray(new String[argList.size()]));
            appId = new Client(clientArguments, yarnConf, sparkConf).submitApplication();
            return JobResult.createSuccessResult(appId.toString());
        } catch(Exception ex) {
            logger.info("", ex);
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }
    }

    /**
     * 执行spark 批处理sql
     * @param jobClient
     * @return
     */
    private JobResult submitSparkSqlJobForBatch(JobClient jobClient){

        if(jobClient.getOperators().size() < 1){
            throw new RdosException("don't have any batch operator for spark sql job. please check it.");
        }

        StringBuffer sb = new StringBuffer("");
        for(Operator operator : jobClient.getOperators()){
            String tmpSql = operator.getSql();
            sb.append(tmpSql)
                    .append(";");
        }

        String exeSql = sb.toString();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("sql", exeSql);
        paramsMap.put("appName", jobClient.getJobName());

        String sqlExeJson = null;
        try{
            sqlExeJson = OBJECT_MAPPER.writeValueAsString(paramsMap);
        }catch (Exception e){
            logger.error("", e);
            throw new RdosException("get unexpected exception:" + e.getMessage());
        }

        List<String> argList = new ArrayList<>();
        argList.add("--jar");
        argList.add(sparkYarnConfig.getSparkSqlProxyPath());
        argList.add("--class");
        argList.add(sparkYarnConfig.getSparkSqlProxyMainClass());
        argList.add("--arg");
        argList.add(sqlExeJson);


        ClientArguments clientArguments = new ClientArguments(argList.toArray(new String[argList.size()]));
        SparkConf sparkConf = buildBasicSparkConf();
        sparkConf.setAppName(jobClient.getJobName());
        fillExtSparkConf(sparkConf, jobClient.getConfProperties());

        ApplicationId appId = null;

        try {
            appId = new Client(clientArguments, yarnConf, sparkConf).submitApplication();
            return JobResult.createSuccessResult(appId.toString());
        } catch(Exception ex) {
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }

    }

    private JobResult submitSparkSqlJobForStream(JobClient jobClient){
        throw new RdosException("not support spark sql job for stream type.");
    }

    @Override
    public JobResult submitSqlJob(JobClient jobClient) throws IOException, ClassNotFoundException {

        ComputeType computeType = jobClient.getComputeType();
        if(computeType == null){
            throw new RdosException("need to set compute type.");
        }

        switch (computeType){
            case BATCH:
                return submitSparkSqlJobForBatch(jobClient);
            case STREAM:
                return submitSparkSqlJobForStream(jobClient);
            default:
                //do nothing

        }

        throw new RdosException("not support for compute type :" + computeType);

    }

    @Override
    public JobResult cancelJob(String jobId) {
        try {
            ApplicationId appId = ConverterUtils.toApplicationId(jobId);
            yarnClient.killApplication(appId);
            return JobResult.createSuccessResult(jobId);
        } catch (Exception e) {
            logger.error("", e);
            return JobResult.createErrorResult(e.getMessage());
        }
    }

    @Override
    public RdosTaskStatus getJobStatus(String jobId) throws IOException {
        if(StringUtils.isEmpty(jobId)){
            return null;
        }
        ApplicationId appId = ConverterUtils.toApplicationId(jobId);
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
                    }else{
                        return RdosTaskStatus.RUNNING;
                    }

                case FAILED:
                    return RdosTaskStatus.FAILED;
                default:
                    throw new RdosException("Unsupported application state");
            }
        } catch (YarnException e) {
            logger.error("", e);
            return RdosTaskStatus.NOTFOUND;
        }

    }

    @Override
    public String getJobMaster() {
        //解析config,获取web-address
        String aliveWebAddr = null;
        for(String addr : webAppAddrList){
            String response = null;
            String reqUrl = String.format(CLUSTER_INFO_WS_FORMAT, addr);
            try{
                response = PoolHttpClient.get(reqUrl);
                if(response.contains(ALIVE_WEB_FLAG)){
                    aliveWebAddr = addr;
                    break;
                }
            }catch (Exception e){
                continue;
            }
        }

        if(!Strings.isNullOrEmpty(aliveWebAddr)){
            webAppAddrList.remove(aliveWebAddr);
            webAppAddrList.add(0, aliveWebAddr);
        }

        return aliveWebAddr;
    }

    public Properties adaptToJarSubmit(JobClient jobClient){

        BatchAddJarOperator jarOperator = null;
        for(Operator operator : jobClient.getOperators()){
            if(operator instanceof BatchAddJarOperator){
                jarOperator = (BatchAddJarOperator) operator;
                break;
            }
        }

        if(jarOperator == null){
            throw new RdosException("submit type of MR need to add jar operator.");
        }

        Properties properties = new Properties();
        properties.setProperty(JOB_JAR_PATH_KEY, jarOperator.getJarPath());
        properties.setProperty(JOB_APP_NAME_KEY, jobClient.getJobName());

        if(jarOperator.getMainClass() != null){
            properties.setProperty(JOB_MAIN_CLASS_KEY, jarOperator.getMainClass());
        }

        if(jobClient.getClassArgs() != null){
            properties.setProperty(JOB_EXE_ARGS, jobClient.getClassArgs());
        }
        return properties;
    }

    /**
     * 处理yarn HA的配置项
     */
    private void yarnConf() {
        Iterator<Map.Entry<String, String>> iterator = yarnConf.iterator();
        List<String> tmpWebAppAddr = Lists.newArrayList();

        while(iterator.hasNext()) {
            Map.Entry<String,String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();

            if(key.contains("yarn.resourcemanager.webapp.address.")){
                if(!value.startsWith(HTTP_PREFIX)){
                    value = HTTP_PREFIX + value.trim();
                }
                tmpWebAppAddr.add(value);
            } else if(key.startsWith("yarn.resourcemanager.hostname.")) {
                String rm = key.substring("yarn.resourcemanager.hostname.".length());
                String addressKey = "yarn.resourcemanager.address." + rm;

                webAppAddrList.add(HTTP_PREFIX + value + ":" + YarnConfiguration.DEFAULT_RM_WEBAPP_PORT);
                if(yarnConf.get(addressKey) == null) {
                    yarnConf.set(addressKey, value + ":" + YarnConfiguration.DEFAULT_RM_PORT);
                }
            }
        }

        if(tmpWebAppAddr.size() != 0){
            webAppAddrList = tmpWebAppAddr;
        }
    }

    @Override
    public String getMessageByHttp(String path) {
        String reqUrl = path;
        if(!path.startsWith(HTTP_PREFIX)){
            reqUrl = String.format("%s%s", getJobMaster(), path);
        }

        try {
            return PoolHttpClient.get(reqUrl);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getJobLog(String jobId) {
        ApplicationId applicationId = ConverterUtils.toApplicationId(jobId);
        SparkJobLog sparkJobLog = new SparkJobLog();

        try {
            ApplicationReport applicationReport = yarnClient.getApplicationReport(applicationId);
            String msgInfo = applicationReport.getDiagnostics();
            sparkJobLog.addAppLog(jobId, msgInfo);
        } catch (Exception e) {
            logger.error("", e);
            sparkJobLog.addAppLog(jobId, "get log from yarn err:" + e.getMessage());
        }

        return sparkJobLog.toString();
    }

    @Override
    public EngineResourceInfo getAvailSlots() {

        SparkYarnResourceInfo resourceInfo = new SparkYarnResourceInfo();
        try {
            List<NodeReport> nodeReports = yarnClient.getNodeReports(NodeState.RUNNING);
            for(NodeReport report : nodeReports){
                Resource capability = report.getCapability();
                Resource used = report.getUsed();
                int totalMem = capability.getMemory();
                int totalCores = capability.getVirtualCores();

                int usedMem = used.getMemory();
                int usedCores = used.getVirtualCores();

                Map<String, Object> workerInfo = Maps.newHashMap();
                workerInfo.put(SparkYarnResourceInfo.CORE_TOTAL_KEY, totalCores);
                workerInfo.put(SparkYarnResourceInfo.CORE_USED_KEY, usedCores);
                workerInfo.put(SparkYarnResourceInfo.CORE_FREE_KEY, totalCores - usedCores);

                workerInfo.put(SparkYarnResourceInfo.MEMORY_TOTAL_KEY, totalMem);
                workerInfo.put(SparkYarnResourceInfo.MEMORY_USED_KEY, usedMem);
                workerInfo.put(SparkYarnResourceInfo.MEMORY_FREE_KEY, totalMem - usedMem);

                resourceInfo.addNodeResource(report.getNodeId().toString(), workerInfo);
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return resourceInfo;
    }
}
