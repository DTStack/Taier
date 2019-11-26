package com.dtstack.engine.spark210;

import com.dtstack.engine.common.exception.RdosException;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.AbsClient;
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.JobParam;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.spark210.enums.Status;
import com.dtstack.engine.spark210.parser.AddJarOperator;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.rest.RestSubmissionClient;
import org.apache.spark.deploy.rest.SubmitRestProtocolResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * spark 提交job
 * Date: 2017/4/10
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class SparkClient extends AbsClient {

    private static final Logger logger = LoggerFactory.getLogger(SparkClient.class);

    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String KEY_PRE_STR = "spark.";

    private static final String DEFAULT_SPARK_SQL_PROXY_JAR_PATH = "/user/spark/spark-0.0.1-SNAPSHOT.jar";

    private static final String DEFAULT_SPARK_SQL_PROXY_MAINCLASS = "com.dtstack.sql.main.SqlProxy";

    private SparkStandaloneConfig sparkConfig;

    private String deployMode = "cluster";

    public SparkClient(){
        this.restartService = new SparkRestartService();
    }

    @Override
    public void init(Properties prop) throws Exception {

        String errorMessage = null;
        sparkConfig = objMapper.readValue(objMapper.writeValueAsBytes(prop), SparkStandaloneConfig.class);
        if(StringUtils.isEmpty(sparkConfig.getSparkMaster())){
            errorMessage = "you need to set sparkMaster when used spark engine.";
        }else if(StringUtils.isEmpty(sparkConfig.getSparkWebMaster())){
            errorMessage = "you need to set sparkWebMaster when used spark engine.";
        }else if(StringUtils.isEmpty(sparkConfig.getSparkSqlProxyPath())){
            logger.info("use default spark proxy jar with path:{}", DEFAULT_SPARK_SQL_PROXY_JAR_PATH);
            sparkConfig.setSparkSqlProxyPath(DEFAULT_SPARK_SQL_PROXY_JAR_PATH);
        }else if(StringUtils.isEmpty(sparkConfig.getSparkSqlProxyMainClass())){
            logger.info("use default spark proxy jar with main class:{}", DEFAULT_SPARK_SQL_PROXY_MAINCLASS);
            sparkConfig.setSparkSqlProxyMainClass(DEFAULT_SPARK_SQL_PROXY_MAINCLASS);
        }

        if(errorMessage != null){
            logger.error(errorMessage);
            throw new RdosException(errorMessage);
        }
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        EJobType jobType = jobClient.getJobType();
        JobResult jobResult = null;
        if(EJobType.MR.equals(jobType)){
            jobResult = submitJobWithJar(jobClient);
        }else if(EJobType.SQL.equals(jobType)){
            jobResult = submitSqlJob(jobClient);
        }
        return jobResult;
    }

    //FIXME spark conf 设置细化
    private JobResult submitJobWithJar(JobClient jobClient) {

        JobParam jobParam = new JobParam(jobClient);

        String mainClass = jobParam.getMainClass();
        String jarPath = jobParam.getJarPath();//只支持hdfs
        String appName = jobParam.getJobName();
        String exeArgsStr = jobParam.getClassArgs();

        if(!jarPath.startsWith("hdfs://")){
            throw new RdosException("spark jar path protocol must be hdfs://");
        }

        if(Strings.isNullOrEmpty(appName)){
            throw new RdosException("spark jar must set app name!");
        }


        String[] appArgs = new String[]{};
        if(StringUtils.isNotBlank(exeArgsStr)){
            appArgs = exeArgsStr.split("\\s+");
        }

        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster(sparkConfig.getSparkMaster());
        sparkConf.set("spark.submit.deployMode", deployMode);
        sparkConf.setAppName(appName);
        sparkConf.set("spark.jars", jarPath);
        fillExtSparkConf(sparkConf, jobClient.getConfProperties());
        SubmitRestProtocolResponse response = RestSubmissionClient.run(jarPath, mainClass,
                appArgs, sparkConf, new scala.collection.immutable.HashMap<String, String>());
        return processRemoteResponse(response);
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

        }

        throw new RdosException("not support for compute type :" + computeType);

    }

    /**
     * 执行spark 批处理sql
     * @param jobClient
     * @return
     */
    private JobResult submitSparkSqlJobForBatch(JobClient jobClient){

        Map<String, Object> paramsMap = new HashMap<>();

        String zipSql = DtStringUtil.zip(jobClient.getSql());
        paramsMap.put("sql", zipSql);
        paramsMap.put("appName", jobClient.getJobName());

        String sqlExeJson = null;
        try{
            sqlExeJson = objMapper.writeValueAsString(paramsMap);
            sqlExeJson = URLEncoder.encode(sqlExeJson, Charsets.UTF_8.name());
        }catch (Exception e){
            logger.error("", e);
            throw new RdosException("get unexpected exception:" + e.getMessage());
        }

        String[] appArgs = new String[]{sqlExeJson};
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster(sparkConfig.getSparkMaster());
        sparkConf.set("spark.submit.deployMode", deployMode);
        sparkConf.setAppName(jobClient.getJobName());
        sparkConf.set("spark.jars", sparkConfig.getSparkSqlProxyPath());
        fillExtSparkConf(sparkConf, jobClient.getConfProperties());
        SubmitRestProtocolResponse response = RestSubmissionClient.run(sparkConfig.getSparkSqlProxyPath(), sparkConfig.getSparkSqlProxyMainClass(),
                appArgs, sparkConf, new scala.collection.immutable.HashMap<String, String>());
       return processRemoteResponse(response);
    }

    /**
     * 通过提交的paramsOperator 设置sparkConf
     * FIXME 解析传递过来的参数是不带spark.前面缀的,如果参数和spark支持不一致的话是否需要转换
     * @param sparkConf
     * @param confProperties
     */
    private void fillExtSparkConf(SparkConf sparkConf, Properties confProperties){
          SparkConfig.initDefautlConf(sparkConf);
        for(Map.Entry<Object, Object> param : confProperties.entrySet()){
            String key = (String) param.getKey();
            String val = (String) param.getValue();
            key = KEY_PRE_STR + key;
            sparkConf.set(key, val);
        }
    }

    private JobResult processRemoteResponse(SubmitRestProtocolResponse response){

        String submitJson = response.toJson();
        boolean submitResult = false;
        String submissionId = "";
        try {
            Map<String, Object> submitMap = objMapper.readValue(submitJson, Map.class);
            submissionId = (String) submitMap.get("submissionId");
            submitResult = (boolean) submitMap.get("success");
            if(Strings.isNullOrEmpty(submissionId)){
                logger.info("submit job failure");
                if(submitMap.containsKey("message")){
                    return JobResult.createErrorResult((String) submitMap.get("message"));
                }
                return JobResult.createErrorResult("submit job get unknown error" + response.toString());
            }
        } catch (IOException e) {
            logger.error("", e);
            throw new RdosException("submit spark job exception:" + e.getMessage());
        }

        logger.info("submit job {} over, result {}.", submissionId, submitResult);
        return JobResult.createSuccessResult(submissionId);
    }

    private JobResult submitSparkSqlJobForStream(JobClient jobClient){
        throw new RdosException("not support spark sql job for stream type.");
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();
        RestSubmissionClient restSubmissionClient = new RestSubmissionClient(sparkConfig.getSparkMaster());
        SubmitRestProtocolResponse response = restSubmissionClient.killSubmission(jobId);
        String responseStr = response.toJson();

        if(Strings.isNullOrEmpty(responseStr)){
            return JobResult.createErrorResult("get null from spark response for kill " + jobId);
        }

        Map<String, Object> responseMap = null;
        try{
            responseMap = objMapper.readValue(responseStr, Map.class);
        }catch (Exception e){
            logger.error("", e);
            return JobResult.createErrorResult(e.getMessage());
        }

        boolean result = (boolean) responseMap.get("success");
        if(!result){
            String msg = (String) responseMap.get("message");
            return JobResult.createErrorResult(msg);
        }

        return JobResult.createSuccessResult(jobId);
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {

        String jobId = jobIdentifier.getEngineJobId();

    	if(StringUtils.isBlank(jobId)){
    		return null;
    	}
        RestSubmissionClient restSubmissionClient = new RestSubmissionClient(sparkConfig.getSparkMaster());
        SubmitRestProtocolResponse response = restSubmissionClient.requestSubmissionStatus(jobId, false);
        String responseStr = response.toJson();
        if(Strings.isNullOrEmpty(responseStr)){
            return RdosTaskStatus.NOTFOUND;
        }

        Map<String, Object> responseMap = objMapper.readValue(responseStr, Map.class);
        String state = (String) responseMap.get("driverState");
        RdosTaskStatus status = RdosTaskStatus.getTaskStatus(state);

        //FIXME 特殊逻辑,所有处于已经提交的状态都认为是等待资源
        if(status == RdosTaskStatus.SUBMITTED){
            status = RdosTaskStatus.WAITCOMPUTE;
        }

        return status;
    }

	@Override
	public String getJobMaster(JobIdentifier jobIdentifier) {
		String webMaster = sparkConfig.getSparkWebMaster();
		String[] webs = webMaster.split(",");
		for(String web:webs){
            String html = null;
            try {
                html = PoolHttpClient.get(String.format("http://%s", web));
            } catch (IOException e) {
               continue;
            }

			Document doc = Jsoup.parse(html);
			Elements unstyled = doc.getElementsByClass("unstyled");
			Elements lis = unstyled.first().getElementsByTag("li");
			String status = lis.last().text();
			if(status!=null){
				String[] ss = status.split(":");
				if(ss.length==2){
					if(Status.ALIVE.name().equals(ss[1].trim())){
						return web;
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getMessageByHttp(String path) {
		String url = getJobMaster(null);
		if(url == null){
		    logger.error("-----spark client maybe down. please check it.------");
		    return null;
        }

        try {
            return PoolHttpClient.get(String.format("http://%s%s", url,path));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();
        SparkJobLog sparkJobLog = new SparkJobLog();
        String rootMessage = getMessageByHttp(SparkStandaloneRestParseUtil.ROOT);

        if (rootMessage == null) {
            String msg = "can not get message from " + SparkStandaloneRestParseUtil.ROOT;
            sparkJobLog.addAppLog(jobId, msg);
            return sparkJobLog.toString();
        }

        String driverLogUrl = SparkStandaloneRestParseUtil.getDriverLogUrl(rootMessage, jobId);

        String driverLog = SparkStandaloneRestParseUtil.getDriverLog(driverLogUrl);
        if (driverLog == null) {
            String msg = "parse driver log message error. see the server log for detail.";
            sparkJobLog.addAppLog(jobId, msg);
            return sparkJobLog.toString();
        }

        String appId = SparkStandaloneRestParseUtil.getAppIdNew(driverLogUrl);
        if (appId == null) {
            String msg = "get spark app id exception. see the server log for detail.";
            sparkJobLog.addAppLog(jobId, msg);
            return sparkJobLog.toString();
        }

        String url = String.format(SparkStandaloneRestParseUtil.APP_LOG_URL_FORMAT, appId);
        String appMessage = getMessageByHttp(url);

        sparkJobLog = SparkStandaloneRestParseUtil.getAppLog(appMessage);
        sparkJobLog.addDriverLog(jobId, driverLog);

        return sparkJobLog.toString();
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        String rootMsg = getMessageByHttp(SparkStandaloneRestParseUtil.ROOT);
        EngineResourceInfo resourceInfo = SparkStandaloneRestParseUtil.getAvailSlots(rootMsg);
        if(resourceInfo == null){
            resourceInfo = new SparkResourceInfo();
        }

        return resourceInfo.judgeSlots(jobClient);
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
