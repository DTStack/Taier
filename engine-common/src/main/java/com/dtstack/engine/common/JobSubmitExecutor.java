package com.dtstack.engine.common;

import com.dtstack.engine.common.queue.GroupPriorityQueue;
import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.queue.ClusterQueueInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * 任务提交执行容器
 * 单独起线程执行
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @author xuchao
 */
public class JobSubmitExecutor implements Closeable{

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitExecutor.class);

    /***循环间隔时间3s*/
    private static final int WAIT_INTERVAL = 2 * 1000;

    /**用于taskListener处理*/
    private LinkedBlockingQueue<JobClient> queueForTaskListener = new LinkedBlockingQueue<>();

    private ClientCache clientCache = ClientCache.getInstance();

    private ClusterQueueInfo clusterQueueInfo = ClusterQueueInfo.getInstance();

    private static JobSubmitExecutor singleton = null;

    private String localAddress;

//    private ExecutorService jobSubmitPool = new ThreadPoolExecutor(3, 10, 60L,TimeUnit.SECONDS,
//                new ArrayBlockingQueue<Runnable>(1), new CustomThreadFactory("jobSubmitPool"),
//            //BlockCallerPolicy
//            (r,executor)->{
//                try {
//                    executor.getQueue().put(r);
//                } catch (InterruptedException e) {
//                    throw new RejectedExecutionException("Unexpected InterruptedException", e);
//                }});

    private JobSubmitExecutor(){
//        init();
    }

    public static JobSubmitExecutor getInstance(){
        if(singleton == null){
            synchronized (JobSubmitExecutor.class){
                if(singleton == null){
                    singleton = new JobSubmitExecutor();
                }
            }
        }
        return singleton;
    }

//    public void init(){
//        try{
//            clientCache.initLocalPlugin(ConfigParse.getEngineTypeList());
//            logger.warn("init JobSubmitExecutor success...");
//        }catch(Exception e){
//            throw new RuntimeException(e);
//        }
//    }

//    public List<String> containerInfos(JobClient jobClient) throws Exception {
//        IClient client = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
//        return client.getContainerInfos(JobIdentifier.createInstance(jobClient.getEngineTaskId(), null, jobClient.getTaskId()));
//    }

//    public LinkedBlockingQueue<JobClient> getQueueForTaskListener(){
//        return queueForTaskListener;
//    }

    public void addJobIntoTaskListenerQueue(JobClient jobClient){
        queueForTaskListener.offer(jobClient);
    }

//    public void startSubmitDealer(Map<String, GroupPriorityQueue> priorityQueueMap, String localAddress) {
//    }



    @Override
    public void close() throws IOException {

    }

}

