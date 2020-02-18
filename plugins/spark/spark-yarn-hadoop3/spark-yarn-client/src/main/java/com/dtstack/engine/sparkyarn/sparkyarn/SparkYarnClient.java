package com.dtstack.engine.sparkyarn.sparkyarn;

import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosException;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.AbstractClient;
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.JobParam;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.HadoopConfTool;
import com.dtstack.engine.sparkyarn.sparkext.ClientExt;
import com.dtstack.engine.sparkyarn.sparkext.ClientExtFactory;
import com.dtstack.engine.sparkyarn.sparkyarn.parser.AddJarOperator;
import com.dtstack.engine.sparkyarn.sparkyarn.util.HadoopConf;
import com.dtstack.engine.sparkyarn.sparkyarn.util.KerberosUtils;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.ClientArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by softfly on 17/8/10.
 */
public class SparkYarnClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(SparkYarnClient.class);

    private static final String HADOOP_USER_NAME = "HADOOP_USER_NAME";

    private static final String SPARK_YARN_MODE = "SPARK_YARN_MODE";

    private static final String IS_CARBON_SPARK_KEY = "isCarbondata";

    private static final String SESSION_CONF_KEY_PREFIX = "session.";

    private static final String KEY_DEFAULT_FILE_FORMAT = "hive.default.fileformat";

    private static final String DEFAULT_FILE_FORMAT = "orc";

    private static final String LOG_LEVEL_KEY = "logLevel";

    private static final String HDFS_PREFIX = "hdfs://";

    private static final String HTTP_PREFIX = "http://";

    private static final String KEY_PRE_STR = "spark.";

    private static final String SPARK_JAVA_OPTS_KEY = "SPARK_JAVA_OPTS";

    private static final String PYTHON_RUNNER_CLASS = "org.apache.spark.deploy.PythonRunner";

    private static final String PYTHON_RUNNER_DEPENDENCY_RES_KEY = "extRefResource";

    private static final String CLUSTER_INFO_WS_FORMAT = "%s/ws/v1/cluster";

    /**如果请求 CLUSTER_INFO_WS_FORMAT 返回信息包含该特征则表示是alive*/
    private static final String ALIVE_WEB_FLAG = "clusterInfo";

    private List<String> webAppAddrList = Lists.newArrayList();

    private SparkYarnConfig sparkYarnConfig;

    private Configuration yarnConf;

    private YarnClient yarnClient;

    private Properties sparkExtProp;

    public SparkYarnClient(){
    }

    @Override
    public void init(Properties prop) throws Exception {
        this.sparkExtProp = prop;
        String propStr = PublicUtil.objToString(prop);
        sparkYarnConfig = PublicUtil.jsonStrToObject(propStr, SparkYarnConfig.class);
        setHadoopUserName(sparkYarnConfig);
        initYarnConf(sparkYarnConfig);
        sparkYarnConfig.setDefaultFs(yarnConf.get(HadoopConfTool.FS_DEFAULTFS));
        System.setProperty(SPARK_YARN_MODE, "true");
        parseWebAppAddr();
        if (sparkYarnConfig.isOpenKerberos()){
            initSecurity();
        }
        logger.info("UGI info: " + UserGroupInformation.getCurrentUser());
        yarnClient = YarnClient.createYarnClient();
        yarnClient.init(yarnConf);
        yarnClient.start();
    }
    private void initSecurity() throws IOException {
        try {
            logger.info("start init security!");
            KerberosUtils.login(sparkYarnConfig);
        } catch (IOException e) {
            logger.error("initSecurity happens error", e);
            throw new IOException("InitSecurity happens error", e);
        }
    }

    private void initYarnConf(SparkYarnConfig sparkConfig){
        HadoopConf customerConf = new HadoopConf();
        customerConf.initHadoopConf(sparkConfig.getHadoopConf());
        customerConf.initYarnConf(sparkConfig.getYarnConf());

        if (sparkYarnConfig.isOpenKerberos()){
            customerConf.initHiveSecurityConf(sparkConfig.getHiveConf());
        }

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
        }else if(EJobType.PYTHON.equals(jobType)){
            jobResult = submitPythonJob(jobClient);
        }
        return jobResult;
    }

    private JobResult submitJobWithJar(JobClient jobClient){
        setHadoopUserName(sparkYarnConfig);
        JobParam jobParam = new JobParam(jobClient);
        String mainClass = jobParam.getMainClass();
        //只支持hdfs
        String jarPath = jobParam.getJarPath();
        String appName = jobParam.getJobName();
        String exeArgsStr = jobParam.getClassArgs();

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

        Properties confProp = jobClient.getConfProperties();
        Boolean isCarbonSpark = MathUtil.getBoolean(confProp.get(IS_CARBON_SPARK_KEY), false);

        List<String> argList = new ArrayList<>();
        argList.add("--jar");
        argList.add(jarPath);
        argList.add("--class");
        argList.add(mainClass);

        for(String appArg : appArgs) {
            argList.add("--arg");
            argList.add(appArg);
        }

        ClientArguments clientArguments = new ClientArguments(argList.toArray(new String[argList.size()]));
        SparkConf sparkConf = buildBasicSparkConf();
        sparkConf.setAppName(appName);
        fillExtSparkConf(sparkConf, jobClient.getConfProperties());

        ApplicationId appId = null;

        try {
            ClientExt clientExt = ClientExtFactory.getClientExt(clientArguments, yarnConf, sparkConf, isCarbonSpark);
            clientExt.setSparkYarnConfig(sparkYarnConfig);
            appId = clientExt.submitApplication(jobClient.getJobPriority());
            return JobResult.createSuccessResult(appId.toString());
        } catch(Exception ex) {
            logger.info("", ex);
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }

    }

    private JobResult submitPythonJob(JobClient jobClient){
        setHadoopUserName(sparkYarnConfig);
        JobParam jobParam = new JobParam(jobClient);
        //.py .egg .zip 存储的hdfs路径
        String pyFilePath = jobParam.getJarPath();
        String appName = jobParam.getJobName();
        String exeArgsStr = jobParam.getClassArgs();

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

        String dependencyResource = "";
        boolean nextIsDependencyVal = false;
        for(String appArg : appArgs) {

            if(nextIsDependencyVal){
                dependencyResource = appArg;
                continue;
            }

            if(PYTHON_RUNNER_DEPENDENCY_RES_KEY.equals(appArg)){
                nextIsDependencyVal = true;
                continue;
            }

            argList.add("--arg");
            argList.add(appArg);
            nextIsDependencyVal = false;
        }

        String pythonExtPath = sparkYarnConfig.getSparkPythonExtLibPath();
        if(Strings.isNullOrEmpty(pythonExtPath)){
            return JobResult.createErrorResult("engine node.yml setting error, " +
                    "commit spark python job need to set param of sparkPythonExtLibPath.");
        }

        //添加自定义的依赖包
        if(!Strings.isNullOrEmpty(dependencyResource)){
            pythonExtPath = pythonExtPath + "," + dependencyResource;
        }

        SparkConf sparkConf = buildBasicSparkConf();
        sparkConf.set("spark.submit.pyFiles", pythonExtPath);
        sparkConf.setAppName(appName);
        fillExtSparkConf(sparkConf, jobClient.getConfProperties());

        try {
            ClientArguments clientArguments = new ClientArguments(argList.toArray(new String[argList.size()]));
            ClientExt clientExt = new ClientExt(clientArguments, yarnConf, sparkConf);
            clientExt.setSparkYarnConfig(sparkYarnConfig);
            appId = clientExt.submitApplication(jobClient.getJobPriority());
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

        Properties confProp = jobClient.getConfProperties();
        Boolean isCarbonSpark = MathUtil.getBoolean(confProp.get(IS_CARBON_SPARK_KEY), false);

        setHadoopUserName(sparkYarnConfig);
        Map<String, Object> paramsMap = new HashMap<>();

        String zipSql = DtStringUtil.zip(jobClient.getSql());
        paramsMap.put("sql", zipSql);
        paramsMap.put("appName", jobClient.getJobName());
        paramsMap.put("sparkSessionConf", getSparkSessionConf(confProp));

        String logLevel = MathUtil.getString(confProp.get(LOG_LEVEL_KEY));
        if (StringUtils.isNotEmpty(logLevel)) {
            paramsMap.put("logLevel", logLevel);
        }

        if(isCarbonSpark){
            paramsMap.put("storePath", sparkYarnConfig.getCarbonStorePath());
        }

        String sqlExeJson = null;
        try{
            sqlExeJson = PublicUtil.objToString(paramsMap);
            sqlExeJson = URLEncoder.encode(sqlExeJson, Charsets.UTF_8.name());
        }catch (Exception e){
            logger.error("", e);
            throw new RdosException("get unexpected exception:" + e.getMessage());
        }

        String sqlProxyClass = sparkYarnConfig.getSparkSqlProxyMainClass();
        if(isCarbonSpark){
            sqlProxyClass = SparkYarnConfig.DEFAULT_CARBON_SQL_PROXY_MAINCLASS;
        }

        List<String> argList = new ArrayList<>();
        argList.add("--jar");
        argList.add(sparkYarnConfig.getSparkSqlProxyPath());
        argList.add("--class");
        argList.add(sqlProxyClass);
        argList.add("--arg");
        argList.add(sqlExeJson);

        ClientArguments clientArguments = new ClientArguments(argList.toArray(new String[argList.size()]));
        SparkConf sparkConf = buildBasicSparkConf();
        sparkConf.setAppName(jobClient.getJobName());
        fillExtSparkConf(sparkConf, confProp);

        ApplicationId appId = null;

        try {
            ClientExt clientExt = ClientExtFactory.getClientExt(clientArguments, yarnConf, sparkConf, isCarbonSpark);
            clientExt.setSparkYarnConfig(sparkYarnConfig);
            appId = clientExt.submitApplication(jobClient.getJobPriority());
            return JobResult.createSuccessResult(appId.toString());
        } catch(Exception ex) {
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }

    }

    private Map<String, String> getSparkSessionConf(Properties confProp){
        Map<String, String> map = Maps.newHashMap();
        map.put(KEY_DEFAULT_FILE_FORMAT, DEFAULT_FILE_FORMAT);

        if(confProp == null || confProp.isEmpty()){
            return map;
        }

        for(Map.Entry<Object, Object> param : confProp.entrySet()){
            String key = (String) param.getKey();
            String val = (String) param.getValue();
            if(key.startsWith(SESSION_CONF_KEY_PREFIX)){
                key = key.replaceFirst("session\\.", "");
                map.put(key, val);
            }
        }

        return map;
    }

    private SparkConf buildBasicSparkConf(){

        SparkConf sparkConf = new SparkConf();
        sparkConf.remove("spark.jars");
        sparkConf.remove("spark.files");
        sparkConf.set("spark.yarn.archive", sparkYarnConfig.getSparkYarnArchive());
        sparkConf.set("spark.yarn.queue", sparkYarnConfig.getQueue());
        sparkConf.set(SPARK_JAVA_OPTS_KEY, sparkYarnConfig.getJvmOptions());
        sparkConf.set("security", "false");

        if (sparkYarnConfig.isOpenKerberos()){
            String keytab = KerberosUtils.localPath(sparkYarnConfig);
            String principal = KerberosUtils.getPrincipal(keytab);
            sparkConf.set("spark.yarn.keytab", keytab);
            sparkConf.set("spark.yarn.principal", principal);
            sparkConf.set("security", String.valueOf(sparkYarnConfig.isOpenKerberos()));
        }
        if(sparkExtProp != null){
            sparkExtProp.forEach((key, value) -> {
                if (key.toString().contains(".")) {
                    sparkConf.set(key.toString(), value.toString());
                }
            });
        }
        SparkConfig.initDefautlConf(sparkConf);
        return sparkConf;
    }

    /**
     * 通过提交的paramsOperator 设置sparkConf
     * 解析传递过来的参数不带spark.前面缀的
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
            if(!key.contains(KEY_PRE_STR)){
                key = KEY_PRE_STR + key;
            }
            sparkConf.set(key, val);
        }
    }


    private JobResult submitSparkSqlJobForStream(JobClient jobClient){
        throw new RdosException("not support spark sql job for stream type.");
    }

    private JobResult submitSqlJob(JobClient jobClient) {

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
    public JobResult cancelJob(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();
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
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {

        String jobId = jobIdentifier.getEngineJobId();

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
    public String getJobMaster(JobIdentifier jobIdentifier) {
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


    private void parseWebAppAddr() {
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
            reqUrl = String.format("%s%s", getJobMaster(null), path);
        }

        try {
            return PoolHttpClient.get(reqUrl);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();
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
    public boolean judgeSlots(JobClient jobClient){

        if (sparkYarnConfig.isOpenKerberos()){
            try {
                logger.debug("start init security!");
                initSecurity();
            } catch (IOException e) {
                logger.error("InitSecurity happens error", e);
            }
        }

        SparkYarnResourceInfo resourceInfo = new SparkYarnResourceInfo();
        try {
            resourceInfo.getYarnSlots(yarnClient, sparkYarnConfig.getQueue(), sparkYarnConfig.getYarnAccepterTaskNumber());
            return resourceInfo.judgeSlots(jobClient);
        } catch (YarnException e) {
            logger.error("", e);
            return false;
        }

    }

    public void setHadoopUserName(SparkYarnConfig sparkYarnConfig){
        if(Strings.isNullOrEmpty(sparkYarnConfig.getHadoopUserName())){
            return;
        }

        UserGroupInformation.setThreadLocalData(HADOOP_USER_NAME, sparkYarnConfig.getHadoopUserName());
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

        while (sqlItera.hasNext()){
            String tmpSql = sqlItera.next();
            if(AddJarOperator.verific(tmpSql)){
                sqlItera.remove();
                JarFileInfo jarFileInfo = AddJarOperator.parseSql(tmpSql);

                if(jobClient.getJobType() == EJobType.SQL){
                    //SQL当前不允许提交jar包,自定义函数已经在web端处理了。
                }else{
                    //非sql任务只允许提交一个附件包
                    jobClient.setCoreJarInfo(jarFileInfo);
                    break;
                }
            }
        }

        jobClient.setSql(String.join(";", sqlList));
    }
}
