package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBack;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBackMethod;
import com.dtstack.rdos.engine.execution.base.components.OrderLinkedBlockingQueue;
import com.dtstack.rdos.engine.execution.base.components.OrderObject;
import com.dtstack.rdos.engine.execution.base.components.SlotNoAvailableJobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.util.SlotJudge;
import com.dtstack.rdos.engine.execution.loader.DtClassLoader;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
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

    public static final String SLOTS_KEY = "slots";//可以并行提交job的线程数

    private int minPollSize = 5;

    private int maxPoolSize = 10;

    private ExecutorService executor;

    private ExecutorService queExecutor;

    private boolean hasInit = false;

    private Map<String, IClient> clientMap = new HashMap<>();

    private List<Map<String, Object>> clientParamsList;

	private static String userDir = System.getProperty("user.dir");

    private static JobSubmitExecutor singleton = new JobSubmitExecutor();

    private ClassLoaderCallBackMethod<Object> classLoaderCallBackMethod = new ClassLoaderCallBackMethod<>();

    private OrderLinkedBlockingQueue<OrderObject> orderLinkedBlockingQueue = new OrderLinkedBlockingQueue<>();

    private SlotNoAvailableJobClient slotNoAvailableJobClients = new SlotNoAvailableJobClient();
    
    private Map<String, EngineResourceInfo> slotsInfo = Maps.newConcurrentMap();

    /**用于控制处理任务的线程在其他条件准备好之后才能开始启动,目前有slot资源获取准备*/
    final CountDownLatch processCountDownLatch = new CountDownLatch(1);

    private JobSubmitExecutor(){}

    public static JobSubmitExecutor getInstance(){
        return singleton;
    }

    public void init(Map<String,Object> engineConf) throws Exception{
        if(!hasInit){
            this.maxPoolSize = ConfigParse.getSlots();
            clientParamsList = (List<Map<String, Object>>) engineConf.get(ENGINE_TYPES_KEY);

            executor = new ThreadPoolExecutor(minPollSize, maxPoolSize,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(1), new CustomThreadFactory("jobExecutor"));

            queExecutor = new ThreadPoolExecutor(3, 3,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(2), new CustomThreadFactory("queExecutor"));

            initJobClient(clientParamsList);
            executionJob();
            noAvailSlotsJobaddExecutionQueue();

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
                        executor.submit(new JobSubmitProcessor(jobClient, clientMap, slotsInfo, slotNoAvailableJobClients));
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
						slotNoAvailableJobClients.noAvailSlotsJobAddExecutionQueue(orderLinkedBlockingQueue);
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

            String key = EngineType.getEngineTypeWithoutVersion(clientTypeStr);
            clientMap.put(key, client);
        }
    }

    private void loadComputerPlugin(String pluginType) throws Exception{
    	String plugin = String.format("%s/plugin/%s", userDir, pluginType);
		File finput = new File(plugin);
		if(!finput.exists()){
			throw new Exception(String.format("%s direcotry not found",plugin));
		}
		ClientFactory.initPluginClass(pluginType, getClassLoad(finput));
    }

	private URLClassLoader getClassLoad(File dir) throws IOException{
		File[] files = dir.listFiles();
		URL[] urls = new URL[files.length];
		int index = 0;
	    if (files!=null && files.length>0){
			for(File f : files){
				String jarName = f.getName();
				if(f.isFile() && jarName.endsWith(".jar")){
					urls[index] = f.toURI().toURL();
					index = index+1;
				}
			}
	    }
    	return new DtClassLoader(urls, this.getClass().getClassLoader());
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
        	Object result = classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
                 @Override
                 public Object execute() throws Exception {
                     return client.getJobStatus(jobId);
                 }
             },client.getClass().getClassLoader(),null,true);

        	if(result == null){
        	    return null;
            }

            RdosTaskStatus status = (RdosTaskStatus) result;

        	if(status == RdosTaskStatus.FAILED){
        		status = SlotJudge.judgeSlotsAndAgainExecute(engineType, jobId) ? RdosTaskStatus.WAITCOMPUTE : status;
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

        //FIXME 资源定时获取--修改为任务提交的时候主动获取
    	/*queExecutor.submit(new Runnable(){

    	    private boolean firstStart = true;

			@Override
			public void run() {

			    while(true){

                    try {
                        Set<Map.Entry<String,IClient>> entrySet = clientMap.entrySet();

                        for(Map.Entry<String, IClient> entry : entrySet){

                            String key = entry.getKey();
                            IClient client = entry.getValue();

                            try{

                                EngineResourceInfo slotInfo = ClassLoaderCallBackMethod.callbackAndReset(new ClassLoaderCallBack<EngineResourceInfo>(){
                                    @Override
                                    public EngineResourceInfo execute() throws Exception {
                                        return client.getAvailSlots();
                                    }
                                }, client.getClass().getClassLoader(),true);

                                slotsInfo.put(key, slotInfo);
                            }catch (Exception e){
                                logger.error("get avail slots for " + key + "error.", e);
                            }

                            if(firstStart){
                                processCountDownLatch.countDown();
                                logger.info("----get available slots thread started-----");
                                firstStart = false;
                            }
                        }
                        Thread.sleep(AVAILABLE_SLOTS_INTERVAL);
                    } catch (InterruptedException e1) {
                        logger.error("", e1);
                    }
                }
			}
    	});*/
    }
    
    
    public String getEngineMessageByHttp(String engineType, String path){
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
        try{
            logInfo = (String) classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
                @Override
                public Object execute() throws Exception {
                    return client.getJobLog(jobId);
                }
            }, client.getClass().getClassLoader(),null,true);
        }catch (Exception e){
            logger.error("", e);
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

}

