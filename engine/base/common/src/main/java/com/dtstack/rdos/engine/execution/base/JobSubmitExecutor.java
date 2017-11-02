package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBack;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBackMethod;
import com.dtstack.rdos.engine.execution.base.components.OrderLinkedBlockingQueue;
import com.dtstack.rdos.engine.execution.base.components.OrderObject;
import com.dtstack.rdos.engine.execution.base.components.SlotNoAvailableJobClient;
import com.dtstack.rdos.engine.execution.base.components.SlotsJudge;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.sql.parser.SqlParser;
import com.dtstack.rdos.engine.execution.base.util.EngineRestParseUtil;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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

    private static final int STATUS_INTERVAL = 5000;

    private static final int THREAD_REJECT_INTERVAL = 5000;

    private Pattern engineNamePattern = Pattern.compile("([a-zA-Z]*).*");

    public static final String SLOTS_KEY = "slots";//可以并行提交job的线程数

    private int minPollSize = 10;

    private int maxPoolSize = 1000;

    private ExecutorService executor;
    
    private ExecutorService queExecutor;

    private boolean hasInit = false;

    private Map<String, IClient> clientMap = new HashMap<>();

    private List<Map<String, Object>> clientParamsList;

	private static String userDir = System.getProperty("user.dir");

    private static JobSubmitExecutor singleton = new JobSubmitExecutor();

    private ClassLoaderCallBackMethod classLoaderCallBackMethod = new ClassLoaderCallBackMethod();

    private OrderLinkedBlockingQueue<OrderObject> orderLinkedBlockingQueue = new OrderLinkedBlockingQueue<OrderObject>();

    private SlotNoAvailableJobClient slotNoAvailableJobClients = new SlotNoAvailableJobClient();
    
    private Map<String,Map<String,Map<String,Object>>> slotsInfo = Maps.newConcurrentMap();
    
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
            getEngineAvailbalSlots();
            hasInit = true;
        }
    }


    private void executionJob(){
    	queExecutor.submit(new Runnable() {
            @Override
            public void run() {
                for(;;){
                    JobClient jobClient = null;
                    try{
                        jobClient = (JobClient)orderLinkedBlockingQueue.take();
                        executor.submit(new JobSubmitProcessor(jobClient));
                    }catch (RejectedExecutionException rejectEx){
                        //如果是添加到执行线程池失败则添加回等待队列,并等待5s
                        try {
                            orderLinkedBlockingQueue.put(jobClient);
                            Thread.sleep(THREAD_REJECT_INTERVAL);
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
				// TODO Auto-generated method stub
				for(;;){
					try {
						Thread.sleep(5000);
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
        		status = judgeSlostsAndAgainExecute(engineType,jobId)?RdosTaskStatus.WAITCOMPUTE:status;
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
    	if(orderLinkedBlockingQueue.remove(jobClient.getTaskId())||slotNoAvailableJobClients.remove(jobClient.getTaskId())){

    	    if(jobClient.getEngineTaskId() == null){
    	        return JobResult.createSuccessResult(jobClient.getTaskId());
            }

            String engineType = jobClient.getEngineType();
            IClient client = clientMap.get(engineType);
            return  (JobResult) classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
                @Override
                public Object execute() throws Exception {
                    return client.cancelJob(jobClient.getEngineTaskId());
                }
            },client.getClass().getClassLoader(),null,true);
    	}
    	jobClient.getJobClientCallBack().execute();
    	return JobResult.createSuccessResult(jobClient.getTaskId());
    }
    
    private void getEngineAvailbalSlots(){

    	queExecutor.submit(new Runnable(){

			@Override
			public void run() {

			    while(true){

                    try {
                        Thread.sleep(STATUS_INTERVAL);
                        Set<Map.Entry<String,IClient>> entrys = clientMap.entrySet();

                        for(Map.Entry<String,IClient> entry : entrys){

                            try{
                                String key = entry.getKey();
                                IClient client = entry.getValue();

                                if(EngineType.isFlink(key)){
                                    String message = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
                                        @Override
                                        public Object execute() throws Exception {
                                            return client.getMessageByHttp(EngineRestParseUtil.FlinkRestParseUtil.SLOTS_INFO);
                                        }

                                    },client.getClass().getClassLoader(),null,true);

                                    slotsInfo.put(key, EngineRestParseUtil.FlinkRestParseUtil.getAvailSlots(message));
                                }else if(EngineType.isSpark(key)){

                                    String message = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
                                        @Override
                                        public Object execute() throws Exception {
                                            return client.getMessageByHttp(EngineRestParseUtil.SparkRestParseUtil.ROOT);
                                        }

                                    },client.getClass().getClassLoader(),null,true);

                                    slotsInfo.put(key, EngineRestParseUtil.SparkRestParseUtil.getAvailSlots(message));
                                }

                            }catch (Exception e){
                                logger.error("", e);
                            }
                        }
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
        String logInfo = "";

        try {
            if (EngineType.isFlink(engineType)) {
                String message = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack() {
                    @Override
                    public Object execute() throws Exception {
                        String excpPath = String.format(EngineRestParseUtil.FlinkRestParseUtil.EXCEPTION_INFO, jobId);
                        return client.getMessageByHttp(excpPath);
                    }
                }, client.getClass().getClassLoader(), null, true);
                logInfo = message;
            } else if (EngineType.isSpark(engineType)) {

                Map<String, List<Map<String, String>>> log = null;
                String rootMessage = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack() {
                    @Override
                    public Object execute() throws Exception {
                        return client.getMessageByHttp(EngineRestParseUtil.SparkRestParseUtil.ROOT);
                    }
                }, client.getClass().getClassLoader(), null, true);

                if (rootMessage == null) {
                    return "can not get message from " + EngineRestParseUtil.SparkRestParseUtil.ROOT;
                }

                String driverLog = EngineRestParseUtil.SparkRestParseUtil.getDriverLog(rootMessage, jobId);
                if (driverLog == null) {
                    return "parse driver log message error. see the server log for detail.";
                }

                String appId = EngineRestParseUtil.SparkRestParseUtil.getAppId(driverLog);
                if (appId == null) {
                    return "get spark app id exception. see the server log for detail.";
                }

                String url = String.format(EngineRestParseUtil.SparkRestParseUtil.APP_LOGURL_FORMAT, appId);
                String appMessage = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack() {
                    @Override
                    public Object execute() throws Exception {
                        return client.getMessageByHttp(url);
                    }
                }, client.getClass().getClassLoader(), null, true);

                log = EngineRestParseUtil.SparkRestParseUtil.getAppLog(appMessage);
                List<Map<String, String>> list = new ArrayList<>();
                Map<String, String> map = new HashMap<>();
                map.put("id", jobId);
                map.put("value", driverLog);
                list.add(map);
                log.put("driverLog", list);

                logInfo = PublicUtil.objToString(log);
            } else {
                logInfo = "not support for " + engineType + " to get exception info.";
            }
        }catch (Exception e){
            logger.error("", e);
            logInfo = "get engine message error," + e.getMessage();
        }

        return logInfo;
    }
    
    public void shutdown(){
        //FIXME 是否需要做同步等processor真正完成
        if(executor!=null){
            executor.shutdown();
        }
        if(queExecutor!=null){
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
    
    
	public boolean judgeSlostsAndAgainExecute(String engineType, String jobId) {
		if(EngineType.isFlink(engineType)){
			String message = getEngineMessageByHttp(engineType,String.format(EngineRestParseUtil.FlinkRestParseUtil.EXCEPTION_INFO,jobId));
			if(StringUtils.isNotBlank(message)){
				if(message.indexOf(EngineRestParseUtil.FlinkRestParseUtil.NORESOURCEAVAIABLEEXCEPYION) >= 0){
					return true;
				}
			}
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
                jobClient.getJobClientCallBack().execute();
                IClient clusterClient = clientMap.get(jobClient.getEngineType());
                JobResult jobResult = null;
                boolean needTaskListener = false;

                if(clusterClient == null){
                    jobResult = JobResult.createErrorResult("job setting client type " +
                            "(" + jobClient.getEngineType()  +") don't found.");
                    listenerJobStatus(jobClient, jobResult);
                    return;
                }

                try {
                    jobClient.setConfProperties(PublicUtil.stringToProperties(jobClient.getTaskParams()));

                    if(slotsjudge.judgeSlots(jobClient, slotsInfo)){
                        needTaskListener = true;
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
            		slotNoAvailableJobClients.put(jobClient);
                }catch (Throwable e){//捕获未处理异常,防止跳出执行线程
                    jobClient.setEngineTaskId(null);
                    jobResult = JobResult.createErrorResult(e);
                    logger.error("get unexpected exception", e);
                }finally {
                    if(needTaskListener){
                        listenerJobStatus(jobClient, jobResult);
                    }
                }
            }
        }

        private void listenerJobStatus(JobClient jobClient, JobResult jobResult){
            //FIXME 需要对本地异常信息做存储
            jobClient.setJobResult(jobResult);
            JobClient.getQueue().offer(jobClient);//添加触发读取任务状态消息
        }
    }
}

class CustomThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    CustomThreadFactory(String name) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" + name + "-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
