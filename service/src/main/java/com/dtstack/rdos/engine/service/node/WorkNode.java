package com.dtstack.rdos.engine.service.node;

import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.EJobCacheStage;
import com.dtstack.rdos.engine.execution.base.enums.EPluginType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.execution.base.queue.GroupInfo;
import com.dtstack.rdos.engine.execution.base.queue.GroupPriorityQueue;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosPluginInfoDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.service.db.dataobject.RdosPluginInfo;
import com.dtstack.rdos.engine.service.enums.RequestStart;
import com.dtstack.rdos.engine.service.send.HttpSendClient;
import com.dtstack.rdos.engine.service.util.TaskIdUtil;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.dtstack.rdos.engine.service.zk.cache.ZkLocalCache;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    /**任务分发到执行engine上最多重试3次*/
    private static final int DISPATCH_RETRY_LIMIT = 3;

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();

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
        ExecutorService recoverExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("recoverDealer"));
        recoverExecutor.submit(new RecoverDealer());

        jobStopQueue = new JobStopQueue(this);
        jobStopQueue.start();

        zkLocalCache.setWorkNode(this);
        JobSubmitExecutor.getInstance().startSubmitDealer(priorityQueueMap, zkDistributed.getLocalAddress());
    }

    public GroupPriorityQueue getEngineTypeQueue(String engineType) {
        return priorityQueueMap.computeIfAbsent(engineType, k -> new GroupPriorityQueue(engineType,
                (groupPriorityQueue, startId) -> {
                    return this.emitJob2GQ(engineType, groupPriorityQueue, startId);
                })
        );
    }

    /**
     * 获取当前节点的队列大小信息
     */
    public Map<String, Map<String, GroupInfo>> getEngineTypeQueueInfo(){
        Map<String, Map<String, GroupInfo>> engineTypeQueueSizeInfo = Maps.newHashMap();
        priorityQueueMap.forEach((engineType, queue) -> engineTypeQueueSizeInfo.computeIfAbsent(engineType, k->{
            Map<String,GroupInfo> groupInfos = Maps.newHashMap();
            queue.getGroupPriorityQueueMap().forEach((group, groupQueue)->groupInfos.computeIfAbsent(group,sk-> {
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setSize(groupQueue.size());
                JobClient topJob = groupQueue.getTop();
                groupInfo.setPriority(topJob==null ? 0 : topJob.getPriority());
                return groupInfo;
            }));
            return groupInfos;
        }));
        return engineTypeQueueSizeInfo;
    }

    /**
     * 任务分发
     */
    public void addStartJob(JobClient jobClient){
        Map<String,Object> distributeResult = distributeTask(jobClient,0,Lists.newArrayList());
        if (!MapUtils.getBooleanValue(distributeResult,"result")){
            String errorInfo = String.format("engine master 下发任务异常:\n %s",MapUtils.getString(distributeResult,"errorInfo"));
            dealSubmitFailJob(jobClient.getTaskId(), jobClient.getComputeType().getType(), errorInfo);
        }
    }

    /**
     * 提交优先级队列->最终提交到具体执行组件
     */
    public void addSubmitJob(JobClient jobClient) {
        Integer computeType = jobClient.getComputeType().getType();
        if(jobClient.getPluginInfo() != null){
            updateJobClientPluginInfo(jobClient.getTaskId(), computeType, jobClient.getPluginInfo());
        }
        String zkTaskId = TaskIdUtil.getZkTaskId(computeType, jobClient.getEngineType(), jobClient.getTaskId());
        jobClient.setCallBack((jobStatus)-> {
            zkLocalCache.updateLocalMemTaskStatus(zkTaskId, jobStatus);
            updateJobStatus(jobClient.getTaskId(), computeType, jobStatus);
        });

        saveCache(jobClient.getTaskId(), jobClient.getEngineType(), computeType, EJobCacheStage.IN_PRIORITY_QUEUE.getStage(), jobClient.getParamAction().toString(), jobClient.getJobName());
        updateJobStatus(jobClient.getTaskId(), computeType, RdosTaskStatus.WAITENGINE.getStatus());

        //加入节点的优先级队列
        this.redirectSubmitJob(jobClient);
    }

    /**
     * 容灾时对已经提交到执行组件的任务，进行恢复
     */
    public void afterSubmitJob(JobClient jobClient) {
        Integer computeType = jobClient.getComputeType().getType();
        if(jobClient.getPluginInfo() != null){
            updateJobClientPluginInfo(jobClient.getTaskId(), computeType, jobClient.getPluginInfo());
        }
        String zkTaskId = TaskIdUtil.getZkTaskId(computeType, jobClient.getEngineType(), jobClient.getTaskId());
        saveCache(jobClient.getTaskId(), jobClient.getEngineType(), computeType, EJobCacheStage.IN_SUBMIT_QUEUE.getStage(), jobClient.getParamAction().toString(), jobClient.getJobName());
        //检查分片
        zkLocalCache.checkShard();
        zkLocalCache.updateLocalMemTaskStatus(zkTaskId,RdosTaskStatus.SUBMITTED.getStatus());
    }

    public void redirectSubmitJob(JobClient jobClient){
        try{
            GroupPriorityQueue groupQueue = priorityQueueMap.computeIfAbsent(jobClient.getEngineType(), k -> new GroupPriorityQueue(jobClient.getEngineType(),
                    (groupPriorityQueue, startId) -> {
                        return this.emitJob2GQ(jobClient.getEngineType(), groupPriorityQueue, startId);
                    })
            );
            groupQueue.add(jobClient);
        }catch (Exception e){
            LOG.error("add to priority queue error:", e);
            dealSubmitFailJob(jobClient.getTaskId(), jobClient.getComputeType().getType(), e.toString());
        }
    }

    public void masterSendSubmitJob(List<String> jobIds){
        List<RdosEngineJobCache> jobCaches = engineJobCacheDao.getJobByIds(jobIds);
        for (RdosEngineJobCache jobCache :jobCaches){
            try {
                ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                JobClient jobClient = new JobClient(paramAction);
                if (EJobCacheStage.IN_PRIORITY_QUEUE.getStage() == jobCache.getStage()) {
                    this.addSubmitJob(jobClient);
                } else {
                    this.afterSubmitJob(jobClient);
                }
            } catch (Exception e) {
                //数据转换异常--打日志
                LOG.error("", e);
                this.dealSubmitFailJob(jobCache.getJobId(), jobCache.getComputeType(), "该任务存储信息异常,无法转换." + e.toString());
            }
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
    public void saveCache(String jobId, String engineType, Integer computeType, int stage, String jobInfo, String jobName){
        String nodeAddress = zkDistributed.getLocalAddress();
        if(engineJobCacheDao.getJobById(jobId) != null){
            engineJobCacheDao.updateJobStage(jobId, stage, nodeAddress);
        }else{
            engineJobCacheDao.insertJob(jobId, engineType, computeType, stage, jobInfo, nodeAddress, jobName);
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
            return false;
        }

        boolean result = groupPriorityQueue.remove(groupName, jobId);
        if(result){
            String zkTaskId = TaskIdUtil.getZkTaskId(computeType, engineType, jobId);
            zkLocalCache.updateLocalMemTaskStatus(zkTaskId, RdosTaskStatus.CANCELED.getStatus());
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
    private Map<String,Object> distributeTask(JobClient jobClient, int retryNum, List<String> excludeNodes){
        Map<String,Object> result = new HashMap<>();
        result.put("result",true);
        try {
            String address = zkLocalCache.getDistributeNode(jobClient.getEngineType(), jobClient.getGroupName(),excludeNodes);
            if(Strings.isNullOrEmpty(address)){
                result.put("result",false);
                result.put("errorInfo","distribute node can not be null");
                return result;
            }
            if (address.equals(zkDistributed.getLocalAddress())){
                this.addSubmitJob(jobClient);
                return result;
            }
            ParamAction paramAction = jobClient.getParamAction();
            paramAction.setRequestStart(RequestStart.NODE.getStart());
            if(HttpSendClient.actionSubmit(address, paramAction)){
                return result;
            }else{
                //处理发送失败的情况(比如网络失败,或者slave主动返回失败)
                if(retryNum >= DISPATCH_RETRY_LIMIT){
                    String errorInfo = String.format("任务 taskId:%s 网络失败超过3次，DISPATCH_RETRY_LIMIT >= 3 ", paramAction.getTaskId());
                    LOG.error(errorInfo);
                    result.put("result",false);
                    result.put("errorInfo",errorInfo);
                    return result;
                }
                retryNum++;
                excludeNodes.add(address);
                return distributeTask(jobClient, retryNum, excludeNodes);
            }

        } catch (Exception e) {
            //只有json 解析的异常才会抛出到这个地方,这应该是不可能发生的
            LOG.error("任务 taskId:{} ---not impossible,please check your program ----,{}", jobClient.getTaskId(), e);
            result.put("result",false);
            result.put("errorInfo",String.format("任务 taskId:%s ---not impossible,please check your program ----,%s",jobClient.getTaskId(),e));
            return result;
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
            rdosEngineStreamJobDao.submitFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), generateErrorMsg(errorMsg));
        }else{
            LOG.error("not support compute type:" + computeType);
        }
    }

    private String generateErrorMsg(String msgInfo){
        return String.format("{\"msg_info\":\"%s\"}", msgInfo);
    }

    class RecoverDealer implements Runnable{
        @Override
        public void run() {
            LOG.info("-----重启后任务开始恢复----");
            try{
                loadQueueFromDB();
            }catch (Exception e){
                LOG.error("----load data from DB error:{}", e);
            }
            LOG.info("-----重启后任务结束恢复-----");
        }
    }

    /**
     * 重启后，各自节点load自己的数据
     */
    public void loadQueueFromDB(){
        List<InterProcessMutex> locks = null;
        String localAddress = zkDistributed.getLocalAddress();
        try {
            locks = zkDistributed.acquireBrokerLock(Lists.newArrayList(localAddress),true);
            long startId = 0L;
            while (true) {
                List<RdosEngineJobCache> jobCaches = engineJobCacheDao.getJobForPriorityQueue(startId, localAddress, null, null);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    //两种情况：
                    //1. 可能本身没有jobcaches的数据
                    //2. master节点已经为此节点做了容灾
                    break;
                }
                for(RdosEngineJobCache jobCache : jobCaches){
                    try {
                        ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                        JobClient jobClient = new JobClient(paramAction);
                        if (EJobCacheStage.IN_PRIORITY_QUEUE.getStage() == jobCache.getStage()) {
                            this.addSubmitJob(jobClient);
                        } else {
                            this.afterSubmitJob(jobClient);
                        }
                        startId = jobCache.getId();
                    } catch (Exception e) {
                        //数据转换异常--打日志
                        LOG.error("", e);
                        dealSubmitFailJob(jobCache.getJobId(), jobCache.getComputeType(), "该任务存储信息异常,无法转换." + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("----broker:{} RecoverDealer error:{}", localAddress, e);
        } finally {
            zkDistributed.releaseLock(locks);
        }
    }

    private Long emitJob2GQ(String engineType, GroupPriorityQueue groupPriorityQueue, Long startId){
        String localAddress = zkDistributed.getLocalAddress();
        try {
            while (true) {
                List<RdosEngineJobCache> jobCaches = engineJobCacheDao.getJobForPriorityQueue(startId, localAddress, EJobCacheStage.IN_PRIORITY_QUEUE.getStage(), engineType);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    break;
                }
                for(RdosEngineJobCache jobCache : jobCaches){
                    try {
                        ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                        JobClient jobClient = new JobClient(paramAction);
                        groupPriorityQueue.add(jobClient);
                        startId = jobCache.getId();
                    } catch (Exception e) {
                        //数据转换异常--打日志
                        LOG.error("", e);
                        dealSubmitFailJob(jobCache.getJobId(), jobCache.getComputeType(), "该任务存储信息异常,无法转换." + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("emitJob2GQ error:{}", localAddress, e);
        }
        return startId;
    }

}
