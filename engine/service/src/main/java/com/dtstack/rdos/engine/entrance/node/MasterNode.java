package com.dtstack.rdos.engine.entrance.node;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineJobCacheDao;
import com.dtstack.rdos.engine.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.entrance.enumeration.RequestStart;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.components.OrderLinkedBlockingQueue;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.EJobCacheStage;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.send.HttpSendClient;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 处理任务优先级队列
 * 1--n 数值越大表明优先级越高
 * TODO 区分flinK 优先级队列和spark优先级队列---根据配置文件的信息生成
 * Date: 2018/1/8
 * Company: www.dtstack.com
 * @author xuchao
 */

public class MasterNode {

    private static final Logger LOG = LoggerFactory.getLogger(MasterNode.class);

    private static final int DEFAULT_PRIORITY_VALUE = 1;

    /**用户填写的优先级占的比重*/
    private static final int PRIORITY_LEVEL_WEIGHT = 10;

    /**经过每轮的判断之后剩下的job优先级数值增量*/
    private static final int PRIORITY_ADD_VAL = 1;

    /***循环间隔时间2s*/
    private static final int WAIT_INTERVAL = 5 * 1000;

    /**任务分发到执行engine上最多重试3次*/
    private static final int DISPATCH_RETRY_LIMIT = 3;

    private static final String CUSTOMER_PRIORITY_VAL = "job.priority";

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosEngineJobCacheDao engineJobCacheDao = new RdosEngineJobCacheDao();

    private RdosEngineBatchJobDAO rdosEngineBatchJobDao = new RdosEngineBatchJobDAO();

    private RdosEngineStreamJobDAO rdosEngineStreamJobDao = new RdosEngineStreamJobDAO();

    /**key: 执行引擎的名称*/
    //TODO 需要引入group概念
    private Map<String, OrderLinkedBlockingQueue<JobClient>> priorityQueueMap = Maps.newHashMap();

    private Map<String, SendDealer> sendDealerMap = Maps.newHashMap();

    private String localAddress = ConfigParse.getLocalAddress();

    private ExecutorService senderExecutor;

    private static MasterNode singleton = new MasterNode();

    private boolean currIsMaster = false;

    public static MasterNode getInstance(){
        return singleton;
    }

    private MasterNode(){

        for(Map<String, Object> params : ConfigParse.getEngineTypeList()) {
            String clientTypeStr = (String) params.get(JobSubmitExecutor.TYPE_NAME_KEY);
            String key = EngineType.getEngineTypeWithoutVersion(clientTypeStr);
            priorityQueueMap.put(key, new OrderLinkedBlockingQueue<>());
        }

        senderExecutor = Executors.newFixedThreadPool(priorityQueueMap.size());
    }



    public void addTask(JobClient jobClient){

        //获取priority值
        Properties properties = jobClient.getConfProperties();
        String valStr = properties.getProperty(CUSTOMER_PRIORITY_VAL);
        int priorityVal = valStr == null ? DEFAULT_PRIORITY_VALUE : MathUtil.getIntegerVal(valStr);
        priorityVal = priorityVal * PRIORITY_LEVEL_WEIGHT;
        jobClient.setPriority(priorityVal);

        try{
            OrderLinkedBlockingQueue queue = priorityQueueMap.get(jobClient.getEngineType());
            if(queue == null){
                throw new RdosException("not support for engine type:" + jobClient.getEngineType());
            }

            queue.put(jobClient);
        }catch (Exception e){
            LOG.error("add to priority queue error:", e);
            dealSubmitFailJob(jobClient.getTaskId(), jobClient.getComputeType().getType(), e.toString());
            return;
        }

        saveCache(jobClient.getParamAction());
    }

