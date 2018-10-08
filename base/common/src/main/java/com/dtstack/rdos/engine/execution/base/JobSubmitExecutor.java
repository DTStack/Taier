package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.queue.ClusterQueueInfo;
import com.dtstack.rdos.engine.execution.base.queue.GroupInfo;
import com.dtstack.rdos.engine.execution.base.queue.GroupPriorityQueue;
import com.dtstack.rdos.engine.execution.base.queue.OrderLinkedBlockingQueue;
import com.dtstack.rdos.engine.execution.base.restart.RestartStrategyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
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
public class JobSubmitExecutor {

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitExecutor.class);

    /**经过每轮的判断之后剩下的job优先级数值增量*/
    private static final int PRIORITY_ADD_VAL = 1;

    /***循环间隔时间3s*/
    private static final int WAIT_INTERVAL = 3 * 1000;

    /**用于taskListener处理*/
    private LinkedBlockingQueue<JobClient> queueForTaskListener = new LinkedBlockingQueue<>();

    private ClientCache clientCache = ClientCache.getInstance();

    private ClusterQueueInfo clusterQueueInfo = ClusterQueueInfo.getInstance();

    private static JobSubmitExecutor singleton = null;

    private String localAddress;

    private ExecutorService jobSubmitPool = new ThreadPoolExecutor(3, 10, 60L,TimeUnit.SECONDS,
                new SynchronousQueue<>(true), new CustomThreadFactory("jobSubmitPool"));

    private JobSubmitExecutor(){
        init();
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

    public void init(){
        try{
            clientCache.initLocalPlugin(ConfigParse.getEngineTypeList());
            RestartStrategyUtil.getInstance();
            logger.warn("init JobSubmitExecutor success...");
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        if(jobClient.getEngineTaskId() == null){
            return JobResult.createSuccessResult(jobClient.getTaskId());
        }

        IClient client = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return client.cancelJob(jobClient.getEngineTaskId());
    }

    public LinkedBlockingQueue<JobClient> getQueueForTaskListener(){
        return queueForTaskListener;
    }

    public void addJobIntoTaskListenerQueue(JobClient jobClient){
        queueForTaskListener.offer(jobClient);
    }

    public void startSubmitDealer(Map<String, GroupPriorityQueue> priorityQueueMap, String localAddress) {
        this.localAddress = localAddress;
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("submitDealer"));
        scheduledService.scheduleWithFixedDelay(
                new SubmitDealer(priorityQueueMap),
                0,
                WAIT_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    private class SubmitDealer implements Runnable{

        private Map<String, GroupPriorityQueue> groupPriorityQueueMap;

        public SubmitDealer(Map<String, GroupPriorityQueue> groupPriorityQueueMap){
            this.groupPriorityQueueMap = groupPriorityQueueMap;
        }

        @Override
        public void run() {
            try{
                for(Map.Entry<String,GroupPriorityQueue> priorityQueueEntry : groupPriorityQueueMap.entrySet()){
                    for(Map.Entry<String,OrderLinkedBlockingQueue<JobClient>> queueEntry : priorityQueueEntry.getValue().getGroupPriorityQueueMap().entrySet()) {
                        String engineType = priorityQueueEntry.getKey();
                        String groupName = queueEntry.getKey();
                        OrderLinkedBlockingQueue<JobClient> queue = queueEntry.getValue();
                        try {
                            submitJobClient(engineType,groupName,queue);
                        } catch (Exception e2){
                            logger.error("", e2);
                        }
                    }
                }
            }catch (Exception e){
                logger.error("", e);
            }
        }

        /**
         * 循环group优先级队列，提交任务到生产消费者队列
         */
        private void submitJobClient(String engineType, String groupName, OrderLinkedBlockingQueue<JobClient> priorityQueue){
            Iterator<JobClient> it = priorityQueue.iterator();
            while (it.hasNext()){
                JobClient jobClient = it.next();
                boolean isRestartJobCanSubmit = System.currentTimeMillis() > jobClient.getRestartTime();
                //重试任务时间未满足条件，出队后进行优先级计算完后重新入队
                if (!isRestartJobCanSubmit){
                    jobClient.setPriority(jobClient.getPriority()-1);
                    continue;
                }
                if (!checkLocalPriorityIsMax(engineType,groupName,jobClient.getPriority())){
                    break;
                }
                //提交任务
                if (submitJob(jobClient,priorityQueue)) {
                    it.remove();
                }
                //更新剩余任务的优先级数据
                updateQueuePriority(priorityQueue);
                break;
            }
        }

        private boolean checkLocalPriorityIsMax(String engineType, String groupName, int localPriority) {
            if(clusterQueueInfo.isEmpty()){
                //等待第一次从zk上获取信息
                return false;
            }
            ClusterQueueInfo.EngineTypeQueueInfo zkInfo = clusterQueueInfo.getEngineTypeQueueInfo(engineType);
            if(zkInfo == null){
                return true;
            }
            boolean result = true;
            for (Map.Entry<String, ClusterQueueInfo.GroupQueueInfo> zkInfoEntry : zkInfo.getGroupQueueInfoMap().entrySet()) {
                String address = zkInfoEntry.getKey();
                if (localAddress.equals(address)) {
                    continue;
                }
                ClusterQueueInfo.GroupQueueInfo groupQueueZkInfo = zkInfoEntry.getValue();
                Map<String, GroupInfo> remoteQueueInfo = groupQueueZkInfo.getGroupInfo();
                GroupInfo groupInfo = remoteQueueInfo.getOrDefault(groupName, new GroupInfo());
                if (groupInfo.getPriority() > localPriority) {
                    result = false;
                    break;
                }
            }
            return result;
        }

        private boolean submitJob(JobClient jobClient,OrderLinkedBlockingQueue<JobClient> priorityQueue){
            try {
                jobSubmitPool.submit(new JobSubmitProcessor(jobClient, ()-> handlerNoResource(jobClient,priorityQueue)));
                return true;
            } catch (RejectedExecutionException e){
                logger.error("", e);
                return false;
            }
        }

        private void handlerNoResource(JobClient jobClient, OrderLinkedBlockingQueue<JobClient> priorityQueue){
            try {
                priorityQueue.put(jobClient);
            } catch (InterruptedException e){
                logger.error("add jobClient: " + jobClient.getTaskId() +" back to queue error:", e);
            }
        }

        /**
         * 每次判断过后对剩下的任务的priority值加上一个固定值
         */
        private void updateQueuePriority(OrderLinkedBlockingQueue<JobClient> priorityQueue){
            for(JobClient jobClient: priorityQueue){
                int currPriority = jobClient.getPriority();
                currPriority = currPriority + PRIORITY_ADD_VAL;
                jobClient.setPriority(currPriority);
            }
        }

    }
}

