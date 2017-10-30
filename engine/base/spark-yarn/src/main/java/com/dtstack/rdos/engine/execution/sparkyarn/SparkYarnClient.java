package com.dtstack.rdos.engine.execution.sparkyarn;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchAddJarOperator;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.google.common.base.Strings;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.ClientArguments;
import org.apache.spark.deploy.yarn.Client;
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

    private static final ObjectMapper objMapper = new ObjectMapper();

    private SparkYarnConfig sparkYarnConfig;

    private String deployMode = "cluster";

    private Configuration yarnConf = new YarnConfiguration();

    private SparkConf sparkConf = new SparkConf();

    private YarnClient yarnClient = YarnClient.createYarnClient();

    private static final String KEY_PRE_STR = "spark.";

    /**默认每个处理器可以使用的内存大小*/
    private static final String DEFAULT_EXE_MEM = "512m";

    /**默认最多可以请求的CPU核心数*/
    private static final String DEFAULT_CORES_MAX = "2";

    private static final String PYTHON_RUNNER_CLASS = "org.apache.spark.deploy.PythonRunner";

    @Override
    public void init(Properties prop) throws Exception {
        String errorMessage = null;
        sparkYarnConfig = objMapper.readValue(objMapper.writeValueAsBytes(prop), SparkYarnConfig.class);

        if(StringUtils.isEmpty(sparkYarnConfig.getSparkYarnArchive())){
            errorMessage = "you need to set sparkYarnArchive when used spark engine.";
        }else if(StringUtils.isEmpty(sparkYarnConfig.getSparkSqlProxyPath())){
            errorMessage = "you need to set sparkSqlProxyPath when used spark engine.";
        }else if(StringUtils.isEmpty(sparkYarnConfig.getSparkSqlProxyMainClass())) {
            errorMessage = "you need to set sparkSqlProxyMainClass when used spark engine.";
        }

        if(errorMessage != null){
            logger.error(errorMessage);
            throw new RdosException(errorMessage);
        }

        if(System.getenv("HADOOP_CONF_DIR") !=  null) {
            File[] xmlFileList = new File(System.getenv("HADOOP_CONF_DIR")).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if(name.endsWith(".xml"))
                        return true;
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
        sparkConf.remove("spark.jars");
        sparkConf.remove("spark.files");
        sparkConf.set("spark.master", "yarn");
        sparkConf.set("spark.yarn.archive", sparkYarnConfig.getSparkYarnArchive());
        sparkConf.set("spark.submit.deployMode",deployMode);

        haYarnConf();
        yarnClient.init(yarnConf);
        yarnClient.start();

    }

    /**
     * 通过提交的paramsOperator 设置sparkConf
     * FIXME 解析传递过来的参数是不带spark.前面缀的,如果参数和spark支持不一致的话是否需要转换
     * @param sparkConf
     * @param confProperties
     */
    private void fillExtSparkConf(SparkConf sparkConf, Properties confProperties){

        sparkConf.set("spark.executor.memory", DEFAULT_EXE_MEM); //默认执行内存
        sparkConf.set("spark.cores.max", DEFAULT_CORES_MAX);  //默认请求的cpu核心数
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
        String jarPath = properties.getProperty(JOB_JAR_PATH_KEY);//只支持hdfs
        String appName = properties.getProperty(JOB_APP_NAME_KEY);
        String exeArgsStr = properties.getProperty(JOB_EXE_ARGS);

        if(!jarPath.startsWith("hdfs://")){
            throw new RdosException("spark jar path protocol must be hdfs://");
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
    private JobResult submitJobWithPython(JobClient jobClient){

        Properties properties = adaptToJarSubmit(jobClient);
        String pyFilePath = properties.getProperty(JOB_JAR_PATH_KEY);//.py .egg .zip 存储的hdfs路径
        String appName = properties.getProperty(JOB_APP_NAME_KEY);

        //FIXME 包参数传递
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

        String pythonExtPath = sparkYarnConfig.getSparkPythonExtLibPath();
        if(Strings.isNullOrEmpty(pythonExtPath)){
            return JobResult.createErrorResult("engine node.yml setting error, " +
                    "commit spark python job need set param of sparkPythonExtLibPath.");
        }

        sparkConf.set("spark.submit.pyFiles", pythonExtPath);
        sparkConf.setAppName(appName);

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
            sqlExeJson = objMapper.writeValueAsString(paramsMap);
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
            e.printStackTrace();
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
                    //return RdosTaskStatus.SUBMITTED;
                    //FIXME 特殊逻辑,认为已提交到计算引擎的状态为等待资源状态
                    return RdosTaskStatus.WAITCOMPUTE;
                case ACCEPTED:
                    return RdosTaskStatus.SCHEDULED;
                case RUNNING:
                    return RdosTaskStatus.RUNNING;
                case FINISHED:
                    return RdosTaskStatus.FINISHED;
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
        return "yarn";
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
        properties.setProperty(JOB_MAIN_CLASS_KEY, jarOperator.getMainClass());

        if(jobClient.getClassArgs() != null){
            properties.setProperty(JOB_EXE_ARGS, jobClient.getClassArgs());
        }
        return properties;
    }

    /**
     * 处理yarn HA的配置项
     */
    private void haYarnConf() {
        Iterator<Map.Entry<String, String>> iterator = yarnConf.iterator();
        while(iterator.hasNext()) {
            Map.Entry<String,String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();

            if(key.startsWith("yarn.resourcemanager.hostname.")) {
                String rm = key.substring("yarn.resourcemanager.hostname.".length());
                String addressKey = "yarn.resourcemanager.address." + rm;
                if(yarnConf.get(addressKey) == null) {
                    yarnConf.set(addressKey, value + ":" + YarnConfiguration.DEFAULT_RM_PORT);
                }
            }
        }
    }

	@Override
	public String getMessageByHttp(String path) {
		// TODO Auto-generated method stub
		return null;
	}
}
