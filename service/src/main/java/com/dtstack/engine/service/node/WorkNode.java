package com.dtstack.engine.service.node;

import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.EPluginType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.service.queue.GroupInfo;
import com.dtstack.engine.service.queue.GroupPriorityQueue;
import com.dtstack.engine.service.db.dao.RdosEngineJobDAO;
import com.dtstack.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.engine.service.db.dao.RdosPluginInfoDAO;
import com.dtstack.engine.service.db.dataobject.RdosEngineJob;
import com.dtstack.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.engine.service.db.dataobject.RdosPluginInfo;
import com.dtstack.engine.service.enums.RequestStart;
import com.dtstack.engine.service.send.HttpSendClient;
import com.dtstack.engine.service.zk.ZkDistributed;
import com.dtstack.engine.service.zk.cache.ZkLocalCache;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
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

    private RdosEngineJobCacheDAO rdosEngineJobCacheDAO = new RdosEngineJobCacheDAO();

    private RdosEngineJobDAO rdosEngineBatchJobDao = new RdosEngineJobDAO();

    private RdosPluginInfoDAO pluginInfoDao = new RdosPluginInfoDAO();

    /**
     * key: jobResource, 计算引擎类型
     * value: queue
     */
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
    }

    /**
     * 获取所有节点的队列大小信息（job已经submitted的除外）
     * key1: nodeAddress,
     * key2: jobResource
     */
    public Map<String, Map<String, GroupInfo>> getAllNodesGroupQueueInfo() {
        List<String> allNodeAddress = rdosEngineJobCacheDAO.getAllNodeAddress();
        Map<String, Map<String, GroupInfo>> allNodeGroupInfo = Maps.newHashMap();
        for (String nodeAddress : allNodeAddress) {
            if (org.apache.commons.lang.StringUtils.isBlank(nodeAddress)) {
                continue;
            }
            allNodeGroupInfo.computeIfAbsent(nodeAddress, na -> {
                Map<String, GroupInfo> nodeGroupInfo = Maps.newHashMap();
                priorityQueueMap.forEach((jobResource, priorityQueue) -> {
                    int groupSize = rdosEngineJobCacheDAO.countByStage(priorityQueue.getEngineType(), priorityQueue.getGroupName(), EJobCacheStage.unSubmitted(), nodeAddress);
                    Long maxPriority = rdosEngineJobCacheDAO.maxPriorityByStage(priorityQueue.getEngineType(), priorityQueue.getGroupName(), EJobCacheStage.PRIORITY.getStage(), nodeAddress);
                    maxPriority = maxPriority == null ? 0 : maxPriority;
                    GroupInfo groupInfo = new GroupInfo();
                    groupInfo.setSize(groupSize);
                    groupInfo.setPriority(maxPriority);
                    nodeGroupInfo.put(jobResource, groupInfo);
                });
                return nodeGroupInfo;
            });
        }
        return allNodeGroupInfo;
    }

    /**
     * 任务分发
     */
    public void addStartJob(JobClient jobClient){
        Map<String,Object> distributeResult = distributeTask(jobClient,0,Lists.newArrayList());
        if (!MapUtils.getBooleanValue(distributeResult,"result")){
            String errorInfo = String.format("engine master 下发任务异常:\n %s",MapUtils.getString(distributeResult,"errorInfo"));
            dealSubmitFailJob(jobClient.getTaskId(), errorInfo);
        }
    }

    /**
     * 提交优先级队列->最终提交到具体执行组件
     */
    public void addSubmitJob(JobClient jobClient, boolean insert) {
        if(jobClient.getPluginInfo() != null){
            updateJobClientPluginInfo(jobClient.getTaskId(), jobClient.getPluginInfo());
        }
        jobClient.setCallBack((jobStatus)-> {
            updateJobStatus(jobClient.getTaskId(), jobStatus);
        });

        saveCache(jobClient, EJobCacheStage.DB.getStage(), insert);
        updateJobStatus(jobClient.getTaskId(), RdosTaskStatus.WAITENGINE.getStatus());

        //加入节点的优先级队列
        this.addGroupPriorityQueue(jobClient, true);
    }

    /**
     * 容灾时对已经提交到执行组件的任务，进行恢复
     */
    public void afterSubmitJob(JobClient jobClient) {
        if(jobClient.getPluginInfo() != null){
            updateJobClientPluginInfo(jobClient.getTaskId(), jobClient.getPluginInfo());
        }
        updateCache(jobClient, EJobCacheStage.SUBMITTED.getStage());
        //检查分片
        zkLocalCache.checkShard();
        zkLocalCache.updateLocalMemTaskStatus(jobClient.getTaskId(),RdosTaskStatus.SUBMITTED.getStatus());
    }

    public boolean addGroupPriorityQueue(JobClient jobClient, boolean judgeBlock){
        try{
            String jobResource = getJobResource(jobClient.getEngineType(), jobClient.getGroupName());
            GroupPriorityQueue groupPriorityQueue = getGroupPriorityQueue(jobResource, jobClient.getEngineType(), jobClient.getGroupName());
            return groupPriorityQueue.add(jobClient, judgeBlock);
        }catch (Exception e){
            LOG.error("add to priority queue error:", e);
            dealSubmitFailJob(jobClient.getTaskId(), e.toString());
            return false;
        }
    }

    public boolean addRestartJob(JobClient jobClient) {
        String jobResource = getJobResource(jobClient.getEngineType(), jobClient.getGroupName());
        GroupPriorityQueue groupPriorityQueue = getGroupPriorityQueue(jobResource, jobClient.getEngineType(), jobClient.getGroupName());
        return groupPriorityQueue.addRestartJob(jobClient);
    }

    public String getJobResource(String engineType, String groupName) {
        return engineType + "_" + groupName;
    }

    private GroupPriorityQueue getGroupPriorityQueue(String jobResource, String engineType, String groupName) {
        GroupPriorityQueue groupQueue = priorityQueueMap.computeIfAbsent(jobResource,
                k -> new GroupPriorityQueue(jobResource, engineType, groupName)
        );
        return groupQueue;
    }

    public void masterSendSubmitJob(List<String> jobIds){
        List<RdosEngineJobCache> jobCaches = rdosEngineJobCacheDAO.getJobByIds(jobIds);
        for (RdosEngineJobCache jobCache :jobCaches){
            try {
                ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                JobClient jobClient = new JobClient(paramAction);
                if (EJobCacheStage.unSubmitted().contains(jobCache.getStage())) {
                    this.addSubmitJob(jobClient, false);
                } else {
                    this.afterSubmitJob(jobClient);
                }
            } catch (Exception e) {
                //数据转换异常--打日志
                LOG.error("", e);
                this.dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + e.toString());
            }
        }
    }



    public boolean workSendStop(ParamAction paramAction){
        return jobStopQueue.tryPutStopJobQueue(paramAction);
    }


    public void updateJobStatus(String jobId, Integer status) {
        rdosEngineBatchJobDao.updateJobStatus(jobId, status);
        LOG.info("jobId:{} update job status to {}", jobId, status);
    }

    public void saveCache(JobClient jobClient, int stage, boolean insert){
        String nodeAddress = zkDistributed.getLocalAddress();
        if(insert){
            rdosEngineJobCacheDAO.insertJob(jobClient.getTaskId(), jobClient.getEngineType(), jobClient.getComputeType().getType(), stage, jobClient.getParamAction().toString(), nodeAddress, jobClient.getJobName(), jobClient.getPriority(), jobClient.getGroupName());
        } else {
            rdosEngineJobCacheDAO.updateJobStage(jobClient.getTaskId(), stage, nodeAddress, jobClient.getPriority(), jobClient.getGroupName());
        }
    }

    public void updateCache(JobClient jobClient, int stage){
        String nodeAddress = zkDistributed.getLocalAddress();
        rdosEngineJobCacheDAO.updateJobStage(jobClient.getTaskId(), stage, nodeAddress, jobClient.getPriority(), jobClient.getGroupName());
    }

    private void updateJobClientPluginInfo(String jobId, String pluginInfoStr){
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
        rdosEngineBatchJobDao.updateJobPluginId(jobId, refPluginInfoId);

    }

    public boolean stopTaskIfExists(String engineType, String groupName, String jobId){
        String jobResource = getJobResource(engineType, groupName);
        GroupPriorityQueue groupPriorityQueue = priorityQueueMap.get(jobResource);
        if(groupPriorityQueue == null){
            return false;
        }

        boolean result = groupPriorityQueue.remove(jobId);
        if(result){
            zkLocalCache.updateLocalMemTaskStatus(jobId, RdosTaskStatus.CANCELED.getStatus());
            rdosEngineJobCacheDAO.deleteJob(jobId);
            //修改任务状态
            rdosEngineBatchJobDao.updateJobStatus(jobId, RdosTaskStatus.CANCELED.getStatus());
            LOG.info("jobId:{} update job status to {}", jobId, RdosTaskStatus.CANCELED.getStatus());
        }

        return result;
    }

    public String getAndUpdateEngineLog(String jobId, String engineJobId, String appId, long pluginId) {
        String engineLog = null;
        try {
            String pluginInfoStr = pluginInfoDao.getPluginInfo(pluginId);
            Map<String, Object> params = PublicUtil.jsonStrToObject(pluginInfoStr, Map.class);
            String engineType = MathUtil.getString(params.get(ConfigParse.TYPE_NAME_KEY));
            JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobId, appId, jobId);
            //从engine获取log
            engineLog = JobClient.getEngineLog(engineType, pluginInfoStr, jobIdentifier);
            if (engineLog != null) {
                updateJobEngineLog(jobId, engineLog, engineType);
            }
        } catch (Throwable e){
            LOG.error("getAndUpdateEngineLog error jobId {} ,error info {}..", jobId, ExceptionUtil.getErrorMessage(e));
        }
        return engineLog;
    }

    public void updateJobEngineLog(String jobId, String jobLog, String engineType) {
        if (!EngineType.isFlink(engineType)) {
            rdosEngineBatchJobDao.updateEngineLog(jobId, jobLog);
            return;
        }

        RdosEngineJob batchJob = rdosEngineBatchJobDao.getRdosTaskByTaskId(jobId);
        if (batchJob != null) {
            if (StringUtils.isEmpty(batchJob.getEngineLog())) {
                rdosEngineBatchJobDao.updateEngineLog(jobId, jobLog);
            } else {
                try {
                    Map<String, Object> newLogMap = PublicUtil.jsonStrToObject(jobLog, Map.class);
                    Map<String, Object> oldLogMap = PublicUtil.jsonStrToObject(batchJob.getEngineLog(), Map.class);
                    newLogMap.forEach((key, value) -> {
                        oldLogMap.put(key, value);
                    });

                    rdosEngineBatchJobDao.updateEngineLog(jobId, PublicUtil.objToString(oldLogMap));
                } catch (Exception e) {
                    LOG.warn("update batch engine log error,new log:{}, old log:{},msg:{}", jobLog, batchJob.getEngineLog(), e);
                }
            }
        }
    }

    /**
     * 根据taskId 补齐 engineTaskId
     * @param paramAction
     */
    public void fillJobClientEngineId(ParamAction paramAction){

        if(paramAction.getEngineTaskId() == null){
            //从数据库补齐数据
            RdosEngineJob batchJob = rdosEngineBatchJobDao.getRdosTaskByTaskId(paramAction.getTaskId());
            if(batchJob != null){
            	paramAction.setEngineTaskId(batchJob.getEngineJobId());
            	paramAction.setApplicationId(batchJob.getApplicationId());
            }
        }

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
                this.addSubmitJob(jobClient, true);
                return result;
            }
            ParamAction paramAction = jobClient.getParamAction();
            paramAction.setRequestStart(RequestStart.NODE.getStart());
            if(HttpSendClient.actionSubmit(address, paramAction)){
                return result;
            }else{
                //处理发送失败的情况(比如网络失败,或者slave主动返回失败)
                if(retryNum >= DISPATCH_RETRY_LIMIT){
                    String errorInfo = String.format("Job taskId:%s the network failed more than 3 times，DISPATCH_RETRY_LIMIT >= 3 ", paramAction.getTaskId());
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
            LOG.error("Job taskId:{} ---not impossible,please check your program ----,{}", jobClient.getTaskId(), e);
            result.put("result",false);
            result.put("errorInfo",String.format("Job taskId:%s ---not impossible,please check your program ----,%s",jobClient.getTaskId(),e));
            return result;
        }
    }

    /**
     * master 节点分发任务失败
     * @param taskId
     */
    public void dealSubmitFailJob(String taskId, String errorMsg){
        rdosEngineJobCacheDAO.deleteJob(taskId);
        rdosEngineBatchJobDao.submitFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), generateErrorMsg(errorMsg));
        LOG.info("jobId:{} update job status:{}, job is finished.", taskId, RdosTaskStatus.SUBMITFAILD.getStatus());
    }

    private String generateErrorMsg(String msgInfo){
        return String.format("{\"msg_info\":\"%s\"}", msgInfo);
    }

    class RecoverDealer implements Runnable{
        @Override
        public void run() {
            LOG.info("-----重启后任务开始恢复----");
            try{
                loadQueueFromDb();
            }catch (Exception e){
                LOG.error("----load data from DB error:{}", e);
            }
            LOG.info("-----重启后任务结束恢复-----");
        }
    }

    /**
     * 重启后，各自节点load自己的数据
     */
    public void loadQueueFromDb(){
        List<InterProcessMutex> locks = null;
        String localAddress = zkDistributed.getLocalAddress();
        try {
            locks = zkDistributed.acquireBrokerLock(Lists.newArrayList(localAddress),true);
            long startId = 0L;
            while (true) {
                List<RdosEngineJobCache> jobCaches = rdosEngineJobCacheDAO.listByNodeAddressStage(startId, localAddress, null, null, null);
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
                        if (EJobCacheStage.unSubmitted().contains(jobCache.getStage())) {
                            this.addSubmitJob(jobClient, false);
                        } else {
                            this.afterSubmitJob(jobClient);
                        }
                        startId = jobCache.getId();
                    } catch (Exception e) {
                        //数据转换异常--打日志
                        LOG.error("", e);
                        dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("----broker:{} RecoverDealer error:{}", localAddress, e);
        } finally {
            zkDistributed.releaseLock(locks);
        }
    }



}
