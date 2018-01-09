package com.dtstack.rdos.engine.entrance.node;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineJobCacheDao;
import com.dtstack.rdos.engine.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.entrance.enumeration.RequestStart;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.components.OrderLinkedBlockingQueue;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.send.HttpSendClient;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final int PRIORITY_WEIGHT = 10;

    private static final int PRIORITY_ADD_VAL = 1;

    /***循环间隔时间2s*/
    private static final int WAIT_INTERVAL = 2 * 1000;

    /**任务分发到执行engine上如果出现异常的话最多重试3次*/
    private static final int DISPATCH_EXCP_LIMIT = 3;

    private static final int DISPATCH_EXCP_RETRY_INTERVAL = 2* 1000;

    private static String CUSTOMER_PRIORITY_VAL = "job.priority";

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosEngineJobCacheDao engineJobCacheDao = new RdosEngineJobCacheDao();

    private RdosEngineBatchJobDAO rdosEngineBatchJobDao = new RdosEngineBatchJobDAO();

    private RdosEngineStreamJobDAO rdosEngineStreamJobDao = new RdosEngineStreamJobDAO();

    /**key: 执行引擎的名称*/
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
        priorityVal = priorityVal * PRIORITY_WEIGHT;
        jobClient.setPriority(priorityVal);

        try{
            OrderLinkedBlockingQueue queue = priorityQueueMap.get(jobClient.getEngineType());
            if(queue == null){
                throw new RdosException("not support for engine type:" + jobClient.getEngineType());
            }

            queue.put(jobClient);
        }catch (Exception e){
            LOG.error("add to priority queue error:", e);
            dealSubmitFailJob(jobClient, e.toString());
            return;
        }

        saveCache(jobClient.getParamAction());
    }

    public void setIsMaster(boolean isMaster){
        if(isMaster && !currIsMaster){
            currIsMaster = true;
            //TODO 需要从数据库load出来队列priorityQueue数据
            for(Map.Entry<String, OrderLinkedBlockingQueue<JobClient>> entry : priorityQueueMap.entrySet()){
                SendDealer sendDealer = new SendDealer(entry.getKey(), entry.getValue());
                senderExecutor.submit(sendDealer);
            }

            LOG.warn("---start master node deal thread------");
        }else if (!isMaster && currIsMaster){

            senderExecutor.shutdownNow();
            LOG.warn("---stop master node deal thread------");
        }
    }

    /**
     * TODO 需要多看几次。。。逻辑比较绕
     * @param jobClient
     * @param sendUntilTrue
     * @param tryNum
     * @param exceptionNum
     * @return
     */
    public boolean sendTask(JobClient jobClient, boolean sendUntilTrue, int tryNum, int exceptionNum){
        String address = zkDistributed.getExecutionNode();
        //如果是目标节点就是当前节点
        if(address.equals(localAddress)){

            if(!JobSubmitExecutor.getInstance().checkCanAddToWaitQueue()){
                return false;
            }

            try{
                jobClient.submitJob();
            }catch (Exception e){
                LOG.error("", e);
                //更新任务状态 && 删除任务cache
                dealSubmitFailJob(jobClient, e.toString());
            }

            return true;
        }

        ParamAction paramAction = jobClient.getParamAction();
        paramAction.setRequestStart(RequestStart.NODE.getStart());
        try {
            if(HttpSendClient.actionSubmit(address, paramAction)){
                return true;
            }else if(sendUntilTrue){
                tryNum++;
                return sendTask(jobClient, sendUntilTrue, tryNum, exceptionNum);
            }else {
                return false;
            }

        } catch (Exception e) {
            LOG.error("", e);
            if(exceptionNum >= DISPATCH_EXCP_LIMIT){
                dealSubmitFailJob(jobClient, "engine master 下发任务异常");
                return true;
            }

            exceptionNum++;
            try {
                Thread.sleep(DISPATCH_EXCP_RETRY_INTERVAL);
            } catch (InterruptedException e1) {
                LOG.error("", e1);
                return false;
            }

            return sendTask(jobClient, sendUntilTrue, tryNum, exceptionNum);
        }
    }

    /**
     * 添加到优先级队列之后保存
     * cache的移除在任务发送完毕之后
     */
    public void saveCache(ParamAction paramAction){
        engineJobCacheDao.insertJob(paramAction.getTaskId(), paramAction.toString());
    }

    /**
     * 在发送失败之后向slave询问是否可以发送任务请求
     */
    public boolean checkCanSend(String address){
        return true;
    }

    /**
     * 每次判断过后对剩下的任务的priority值加上一个固定值
     */
    public void updateQueuePriority(){

        for(OrderLinkedBlockingQueue<JobClient> queue : priorityQueueMap.values()){
            queue.forEach(jobClient -> {
                int currPriority = jobClient.getPriority();
                currPriority = currPriority + PRIORITY_ADD_VAL;
                jobClient.setPriority(currPriority);
            });
        }

    }


    /**
     * master 节点分发任务失败
     * @param jobClient
     */
    public void dealSubmitFailJob(JobClient jobClient, String errorMsg){
        engineJobCacheDao.deleteJob(jobClient.getTaskId());

        if(ComputeType.BATCH.equals(jobClient.getComputeType())){
            rdosEngineBatchJobDao.updateJobStatus(jobClient.getTaskId(), RdosTaskStatus.SUBMITFAILD.getStatus());
            rdosEngineBatchJobDao.updateSubmitLog(jobClient.getTaskId(), generateErrorMsg(errorMsg));
        }else if(ComputeType.STREAM.equals(jobClient.getComputeType())){
            rdosEngineStreamJobDao.updateTaskStatus(jobClient.getTaskId(), RdosTaskStatus.SUBMITFAILD.getStatus());
            rdosEngineStreamJobDao.updateSubmitLog(jobClient.getTaskId(), generateErrorMsg(errorMsg));
        }else{
            LOG.error("not support compute type:" + jobClient.getComputeType());
        }
    }

    public String generateErrorMsg(String msgInfo){
        return String.format("{\"msg_info\":\"%s\"}", msgInfo);
    }

    public void loadQueueFromDB(){
        for(OrderLinkedBlockingQueue queue : priorityQueueMap.values()){
            queue.clear();
        }
        //TODO 根据参数的优先级和时间算出job的优先级
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
            while (isRun){
                for(JobClient jobClient : priorityQueue){
                    try{
                        //发送任务
                        if(sendTask(jobClient, true, 0, 0)){
                            priorityQueue.remove(jobClient);
                        }else{
                            dealSubmitFailJob(jobClient, "发送任务给执行节点失败.");
                        }

                        //更新剩余任务的优先级数据
                        updateQueuePriority();
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
        }

        public void stop(){
            isRun = false;
        }

    }

}
