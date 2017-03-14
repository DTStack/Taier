package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.ClientType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * 任务提交执行容器
 * 单独起线程执行
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class JobSubmitExecutor{

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitExecutor.class);

    public static final String SLOTS_KEY = "slots";

    private BlockingQueue<JobClient> submitQueue = Queues.newLinkedBlockingQueue();

    private int poolSize = 1;

    private ExecutorService executor;

    private List<JobSubmitProcessor> processorList = new ArrayList<>();

    private boolean hasInit = false;

    private boolean isStarted = false;

    //为了获取job状态
    private IClient client;

    private static JobSubmitExecutor singleton = new JobSubmitExecutor();

    private JobSubmitExecutor(){}

    public void init(ClientType type, Properties clusterProp){

        Object slots = clusterProp.get(SLOTS_KEY);
        this.poolSize = slots == null ? 10 : (int) slots;

        executor = Executors.newFixedThreadPool(poolSize);
        for(int i=0; i<poolSize; i++){
            JobSubmitProcessor processor = new JobSubmitProcessor(type, clusterProp);
            processorList.add(processor);
        }

        client = ClientFactory.getClient(type);
        client.init(clusterProp);

        hasInit = true;
    }

    public static JobSubmitExecutor getInstance(){
        return singleton;
    }

    public void submitJob(JobClient jobClient){
        submitQueue.add(jobClient);
    }

    public RdosTaskStatus getJobStatus(String jobId){
        return client.getJobStatus(jobId);
    }

    public JobResult stopJob(String jobId){
        return client.cancleJob(jobId);
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

        private IClient clusterClient;

        public JobSubmitProcessor(ClientType type, Properties clusterProp){
            clusterClient = ClientFactory.getClient(type);
            clusterClient.init(clusterProp);
        }

        @Override
        public Object call() throws Exception {
            while(runnable){
                JobClient jobClient = getNextJob();

                if(jobClient != null){
                    JobResult jobResult = null;
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
                        logger.error("get error, please check program!!!!", e);
                    }

                    //FIXME 之后需要对本地异常信息做存储
                    jobClient.setJobResult(jobResult);
                    JobClient.getQueue().offer(jobClient);//添加触发读取任务状态消息
                }
            }

            return null;
        }

        public boolean isRunnable() {
            return runnable;
        }

        public void setRunnable(boolean runnable) {
            this.runnable = runnable;
        }
    }

}
