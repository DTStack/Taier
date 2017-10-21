package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.loader.DtClassLoader;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.execution.base.sql.parser.SqlParser;
import com.dtstack.rdos.engine.execution.base.util.EngineRestParseUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.dtstack.rdos.engine.execution.base.components.SlotsJudge;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBack;
import com.dtstack.rdos.engine.execution.base.callback.ClassLoaderCallBackMethod;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;
import com.dtstack.rdos.engine.execution.base.components.OrderLinkedBlockingQueue;
import com.dtstack.rdos.engine.execution.base.components.OrderObject;
import com.dtstack.rdos.engine.execution.base.components.SlotNoAvailableJobClient;
/**
 * 任务提交执行容器
 * 单独起线程执行
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class JobSubmitExecutor{

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitExecutor.class);

    private static final String Engine_TYPES_KEY = "engineTypes";

    private static final String TYPE_NAME_KEY = "typeName";

    private static final int STATUS_INTERVAL = 5000;

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
            clientParamsList = (List<Map<String, Object>>) engineConf.get(Engine_TYPES_KEY);
            executor = new ThreadPoolExecutor(minPollSize, maxPoolSize,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(1));
            queExecutor = new ThreadPoolExecutor(3, 3,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(2));
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
                    try{
                        JobClient jobClient = (JobClient)orderLinkedBlockingQueue.take();
                        executor.submit(new JobSubmitProcessor(jobClient));
                    }catch(Exception e){
                        logger.error("",e);
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
						// TODO Auto-generated catch block
						e.printStackTrace();
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
            return  (RdosTaskStatus) classLoaderCallBackMethod.callback(new ClassLoaderCallBack(){
                @Override
                public Object execute() throws Exception {
                    return client.getJobStatus(jobId);
                }
            },client.getClass().getClassLoader(),null,true);
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
                        for(Map.Entry<String,IClient> entry:entrys){
                            try{
                                String key= entry.getKey();
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
                        // TODO Auto-generated catch block
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
			        return client.getMessageByHttp(EngineRestParseUtil.SparkRestParseUtil.ROOT);
			    }
			},client.getClass().getClassLoader(),null,true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return message;
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
    
    
	public boolean judgeSlostsAndAgainExecute(String engineType,String jobId) {
		// TODO Auto-generated method stub
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

                if(clusterClient == null){
                    jobResult = JobResult.createErrorResult("job setting client type " +
                            "(" + jobClient.getEngineType()  +") don't found.");
                    listenerJobStatus(jobClient, jobResult);
                    return;
                }

                try {
                    jobClient.setConfProperties(PublicUtil.stringToProperties(jobClient.getTaskParams()));

                    if(slotsjudge.judgeSlots(jobClient, slotsInfo)){
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
                    listenerJobStatus(jobClient, jobResult);
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
