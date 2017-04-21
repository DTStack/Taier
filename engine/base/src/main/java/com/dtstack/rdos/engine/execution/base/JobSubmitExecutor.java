package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

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

    public static final String SLOTS_KEY = "slots";//可以并行提交job的线程数

    public static final int DEAFULT_SLOT_NUM = 3;

    private BlockingQueue<JobClient> submitQueue = Queues.newLinkedBlockingQueue();

    private int poolSize = 1;

    private ExecutorService executor;

    private List<JobSubmitProcessor> processorList = new ArrayList<>();

    private boolean hasInit = false;

    private boolean isStarted = false;

    //为了获取job状态,FIXME 是否有更合适的方式?
    private Map<EngineType, IClient> clientMap = new HashMap<>();

    private static JobSubmitExecutor singleton = new JobSubmitExecutor();

    private JobSubmitExecutor(){}

    public void init(Map<String,Object> engineConf){

        Object slots = engineConf.get(SLOTS_KEY);
        this.poolSize = slots == null ? DEAFULT_SLOT_NUM : (int) slots;

        List<Map<String, Object>> clientParamsList = (List<Map<String, Object>>) engineConf.get(Engine_TYPES_KEY);
        executor = Executors.newFixedThreadPool(poolSize);
        for(int i=0; i<poolSize; i++){
            JobSubmitProcessor processor = new JobSubmitProcessor(clientParamsList);
            processorList.add(processor);
        }

        initJobStatusClient(clientParamsList);
        hasInit = true;
    }

    private void initJobStatusClient(List<Map<String, Object>> clientParamsList){

        for(Map<String, Object> params : clientParamsList){
            String clientTypeStr = (String) params.get(TYPE_NAME_KEY);
            IClient client = ClientFactory.getClient(clientTypeStr);
            Properties clusterProp = new Properties();
            clusterProp.putAll(params);
            client.init(clusterProp);

            EngineType engineType = EngineType.getEngineType(clientTypeStr);
            clientMap.put(engineType, client);
        }
    }

    public static JobSubmitExecutor getInstance(){
        return singleton;
    }

    public void submitJob(JobClient jobClient){
        submitQueue.add(jobClient);
    }

    public RdosTaskStatus getJobStatus(EngineType engineType, String jobId){
        IClient client = clientMap.get(engineType);
        try{
            return client.getJobStatus(jobId);
        }catch (Exception e){
            logger.error("", e);
            return RdosTaskStatus.FAILED;//FIXME 是否应该抛出异常或者提供新的状态
        }
    }

    public JobResult stopJob(EngineType engineType, String jobId){
        IClient client = clientMap.get(engineType);
        return client.cancelJob(jobId);
    }

    public void start(){

        if(!hasInit){
            logger.error("need to init JobSubmitExecutor first. please check your program!!!");
            System.exit(-1);
        }

        if(isStarted){
            logger.error("processor is already started");
            return;
        }

        for(JobSubmitProcessor processor : processorList){
            processor.setRunnable(true);
            executor.submit(processor);
        }

        isStarted = true;
    }

    public void shutdown(){
        isStarted = false;
        //FIXME 是否需要做同步等processor真正完成
        executor.shutdownNow();
    }

    public int getCurrJobQueue(){
        return submitQueue.size();
    }

    private synchronized JobClient getNextJob() throws InterruptedException {
        JobClient jobClient = submitQueue.poll(2000, TimeUnit.MILLISECONDS);
        return jobClient;
    }

    class JobSubmitProcessor implements Callable{

        private boolean runnable;

        private Map<EngineType, IClient> clusterClientMap = new HashMap<>();

        public JobSubmitProcessor(List<Map<String, Object>> clientParamsList){

            for(Map<String, Object> clientParams : clientParamsList){
                String clientTypeStr = (String) clientParams.get(TYPE_NAME_KEY);
                if(clientTypeStr == null){
                    logger.error("node.yml of engineTypes setting error, typeName must not be null!!!");
                    throw new RdosException("node.yml of engineTypes setting error, typeName must not be null!!!");
                }

                IClient client = ClientFactory.getClient(clientTypeStr);
                if(client == null){
                    throw new RdosException("not support for client type " + clientTypeStr);
                }

                Properties clusterProp = new Properties();
                clusterProp.putAll(clientParams);
                client.init(clusterProp);

                EngineType engineType = EngineType.getEngineType(clientTypeStr);
                clusterClientMap.put(engineType, client);
            }
        }

        @Override
        public Object call() throws Exception {
            while(runnable){
                JobClient jobClient = getNextJob();

                if(jobClient != null){

                    IClient clusterClient = clusterClientMap.get(jobClient.getEngineType());
                    JobResult jobResult = null;

                    if(clusterClient == null){
                        jobResult = JobResult.createErrorResult("job setting client type " +
                                "(" + jobClient.getEngineType()  +") don't found.");
                        updateJobStatus(jobClient, jobResult);
                        continue;
                    }

                    try{
                        jobResult = clusterClient.submitJob(jobClient);
                        logger.info("submit job result is:{}.", jobResult);
                        String jobId = jobResult.getData(JobResult.JOB_ID_KEY);
                        jobClient.setEngineTaskId(jobId);
                    }catch (Exception e){//捕获未处理异常,防止跳出执行线程
                        jobResult = JobResult.createErrorResult(e);
                        logger.error("get unexpected exception", e);
                    }catch (Error e){
                        jobResult = JobResult.createErrorResult(e);
                        logger.error("get an error, please check program!!!!", e);
                    }

                    updateJobStatus(jobClient, jobResult);
                }
            }

            return null;
        }

        private void updateJobStatus(JobClient jobClient, JobResult jobResult){
            //FIXME 之后需要对本地异常信息做存储
            jobClient.setJobResult(jobResult);
            JobClient.getQueue().offer(jobClient);//添加触发读取任务状态消息
        }

        public boolean isRunnable() {
            return runnable;
        }

        public void setRunnable(boolean runnable) {
            this.runnable = runnable;
        }
    }

}
