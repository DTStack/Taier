package com.dtstack.rdos.engine.service.node;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobClientCallBack;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.EJobCacheStage;
import com.dtstack.rdos.engine.execution.base.enums.EPluginType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.execution.base.queue.OrderLinkedBlockingQueue;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosPluginInfoDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosPluginInfo;
import com.dtstack.rdos.engine.service.enums.RequestStart;
import com.dtstack.rdos.engine.service.send.HttpSendClient;
import com.dtstack.rdos.engine.service.util.TaskIdUtil;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 处理任务优先级队列
 * 1--n 数值越大表明优先级越高
 * 任务停止队列
 * Date: 2018/1/8
 * Company: www.dtstack.com
 * @author xuchao
 */

public class WorkNode {

    private static final Logger LOG = LoggerFactory.getLogger(WorkNode.class);

    /**经过每轮的判断之后剩下的job优先级数值增量*/
    private static final int PRIORITY_ADD_VAL = 1;

    /***循环间隔时间3s*/
    private static final int WAIT_INTERVAL = 3 * 1000;

    /**任务分发到执行engine上最多重试3次*/
    private static final int DISPATCH_RETRY_LIMIT = 3;

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosEngineJobCacheDAO engineJobCacheDao = new RdosEngineJobCacheDAO();

    private RdosEngineBatchJobDAO rdosEngineBatchJobDao = new RdosEngineBatchJobDAO();

    private RdosEngineStreamJobDAO rdosEngineStreamJobDao = new RdosEngineStreamJobDAO();

    private RdosPluginInfoDAO pluginInfoDao = new RdosPluginInfoDAO();

    /**key: 执行引擎的名称*/
    private Map<String, GroupPriorityQueue> priorityQueueMap = Maps.newConcurrentMap();

    private JobStopQueue jobStopQueue;

    private static WorkNode singleton = new WorkNode();

    public static WorkNode getInstance(){
        return singleton;
    }

    private WorkNode(){
        ExecutorService senderExecutor = Executors.newSingleThreadExecutor();
        SubmitDealer submitDealer = new SubmitDealer(priorityQueueMap);
        senderExecutor.submit(submitDealer);

        jobStopQueue = new JobStopQueue(this);
        jobStopQueue.start();
    }

    public void addStartJob(JobClient jobClient){
        boolean distribute = distributeTask(jobClient,0,Lists.newArrayList());
        if (!distribute){
            dealSubmitFailJob(jobClient.getTaskId(), jobClient.getComputeType().getType(), "engine master 下发任务异常");
        }
    }

    public void addSubmitJob(JobClient jobClient) {
        Integer computeType = jobClient.getComputeType().getType();
        if(jobClient.getPluginInfo() != null){
            updateJobClientPluginInfo(jobClient.getTaskId(), computeType, jobClient.getPluginInfo());
        }
        String zkTaskId = TaskIdUtil.getZkTaskId(computeType, jobClient.getEngineType(), jobClient.getTaskId());
        jobClient.setJobClientCallBack(new JobClientCallBack() {
            @Override
            public void execute(Map<String, ? extends Object> params) {
                if(!params.containsKey(JOB_STATUS)){
                    return;
                }
                int jobStatus = MathUtil.getIntegerVal(params.get(JOB_STATUS));
                zkDistributed.updateJobZKStatus(zkTaskId, jobStatus);
                updateJobStatus(jobClient.getTaskId(), computeType, jobStatus);
            }
        });

        saveCache(jobClient.getTaskId(), jobClient.getEngineType(), computeType, EJobCacheStage.IN_PRIORITY_QUEUE.getStage(), jobClient.getParamAction().toString());
        zkDistributed.updateJobZKStatus(zkTaskId,RdosTaskStatus.WAITENGINE.getStatus());
        updateJobStatus(jobClient.getTaskId(), computeType, RdosTaskStatus.WAITENGINE.getStatus());

        //加入节点的优先级队列
        this.redirectSubmitJob(jobClient);
    }

    public void redirectSubmitJob(JobClient jobClient){
        try{
            GroupPriorityQueue groupQueue = priorityQueueMap.computeIfAbsent(jobClient.getEngineType(), k->new GroupPriorityQueue());
            groupQueue.add(jobClient);
        }catch (Exception e){
            LOG.error("add to priority queue error:", e);
            dealSubmitFailJob(jobClient.getTaskId(), jobClient.getComputeType().getType(), e.toString());
        }
    }

    public void addStopJob(ParamAction paramAction){
        jobStopQueue.addJob(paramAction);
    }

    private void updateJobStatus(String jobId, Integer computeType, Integer status) {
        if (ComputeType.STREAM.getType().equals(computeType)) {
            rdosEngineStreamJobDao.updateTaskStatus(jobId, status);
        } else {
            rdosEngineBatchJobDao.updateJobStatus(jobId, status);
        }
    }

    /**
     * 1. 接受到任务的时候需要将数据缓存
     * 2. 添加到优先级队列之后保存
     * 3. cache的移除在任务发送完毕之后
     */
    private void saveCache(String jobId, String engineType, Integer computeType, int stage, String jobInfo){
        if(engineJobCacheDao.getJobById(jobId) != null){
            engineJobCacheDao.updateJobStage(jobId, stage);
        }else{
            engineJobCacheDao.insertJob(jobId, engineType, computeType, stage, jobInfo);
        }
    }

