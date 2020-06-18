//package com.dtstack.engine.sparkyarn.sparkyarn;
//
//import com.dtstack.engine.common.JarFileInfo;
//import com.dtstack.engine.common.JobClient;
//import com.dtstack.engine.common.JobIdentifier;
//import com.dtstack.engine.common.client.AbstractClient;
//import com.dtstack.engine.common.enums.ComputeType;
//import com.dtstack.engine.common.enums.EJobType;
//import com.dtstack.engine.common.enums.RdosTaskStatus;
//import com.dtstack.engine.common.exception.ExceptionUtil;
//import com.dtstack.engine.common.exception.RdosDefineException;
//import com.dtstack.engine.common.http.PoolHttpClient;
//import com.dtstack.engine.common.pojo.JobResult;
//import com.dtstack.engine.common.util.DtStringUtil;
//import com.dtstack.engine.common.util.MathUtil;
//import com.dtstack.engine.common.util.PublicUtil;
//import com.dtstack.engine.sparkyarn.sparkyarn.config.SparkConfig;
//import com.dtstack.engine.sparkyarn.sparkyarn.parser.AddJarOperator;
//import com.google.common.base.Charsets;
//import com.google.common.base.Strings;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.spark.SparkConf;
//import org.apache.spark.deploy.k8s.submit.ClientArguments;
//import org.apache.spark.deploy.k8s.submit.DtKubernetesClientApplication;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.util.*;
//
///**
// * Created by softfly on 17/8/10.
// */
//public class SparkYarnClient extends AbstractClient {
//
//    private static final Logger logger = LoggerFactory.getLogger(SparkYarnClient.class);
//
//
//    private static final String SESSION_CONF_KEY_PREFIX = "session.";
//
//    private static final String KEY_DEFAULT_FILE_FORMAT = "hive.default.fileformat";
//
//    private static final String DEFAULT_FILE_FORMAT = "orc";
//
//    private static final String LOG_LEVEL_KEY = "logLevel";
//
//    private static final String HTTP_PREFIX = "http://";
//
//    private static final String KEY_PRE_STR = "spark.";
//
//    private static final String SPARK_JAVA_OPTS_KEY = "SPARK_JAVA_OPTS";
//
//    private static final String PYTHON_RUNNER_CLASS = "org.apache.spark.deploy.PythonRunner";
//
//    private static final String PYTHON_RUNNER_DEPENDENCY_RES_KEY = "extRefResource";
//
//    private static final String CLUSTER_INFO_WS_FORMAT = "%s/ws/v1/cluster";
//
//    /**如果请求 CLUSTER_INFO_WS_FORMAT 返回信息包含该特征则表示是alive*/
//    private static final String ALIVE_WEB_FLAG = "clusterInfo";
//
//    private List<String> webAppAddrList = Lists.newArrayList();
//
//    private SparkYarnConfig sparkYarnConfig;
//
//    private Properties sparkExtProp;
//
//    @Override
//    public void init(Properties prop) throws Exception {
//        this.sparkExtProp = prop;
//        String propStr = PublicUtil.objToString(prop);
//        sparkYarnConfig = PublicUtil.jsonStrToObject(propStr, SparkYarnConfig.class);
//
//    }
//
//    @Override
//    protected JobResult processSubmitJobWithType(JobClient jobClient) {
//        return submitSqlJob(jobClient);
//    }
//
//    /**
//     * 执行spark 批处理sql
//     * @param jobClient
//     * @return
//     */
//    private JobResult submitSparkSqlJobForBatch(JobClient jobClient) {
//
//        Properties confProp = jobClient.getConfProperties();
//        Map<String, Object> paramsMap = new HashMap<>();
//
//        String zipSql = DtStringUtil.zip(jobClient.getSql());
//        paramsMap.put("sql", zipSql);
//        paramsMap.put("appName", jobClient.getJobName());
//        paramsMap.put("sparkSessionConf", getSparkSessionConf(confProp));
//
//        String logLevel = MathUtil.getString(confProp.get(LOG_LEVEL_KEY));
//        if (StringUtils.isNotEmpty(logLevel)) {
//            paramsMap.put("logLevel", logLevel);
//        }
//
//        String sqlExeJson = null;
//        try {
//            sqlExeJson = PublicUtil.objToString(paramsMap);
//            sqlExeJson = URLEncoder.encode(sqlExeJson, Charsets.UTF_8.name());
//        } catch (Exception e) {
//            logger.error("", e);
//            throw new RdosDefineException("get unexpected exception:" + e.getMessage());
//        }
//
//        String sqlProxyClass = sparkYarnConfig.getSparkSqlProxyMainClass();
//
//        List<String> argList = new ArrayList<>();
//        argList.add("--jar");
//        argList.add(sparkYarnConfig.getSparkSqlProxyPath());
//        argList.add("--class");
//        argList.add(sqlProxyClass);
//        argList.add("--arg");
//        // 暂时不加参数
////        argList.add(sqlExeJson);
//
//        DtKubernetesClientApplication k8sClient = new DtKubernetesClientApplication();
//        ClientArguments clientArguments = ClientArguments.fromCommandLineArgs(argList.toArray(new String[argList.size()]));
//
//        SparkConf sparkConf = buildBasicSparkConf();
//        sparkConf.setAppName(jobClient.getJobName());
//        fillExtSparkConf(sparkConf, confProp);
//
//        try {
//            String appId = k8sClient.run(clientArguments, sparkConf);
//            return JobResult.createSuccessResult(appId.toString());
//        } catch (Exception ex) {
//            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
//        }
//    }
//
//    private Map<String, String> getSparkSessionConf(Properties confProp) {
//        Map<String, String> map = Maps.newHashMap();
//        map.put(KEY_DEFAULT_FILE_FORMAT, DEFAULT_FILE_FORMAT);
//
//        if (confProp == null || confProp.isEmpty()) {
//            return map;
//        }
//
//        for (Map.Entry<Object, Object> param : confProp.entrySet()) {
//            String key = (String) param.getKey();
//            String val = (String) param.getValue();
//            if (key.startsWith(SESSION_CONF_KEY_PREFIX)) {
//                key = key.replaceFirst("session\\.", "");
//                map.put(key, val);
//            }
//        }
//
//        return map;
//    }
//
//    private SparkConf buildBasicSparkConf() {
//        SparkConf sparkConf = new SparkConf();
//        //sparkConf.set(SPARK_JAVA_OPTS_KEY, sparkYarnConfig.getJvmOptions());
//
//        if (sparkExtProp != null) {
//            sparkExtProp.forEach((key, value) -> {
//                if (key.toString().contains(".")) {
//                    sparkConf.set(key.toString(), value.toString());
//                }
//            });
//        }
//        SparkConfig.initDefautlConf(sparkConf);
//        return sparkConf;
//    }
//
//    /**
//     * 通过提交的paramsOperator 设置sparkConf
//     * 解析传递过来的参数不带spark.前面缀的
//     * @param sparkConf
//     * @param confProperties
//     */
//    private void fillExtSparkConf(SparkConf sparkConf, Properties confProperties) {
//
//        if (confProperties == null) {
//            return;
//        }
//
//        for (Map.Entry<Object, Object> param : confProperties.entrySet()) {
//            String key = (String) param.getKey();
//            String val = (String) param.getValue();
//            if (!key.contains(KEY_PRE_STR)) {
//                key = KEY_PRE_STR + key;
//            }
//            sparkConf.set(key, val);
//        }
//    }
//
//
//    private JobResult submitSparkSqlJobForStream(JobClient jobClient) {
//        throw new RdosDefineException("not support spark sql job for stream type.");
//    }
//
//    private JobResult submitSqlJob(JobClient jobClient) {
//
//        ComputeType computeType = jobClient.getComputeType();
//        if (computeType == null) {
//            throw new RdosDefineException("need to set compute type.");
//        }
//
//        switch (computeType) {
//            case BATCH:
//                return submitSparkSqlJobForBatch(jobClient);
//            case STREAM:
//                return submitSparkSqlJobForStream(jobClient);
//            default:
//                //do nothing
//
//        }
//
//        throw new RdosDefineException("not support for compute type :" + computeType);
//
//    }
//
//    @Override
//    public JobResult cancelJob(JobIdentifier jobIdentifier) {
//
//        //        String jobId = jobIdentifier.getEngineJobId();
//        //        try {
//        //            ApplicationId appId = ConverterUtils.toApplicationId(jobId);
//        //            getYarnClient().killApplication(appId);
//        //            return JobResult.createSuccessResult(jobId);
//        //        } catch (Exception e) {
//        //            logger.error("", e);
//        //            return JobResult.createErrorResult(e.getMessage());
//        //        }
//        return null;
//    }
//
//    @Override
//    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
//        //
//        //        String jobId = jobIdentifier.getEngineJobId();
//        //
//        //        if(StringUtils.isEmpty(jobId)){
//        //            return null;
//        //        }
//        //
//        //        ApplicationId appId = ConverterUtils.toApplicationId(jobId);
//        //        try {
//        //            ApplicationReport report = getYarnClient().getApplicationReport(appId);
//        //            YarnApplicationState applicationState = report.getYarnApplicationState();
//        //            switch(applicationState) {
//        //                case KILLED:
//        //                    return RdosTaskStatus.KILLED;
//        //                case NEW:
//        //                case NEW_SAVING:
//        //                    return RdosTaskStatus.CREATED;
//        //                case SUBMITTED:
//        //                    //FIXME 特殊逻辑,认为已提交到计算引擎的状态为等待资源状态
//        //                    return RdosTaskStatus.WAITCOMPUTE;
//        //                case ACCEPTED:
//        //                    return RdosTaskStatus.SCHEDULED;
//        //                case RUNNING:
//        //                    return RdosTaskStatus.RUNNING;
//        //                case FINISHED:
//        //                    //state 为finished状态下需要兼顾判断finalStatus.
//        //                    FinalApplicationStatus finalApplicationStatus = report.getFinalApplicationStatus();
//        //                    if(finalApplicationStatus == FinalApplicationStatus.FAILED){
//        //                        return RdosTaskStatus.FAILED;
//        //                    }else if(finalApplicationStatus == FinalApplicationStatus.SUCCEEDED){
//        //                        return RdosTaskStatus.FINISHED;
//        //                    }else if(finalApplicationStatus == FinalApplicationStatus.KILLED){
//        //                        return RdosTaskStatus.KILLED;
//        //                    }else{
//        //                        return RdosTaskStatus.RUNNING;
//        //                    }
//        //
//        //                case FAILED:
//        //                    return RdosTaskStatus.FAILED;
//        //                default:
//        //                    throw new RdosDefineException("Unsupported application state");
//        //            }
//        //        } catch (YarnException e) {
//        //            logger.error("", e);
//        //            return RdosTaskStatus.NOTFOUND;
//        //        }
//        return null;
//    }
//
//    @Override
//    public String getJobMaster(JobIdentifier jobIdentifier) {
//        //解析config,获取web-address
//        String aliveWebAddr = null;
//        for (String addr : webAppAddrList) {
//            String response = null;
//            String reqUrl = String.format(CLUSTER_INFO_WS_FORMAT, addr);
//            try {
//                response = PoolHttpClient.get(reqUrl);
//                if (response.contains(ALIVE_WEB_FLAG)) {
//                    aliveWebAddr = addr;
//                    break;
//                }
//            } catch (Exception e) {
//                continue;
//            }
//        }
//
//        if (!Strings.isNullOrEmpty(aliveWebAddr)) {
//            webAppAddrList.remove(aliveWebAddr);
//            webAppAddrList.add(0, aliveWebAddr);
//        }
//
//        return aliveWebAddr;
//    }
//
//
//    @Override
//    public String getMessageByHttp(String path) {
//        String reqUrl = path;
//        if (!path.startsWith(HTTP_PREFIX)) {
//            reqUrl = String.format("%s%s", getJobMaster(null), path);
//        }
//
//        try {
//            return PoolHttpClient.get(reqUrl);
//        } catch (IOException e) {
//            return null;
//        }
//    }
//
//    @Override
//    public String getJobLog(JobIdentifier jobIdentifier) {
//
//        //TODO  构建kubeClient 并根据applicationId去获取driver pod状态
//
//        //        String jobId = jobIdentifier.getEngineJobId();
//        //        ApplicationId applicationId = ConverterUtils.toApplicationId(jobId);
//        //        SparkJobLog sparkJobLog = new SparkJobLog();
//        //
//        //        try {
//        //            ApplicationReport applicationReport = getYarnClient().getApplicationReport(applicationId);
//        //            String msgInfo = applicationReport.getDiagnostics();
//        //            sparkJobLog.addAppLog(jobId, msgInfo);
//        //        } catch (Exception e) {
//        //            logger.error("", e);
//        //            sparkJobLog.addAppLog(jobId, "get log from yarn err:" + e.getMessage());
//        //        }
//
//        //        return sparkJobLog.toString();
//
//        return "";
//    }
//
//    @Override
//    public boolean judgeSlots(JobClient jobClient) {
//
//        //        try {
//        //            return KerberosUtils.login(sparkYarnConfig, () -> {
//        //                SparkYarnResourceInfo resourceInfo = new SparkYarnResourceInfo();
//        //                try {
//        //                    resourceInfo.getYarnSlots(getYarnClient(), sparkYarnConfig.getQueue(), sparkYarnConfig.getYarnAccepterTaskNumber());
//        //                    return resourceInfo.judgeSlots(jobClient);
//        //                } catch (YarnException e) {
//        //                    logger.error("", e);
//        //                    return false;
//        //                }
//        //            });
//        //        } catch (IOException e) {
//        //            logger.error("judgeSlots error", e);
//        //        }
//        //        return false;
//        return true;
//    }
//
//
//    @Override
//    public void beforeSubmitFunc(JobClient jobClient) {
//        String sql = jobClient.getSql();
//        List<String> sqlArr = DtStringUtil.splitIgnoreQuota(sql, ';');
//        if (sqlArr.size() == 0) {
//            return;
//        }
//
//        List<String> sqlList = Lists.newArrayList(sqlArr);
//        Iterator<String> sqlItera = sqlList.iterator();
//
//        while (sqlItera.hasNext()) {
//            String tmpSql = sqlItera.next();
//            if (AddJarOperator.verific(tmpSql)) {
//                sqlItera.remove();
//                JarFileInfo jarFileInfo = AddJarOperator.parseSql(tmpSql);
//
//                if (jobClient.getJobType() == EJobType.SQL) {
//                    //SQL当前不允许提交jar包,自定义函数已经在web端处理了。
//                } else {
//                    //非sql任务只允许提交一个附件包
//                    jobClient.setCoreJarInfo(jarFileInfo);
//                    break;
//                }
//            }
//        }
//
//        jobClient.setSql(String.join(";", sqlList));
//    }
//
//    //    public YarnClient getYarnClient(){
//    //        try{
//    //            if(yarnClient == null){
//    //                synchronized (this){
//    //                    if(yarnClient == null){
//    //                        YarnClient yarnClient1 = YarnClient.createYarnClient();
//    //                        yarnClient1.init(yarnConf);
//    //                        yarnClient1.start();
//    //                        yarnClient = yarnClient1;
//    //                    }
//    //                }
//    //            }else{
//    //                //判断下是否可用
//    //                yarnClient.getAllQueues();
//    //            }
//    //        }catch(Throwable e){
//    //            logger.error("getYarnClient error:{}",e);
//    //            synchronized (this){
//    //                if(yarnClient != null){
//    //                    boolean flag = true;
//    //                    try{
//    //                        //判断下是否可用
//    //                        yarnClient.getAllQueues();
//    //                    }catch(Throwable e1){
//    //                        logger.error("getYarnClient error:{}",e1);
//    //                        flag = false;
//    //                    }
//    //                    if(!flag){
//    //                        try{
//    //                            yarnClient.stop();
//    //                        }finally {
//    //                            yarnClient = null;
//    //                        }
//    //                    }
//    //                }
//    //                if(yarnClient == null){
//    //                    YarnClient yarnClient1 = YarnClient.createYarnClient();
//    //                    yarnClient1.init(yarnConf);
//    //                    yarnClient1.start();
//    //                    yarnClient = yarnClient1;
//    //                }
//    //            }
//    //        }
//    //        return yarnClient;
//    //    }
//}
