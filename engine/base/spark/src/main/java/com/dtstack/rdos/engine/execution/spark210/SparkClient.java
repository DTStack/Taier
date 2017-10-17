package com.dtstack.rdos.engine.execution.spark210;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchAddJarOperator;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.execution.spark210.enums.Status;
import com.google.common.base.Strings;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

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

    /**默认每个处理器可以使用的内存大小*/
    private static final String DEFAULT_EXE_MEM = "512m";

    /**默认最多可以请求的CPU核心数*/
    private static final String DEFAULT_CORES_MAX = "1";

    /**失败后是否重启Driver，仅限于Spark Alone模式*/
    private static final String DEFAULT_SUPERVISE = "false";

    private SparkConfig sparkConfig;

    private String deployMode = "cluster";

    @Override
    public void init(Properties prop) throws Exception {

        String errorMessage = null;
        sparkConfig = objMapper.readValue(objMapper.writeValueAsBytes(prop), SparkConfig.class);
        if(sparkConfig.getSparkMaster() == null){
            errorMessage = "you need to set sparkMaster when used spark engine.";
        }else if(sparkConfig.getSparkWebMaster() == null){
            errorMessage = "you need to set sparkWebMaster when used spark engine.";
        }else if(sparkConfig.getSparkSqlProxyPath() == null){
            errorMessage = "you need to set sparkSqlProxyPath when used spark engine.";
        }else if(sparkConfig.getSparkSqlProxyMainClass() == null){
            errorMessage = "you need to set sparkSqlProxyMainClass when used spark engine.";
        }
        if(errorMessage != null){
            logger.error(errorMessage);
            throw new RdosException(errorMessage);
        }
    }

    //FIXME spark conf 设置细化
    @Override
    public JobResult submitJobWithJar(JobClient jobClient) {

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

        String[] appArgs = new String[]{sqlExeJson};
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster(sparkConfig.getSparkMaster());
        sparkConf.set("spark.submit.deployMode", deployMode);
        sparkConf.setAppName(jobClient.getJobName());
        sparkConf.set("spark.jars", sparkConfig.getSparkSqlProxyPath());
        sparkConf.set("spark.driver.supervise", "false");
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

        sparkConf.set("spark.executor.memory", DEFAULT_EXE_MEM); //默认执行内存
        sparkConf.set("spark.cores.max", DEFAULT_CORES_MAX);  //默认请求的cpu核心数
        sparkConf.set("spark.driver.supervise",DEFAULT_SUPERVISE);
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
    public JobResult cancelJob(ParamAction paramAction) {
        String jobId = paramAction.getEngineTaskId();

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
    public RdosTaskStatus getJobStatus(String jobId) throws IOException {
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
    public String getJobDetail(String jobId) {
        return null;
    }

	@Override
	public synchronized JobResult immediatelySubmitJob(JobClient jobClient) {
		// TODO Auto-generated method stub
        return null;
	}

	@Override
	public String getJobMaster() {
		// TODO Auto-generated method stub
		String webMaster = sparkConfig.getSparkWebMaster();
		String[] webs = webMaster.split(",");
		for(String web:webs){
			String html = PoolHttpClient.get(String.format("http://%s", web));
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

}