    public void setIsMaster(boolean isMaster){
        if(isMaster && !currIsMaster){
            currIsMaster = true;
            if(senderExecutor.isShutdown()){
                senderExecutor = Executors.newFixedThreadPool(priorityQueueMap.size());
            }

            //TODO 需要从数据库load出来队列priorityQueue数据
            for(Map.Entry<String, OrderLinkedBlockingQueue<JobClient>> entry : priorityQueueMap.entrySet()){
                SendDealer sendDealer = new SendDealer(entry.getKey(), entry.getValue());
                sendDealerMap.put(entry.getKey(), sendDealer);
                senderExecutor.submit(sendDealer);
            }

            LOG.warn("---start master node deal thread------");
        }else if (!isMaster && currIsMaster){
            sendDealerMap.forEach((name, sendDealer) -> {
                sendDealer.stop();
            });

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
        if(!checkCanSend(address, jobClient.getEngineType())){
            excludeNodes.add(address);
            sendTask(jobClient, retryNum, excludeNodes);
        }

        //如果是目标节点就是当前节点--直接加入等待队列
        if(address.equals(localAddress)){

            try{
                jobClient.submitJob();
            }catch (Exception e){
                LOG.error("", e);
                //更新任务状态 && 删除任务cache
                dealSubmitFailJob(jobClient.getTaskId(), jobClient.getComputeType().getType(), e.toString());
            }

            return true;
        }

        ParamAction paramAction = jobClient.getParamAction();
        paramAction.setRequestStart(RequestStart.NODE.getStart());
        try {
            if(HttpSendClient.actionSubmit(address, paramAction)){
                return true;
            }else{
                //TODO 处理发送失败的情况(比如网络失败,或者slave主动返回失败)
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
        engineJobCacheDao.insertJob(paramAction.getTaskId(), paramAction.getEngineType(), paramAction.getComputeType(),
                EJobCacheStage.IN_PRIORITY_QUEUE.getStage(), paramAction.toString());
    }

    /**
     * 在发送失败之后向slave询问是否可以发送任务请求
     */
    public boolean checkCanSend(String address, String engineType){
        if(address.equals(localAddress)){
            JobSubmitExecutor.getInstance().checkCanAddToWaitQueue(engineType);
        }else{
            Map<String, Object> param = Maps.newHashMap();
            param.put("engineType", engineType);
            try{
                HttpSendClient.actionCheck(localAddress, param);
            }catch (Exception e){
                LOG.error("", e);
                return false;
            }
        }

        return true;
    }


    /**
     * master 节点分发任务失败
     * @param taskId
     */
    public void dealSubmitFailJob(String taskId, Integer computeType, String errorMsg){
        engineJobCacheDao.deleteJob(taskId);

        if(ComputeType.BATCH.typeEqual(computeType)){
            rdosEngineBatchJobDao.updateJobStatus(taskId, RdosTaskStatus.SUBMITFAILD.getStatus());
            rdosEngineBatchJobDao.updateSubmitLog(taskId, generateErrorMsg(errorMsg));

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
     * TODO 修改rdos_engine_job_cache表添加engine_type字段
     * 如果不修改当前的已经停止机器的任务恢复的话，需要修改rdos_engine_job_cache添加字段stage:用于标识任务是否已经下发。
     * @param engineType
     * @param queue
     */
    public void loadQueueFromDB(String engineType, OrderLinkedBlockingQueue<JobClient> queue){
        List<RdosEngineJobCache> jobCaches = engineJobCacheDao.getJobForPriorityQueue(EJobCacheStage.IN_PRIORITY_QUEUE.getStage(), engineType);
        jobCaches.forEach(jobCache ->{

            try{
                ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                JobClient jobClient = new JobClient(paramAction);
                queue.add(jobClient);
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

        private String name;

        private OrderLinkedBlockingQueue<JobClient> priorityQueue;

        private boolean isRun = true;

        public SendDealer(String name, OrderLinkedBlockingQueue<JobClient> priorityQueue){
            this.name = name;
            this.priorityQueue = priorityQueue;
        }

        @Override
        public void run() {
            LOG.info("-----{}:优先级队列触发开始执行----", name);

            loadQueueFromDB(name, priorityQueue);

            while (isRun){
                try{
                    for(JobClient jobClient : priorityQueue) {
                        //发送任务
                        List<String> excludeNodes = Lists.newArrayList();
                        if (sendTask(jobClient, 0, excludeNodes)) {
                            priorityQueue.remove(jobClient);
                        } else {
                            //更新剩余任务的优先级数据
                            updateQueuePriority();
                            break;
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

            LOG.info("-----{}:优先级队列停止执行----", name);
        }

        public void stop(){
            isRun = false;
        }

        /**
         * 每次判断过后对剩下的任务的priority值加上一个固定值
         */
        public void updateQueuePriority(){

            for(JobClient jobClient: priorityQueue){
                int currPriority = jobClient.getPriority();
                currPriority = currPriority + PRIORITY_ADD_VAL;
                jobClient.setPriority(currPriority);
            }

        }

    }

}
