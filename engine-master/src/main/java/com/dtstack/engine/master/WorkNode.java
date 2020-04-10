package com.dtstack.engine.master;

import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EPluginType;
import com.dtstack.engine.common.util.GenerateErrorMsgUtil;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.queue.GroupInfo;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.PluginInfoDao;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.PluginInfo;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.queue.JobPartitioner;
import com.dtstack.engine.master.resource.JobComputeResourcePlain;
import com.dtstack.engine.master.taskdealer.TaskSubmittedDealer;
import com.dtstack.engine.master.cache.ShardCache;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/29
 */
@Component
public class WorkNode implements InitializingBean, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(WorkNode.class);

    private ApplicationContext applicationContext;

    @Autowired
    private JobComputeResourcePlain jobComputeResourcePlain;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EngineJobDao engineJobDao;

    @Autowired
    private PluginInfoDao pluginInfoDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private TaskSubmittedDealer taskSubmittedDealer;

    @Autowired
    private JobPartitioner jobPartitioner;

    @Autowired
    private WorkerOperator workerOperator;

    /**
     * key: jobResource, 计算引擎类型
     * value: queue
     */
    private Map<String, GroupPriorityQueue> priorityQueueMap = Maps.newConcurrentMap();

    private ExecutorService executors = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory("taskSubmittedDealer"));

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("Initializing " + this.getClass().getName());

        executors.execute(taskSubmittedDealer);

        ExecutorService recoverExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory(this.getClass().getSimpleName()));
        recoverExecutor.submit(new RecoverDealer());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取所有节点的队列大小信息（job已经submitted的除外）
     * key1: nodeAddress,
     * key2: jobResource
     */
    public Map<String, Map<String, GroupInfo>> getAllNodesGroupQueueInfo() {
        List<String> allNodeAddress = engineJobCacheDao.getAllNodeAddress();
        Map<String, Map<String, GroupInfo>> allNodeGroupInfo = Maps.newHashMap();
        for (String nodeAddress : allNodeAddress) {
            if (StringUtils.isBlank(nodeAddress)) {
                continue;
            }
            allNodeGroupInfo.computeIfAbsent(nodeAddress, na -> {
                Map<String, GroupInfo> nodeGroupInfo = Maps.newHashMap();
                priorityQueueMap.forEach((jobResource, priorityQueue) -> {
                    int groupSize = engineJobCacheDao.countByStage(jobResource, EJobCacheStage.unSubmitted(), nodeAddress);
                    Long minPriority = engineJobCacheDao.minPriorityByStage(jobResource, Lists.newArrayList(EJobCacheStage.PRIORITY.getStage(), EJobCacheStage.LACKING.getStage()), nodeAddress);
                    minPriority = minPriority == null ? 0 : minPriority;
                    GroupInfo groupInfo = new GroupInfo();
                    groupInfo.setSize(groupSize);
                    groupInfo.setPriority(minPriority);
                    nodeGroupInfo.put(jobResource, groupInfo);
                });
                return nodeGroupInfo;
            });
        }
        return allNodeGroupInfo;
    }

    /**
     * 提交优先级队列->最终提交到具体执行组件
     */
    public void addSubmitJob(JobClient jobClient, boolean insert) {
        String jobResource = jobComputeResourcePlain.getJobResource(jobClient);
        if (jobClient.getPluginInfo() != null) {
            updateJobClientPluginInfo(jobClient.getTaskId(), jobClient.getPluginInfo());
        }
        jobClient.setCallBack((jobStatus) -> {
            updateJobStatus(jobClient.getTaskId(), jobStatus);
        });

        saveCache(jobClient, jobResource, EJobCacheStage.DB.getStage(), insert);
        jobClient.doStatusCallBack(RdosTaskStatus.WAITENGINE.getStatus());

        //加入节点的优先级队列
        this.addGroupPriorityQueue(jobResource, jobClient, true);
    }

    /**
     * 容灾时对已经提交到执行组件的任务，进行恢复
     */
    public void afterSubmitJob(JobClient jobClient) {
        if (jobClient.getPluginInfo() != null) {
            updateJobClientPluginInfo(jobClient.getTaskId(), jobClient.getPluginInfo());
        }
        updateCache(jobClient, EJobCacheStage.SUBMITTED.getStage());
        shardCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.SUBMITTED.getStatus());
    }

    public boolean addGroupPriorityQueue(String jobResource, JobClient jobClient, boolean judgeBlock) {
        try {
            GroupPriorityQueue groupPriorityQueue = getGroupPriorityQueue(jobResource);
            return groupPriorityQueue.add(jobClient, judgeBlock);
        } catch (Exception e) {
            LOG.error("", e);
            dealSubmitFailJob(jobClient.getTaskId(), e.toString());
            return false;
        }
    }

    public boolean addRestartJob(JobClient jobClient) {
        String jobResource = jobComputeResourcePlain.getJobResource(jobClient);
        GroupPriorityQueue groupPriorityQueue = getGroupPriorityQueue(jobResource);
        return groupPriorityQueue.addRestartJob(jobClient);
    }

    public GroupPriorityQueue getGroupPriorityQueue(String jobResource) {
        GroupPriorityQueue groupPriorityQueue = priorityQueueMap.computeIfAbsent(jobResource, k -> GroupPriorityQueue.builder()
                .setApplicationContext(applicationContext)
                .setJobResource(jobResource)
                .setWorkNode(this)
                .build());
        return groupPriorityQueue;
    }

    public void updateJobStatus(String jobId, Integer status) {
        engineJobDao.updateJobStatus(jobId, status);
        LOG.info("jobId:{} update job status:{}.", jobId, status);
    }

    private void saveCache(JobClient jobClient, String jobResource, int stage, boolean insert) {
        String nodeAddress = environmentContext.getLocalAddress();
        if (insert) {
            engineJobCacheDao.insert(jobClient.getTaskId(), jobClient.getEngineType(), jobClient.getComputeType().getType(), stage, jobClient.getParamAction().toString(), nodeAddress, jobClient.getJobName(), jobClient.getPriority(), jobResource);
        } else {
            engineJobCacheDao.updateStage(jobClient.getTaskId(), stage, nodeAddress, jobClient.getPriority());
        }
    }

    public void updateCache(JobClient jobClient, int stage) {
        String nodeAddress = environmentContext.getLocalAddress();
        engineJobCacheDao.updateStage(jobClient.getTaskId(), stage, nodeAddress, jobClient.getPriority());
    }

    private void updateJobClientPluginInfo(String jobId, String pluginInfoStr) {
        Long refPluginInfoId = -1L;

        //请求不带插件的连接信息的话则默认为使用本地默认的集群配置---pluginInfoId = -1;
        if (!Strings.isNullOrEmpty(pluginInfoStr)) {
            String pluginKey = MD5Util.getMd5String(pluginInfoStr);
            PluginInfo pluginInfo = pluginInfoDao.getByKey(pluginKey);
            if (pluginInfo == null) {
                pluginInfo = new PluginInfo();
                pluginInfo.setPluginInfo(pluginInfoStr);
                pluginInfo.setPluginKey(pluginKey);
                pluginInfo.setType(EPluginType.DYNAMIC.getType());

                refPluginInfoId = pluginInfoDao.replaceInto(pluginInfo);
            } else {
                refPluginInfoId = pluginInfo.getId();
            }
        }
        //更新任务ref的pluginInfo
        engineJobDao.updateJobPluginId(jobId, refPluginInfoId);

    }

    public String getAndUpdateEngineLog(String jobId, String engineJobId, String appId, long pluginId) {
        String engineLog = null;
        try {
            String pluginInfoStr = pluginInfoDao.getPluginInfo(pluginId);
            Map<String, Object> params = PublicUtil.jsonStrToObject(pluginInfoStr, Map.class);
            String engineType = MathUtil.getString(params.get(ConfigConstant.TYPE_NAME_KEY));
            JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobId, appId, jobId);
            //从engine获取log
            engineLog = workerOperator.getEngineLog(engineType, pluginInfoStr, jobIdentifier);
            if (engineLog != null) {
                engineJobDao.updateEngineLog(jobId, engineLog);
            }
        } catch (Throwable e) {
            LOG.error("getAndUpdateEngineLog error jobId:{} error:{}.", jobId, e);
        }
        return engineLog;
    }

    /**
     * master 节点分发任务失败
     *
     * @param taskId
     */
    public void dealSubmitFailJob(String taskId, String errorMsg) {
        engineJobCacheDao.delete(taskId);
        engineJobDao.jobFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), GenerateErrorMsgUtil.generateErrorMsg(errorMsg));
        LOG.info("jobId:{} update job status:{}, job is finished.", taskId, RdosTaskStatus.SUBMITFAILD.getStatus());
    }

    class RecoverDealer implements Runnable {
        @Override
        public void run() {
            LOG.info("-----重启后任务开始恢复----");
            String localAddress = environmentContext.getLocalAddress();
            try {
                long startId = 0L;
                while (true) {
                    List<EngineJobCache> jobCaches = engineJobCacheDao.listByStage(startId, localAddress, null, null);
                    if (CollectionUtils.isEmpty(jobCaches)) {
                        //两种情况：
                        //1. 可能本身没有jobcaches的数据
                        //2. master节点已经为此节点做了容灾
                        break;
                    }
                    for (EngineJobCache jobCache : jobCaches) {
                        try {
                            ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                            JobClient jobClient = new JobClient(paramAction);
                            if (EJobCacheStage.unSubmitted().contains(jobCache.getStage())) {
                                WorkNode.this.addSubmitJob(jobClient, false);
                            } else {
                                WorkNode.this.afterSubmitJob(jobClient);
                            }
                            startId = jobCache.getId();
                        } catch (Exception e) {
                            LOG.error("", e);
                            //数据转换异常--打日志
                            dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + e.toString());
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("----broker:{} RecoverDealer error:{}", localAddress, e);
            }

            LOG.info("-----重启后任务结束恢复-----");
        }
    }

}
