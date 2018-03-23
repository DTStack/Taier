package com.dtstack.rdos.engine.entrance.node;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.entrance.enums.RequestStart;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.components.OrderLinkedBlockingQueue;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.EJobCacheStage;
import com.dtstack.rdos.engine.execution.base.enums.EngineType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.execution.queue.ExeQueueMgr;
import com.dtstack.rdos.engine.send.HttpSendClient;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class MasterNode {

    private static final Logger LOG = LoggerFactory.getLogger(MasterNode.class);

    /**经过每轮的判断之后剩下的job优先级数值增量*/
    private static final int PRIORITY_ADD_VAL = 1;

    /***循环间隔时间2s*/
    private static final int WAIT_INTERVAL = 5 * 1000;

    /**任务分发到执行engine上最多重试3次*/
    private static final int DISPATCH_RETRY_LIMIT = 3;

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosEngineJobCacheDAO engineJobCacheDao = new RdosEngineJobCacheDAO();

    private RdosEngineBatchJobDAO rdosEngineBatchJobDao = new RdosEngineBatchJobDAO();

    private RdosEngineStreamJobDAO rdosEngineStreamJobDao = new RdosEngineStreamJobDAO();

    /**key: 执行引擎的名称*/
    private Map<String, GroupPriorityQueue> priorityQueueMap = Maps.newConcurrentMap();

    private SendDealer sendDealer;

    private String localAddress = ConfigParse.getLocalAddress();

    private JobStopQueue jobStopQueue;

    private ExecutorService senderExecutor;

    private static MasterNode singleton = new MasterNode();

    private boolean currIsMaster = false;

    public static MasterNode getInstance(){
        return singleton;
    }

    private MasterNode(){

        for(Map<String, Object> params : ConfigParse.getEngineTypeList()) {
            String clientTypeStr = (String) params.get(ConfigParse.TYPE_NAME_KEY);
            String key = EngineType.getEngineTypeWithoutVersion(clientTypeStr);
            priorityQueueMap.put(key, new GroupPriorityQueue());
        }

        senderExecutor = Executors.newSingleThreadExecutor();
        jobStopQueue = new JobStopQueue(this);
        jobStopQueue.start();
    }

    public void addStartJob(JobClient jobClient){

        try{
            GroupPriorityQueue groupQueue = priorityQueueMap.get(jobClient.getEngineType());
            if(groupQueue == null){
                groupQueue = new GroupPriorityQueue();
                priorityQueueMap.put(jobClient.getEngineType(), groupQueue);
            }

            groupQueue.add(jobClient);
        }catch (Exception e){
            LOG.error("add to priority queue error:", e);
            dealSubmitFailJob(jobClient.getTaskId(), jobClient.getComputeType().getType(), e.toString());
            return;
        }

        saveCache(jobClient.getParamAction());
    }

    public void addStopJob(ParamAction paramAction){
        jobStopQueue.addJob(paramAction);
    }

    /**
     * TODO 需要和send Task线程做同步
     * @param engineType
     * @param groupName
     * @param jobId
     * @return
     */
    public boolean stopTaskIfExists(String engineType, String groupName, String jobId, Integer computeType){
        GroupPriorityQueue groupPriorityQueue = priorityQueueMap.get(engineType);
        if(groupPriorityQueue == null){
            throw new RdosException("not support engine type:" + engineType);
        }

        boolean result = groupPriorityQueue.remove(groupName, jobId);
        if(result){
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

    public void setIsMaster(boolean isMaster){
        if(isMaster && !currIsMaster){
            currIsMaster = true;
            if(senderExecutor.isShutdown()){
                senderExecutor = Executors.newFixedThreadPool(priorityQueueMap.size());
            }

            sendDealer = new SendDealer(priorityQueueMap);
            senderExecutor.submit(sendDealer);
            LOG.warn("---start master node deal thread------");
        }else if (!isMaster && currIsMaster){
            currIsMaster = false;

            if(sendDealer != null){
                sendDealer.stop();
            }

            senderExecutor.shutdownNow();
            LOG.warn("---stop master node deal thread------");
        }
    }

    /**
     * TODO 需要多看几次。。。逻辑比较绕
     * @param jobClient
     * @param retryNum
     * @param excludeNodes
     * @return
     */
    public boolean sendTask(JobClient jobClient, int retryNum, List<String> excludeNodes){

        String address = zkDistributed.getExecutionNode(excludeNodes);
        if(Strings.isNullOrEmpty(address)){
            return false;
        }

        //不做严格的队列长度限制,只要请求的时候返回true就认为可以发送
        if(!checkCanSend(address, jobClient.getEngineType(), jobClient.getGroupName())){
            excludeNodes.add(address);
            return sendTask(jobClient, retryNum, excludeNodes);
        }


        try {
            ParamAction paramAction = jobClient.getParamAction();
            paramAction.setRequestStart(RequestStart.NODE.getStart());
            if(HttpSendClient.actionSubmit(address, paramAction)){
                return true;
            }else{
                //处理发送失败的情况(比如网络失败,或者slave主动返回失败)
                if(retryNum >= DISPATCH_RETRY_LIMIT){
                    return false;
                }

                retryNum++;
                return sendTask(jobClient, retryNum, excludeNodes);
            }

        } catch (Exception e) {
            //只有json 解析的异常才会抛出到这个地方,这应该是不可能发生的
            LOG.error("---not impossible,please check your program ----", e);
            dealSubmitFailJob(jobClient.getTaskId(), jobClient.getComputeType().getType(), "engine master 下发任务异常");
            return true;
        }
    }

    /**
     * 添加到优先级队列之后保存
     * cache的移除在任务发送完毕之后
     */
    public void saveCache(ParamAction paramAction){
        if(engineJobCacheDao.getJobById(paramAction.getTaskId()) != null){
            engineJobCacheDao.updateJobStage(paramAction.getTaskId(), EJobCacheStage.IN_PRIORITY_QUEUE.getStage());
        }else{
            engineJobCacheDao.insertJob(paramAction.getTaskId(), paramAction.getEngineType(), paramAction.getComputeType(),
                    EJobCacheStage.IN_PRIORITY_QUEUE.getStage(), paramAction.toString());
        }

    }

    /**
     * 在发送失败之后向slave询问是否可以发送任务请求
     */
    public boolean checkCanSend(String address, String engineType, String groupName){
        if(address.equals(localAddress)){
            return ExeQueueMgr.getInstance().checkCanAddToWaitQueue(engineType, groupName);
        }else{
            Map<String, Object> param = Maps.newHashMap();
            param.put("engineType", engineType);
            param.put("groupName", groupName);
            try{
                return HttpSendClient.actionCheck(localAddress, param);
            }catch (Exception e){
                LOG.error("", e);
                return false;
            }
        }
    }


    /**
     * master 节点分发任务失败
     * @param taskId
     */
    public void dealSubmitFailJob(String taskId, Integer computeType, String errorMsg){
        engineJobCacheDao.deleteJob(taskId);

        if(ComputeType.BATCH.typeEqual(computeType)){
            rdosEngineBatchJobDao.submitFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), generateErrorMsg(errorMsg));

        }else if(ComputeType.STREAM.typeEqual(computeType)){
            rdosEngineStreamJobDao.updateTaskStatus(taskId, RdosTaskStatus.SUBMITFAILD.getStatus());
            rdosEngineStreamJobDao.updateSubmitLog(taskId, generateErrorMsg(errorMsg));

        }else{
            LOG.error("not support compute type:" + computeType);
        }
    }

    public String generateErrorMsg(String msgInfo){
        return String.format("{\"msg_info\":\"%s\"}", msgInfo);
    }

    /**
     * 转变为master之后
     */
    public void loadQueueFromDB(){
        List<RdosEngineJobCache> jobCaches = engineJobCacheDao.getJobForPriorityQueue(EJobCacheStage.IN_PRIORITY_QUEUE.getStage());
        if(CollectionUtils.isEmpty(jobCaches)){
            return;
        }

        jobCaches.forEach(jobCache ->{

            try{
                ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                JobClient jobClient = new JobClient(paramAction);
                //更新任务状态为engineAccepted
                rdosEngineBatchJobDao.updateJobStatus(jobCache.getJobId(), RdosTaskStatus.ENGINEACCEPTED.getStatus());
                MasterNode.getInstance().addStartJob(jobClient);
            }catch (Exception e){
                //数据转换异常--打日志
                LOG.error("", e);
                dealSubmitFailJob(jobCache.getJobId(), jobCache.getComputeType(), "该任务存储信息异常,无法转换." + e.toString());
            }

        });
    }

    /**
     * FIXME 暂时不添加也是可以的，使用老的处理逻辑
     * 注意对已经提交完成的任务的处理(这些任务需要获取任务状态和任务日志)
     */
    public void getKilledSlaveNodeJob(){

    }

    class SendDealer implements Runnable{

        private Map<String, GroupPriorityQueue> groupPriorityQueueMap;

        private boolean isRun = true;

        public SendDealer(Map<String, GroupPriorityQueue> groupPriorityQueueMap){
            this.groupPriorityQueueMap = groupPriorityQueueMap;
        }

        @Override
        public void run() {
            LOG.info("-----{}:优先级队列发送任务线程触发开始执行----");

            try{
                loadQueueFromDB();
            }catch (Exception e){
                LOG.error("----load data from DB error:", e);
            }

            while (isRun){
                try{
                    for(GroupPriorityQueue priorityQueue : groupPriorityQueueMap.values()){
                        for(OrderLinkedBlockingQueue queue : priorityQueue.getOrderList()) {
                            sendJobClient(queue);
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

            LOG.info("-----{}:优先级队列发送线程停止执行----");
        }

        public void stop(){
            isRun = false;
        }

        /**
         * 循环group优先级队列发送任务--直到不能发送
         * @param priorityQueue
         */
        public void sendJobClient(OrderLinkedBlockingQueue<JobClient> priorityQueue){

            for(JobClient jobClient : priorityQueue){
                List<String> excludeNodes = Lists.newArrayList();
                if (sendTask(jobClient, 0, excludeNodes)) {
                    priorityQueue.remove(jobClient);
                } else {
                    break;
                }
            }

            //更新剩余任务的优先级数据
            updateQueuePriority(priorityQueue);

        }

        /**
         * 每次判断过后对剩下的任务的priority值加上一个固定值
         */
        public void updateQueuePriority(OrderLinkedBlockingQueue<JobClient> priorityQueue){

            for(JobClient jobClient: priorityQueue){
                int currPriority = jobClient.getPriority();
                currPriority = currPriority + PRIORITY_ADD_VAL;
                jobClient.setPriority(currPriority);
            }

        }

    }

}