    private void updateJobClientPluginInfo(String jobId, Integer computeType, String pluginInfoStr){
        Long refPluginInfoId = -1L;

        //请求不带插件的连接信息的话则默认为使用本地默认的集群配置---pluginInfoId = -1;
        if(!Strings.isNullOrEmpty(pluginInfoStr)){
            RdosPluginInfo pluginInfo = pluginInfoDao.getByPluginInfo(pluginInfoStr);
            if(pluginInfo == null){
                refPluginInfoId = pluginInfoDao.replaceInto(pluginInfoStr, EPluginType.DYNAMIC.getType());
            }else{
                refPluginInfoId = pluginInfo.getId();
            }
        }
        //更新任务ref的pluginInfo
        if(ComputeType.STREAM.getType().equals(computeType)){
            rdosEngineStreamJobDao.updateTaskPluginId(jobId, refPluginInfoId);
        } else{
            rdosEngineBatchJobDao.updateJobPluginId(jobId, refPluginInfoId);
        }

    }

    public boolean stopTaskIfExists(String engineType, String groupName, String jobId, Integer computeType){
        GroupPriorityQueue groupPriorityQueue = priorityQueueMap.get(engineType);
        if(groupPriorityQueue == null){
            throw new RdosException("not support engine type:" + engineType);
        }

        boolean result = groupPriorityQueue.remove(groupName, jobId);
        if(result){
            zkDistributed.updateJobZKStatus(jobId, RdosTaskStatus.CANCELED.getStatus());
            engineJobCacheDao.deleteJob(jobId);
            //修改任务状态
            if(ComputeType.BATCH.getType().equals(computeType)){
                rdosEngineBatchJobDao.updateJobStatus(jobId, RdosTaskStatus.CANCELED.getStatus());
            }else if(ComputeType.STREAM.getType().equals(computeType)){
                rdosEngineStreamJobDao.updateTaskStatus(jobId, RdosTaskStatus.CANCELED.getStatus());
            }
        }

        return result;
    }

    /**
     * 判断任务队列长度，分发任务到其他work节点
     */
    private boolean distributeTask(JobClient jobClient, int retryNum, List<String> excludeNodes){

        String address = zkDistributed.getDistributeNode(excludeNodes);
        if(Strings.isNullOrEmpty(address)){
            return false;
        }
        if (address.equals(zkDistributed.getLocalAddress())){
            this.addSubmitJob(jobClient);
            return true;
        }
        try {
            ParamAction paramAction = jobClient.getParamAction();
            paramAction.setRequestStart(RequestStart.NODE.getStart());
            if(HttpSendClient.actionSubmit(address, paramAction)){
                return true;
            }else{
                //处理发送失败的情况(比如网络失败,或者slave主动返回失败)
                if(retryNum >= DISPATCH_RETRY_LIMIT){
                    LOG.error("任务 taskId:{} 网络失败超过3次，DISPATCH_RETRY_LIMIT >= 3 ",paramAction.getTaskId());
                    return false;
                }

                retryNum++;
                return distributeTask(jobClient, retryNum, excludeNodes);
            }

        } catch (Exception e) {
            //只有json 解析的异常才会抛出到这个地方,这应该是不可能发生的
            LOG.error("任务 taskId:{} ---not impossible,please check your program ----,{}", jobClient.getTaskId(), e);
            return false;
        }
    }

    /**
     * master 节点分发任务失败
     * @param taskId
     */
    private void dealSubmitFailJob(String taskId, Integer computeType, String errorMsg){
        engineJobCacheDao.deleteJob(taskId);

        if(ComputeType.BATCH.typeEqual(computeType)){
            rdosEngineBatchJobDao.submitFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), generateErrorMsg(errorMsg));
        }else if(ComputeType.STREAM.typeEqual(computeType)){
            rdosEngineStreamJobDao.submitFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), generateErrorMsg(errorMsg));
        }else{
            LOG.error("not support compute type:" + computeType);
        }
    }

    private String generateErrorMsg(String msgInfo){
        return String.format("{\"msg_info\":\"%s\"}", msgInfo);
    }


    class SubmitDealer implements Runnable{

        private Map<String, GroupPriorityQueue> groupPriorityQueueMap;

        public SubmitDealer(Map<String, GroupPriorityQueue> groupPriorityQueueMap){
            this.groupPriorityQueueMap = groupPriorityQueueMap;
        }

        @Override
        public void run() {
            LOG.info("-----{}:优先级队列发送任务线程触发开始执行----");

            while (true){
                try{
                    for(GroupPriorityQueue priorityQueue : groupPriorityQueueMap.values()){
                        for(OrderLinkedBlockingQueue queue : priorityQueue.getOrderList()) {
                            submitJobClient(queue);
                        }
                    }

                }catch (Exception e){
                    LOG.error("", e);
                }finally {
                    try {
                        Thread.sleep(WAIT_INTERVAL);
                    } catch (InterruptedException e) {
                        LOG.error("", e);
                    }
                }
            }
        }

        /**
         * 循环group优先级队列，提交任务到生产消费者队列
         * @param priorityQueue
         */
        private void submitJobClient(OrderLinkedBlockingQueue<JobClient> priorityQueue){

            Iterator<JobClient> it = priorityQueue.iterator();
            while (it.hasNext()){
                JobClient jobClient = it.next();
                boolean isRestartJobCanSubmit = System.currentTimeMillis() > jobClient.getRestartTime();
                //重试任务时间未满足条件，出队后进行优先级计算完后重新入队
                if (!isRestartJobCanSubmit){
                    it.remove();
                    updateQueuePriority(priorityQueue);
                    redirectSubmitJob(jobClient);
                    break;
                }
                //提交到生产者消费者队列
                if (jobClient.submitJob()) {
                    priorityQueue.remove(jobClient);
                } else {
                    //更新剩余任务的优先级数据
                    updateQueuePriority(priorityQueue);
                    break;
                }
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
