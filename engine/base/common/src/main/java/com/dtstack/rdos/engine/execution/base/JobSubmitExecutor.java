package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBack;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBackMethod;
import com.dtstack.rdos.engine.execution.base.components.EngineDeployInfo;
import com.dtstack.rdos.engine.execution.base.components.OrderLinkedBlockingQueue;
import com.dtstack.rdos.engine.execution.base.components.OrderObject;
import com.dtstack.rdos.engine.execution.base.components.SlotNoAvailableJobClient;
import com.dtstack.rdos.engine.execution.base.components.SlotsJudge;
import com.dtstack.rdos.engine.execution.base.enumeration.EDeployType;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.pojo.SparkJobLog;
import com.dtstack.rdos.engine.execution.base.sql.parser.SqlParser;
import com.dtstack.rdos.engine.execution.base.util.FlinkStandaloneRestParseUtil;
import com.dtstack.rdos.engine.execution.base.util.SparkStandaloneRestParseUtil;
import com.dtstack.rdos.engine.execution.base.util.SparkYarnRestParseUtil;
import com.dtstack.rdos.engine.execution.loader.DtClassLoader;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 任务提交执行容器
 * 单独起线程执行
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
public class JobSubmitExecutor{

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitExecutor.class);

    private static final String ENGINE_TYPES_KEY = "engineTypes";

    private static final String TYPE_NAME_KEY = "typeName";

    private static final int AVAILABLE_SLOTS_INTERVAL = 5000;//mills

    private static final int THREAD_REJECT_INTERVAL = 5000;//mills

    private Pattern engineNamePattern = Pattern.compile("([a-zA-Z]*).*");

    public static final String SLOTS_KEY = "slots";//可以并行提交job的线程数

    public static final String ENGINE_TYPE_KEY = "engineTypes";

    private int minPollSize = 10;

    private int maxPoolSize = 1000;

    private ExecutorService executor;
    
    private ExecutorService queExecutor;

    private boolean hasInit = false;

    private Map<String, IClient> clientMap = new HashMap<>();

    private List<Map<String, Object>> clientParamsList;

	private static String userDir = System.getProperty("user.dir");

    private static JobSubmitExecutor singleton = new JobSubmitExecutor();

    private ClassLoaderCallBackMethod classLoaderCallBackMethod = new ClassLoaderCallBackMethod<Object>();

    private OrderLinkedBlockingQueue<OrderObject> orderLinkedBlockingQueue = new OrderLinkedBlockingQueue<OrderObject>();

    private SlotNoAvailableJobClient slotNoAvailableJobClients = new SlotNoAvailableJobClient();
    
    private Map<String,Map<String,Map<String,Object>>> slotsInfo = Maps.newConcurrentMap();

    private EngineDeployInfo engineDeployInfo;

    /**用于控制处理任务的线程在其他条件准备好之后才能开始启动,目前有slot资源获取准备*/
    final CountDownLatch processCountDownLatch = new CountDownLatch(1);
    
    private SlotsJudge slotsjudge = new SlotsJudge();
    
    private JobSubmitExecutor(){}

    public void init(Map<String,Object> engineConf) throws Exception{
        if(!hasInit){
            Object slots = engineConf.get(SLOTS_KEY);
            if(slots!=null){this.maxPoolSize = (int) slots;}
            clientParamsList = (List<Map<String, Object>>) engineConf.get(ENGINE_TYPES_KEY);

            executor = new ThreadPoolExecutor(minPollSize, maxPoolSize,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(1));


            queExecutor = new ThreadPoolExecutor(3, 3,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(2), new CustomThreadFactory("queExecutor"));
            initJobClient(clientParamsList);
            executionJob();
            noAvailSlotsJobaddExecutionQueue();

            List<Map<String, Object>> engineTypeList = (List<Map<String, Object>>) engineConf.get(ENGINE_TYPE_KEY);
            engineDeployInfo = new EngineDeployInfo(engineTypeList);

            getEngineAvailableSlots();
            hasInit = true;
        }
    }


    private void executionJob(){
    	queExecutor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    processCountDownLatch.await();
                    logger.info("----start JobSubmitProcessor-----");
                } catch (InterruptedException e) {
                    logger.error("", e);
                }

                for(;;){
                    JobClient jobClient = null;
                    try{
                        jobClient = (JobClient)orderLinkedBlockingQueue.take();
                        executor.submit(new JobSubmitProcessor(jobClient));
                    }catch (RejectedExecutionException rejectEx){
                        //如果添加到执行线程池失败则添加回等待队列,并等待5s
                        try {
                            Thread.sleep(THREAD_REJECT_INTERVAL);
                            orderLinkedBlockingQueue.put(jobClient);
                        } catch (InterruptedException e) {
                            logger.error("", e);
                        }
                    }catch(Exception e){
                        logger.error("", e);
                    }
                }
            }
        });
    }
    
    private void noAvailSlotsJobaddExecutionQueue(){
    	queExecutor.submit(new Runnable(){
			@Override
			public void run() {
				for(;;){
					try {
						Thread.sleep(THREAD_REJECT_INTERVAL);
						slotNoAvailableJobClients.noAvailSlotsJobaddExecutionQueue(orderLinkedBlockingQueue);
					} catch (InterruptedException e) {
						logger.error("", e);
					}
				}
			}
    	});
    }
    
    private void initJobClient(List<Map<String, Object>> clientParamsList) throws Exception{
        for(Map<String, Object> params : clientParamsList){
            String clientTypeStr = (String) params.get(TYPE_NAME_KEY);
            if(clientTypeStr == null){
                String errorMess = "node.yml of engineTypes setting error, typeName must not be null!!!";
                logger.error(errorMess);
                throw new RdosException(errorMess);
            }

            loadComputerPlugin(clientTypeStr);
            IClient client = ClientFactory.getClient(clientTypeStr);
            Properties clusterProp = new Properties();
            clusterProp.putAll(params);

            classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
                @Override
                public Object execute() throws Exception {
                    client.init(clusterProp);
                    return null;
                }
            },client.getClass().getClassLoader(),null,true);

            String key = getEngineName(clientTypeStr);
            clientMap.put(key, client);
        }
    }

    private void loadComputerPlugin(String pluginType) throws Exception{
    	String plugin = String.format("%s/plugin/%s", userDir,pluginType);
		File finput = new File(plugin);
		if(!finput.exists()){
			throw new Exception(String.format("%s direcotry not found",plugin));
		}
		ClientFactory.initPluginClass(pluginType, getClassLoad(finput));
    }

	private URLClassLoader getClassLoad(File dir) throws MalformedURLException, IOException{
		File[] files = dir.listFiles();
		URL[] urls = new URL[files.length];
		int index = 0;
	    if (files!=null&&files.length>0){
			for(File f:files){
				String jarName = f.getName();
				if(f.isFile()&&jarName.endsWith(".jar")){
					urls[index] = f.toURI().toURL();
					index = index+1;
				}
			}
	    }
    	return new DtClassLoader(urls, this.getClass().getClassLoader());
	}

    public static JobSubmitExecutor getInstance(){
        return singleton;
    }

    public void submitJob(JobClient jobClient) throws Exception{
        orderLinkedBlockingQueue.put(jobClient);
    }

    public RdosTaskStatus getJobStatus(String engineType, String jobId){

        if(Strings.isNullOrEmpty(jobId)){
            throw new RdosException("can't get job of jobId is empty or null!");
        }

        IClient client = clientMap.get(engineType);
        try{
        	RdosTaskStatus status = (RdosTaskStatus) classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
                 @Override
                 public Object execute() throws Exception {
                     return client.getJobStatus(jobId);
                 }
             },client.getClass().getClassLoader(),null,true);

        	if(status == RdosTaskStatus.FAILED){
        		status = judgeSlotsAndAgainExecute(engineType,jobId)?RdosTaskStatus.WAITCOMPUTE:status;
        	}
            return status;
        }catch (Exception e){
            logger.error("", e);
            throw new RdosException("get job:" + jobId + " exception:" + e.getMessage());
        }
    }

    public Map<String, String> getJobMaster(){
    	final Map<String,String> jobMasters = Maps.newConcurrentMap();
        clientMap.forEach((k,v)->{
            if(StringUtils.isNotBlank(v.getJobMaster())){
                try {
                    classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
                      @Override
                      public Object execute() throws Exception {
                          jobMasters.put(k, v.getJobMaster());
                          return null;
                      }
                  },v.getClass().getClassLoader(),null,true);
                } catch (Exception e) {
                   logger.error("",e);
                }
            }
    	});
        return jobMasters;
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {

        if(orderLinkedBlockingQueue.remove(jobClient.getTaskId())
                || slotNoAvailableJobClients.remove(jobClient.getTaskId())){
            //直接移除
            Map<String, Integer> jobStatus = Maps.newHashMap();
            jobStatus.put(JobClientCallBack.JOB_STATUS, RdosTaskStatus.CANCELED.getStatus());
            jobClient.getJobClientCallBack().execute(jobStatus);
        }

        if(jobClient.getEngineTaskId() == null){
            return JobResult.createSuccessResult(jobClient.getTaskId());
        }

        String engineType = jobClient.getEngineType();
        IClient client = clientMap.get(engineType);
        JobResult jobResult = (JobResult) classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
            @Override
            public Object execute() throws Exception {
                return client.cancelJob(jobClient.getEngineTaskId());
            }
        }, client.getClass().getClassLoader(),null,true);

    	return jobResult;
    }
    
    private void getEngineAvailableSlots(){

    	queExecutor.submit(new Runnable(){

    	    private boolean firstStart = true;

			@Override
			public void run() {

			    while(true){

                    try {
                        Set<Map.Entry<String,IClient>> entrys = clientMap.entrySet();

                        for(Map.Entry<String, IClient> entry : entrys){

                            try{
                                String key = entry.getKey();
                                String keyWithoutVersion = EngineType.getEngineTypeWithoutVersion(key);
                                Integer deployTypeVal = engineDeployInfo.getDeployMap().get(keyWithoutVersion);

                                IClient client = entry.getValue();

                                if(EngineType.isFlink(key) && EDeployType.STANDALONE.getType() == deployTypeVal){
                                    String message = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
                                        @Override
                                        public Object execute() throws Exception {
                                            return client.getMessageByHttp(FlinkStandaloneRestParseUtil.SLOTS_INFO);
                                        }

                                    },client.getClass().getClassLoader(),null,true);

                                    slotsInfo.put(key, FlinkStandaloneRestParseUtil.getAvailSlots(message));
                                }else if(EngineType.isSpark(key) && EDeployType.STANDALONE.getType() == deployTypeVal){

                                    String message = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
                                        @Override
                                        public Object execute() throws Exception {
                                            return client.getMessageByHttp(SparkStandaloneRestParseUtil.ROOT);
                                        }

                                    },client.getClass().getClassLoader(),null,true);

                                    slotsInfo.put(key, SparkStandaloneRestParseUtil.getAvailSlots(message));
                                }

                            }catch (Exception e){
                                logger.error("", e);
                            }

                            if(firstStart){
                                processCountDownLatch.countDown();
                                logger.info("----get available slots thread started-----");
                            }

                        }
                        Thread.sleep(AVAILABLE_SLOTS_INTERVAL);
                    } catch (InterruptedException e1) {
                        logger.error("", e1);
                    }
                }
			}
    	});
    }
    
    
    public String  getEngineMessageByHttp(String engineType,String path){
    	IClient client = clientMap.get(engineType);
	    String message = "";
		try {
			message = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
			    @Override
			    public Object execute() throws Exception {
			        return client.getMessageByHttp(path);
			    }
			},client.getClass().getClassLoader(),null,true);
		} catch (Exception e) {
			logger.error("", e);
		}
	    return message;
    }

    public String getEngineLogByHttp(String engineType, String jobId) {
        IClient client = clientMap.get(engineType);
        EDeployType deployType = JobSubmitExecutor.getInstance().getEngineDeployType(engineType);

        String logInfo = "";

        try {
            if (EngineType.isFlink(engineType) && deployType == EDeployType.STANDALONE) {
                Map<String,String> logJsonMap = new ClassLoaderCallBackMethod<Map<String,String>>()
                        .callback(()-> {
                            String exceptPath = String.format(FlinkStandaloneRestParseUtil.EXCEPTION_INFO, jobId);
                            String except = client.getMessageByHttp(exceptPath);
                            String jobPath = String.format(FlinkStandaloneRestParseUtil.JOB_INFO, jobId);
                            String jobInfo = client.getMessageByHttp(jobPath);
                            String accuPath = String.format(FlinkStandaloneRestParseUtil.JOB_ACCUMULATOR_INFO, jobId);
                            String accuInfo = client.getMessageByHttp(accuPath);
                            Map<String,String> retMap = new HashMap<String, String>();
                            retMap.put("except", except);
                            retMap.put("jobInfo", jobInfo);
                            retMap.put("accuInfo", accuInfo);
                            return retMap;
                        }, client.getClass().getClassLoader(), null, true);

                logInfo = FlinkStandaloneRestParseUtil.parseEngineLog(logJsonMap);

            } else if (EngineType.isSpark(engineType) && deployType == EDeployType.STANDALONE) {

                String rootMessage = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack() {
                    @Override
                    public Object execute() throws Exception {
                        return client.getMessageByHttp(SparkStandaloneRestParseUtil.ROOT);
                    }
                }, client.getClass().getClassLoader(), null, true);

                if (rootMessage == null) {
                    return "can not get message from " + SparkStandaloneRestParseUtil.ROOT;
                }

                String driverLog = SparkStandaloneRestParseUtil.getDriverLog(rootMessage, jobId);
                if (driverLog == null) {
                    return "parse driver log message error. see the server log for detail.";
                }

                String appId = SparkStandaloneRestParseUtil.getAppId(driverLog);
                if (appId == null) {
                    return "get spark app id exception. see the server log for detail.";
                }

                String url = String.format(SparkStandaloneRestParseUtil.APP_LOGURL_FORMAT, appId);
                String appMessage = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack() {
                    @Override
                    public Object execute() throws Exception {
                        return client.getMessageByHttp(url);
                    }
                }, client.getClass().getClassLoader(), null, true);

                SparkJobLog sparkJobLog = SparkStandaloneRestParseUtil.getAppLog(appMessage);
                sparkJobLog.addDriverLog(jobId, driverLog);

                logInfo = sparkJobLog.toString();
            } else if(EngineType.isSpark(engineType) && deployType == EDeployType.YARN){

                String appReqUrl = String.format(SparkYarnRestParseUtil.APPLICATION_WS_FORMAT, jobId);
                String rootMessage = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack() {
                    @Override
                    public Object execute() throws Exception {
                        return client.getMessageByHttp(appReqUrl);
                    }
                }, client.getClass().getClassLoader(), null, true);

                String containerLogURL = SparkYarnRestParseUtil.getContainerLogURL(rootMessage);
                if(containerLogURL == null){
                    return "can not get amContainerLogs by yarn webservice api.";
                }

                String appLogUrl = String.format(SparkYarnRestParseUtil.APPLICATION_LOG_URL_FORMAT, containerLogURL);
                String logMsg = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack() {
                    @Override
                    public Object execute() throws Exception {
                        return client.getMessageByHttp(appLogUrl);
                    }
                }, client.getClass().getClassLoader(), null, true);

                SparkJobLog sparkJobLog = new SparkJobLog();
                sparkJobLog.addAppLog(jobId, logMsg);
                logInfo = sparkJobLog.toString();
            }else {
                logInfo = "not support for " + engineType + " to get exception info.";
            }
        }catch (Exception e){
            logger.error("", e);
            logInfo = "get engine message error," + e.getMessage();
        }

        return logInfo;
    }
    
    public void shutdown(){
        if(executor != null){
            executor.shutdown();
        }

        if(queExecutor != null){
        	queExecutor.shutdown();
        }
    }
    
    /**
     * FIXME 去掉引擎版本号作为key
     * @param clientTypeStr
     * @return
     */
    public String getEngineName(String clientTypeStr){

        Matcher matcher = engineNamePattern.matcher(clientTypeStr);
        if(matcher.find()){
            return matcher.group(1).toLowerCase();
        }else{
            logger.error("can't match clientTypeStr:{} by ([a-zA-Z]*).*", clientTypeStr);
            return clientTypeStr;
        }
    }

    public EDeployType getEngineDeployType(String clientTypeStr){
        Integer type = engineDeployInfo.getDeployMap().get(clientTypeStr);
        if(type == null){
            return  null;
        }

        return EDeployType.getDeployType(type);
    }
    
    
	public boolean judgeSlotsAndAgainExecute(String engineType, String jobId) {
		if(EngineType.isFlink(engineType)){
			String message = getEngineMessageByHttp(engineType,String.format(FlinkStandaloneRestParseUtil.EXCEPTION_INFO,jobId));
            return FlinkStandaloneRestParseUtil.checkNoSlots(message);
		}
		return false;
	}

    class JobSubmitProcessor implements Runnable{

        private JobClient jobClient;

        public JobSubmitProcessor(JobClient jobClient) throws Exception{
            this.jobClient = jobClient;
        }

        @Override
        public void run(){
            if(jobClient != null){

                Map<String, Integer> updateStatus = Maps.newHashMap();
                updateStatus.put(JobClientCallBack.JOB_STATUS, RdosTaskStatus.WAITCOMPUTE.getStatus());

                jobClient.getJobClientCallBack().execute(updateStatus);
                IClient clusterClient = clientMap.get(jobClient.getEngineType());
                JobResult jobResult = null;

                if(clusterClient == null){
                    jobResult = JobResult.createErrorResult("setting client type " +
                            "(" + jobClient.getEngineType()  +") don't found.");
                    listenerJobStatus(jobClient, jobResult);
                    return;
                }

                try {
                    jobClient.setConfProperties(PublicUtil.stringToProperties(jobClient.getTaskParams()));

                    if(slotsjudge.judgeSlots(jobClient, slotsInfo)){

                        logger.info("--------submit job:{} to engine start----.", jobClient.getTaskId());
                        updateStatus.put(JobClientCallBack.JOB_STATUS, RdosTaskStatus.SUBMITTING.getStatus());
                        jobClient.getJobClientCallBack().execute(updateStatus);

                        jobClient.setOperators(SqlParser.parser(jobClient.getEngineType(), jobClient.getComputeType().getComputeType(), jobClient.getSql()));

                        jobResult = (JobResult) classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
                            @Override
                            public Object execute() throws Exception {
                                return clusterClient.submitJob(jobClient);
                            }
                        },clusterClient.getClass().getClassLoader(),null,true);

                        logger.info("submit job result is:{}.", jobResult);
                        String jobId = jobResult.getData(JobResult.JOB_ID_KEY);
                        jobClient.setEngineTaskId(jobId);
                	}

                }catch (Throwable e){
                    //捕获未处理异常,防止跳出执行线程
                    jobClient.setEngineTaskId(null);
                    jobResult = JobResult.createErrorResult(e);
                    logger.error("get unexpected exception", e);
                }finally {
                    jobClient.setJobResult(jobResult);
                    slotNoAvailableJobClients.put(jobClient);
                    logger.info("--------submit job:{} to engine end----", jobClient.getTaskId());
                }
            }
        }

        private void listenerJobStatus(JobClient jobClient, JobResult jobResult){
            jobClient.setJobResult(jobResult);
            JobClient.getQueue().offer(jobClient);//添加触发读取任务状态消息
        }
    }
}

