package com.dtstack.engine.master.jobdealer;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.BlockCallerPolicy;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.EQueueSourceType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.ClientArgumentException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.exception.WorkerAccessException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.pojo.SimpleJobDelay;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.common.util.SleepUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.queue.GroupInfo;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import com.dtstack.engine.master.queue.JobPartitioner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
public class JobSubmitDealer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSubmitDealer.class);

    /**
     * 用于taskListener处理, 此处为static修饰，全局共用一个
     */
    private static LinkedBlockingQueue<JobClient> submittedQueue = new LinkedBlockingQueue<>();

    private JobPartitioner jobPartitioner;
    private WorkerOperator workerOperator;
    private EngineJobCacheDao engineJobCacheDao;
    private ShardCache shardCache;
    private ScheduleJobDao scheduleJobDao;

    private long jobRestartDelay;
    private long jobLackingDelay;
    private long jobPriorityStep;
    private long jobLackingInterval;
    private long jobSubmitExpired;
    private long jobLackingCountLimited = 3;
    private boolean checkJobMaxPriorityStrategy = false;
    private int jobSubmitConcurrent = 1;

    private String localAddress;
    private String jobResource = null;
    private GroupPriorityQueue priorityQueue;
    private PriorityBlockingQueue<JobClient> queue = null;
    private DelayBlockingQueue<SimpleJobDelay<JobClient>> delayJobQueue = null;
    private JudgeResult workerNotFindResult = JudgeResult.notOk( "worker not find");
    private ExecutorService jobSubmitConcurrentService;

    public JobSubmitDealer(String localAddress, GroupPriorityQueue priorityQueue, ApplicationContext applicationContext) {
        this.jobPartitioner = applicationContext.getBean(JobPartitioner.class);
        this.workerOperator = applicationContext.getBean(WorkerOperator.class);
        this.engineJobCacheDao = applicationContext.getBean(EngineJobCacheDao.class);
        this.shardCache = applicationContext.getBean(ShardCache.class);
        this.scheduleJobDao = applicationContext.getBean(ScheduleJobDao.class);
        EnvironmentContext environmentContext = applicationContext.getBean(EnvironmentContext.class);
        if (null == priorityQueue) {
            throw new RdosDefineException("priorityQueue must not null.");
        }

        jobRestartDelay = environmentContext.getJobRestartDelay();
        jobLackingDelay = environmentContext.getJobLackingDelay();
        jobPriorityStep = environmentContext.getJobPriorityStep();
        jobLackingInterval = environmentContext.getJobLackingInterval();
        jobSubmitExpired = environmentContext.getJobSubmitExpired();
        jobLackingCountLimited = environmentContext.getJobLackingCountLimited();
        checkJobMaxPriorityStrategy = environmentContext.getCheckJobMaxPriorityStrategy();
        jobSubmitConcurrent = environmentContext.getJobSubmitConcurrent();

        this.localAddress = localAddress;
        this.priorityQueue = priorityQueue;
        this.jobResource = priorityQueue.getJobResource();
        this.queue = priorityQueue.getQueue();
        this.delayJobQueue = new DelayBlockingQueue<SimpleJobDelay<JobClient>>(priorityQueue.getQueueSizeLimited());

        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory(this.getClass().getSimpleName() + "_" + jobResource + "_DelayJobProcessor"));
        executorService.submit(new RestartJobProcessor());

        this.jobSubmitConcurrentService = new ThreadPoolExecutor(jobSubmitConcurrent, jobSubmitConcurrent, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(true), new CustomThreadFactory(this.getClass().getSimpleName() + "_" + jobResource + "_JobSubmitConcurrent"), new BlockCallerPolicy());
    }

    private class RestartJobProcessor implements Runnable {
        @Override
        public void run() {
            while (true) {
                SimpleJobDelay<JobClient> simpleJobDelay = null;
                JobClient jobClient = null;
                try {
                    simpleJobDelay = delayJobQueue.take();
                    jobClient = simpleJobDelay.getJob();
                    if (jobClient != null) {
                        jobClient.setQueueSourceType(EQueueSourceType.DELAY.getCode());
                        queue.put(jobClient);
                        LOGGER.info("jobId:{} stage:{} take job from delayJobQueue queue size:{} and add to priorityQueue.", jobClient.getTaskId(), simpleJobDelay.getStage(), delayJobQueue.size());
                    }
                } catch (Exception e) {
                    if (simpleJobDelay != null && jobClient != null) {
                        LOGGER.error("jobId:{} stage:{}", jobClient.getTaskId(), simpleJobDelay.getStage(), e);
                    } else {
                        LOGGER.error("", e);
                    }
                }
            }
        }
    }

    public boolean tryPutRestartJob(JobClient jobClient) {
        boolean tryPut = delayJobQueue.tryPut(new SimpleJobDelay<>(jobClient, EJobCacheStage.RESTART.getStage(), Objects.isNull(jobClient.getRetryIntervalTime()) ? jobRestartDelay : jobClient.getRetryIntervalTime()));
        LOGGER.info("jobId:{} {} add job to restart delayJobQueue.", jobClient.getTaskId(), tryPut ? "success" : "failed");
        if (tryPut) {
            //restart的状态修改会在外面处理，这里只需要set stage
            engineJobCacheDao.updateStage(jobClient.getTaskId(), EJobCacheStage.RESTART.getStage(), localAddress, jobClient.getPriority(), null);
        }
        return tryPut;
    }

    private void putLackingJob(JobClient jobClient, JudgeResult judgeResult) {
        try {
            delayJobQueue.put(new SimpleJobDelay<>(jobClient, EJobCacheStage.LACKING.getStage(), jobLackingDelay));
            jobClient.lackingCountIncrement();
            engineJobCacheDao.updateStage(jobClient.getTaskId(), EJobCacheStage.LACKING.getStage(), localAddress, jobClient.getPriority(), judgeResult.getReason());
            jobClient.doStatusCallBack(RdosTaskStatus.LACKING.getStatus());
        } catch (InterruptedException e) {
            queue.put(jobClient);
            LOGGER.error("jobId:{} delayJobQueue.put failed.",jobClient.getTaskId(), e);
        }
        LOGGER.info("jobId:{} success add job to lacking delayJobQueue, job's lackingCount:{}.", jobClient.getTaskId(), jobClient.getLackingCount());
    }

    public int getDelayJobQueueSize() {
        return delayJobQueue.size();
    }

    @Override
    public void run() {
        while (true) {
            try {
                JobClient jobClient = queue.take();
                LOGGER.info("jobId:{} jobResource:{} queue size:{} take job from priorityQueue.", jobClient.getTaskId(), jobResource, queue.size());
                if (checkIsFinished(jobClient)) {
                    continue;
                }
                if (checkJobSubmitExpired(jobClient)){
                    shardCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.AUTOCANCELED.getStatus());
                    jobClient.doStatusCallBack(RdosTaskStatus.AUTOCANCELED.getStatus());
                    engineJobCacheDao.delete(jobClient.getTaskId());
                    LOGGER.info("jobId:{} checkJobSubmitExpired is true, job ignore to submit.", jobClient.getTaskId());
                    continue;
                }
                if (!checkMaxPriority(jobResource)) {
                    LOGGER.info("jobId:{} checkMaxPriority is false, wait other node job which priority higher.", jobClient.getTaskId());
                    queue.put(jobClient);
                    Thread.sleep(jobLackingInterval);
                    continue;
                }

                //提交任务
                jobSubmitConcurrentService.submit(()->{
                    submitJob(jobClient);
                });
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    private boolean checkIsFinished(JobClient jobClient) {
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobClient.getTaskId());
        try {
            if (null == jobClient.getQueueSourceType() || EQueueSourceType.NORMAL.getCode() == jobClient.getQueueSourceType()) {
                if (null == engineJobCache) {
                    shardCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.CANCELED.getStatus());
                    jobClient.doStatusCallBack(RdosTaskStatus.CANCELED.getStatus());
                    LOGGER.info("jobId:{} checkIsFinished is true, job is Finished.", jobClient.getTaskId());
                    return true;
                }
            } else {
                if (null == engineJobCache) {
                    //如果任务出现资源不足 一直deploy加大延时  界面杀死重跑立马完成之后 deployQueue数据未移除
                    //重新放入之后直接取消 导致状态更新waitEngine 状态不一致 所以需要判断下数据是否存在
                    LOGGER.info("jobId:{} stage:{} take job from delayJobQueue  but engine job cache has deleted", jobClient.getTaskId(), delayJobQueue.size());
                    return true;
                } else {
                    //如果任务存在 还需要判断cache表数据是否为重跑后插入生成的
                    boolean checkCanSubmit = true;
                    if (null != jobClient.getSubmitCacheTime()) {
                        long insertDbCacheTime = engineJobCache.getGmtCreate().getTime();
                        checkCanSubmit = insertDbCacheTime <= jobClient.getSubmitCacheTime();

                    }
                    if (checkCanSubmit) {
                        engineJobCacheDao.updateStage(jobClient.getTaskId(), EJobCacheStage.PRIORITY.getStage(), localAddress, jobClient.getPriority(), null);
                        jobClient.doStatusCallBack(RdosTaskStatus.WAITENGINE.getStatus());
                        return false;
                    } else {
                        //插入cache表的时间 比 jobClient 第一次提交时间晚 认为任务重新提交过 当前延时队列的jobClient 抛弃 不做任何处理
                        LOGGER.info("jobId:{} checkIsFinished is true checkCanSubmit is false jobClient cacheSubmitTime {} cacheDB SubmitTime {}, job is Finished.",
                                jobClient.getTaskId(), jobClient.getSubmitCacheTime(), engineJobCache.getGmtCreate().getTime());
                        return true;
                    }

                }
            }
        } finally {
            //重置状态
            jobClient.setQueueSourceType(EQueueSourceType.NORMAL.getCode());
            if (null != engineJobCache && null == jobClient.getSubmitCacheTime()) {
                LOGGER.info("jobId:{} set submitCacheTime is {},", jobClient.getTaskId(), engineJobCache.getGmtCreate().getTime());
                jobClient.setSubmitCacheTime(engineJobCache.getGmtCreate().getTime());
            }
        }
        return false;
    }

    private boolean checkJobSubmitExpired(JobClient jobClient) {
        long submitExpiredTime;
        if ((submitExpiredTime = jobClient.getSubmitExpiredTime()) > 0){
            return System.currentTimeMillis() - jobClient.getGenerateTime() > submitExpiredTime;
        } else if (jobSubmitExpired > 0) {
            return System.currentTimeMillis() - jobClient.getGenerateTime() > jobSubmitExpired;
        }
        return false;
    }

    private boolean checkMaxPriority(String jobResource) {
        //根据配置要求是否需要对job判断最高的优先级
        if (!checkJobMaxPriorityStrategy) {
            return true;
        }

        Map<String, GroupInfo> groupInfoMap = jobPartitioner.getGroupInfoByJobResource(jobResource);
        if (null == groupInfoMap) {
            return true;
        }
        String minPriorityAddress = null;
        long minPriority = Long.MAX_VALUE;
        long localPriority = Long.MAX_VALUE;
        for (Map.Entry<String, GroupInfo> groupInfoEntry : groupInfoMap.entrySet()) {
            String address = groupInfoEntry.getKey();
            GroupInfo groupInfo = groupInfoEntry.getValue();

            if (groupInfo.getPriority() > 0 && localAddress.equals(address)) {
                localPriority = groupInfo.getPriority();
            }

            //Priority值越低，优先级越高
            if (groupInfo.getPriority() > 0 && groupInfo.getPriority() < minPriority) {
                minPriorityAddress = address;
                minPriority = groupInfo.getPriority();
            }
        }
        // hashmap不排序，防止多节点下a、b相同priority逻辑死锁
        if (localAddress.equalsIgnoreCase(minPriorityAddress) || localPriority == minPriority) {
            return true;
        } else {
            return false;
        }
    }

    private void submitJob(JobClient jobClient) {

        JobResult jobResult = null;
        try {

            // 判断资源
            JudgeResult judgeResult = workerOperator.judgeSlots(jobClient);
            if (JudgeResult.JudgeType.OK == judgeResult.getResult()) {
                LOGGER.info("jobId:{} engineType:{} submit jobClient:{} to engine start.", jobClient.getTaskId(), jobClient.getEngineType(), jobClient);

                jobClient.doStatusCallBack(RdosTaskStatus.COMPUTING.getStatus());

                // 保存提交用户用户名
                saveSubmitUserName(jobClient);
                // 提交任务
                jobResult = workerOperator.submitJob(jobClient);

                LOGGER.info("jobId:{} engineType:{} submit jobResult:{}.", jobClient.getTaskId(), jobClient.getEngineType(), jobResult);
                // 提交任务返回新的任务ID
                String jobId = jobResult.getData(JobResult.JOB_ID_KEY);
                jobClient.setEngineTaskId(jobId);
                addToTaskListener(jobClient, jobResult);
                LOGGER.info("jobId:{} engineType:{} submit to engine end.", jobClient.getTaskId(), jobClient.getEngineType());
            } else if (JudgeResult.JudgeType.LIMIT_ERROR == judgeResult.getResult()) {
                LOGGER.info("jobId:{} engineType:{} submitJob happens system limitError:{}", jobClient.getTaskId(), jobClient.getEngineType(), judgeResult.getReason());
                jobClient.setEngineTaskId(null);
                jobResult = JobResult.createErrorResult(false, judgeResult.getReason());
                addToTaskListener(jobClient, jobResult);
            } else if (JudgeResult.JudgeType.EXCEPTION == judgeResult.getResult()) {
                LOGGER.info("jobId:{} engineType:{} judgeSlots result is exception {}", jobClient.getTaskId(), jobClient.getEngineType(), judgeResult.getReason());
                handlerFailedWithRetry(jobClient, true, new Exception(judgeResult.getReason()));
            } else {
                LOGGER.info("jobId:{} engineType:{} judgeSlots result is false.", jobClient.getTaskId(), jobClient.getEngineType());
                handlerNoResource(jobClient, judgeResult);
            }
        } catch (WorkerAccessException e) {
            LOGGER.info(" jobId:{} engineType:{} worker not find.", jobClient.getTaskId(), jobClient.getEngineType());
            handlerNoResource(jobClient, workerNotFindResult);
        } catch (ClientAccessException | ClientArgumentException e) {
            handlerFailedWithRetry(jobClient, false, e);
        } catch (Throwable e) {
            handlerFailedWithRetry(jobClient, true, e);
        }
    }

    /**
     * 报存提交的时候用户名
     * @param jobClient
     */
    private void saveSubmitUserName(JobClient jobClient) {
        try {
            JSONObject pluginInfo = JSONObject.parseObject(jobClient.getPluginInfo());
            if (null == pluginInfo || pluginInfo.isEmpty()) {
                return;
            }
            String ldapUserName = pluginInfo.getString(ConfigConstant.LDAP_USER_NAME);
            if (StringUtils.isNotBlank(ldapUserName)) {
                LOGGER.info("update jobId {} ldap userName {}", jobClient.getTaskId(), ldapUserName);
                ScheduleJob updateJob = new ScheduleJob();
                updateJob.setJobId(jobClient.getTaskId());
                updateJob.setSubmitUserName(ldapUserName);
                scheduleJobDao.update(updateJob);
            }
        } catch (Exception e) {
            LOGGER.info("update jobId {} ldap userName error", jobClient.getTaskId(), e);
        }
    }

    private void handlerFailedWithRetry(JobClient jobClient, boolean checkRetry, Throwable e) {
        LOGGER.error("jobId:{} engineType:{} submitJob happens system error:", jobClient.getTaskId(), jobClient.getEngineType(), e);
        jobClient.setEngineTaskId(null);
        addToTaskListener(jobClient, JobResult.createErrorResult(checkRetry, e));
    }

    private void handlerNoResource(JobClient jobClient, JudgeResult judgeResult) {
        //因为资源不足提交任务失败，优先级数值增加 WAIT_INTERVAL
        jobClient.setPriority(jobClient.getPriority() + jobPriorityStep);

        //delayQueue的任务比重过大时，直接放入优先级队列重试
        if (jobClient.lackingCountIncrement() > jobLackingCountLimited && delayJobQueue.size() < priorityQueue.getQueueSizeLimited()) {
            putLackingJob(jobClient, judgeResult);
        } else {
            engineJobCacheDao.updateStage(jobClient.getTaskId(), EJobCacheStage.PRIORITY.getStage(), localAddress, jobClient.getPriority(), null);
            queue.put(jobClient);
            SleepUtil.sleep(jobLackingInterval);
            LOGGER.info("jobId:{} unlimited_lackingCount:{} add to priorityQueue.", jobClient.getTaskId(), jobClient.getLackingCount());

        }
    }

    private void addToTaskListener(JobClient jobClient, JobResult jobResult) {
        jobClient.setJobResult(jobResult);
        //添加触发读取任务状态消息
        submittedQueue.offer(jobClient);
    }

    public static LinkedBlockingQueue<JobClient> getSubmittedQueue() {
        return submittedQueue;
    }
}


