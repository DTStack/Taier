package com.dtstack.engine.master;

import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.util.GenerateErrorMsgUtil;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.EPluginType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.queue.GroupInfo;
import com.dtstack.engine.common.queue.GroupPriorityQueue;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.PluginInfoDao;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.domain.EngineJob;
import com.dtstack.engine.domain.PluginInfo;
import com.dtstack.engine.common.enums.RequestStart;
import com.dtstack.engine.common.util.TaskIdUtil;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.impl.JobStopQueue;
import com.dtstack.engine.master.resource.JobComputeResourcePlain;
import com.dtstack.engine.master.send.HttpSendClient;
//import com.dtstack.engine.master.task.QueueListener;
import com.dtstack.engine.master.task.TaskListener;
import com.dtstack.engine.master.task.TaskStatusListener;
import com.dtstack.engine.master.zookeeper.ZkDistributed;
import com.dtstack.engine.master.cache.ZkLocalCache;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class WorkNode {

    private static final Logger LOG = LoggerFactory.getLogger(WorkNode.class);

    /**任务分发到执行engine上最多重试3次*/
    private static final int DISPATCH_RETRY_LIMIT = 3;

    @Autowired
    private JobComputeResourcePlain jobComputeResourcePlain;

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EngineJobDao rdosEngineBatchJobDao;

    @Autowired
    private PluginInfoDao pluginInfoDao;

    @Autowired
    private EnvironmentContext environmentContext;

    /**
     * key: 计算引擎类型（集群groupName + computeResourceType）
     * value: queue
     */
    private Map<String, GroupPriorityQueue> priorityQueueMap = Maps.newConcurrentMap();

    private JobStopQueue jobStopQueue;

    private static WorkNode singleton = new WorkNode();

    public static WorkNode getInstance(){
        return singleton;
    }

    private ExecutorService executors  = new ThreadPoolExecutor(3, 3,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    private WorkNode(){
    }

    public void init() {
        executors.execute(new TaskListener());
        executors.execute(new TaskStatusListener());
//        executors.execute(new QueueListener());

        ExecutorService recoverExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("recoverDealer"));
        recoverExecutor.submit(new RecoverDealer());

        jobStopQueue = new JobStopQueue(this);
        jobStopQueue.start();

        zkLocalCache.setWorkNode(this);
    }

    public GroupPriorityQueue getPriorityQueue(JobClient jobClient) {
        String jobResource = jobComputeResourcePlain.getJobResource(jobClient);
        return priorityQueueMap.get(jobResource);
    }

    public GroupPriorityQueue getPriorityQueue(String jobResource) {
        return priorityQueueMap.get(jobResource);
    }

    /**
     * 获取所有节点的队列大小信息
     * key1: address,
     * key2: jobResource
     */
    public Map<String, Map<String, GroupInfo>> getQueueInfo(){
        //todo,获取所有节点的队列信息，
        //todo max priority 取在内存队列的，非重试任务的最大的priority
//        String localAddress = zkDistributed.getLocalAddress();
//        Map<String, GroupInfo> queueInfo = Maps.newHashMap();
//        priorityQueueMap.forEach((jobResource, priorityQueue) -> {
//            int queueSize = engineJobCacheDao.countGroupQueueJob(jobResource, EJobCacheStage.IN_PRIORITY_QUEUE.getStage(), localAddress);
//
//            Iterator<JobClient> it = priorityQueue.getQueue().iterator();
//            JobClient topPriorityJob = null;
//            while (it.hasNext()){
//                JobClient topJob = it.next();
//                if (topJob.isJobRetryWaiting()){
//                    continue;
//                }
//                topPriorityJob = topJob;
//                break;
//            }
//            GroupInfo groupInfo = new GroupInfo();
//            groupInfo.setSize(queueSize);
//            groupInfo.setPriority(topPriorityJob == null ? 0 : topPriorityJob.getPriority());
//            queueInfo.put(jobResource, groupInfo);
//        });

        return null;
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

    public void addSubmitJob(Map<String, Object> params) {
        try{
            ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
//            checkParam(paramAction);
//            if(!checkSubmitted(paramAction)){
//                return result;
//            }
            JobClient jobClient = new JobClient(paramAction);
            this.addSubmitJob(jobClient, true);
        }catch (Exception e){
            LOG.error("", e);
        }
    }

    /**
     * 提交优先级队列->最终提交到具体执行组件
     */
    public void addSubmitJob(JobClient jobClient, boolean insert) {
        String jobResource = jobComputeResourcePlain.getJobResource(jobClient);
        Integer computeType = jobClient.getComputeType().getType();
        if(jobClient.getPluginInfo() != null){
            updateJobClientPluginInfo(jobClient.getTaskId(), computeType, jobClient.getPluginInfo());
        }
        jobClient.setCallBack((jobStatus)-> {
            updateJobStatus(jobClient.getTaskId(), computeType, jobStatus);
        });

        saveCache(jobClient, jobResource, EJobCacheStage.IN_PRIORITY_QUEUE.getStage(), insert);
        updateJobStatus(jobClient.getTaskId(), computeType, RdosTaskStatus.WAITENGINE.getStatus());

        //加入节点的优先级队列
        this.redirectSubmitJob(jobResource, jobClient);
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
        updateCache(jobClient, EJobCacheStage.IN_SUBMIT_QUEUE.getStage());
        //检查分片
        zkLocalCache.checkShard();
        zkLocalCache.updateLocalMemTaskStatus(zkTaskId,RdosTaskStatus.SUBMITTED.getStatus());
    }

    public void redirectSubmitJob(String jobResource, JobClient jobClient){
        try{
            GroupPriorityQueue groupQueue = priorityQueueMap.computeIfAbsent(jobResource, k -> new GroupPriorityQueue(jobResource, environmentContext.getQueueSize(), environmentContext.getJobRestartDelay(),
                    (groupPriorityQueue, startId, limited) -> {
                        return this.emitJob2GQ(jobClient.getEngineType(), groupPriorityQueue, startId, limited);
                    })
            );
            groupQueue.add(jobClient);
        }catch (Exception e){
            LOG.error("add to priority queue error:", e);
            dealSubmitFailJob(jobClient.getTaskId(), jobClient.getComputeType().getType(), e.toString());
        }
    }

    public void addRestartJob(JobClient jobClient) {
        String jobResource = jobComputeResourcePlain.getJobResource(jobClient);
        GroupPriorityQueue queue = priorityQueueMap.get(jobResource);
        queue.addRestartJob(jobClient);
    }

    public void masterSendSubmitJob(List<String> jobIds){
        List<EngineJobCache> jobCaches = engineJobCacheDao.getByJobIds(jobIds);
        for (EngineJobCache jobCache :jobCaches){
            try {
                ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                JobClient jobClient = new JobClient(paramAction);
                if (EJobCacheStage.IN_PRIORITY_QUEUE.getStage() == jobCache.getStage()) {
                    this.addSubmitJob(jobClient, false);
                } else {
                    this.afterSubmitJob(jobClient);
                }
            } catch (Exception e) {
                //数据转换异常--打日志
                LOG.error("", e);
                this.dealSubmitFailJob(jobCache.getJobId(), jobCache.getComputeType(), "This task stores information exception and cannot be converted." + e.toString());
            }
        }

        priorityQueueMap.forEach((k,v)->v.resetStartId());
    }



    public boolean workSendStop(ParamAction paramAction){
        return jobStopQueue.tryPutStopJobQueue(paramAction);
    }


    private void updateJobStatus(String jobId, Integer computeType, Integer status) {
        rdosEngineBatchJobDao.updateJobStatus(jobId, status);
        LOG.info("jobId:{} update job status to {}", jobId, status);
    }

    public void saveCache(JobClient jobClient, String jobResource, int stage, boolean insert){
        String nodeAddress = zkDistributed.getLocalAddress();
        if(insert){
            engineJobCacheDao.insert(jobClient.getTaskId(), jobClient.getEngineType(), jobClient.getComputeType().getType(), stage, jobClient.getParamAction().toString(), nodeAddress, jobClient.getJobName(), jobClient.getPriority(), jobResource);
        } else {
            engineJobCacheDao.updateStage(jobClient.getTaskId(), stage, nodeAddress, jobClient.getPriority());
        }
    }

    public void updateCache(JobClient jobClient, int stage){
        String nodeAddress = zkDistributed.getLocalAddress();
        engineJobCacheDao.updateStage(jobClient.getTaskId(), stage, nodeAddress, jobClient.getPriority());
    }

    private void updateJobClientPluginInfo(String jobId, Integer computeType, String pluginInfoStr){
        Long refPluginInfoId = -1L;

        //请求不带插件的连接信息的话则默认为使用本地默认的集群配置---pluginInfoId = -1;
        if(!Strings.isNullOrEmpty(pluginInfoStr)){
            PluginInfo pluginInfo = pluginInfoDao.getByKey(pluginInfoStr);
            if(pluginInfo == null){
                //TODO
//                refPluginInfoId = pluginInfoDao.replaceInto(pluginInfoStr, EPluginType.DYNAMIC.getType());
            }else{
                refPluginInfoId = pluginInfo.getId();
            }
        }
        //更新任务ref的pluginInfo
        rdosEngineBatchJobDao.updateJobPluginId(jobId, refPluginInfoId);

    }

    public boolean stopTaskIfExists(String engineType, String groupName, String jobId, Integer computeType){
        GroupPriorityQueue groupPriorityQueue = priorityQueueMap.get(engineType);
        if(groupPriorityQueue == null){
            return false;
        }

        boolean result = groupPriorityQueue.remove(jobId);
        if(result){
            String zkTaskId = TaskIdUtil.getZkTaskId(computeType, engineType, jobId);
            zkLocalCache.updateLocalMemTaskStatus(zkTaskId, RdosTaskStatus.CANCELED.getStatus());
            engineJobCacheDao.delete(jobId);
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
            String engineType = MathUtil.getString(params.get(ConfigConstant.TYPE_NAME_KEY));
            JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobId, appId, jobId);
            //从engine获取log
            engineLog = JobClient.getEngineLog(engineType, pluginInfoStr, jobIdentifier);
            if (engineLog != null) {
                rdosEngineBatchJobDao.updateEngineLog(jobId, engineLog);
            }
        } catch (Throwable e){
            LOG.error("getAndUpdateEngineLog error jobId {} ,error info {}..", jobId, ExceptionUtil.getErrorMessage(e));
        }
        return engineLog;
    }

    /**
     * 根据taskId 补齐 engineTaskId
     * @param paramAction
     */
    public void fillJobClientEngineId(ParamAction paramAction){

        if(paramAction.getEngineTaskId() == null){
            //从数据库补齐数据
            EngineJob batchJob = rdosEngineBatchJobDao.getRdosJobByJobId(paramAction.getTaskId());
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
    public void dealSubmitFailJob(String taskId, Integer computeType, String errorMsg){
        engineJobCacheDao.delete(taskId);
        rdosEngineBatchJobDao.jobFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), GenerateErrorMsgUtil.generateErrorMsg(errorMsg));
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
                List<EngineJobCache> jobCaches = engineJobCacheDao.listByStage(startId, localAddress, null, null);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    //两种情况：
                    //1. 可能本身没有jobcaches的数据
                    //2. master节点已经为此节点做了容灾
                    break;
                }
                for(EngineJobCache jobCache : jobCaches){
                    try {
                        ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                        JobClient jobClient = new JobClient(paramAction);
                        if (EJobCacheStage.IN_PRIORITY_QUEUE.getStage() == jobCache.getStage()) {
                            this.addSubmitJob(jobClient, false);
                        } else {
                            this.afterSubmitJob(jobClient);
                        }
                        startId = jobCache.getId();
                    } catch (Exception e) {
                        //数据转换异常--打日志
                        LOG.error("", e);
                        dealSubmitFailJob(jobCache.getJobId(), jobCache.getComputeType(), "This task stores information exception and cannot be converted." + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("----broker:{} RecoverDealer error:{}", localAddress, e);
        } finally {
            zkDistributed.releaseLock(locks);
        }
    }

    private Long emitJob2GQ(String engineType, GroupPriorityQueue groupPriorityQueue, long startId, int limited){
        String localAddress = zkDistributed.getLocalAddress();
        try {
            int count = 0;
            outLoop :
            while (true) {
                List<EngineJobCache> jobCaches = engineJobCacheDao.listByStage(startId, localAddress, EJobCacheStage.IN_PRIORITY_QUEUE.getStage(), engineType);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    break;
                }
                for(EngineJobCache jobCache : jobCaches){
                    try {
                        ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                        JobClient jobClient = new JobClient(paramAction);
                        jobClient.setCallBack((jobStatus)-> {
                            updateJobStatus(jobClient.getTaskId(), jobClient.getComputeType().getType(), jobStatus);
                        });
                        groupPriorityQueue.add(jobClient);
                        LOG.info("jobId:{} load from db, emit job to queue.", jobClient.getTaskId());
                        startId = jobCache.getId();
                        if (++count >= limited){
                            break outLoop;
                        }
                    } catch (Exception e) {
                        //数据转换异常--打日志
                        LOG.error("", e);
                        dealSubmitFailJob(jobCache.getJobId(), jobCache.getComputeType(), "This task stores information exception and cannot be converted." + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("emitJob2GQ error:{}", localAddress, e);
        }
        return startId;
    }

}
