package com.dtstack.engine.master.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.DependencyType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.impl.*;
import com.dtstack.engine.master.scheduler.parser.ESchedulePeriodType;
import com.dtstack.engine.master.scheduler.parser.ScheduleCron;
import com.dtstack.engine.master.scheduler.parser.ScheduleFactory;
import com.dtstack.engine.master.utils.JobGraphUtils;
import com.dtstack.schedule.common.enums.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.NumericNode;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 1. 变为Master节点时会主动触发一次是否构建jobgraph的判断
 * 2. 定时任务调度时触发
 * <p>
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/30
 */
@Component
public class JobGraphBuilder {

    private static final Logger logger = LoggerFactory.getLogger(JobGraphBuilder.class);

    /**
     * 系统调度的时候插入的默认batch_job名称
     */
    private static final String CRON_JOB_NAME = "cronJob";
    private static final String FILL_DATA_TYPE = "fillData";
    private static final String CRON_TRIGGER_TYPE = "cronTrigger";

    public static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EScheduleJobType.WORK_FLOW.getVal(), EScheduleJobType.ALGORITHM_LAB.getVal());

    private static final int TASK_BATCH_SIZE = 50;
    private static final int JOB_BATCH_SIZE = 50;
    private static final int MAX_TASK_BUILD_THREAD = 20;

    @Autowired
    private ScheduleTaskShadeService batchTaskShadeService;

    @Autowired
    private ScheduleJobService batchJobService;

    @Autowired
    private ScheduleTaskTaskShadeService taskTaskShadeService;

    @Autowired
    private JobGraphTriggerService jobGraphTriggerService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private EnvironmentContext environmentContext;

    private Lock lock = new ReentrantLock();

    private volatile boolean isBuildError = false;

    /**
     * 1：如果当前节点是master-->每天晚上10点预先生成第二天的任务依赖;
     * 2：如果初始化master节点-->获取当天的jobgraph为null-->生成
     * 可能多线程调用
     *
     * @param triggerDay yyyy-MM-dd
     * @return
     */
    public void buildTaskJobGraph(String triggerDay) {

        lock.lock();

        try {
            isBuildError = false;
            //检查是否已经生成过
            String triggerTimeStr = triggerDay + " 00:00:00";
            Timestamp triggerTime = Timestamp.valueOf(triggerTimeStr);

            boolean hasBuild = jobGraphTriggerService.checkHasBuildJobGraph(triggerTime);
            if (hasBuild) {
                return;
            }
            //清理周期实例脏数据
            cleanDirtyJobGraph(triggerDay);

            int totalTask = batchTaskShadeService.countTaskByStatus(ESubmitStatus.SUBMIT.getStatus(), EProjectScheduleStatus.NORMAL.getStatus());
            logger.info("Counting task which status=SUBMIT scheduleStatus=NORMAL totalTask:{}", totalTask);

            if (totalTask <= 0) {
                return;
            }

            ExecutorService jobGraphBuildPool = new ThreadPoolExecutor(MAX_TASK_BUILD_THREAD, MAX_TASK_BUILD_THREAD, 10L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(MAX_TASK_BUILD_THREAD), new CustomThreadFactory("JobGraphBuilder"));

            List<ScheduleBatchJob> allJobs = new ArrayList<>(totalTask);
            Map<String, String> flowJobId = new ConcurrentHashMap<>(totalTask);
            //限制 thread 并发
            int totalBatch = totalTask / TASK_BATCH_SIZE;
            if (totalTask % TASK_BATCH_SIZE != 0) {
                totalBatch++;
            }
            Semaphore buildSemaphore = new Semaphore(MAX_TASK_BUILD_THREAD);
            CountDownLatch ctl = new CountDownLatch(totalBatch);
            long startId = 0L;
            int i = 0;

            while (true) {
                final int batchIdx = ++i;
                if (batchIdx > totalBatch) {
                    break;
                }
                final List<ScheduleTaskShade> batchTaskShades = batchTaskShadeService.listTaskByStatus(startId, ESubmitStatus.SUBMIT.getStatus(), EProjectScheduleStatus.NORMAL.getStatus(), TASK_BATCH_SIZE);
                if (batchTaskShades.isEmpty()) {
                    break;
                }

                startId = batchTaskShades.get(batchTaskShades.size() - 1).getId();
                logger.info("batch-number:{} startId:{}", batchIdx, startId);

                try {
                    buildSemaphore.acquire();
                    jobGraphBuildPool.execute(() -> {
                        try {
                            for (ScheduleTaskShade task : batchTaskShades) {
                                try {
                                    List<ScheduleBatchJob> jobRunBeans = RetryUtil.executeWithRetry(() -> {
                                        String cronJobName = CRON_JOB_NAME + "_" + task.getName();
                                        return buildJobRunBean(task, CRON_TRIGGER_TYPE, EScheduleType.NORMAL_SCHEDULE,
                                                true, true, triggerDay, cronJobName, null, task.getProjectId(), task.getTenantId());
                                    }, environmentContext.getBuildJobErrorRetry(), 200, false);
                                    synchronized (allJobs) {
                                        allJobs.addAll(jobRunBeans);
                                    }

                                    if (SPECIAL_TASK_TYPES.contains(task.getTaskType())) {
                                        for (ScheduleBatchJob jobRunBean : jobRunBeans) {
                                            flowJobId.put(JobGraphUtils.buildFlowReplaceId(task.getTaskId(), jobRunBean.getCycTime(), task.getAppType()), jobRunBean.getJobId());
                                        }
                                    }
                                } catch (Throwable e) {
                                    logger.error("build task failure taskId:{} apptype:{}",task.getTaskId(),task.getAppType(), e);
                                }
                            }
                            logger.info("batch-number:{} done!!! allJobs size:{}", batchIdx, allJobs.size());
                        } catch (Throwable e) {
                            logger.error("!!! buildTaskJobGraph  build job error !!!", e);
                        } finally {
                            buildSemaphore.release();
                            ctl.countDown();
                        }
                    });
                } catch (Throwable e) {
                    logger.error("[acquire pool error]:", e);
                    isBuildError = true;
                    throw new RdosDefineException(e);
                }
            }
            ctl.await();
            if (isBuildError) {
                logger.info("buildTaskJobGraph happend error jobSize {}", allJobs.size());
                return;
            }
            logger.info("buildTaskJobGraph all done!!! allJobs size:{}", allJobs.size());
            jobGraphBuildPool.shutdown();

            JobGraphUtils.doSetFlowJobIdForSubTasks(allJobs, flowJobId);

            allJobs.sort((ebj1, ebj2) -> {
                Long date1 = Long.valueOf(ebj1.getCycTime());
                Long date2 = Long.valueOf(ebj2.getCycTime());
                if (date1 < date2) {
                    return -1;
                } else if (date1 > date2) {
                    return 1;
                }
                return 0;
            });

            //存储生成的jobRunBean
            saveJobGraph(allJobs, triggerDay);
        } catch (Exception e) {
            logger.error("buildTaskJobGraph ！！！", e);
        } finally {
            lock.unlock();
        }
    }




    /**
     * 清理周期实例脏数据
     * @param triggerDay
     */
    private void cleanDirtyJobGraph(String triggerDay) {
        String preCycTime = DateUtil.getTimeStrWithoutSymbol(triggerDay);
        int totalJob = batchJobService.countByCyctimeAndJobName(preCycTime, CRON_JOB_NAME, EScheduleType.NORMAL_SCHEDULE.getType());
        if (totalJob <= 0) {
            return;
        }
        logger.info("Start cleaning dirty cron job graph,  totalJob:{}", totalJob);

        int totalBatch;
        if (totalJob % JOB_BATCH_SIZE != 0) {
            totalBatch = totalJob / JOB_BATCH_SIZE + 1;
        } else {
            totalBatch = totalJob / JOB_BATCH_SIZE;
        }
        long startId = 0L;
        int i = 0;

        while (true) {
            final int batchIdx = ++i;
            if (batchIdx > totalBatch) {
                break;
            }
            final List<ScheduleJob> scheduleJobList = batchJobService.listByCyctimeAndJobName(startId, preCycTime,
                    CRON_JOB_NAME, EScheduleType.NORMAL_SCHEDULE.getType(), JOB_BATCH_SIZE);
            if (scheduleJobList.isEmpty()) {
                break;
            }
            logger.info("Start clean batchJobList, batch-number:{} startId:{}", batchIdx, startId);
            startId = scheduleJobList.get(scheduleJobList.size() - 1).getId();
            List<String> jobKeyList = new ArrayList<>();
            for ( ScheduleJob scheduleJob : scheduleJobList) {
                jobKeyList.add(scheduleJob.getJobKey());
            }
            batchJobService.deleteJobsByJobKey(jobKeyList);
            logger.info("batch-number:{} done! Cleaning dirty jobs size:{}", batchIdx, scheduleJobList.size());
        }
    }



    /**
     * 保存生成的jobGraph记录
     *
     * @param jobList
     * @param triggerDay
     * @return
     */
    @Transactional
    public boolean saveJobGraph(List<ScheduleBatchJob> jobList, String triggerDay) {
        logger.info("start saveJobGraph to db {} jobSize {}", triggerDay, jobList.size());
        //需要保存BatchJob, BatchJobJob
        batchJobService.insertJobList(jobList, EScheduleType.NORMAL_SCHEDULE.getType());

        //记录当天job已经生成
        String triggerTimeStr = triggerDay + " 00:00:00";
        Timestamp timestamp = Timestamp.valueOf(triggerTimeStr);
        try {
            RetryUtil.executeWithRetry(() -> {
                jobGraphTriggerService.addJobTrigger(timestamp);
                return null;
            }, environmentContext.getBuildJobErrorRetry(), 200, false);
        } catch (Exception e) {
            logger.error("addJobTrigger triggerTimeStr {} error ", triggerTimeStr,e);
            throw new RdosDefineException(e);
        }

        return true;
    }



    public List<ScheduleBatchJob> buildJobRunBean(ScheduleTaskShade task, String keyPreStr, EScheduleType scheduleType,
                                                  boolean needAddFather, boolean needSelfDependency, String triggerDay,
                                                  String jobName, Long createUserId, Long projectId, Long tenantId) throws Exception {
        return buildJobRunBean(task, keyPreStr, scheduleType, needAddFather, needSelfDependency, triggerDay,
                jobName, createUserId, null, null, projectId, tenantId);
    }

    /**
     * 根据 task 生成需要执行的job信息
     *
     * @param task
     * @param keyPreStr          生成jobkey的前缀
     * @param scheduleType       正常调度/补数据
     * @param needAddFather      是否需要处理父任务依赖 -- 补数据第一个层级不需要
     * @param needSelfDependency 是否需要处理自依赖
     * @param triggerDay
     * @param jobName
     * @param projectId
     * @param tenantId
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public List<ScheduleBatchJob> buildJobRunBean(ScheduleTaskShade task, String keyPreStr, EScheduleType scheduleType, boolean needAddFather,
                                                  boolean needSelfDependency, String triggerDay, String jobName, Long createUserId,
                                                  String beginTime, String endTime, Long projectId, Long tenantId) throws Exception {

        String scheduleStr = task.getScheduleConf();
        ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleStr);
        List<ScheduleBatchJob> jobList = Lists.newArrayList();
        Timestamp timestampNow = Timestamp.valueOf(LocalDateTime.now());
        Timestamp jobBuildTime = Timestamp.valueOf(triggerDay + " 00:00:00");
        boolean isFirst = true;

        //正常调度生成job需要判断--任务有效时间范围
        if (scheduleType.equals(EScheduleType.NORMAL_SCHEDULE) &&
                (scheduleCron.getBeginDate().after(jobBuildTime) || scheduleCron.getEndDate().before(jobBuildTime))) {
            logger.error("appType {} task {} out of normal schedule time " ,task.getTaskId(), task.getAppType());
            return jobList;
        }

        List<String> triggerDayList = scheduleCron.getTriggerTime(triggerDay);

        // 处理分钟粒度任务
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            JobGraphUtils.dealConcreteTime(triggerDayList, triggerDay, beginTime, endTime);
            scheduleCron.setSelfReliance(DependencyType.NO_SELF_DEPENDENCY.getType());
        }

        //工作流任务
        ScheduleTaskShade flowTaskShade = null;
        if (task.getFlowId() > 0) {
            flowTaskShade = batchTaskShadeService.getBatchTaskById(task.getFlowId(), task.getAppType());
        }
        List<ScheduleTaskTaskShade> taskTasks = taskTaskShadeService.getAllParentTask(task.getTaskId(),task.getAppType());
        //是否有父任务
        boolean isHasParentTask = CollectionUtils.isNotEmpty(taskTasks);
        List<Long> parentTaskIds = taskTasks.stream().filter(t -> t.getParentTaskId() !=-1).map(ScheduleTaskTaskShade::getParentTaskId).collect(Collectors.toList());
        List<ScheduleTaskShade> parentTasks = batchTaskShadeService.getTaskByIds(parentTaskIds,task.getAppType());

        for (int idx = 0; idx < triggerDayList.size(); idx++) {
            String triggerTime = triggerDayList.get(idx);
            String nextTriggerTime = buildNextCycTime(scheduleCron, jobBuildTime, triggerDayList, idx);
            triggerTime = DateUtil.getTimeStrWithoutSymbol(triggerTime);
            String jobKey = JobGraphUtils.generateJobKey(keyPreStr, task.getId(), triggerTime);
            String targetJobName = jobName;
            if (scheduleType.equals(EScheduleType.NORMAL_SCHEDULE)) {
                targetJobName = targetJobName + "_" + triggerTime;
            } else if (scheduleType.equals(EScheduleType.FILL_DATA)) {
                //补数据的名称和后缀用‘-’分割开-->在查询的时候会用到
                targetJobName = targetJobName + "-" + task.getName() + "-" + triggerTime;
            }
            //构建scheduleJob基本属性
            ScheduleJob scheduleJob = createScheduleJob(task, scheduleType, createUserId, projectId, tenantId, scheduleCron, timestampNow, triggerTime, nextTriggerTime, jobKey, targetJobName);
            ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJob);
            //构建flowJobId
            buildFlowJobId(task, triggerDay, scheduleCron, flowTaskShade, triggerTime, scheduleJob);


            //执行时间 -1 天
            String businessDate = JobGraphUtils.generateBizDateFromCycTime(triggerTime);
            scheduleJob.setBusinessDate(businessDate);

            //任务流中的子任务，起始节点将任务流节点作为父任务加入
            if (task.getFlowId() > 0 && !isHasParentTask) {
                List<String> keys = getJobKeys(Lists.newArrayList(flowTaskShade), scheduleJob, scheduleCron, keyPreStr);
                scheduleBatchJob.addBatchJobJob(createNewJobJob(scheduleJob, jobKey, keys.get(0), timestampNow));
            }

            //获取依赖的父task 的 jobKey
            if (needAddFather) {
                List<String> fatherDependency = getDependencyJobKeys(scheduleType, scheduleJob, scheduleCron, keyPreStr, parentTasks);
                for (String dependencyJobKey : fatherDependency) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("get Job {} Job key  {} cron {} cycTime {}", jobKey, dependencyJobKey, JSONObject.toJSONString(scheduleCron), scheduleJob.getCycTime());
                    }
                    scheduleBatchJob.addBatchJobJob(createNewJobJob(scheduleJob, jobKey, dependencyJobKey, timestampNow));
                }
            }

            if (needSelfDependency) {
                dealSelfDependency(scheduleCron.getSelfReliance(), scheduleJob, scheduleCron, isFirst, scheduleBatchJob, keyPreStr, scheduleType, jobKey, timestampNow, task);
            }

            jobList.add(scheduleBatchJob);
            isFirst = false;
        }

        return jobList;
    }

    /**
     * 填充周期实例对象的基本属性
     *
     * @param task
     * @param scheduleType
     * @param createUserId
     * @param projectId
     * @param tenantId
     * @param scheduleCron
     * @param timestampNow
     * @param triggerTime
     * @param nextTriggerTime
     * @param jobKey
     * @param targetJobName
     * @return
     */
    private ScheduleJob createScheduleJob(ScheduleTaskShade task, EScheduleType scheduleType, Long createUserId, Long projectId, Long tenantId, ScheduleCron scheduleCron, Timestamp timestampNow, String triggerTime, String nextTriggerTime, String jobKey, String targetJobName) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(actionService.generateUniqueSign());
        scheduleJob.setJobKey(jobKey);
        scheduleJob.setJobName(targetJobName);
        scheduleJob.setPeriodType(scheduleCron.getPeriodType());
        scheduleJob.setTaskId(task.getTaskId());


        scheduleJob.setGmtCreate(timestampNow);
        scheduleJob.setGmtModified(timestampNow);
        if (createUserId == null) {
            scheduleJob.setCreateUserId(task.getCreateUserId());
        } else {
            scheduleJob.setCreateUserId(createUserId);
        }
        //针对跨项目补数据，项目id需要随机应变
        scheduleJob.setTenantId(tenantId);
        scheduleJob.setProjectId(projectId);
        scheduleJob.setDtuicTenantId(task.getDtuicTenantId());
        scheduleJob.setAppType(task.getAppType());

        scheduleJob.setType(scheduleType.getType());
        scheduleJob.setCycTime(triggerTime);
        scheduleJob.setIsRestart(Restarted.NORMAL.getStatus());

        scheduleJob.setDependencyType(scheduleCron.getSelfReliance());

        scheduleJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
        scheduleJob.setTaskType(task.getTaskType());
        scheduleJob.setMaxRetryNum(scheduleCron.getMaxRetryNum());
        scheduleJob.setVersionId(task.getVersionId());
        scheduleJob.setNextCycTime(nextTriggerTime);
        return scheduleJob;
    }

    /**
     * 工作流子任务的flowJobId占位符
     *
     * @param task
     * @param triggerDay
     * @param scheduleCron
     * @param flowTaskShade
     * @param triggerTime
     * @param scheduleJob
     */
    private void buildFlowJobId(ScheduleTaskShade task, String triggerDay, ScheduleCron scheduleCron, ScheduleTaskShade flowTaskShade, String triggerTime, ScheduleJob scheduleJob) {
        //普通任务
        if (task.getFlowId() == 0 || null == flowTaskShade) {
            scheduleJob.setFlowJobId(JobGraphUtils.NORMAL_TASK_FLOW_ID);
            return;
        }
        String flowJobTime = triggerTime;
        //非 小时&分钟 任务
        if (scheduleCron.getPeriodType() != ESchedulePeriodType.MIN.getVal() && scheduleCron.getPeriodType() != ESchedulePeriodType.HOUR.getVal()) {
            List<String> cycTime = getFlowWorkCycTime(task.getFlowId(), triggerDay, task.getAppType());
            //其他类型的任务每天只会生成一个实例
            if (CollectionUtils.isNotEmpty(cycTime)) {
                flowJobTime = DateUtil.getTimeStrWithoutSymbol(cycTime.get(0));
            }
        }
        scheduleJob.setFlowJobId(JobGraphUtils.buildFlowReplaceId(flowTaskShade.getTaskId(), flowJobTime, flowTaskShade.getAppType()));
    }

    /**
     * 最近的下一次调度的时间
     *
     * @param scheduleCron
     * @param jobBuildTime
     * @param triggerDayList
     * @param idx
     * @return
     * @throws ParseException
     */
    private String buildNextCycTime(ScheduleCron scheduleCron, Timestamp jobBuildTime, List<String> triggerDayList, int idx) throws ParseException {
        String nextTriggerTime = "";
        if ((scheduleCron.getPeriodType() == ESchedulePeriodType.MIN.getVal() || scheduleCron.getPeriodType() == ESchedulePeriodType.HOUR.getVal())) {
            if ((idx < triggerDayList.size() - 1)) {
                //不是当前最后一个
                nextTriggerTime = triggerDayList.get(idx + 1);
            } else {
                DateTime nextDayExecute = new DateTime(jobBuildTime.getTime()).plusDays(1);
                //当前最后一个
                List<String> nextTriggerDays = scheduleCron.getTriggerTime(nextDayExecute.toString("yyyy-MM-dd"));
                if (CollectionUtils.isNotEmpty(nextTriggerDays)) {
                    nextTriggerTime = nextTriggerDays.get(0);
                }
            }

        }
        return nextTriggerTime;
    }


    private List<String> getFlowWorkCycTime(Long flowId, String triggerDay, Integer appType) {
        ScheduleTaskShade flowWork = batchTaskShadeService.getBatchTaskById(flowId, appType);
        List<String> triggerTime = Lists.newArrayList();
        if (flowWork != null) {
            try {
                String scheduleStr = flowWork.getScheduleConf();
                ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleStr);
                triggerTime = scheduleCron.getTriggerTime(triggerDay);
            } catch (Exception e) {
                logger.error("getFlowWorkCycTime error with flowId: " + flowId, e);
            }
        }
        return triggerTime;
    }

    /**
     * 第一次执行的时候是没有上一个周期的----所以如果查询不到上一个周期的话则不设置依赖
     * 如果是自依赖的话获取上一个周期的jobKey
     *
     * @param selfReliance
     * @param scheduleJob
     * @param scheduleCron
     * @param isFirst
     * @param scheduleBatchJob
     * @param keyPreStr 生成jobKey的前缀
     * @param scheduleType
     * @param jobKey
     * @param timestampNow
     * @throws ParseException
     */
    private void dealSelfDependency(Integer selfReliance, ScheduleJob scheduleJob, ScheduleCron scheduleCron, boolean isFirst,
                                    ScheduleBatchJob scheduleBatchJob, String keyPreStr, EScheduleType scheduleType, String jobKey,
                                    Timestamp timestampNow,ScheduleTask task) {
        if (!DependencyType.SELF_DEPENDENCY_SUCCESS.getType().equals(selfReliance)
                && !DependencyType.SELF_DEPENDENCY_END.getType().equals(selfReliance)
                && !DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_SUCCESS.getType().equals(selfReliance)
                && !DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_END.getType().equals(selfReliance)) {
            return;
        }

        String preSelfJobKey = getSelfDependencyJobKeys(scheduleJob, scheduleCron, keyPreStr,task.getId());
        if (preSelfJobKey != null) {
            if (isFirst) {//需要查库判断是否存在
                ScheduleJob dbScheduleJob = batchJobService.getJobByJobKeyAndType(preSelfJobKey, scheduleType.getType());
                if (dbScheduleJob != null) {
                    scheduleBatchJob.addBatchJobJob(createNewJobJob(scheduleJob, jobKey, preSelfJobKey, timestampNow));
                }
            } else {
                scheduleBatchJob.addBatchJobJob(createNewJobJob(scheduleJob, jobKey, preSelfJobKey, timestampNow));
            }
        }
    }


    public ScheduleJobJob createNewJobJob(ScheduleJob scheduleJob, String jobKey, String parentKey, Timestamp timestamp) {
        ScheduleJobJob jobJobJob = new ScheduleJobJob();
        jobJobJob.setTenantId(scheduleJob.getTenantId());
        jobJobJob.setProjectId(scheduleJob.getProjectId());
        jobJobJob.setDtuicTenantId(scheduleJob.getDtuicTenantId());
        jobJobJob.setAppType(scheduleJob.getAppType());
        jobJobJob.setJobKey(jobKey);
        jobJobJob.setParentJobKey(parentKey);
        jobJobJob.setGmtModified(timestamp);
        jobJobJob.setGmtCreate(timestamp);
        return jobJobJob;
    }


    /**
     * 如果当前任务的任务周期是天---依赖的任务周期也是天---则获取当天的父任务key
     * 其他情况:获取依赖的父任务中小于等于子任务的触发时间点
     *
     * @param scheduleType
     * @param scheduleJob
     * @param scheduleCron
     * @return
     */
    public List<String> getDependencyJobKeys(EScheduleType scheduleType, ScheduleJob scheduleJob, ScheduleCron scheduleCron, String keyPreStr,List<ScheduleTaskShade> pTaskList) {
        //所有父任务的jobKey
        //return getJobKeys(pIdList, batchJob, scheduleCron, keyPreStr);
        //补数据运行时，需要所有周期实例立即运行
        if (EScheduleType.FILL_DATA.getType() == scheduleType.getType()) {
            return getJobKeys(pTaskList, scheduleJob, scheduleCron, keyPreStr);
        }
        //假设2019年7月10号创建一个月调度周期实例，每月20日执行，子任务是天任务。这时，7月20日之前，父任务从未生成过实例，子任务都不能调度执行。
        return getExternalJobKeys(pTaskList, scheduleJob, scheduleCron, keyPreStr);

    }


    /**
     * <p>
     * 获取任务外部父任务依赖，排除由于人为原因(创建时间、系统问题)导致的被依赖任务没生成的情况，比如
     * 日任务依赖周任务，日任务依赖月任务，周任务依赖月任务，但所被依赖的父任务没有生成graph，导致当前任务不能执行或者执行失败。
     * </p>
     *
     * @param pTaskList
     * @param scheduleJob
     * @param scheduleCron
     * @param keyPreStr
     * @return
     */
    private List<String> getExternalJobKeys(List<ScheduleTaskShade> pTaskList,ScheduleJob scheduleJob, ScheduleCron scheduleCron, String keyPreStr) {
        List<String> jobKeyList = Lists.newArrayList();
        DateTime jobCycTime = new DateTime(DateUtil.getTimestamp(scheduleJob.getCycTime(), JobGraphUtils.dtfFormatString));
        for (ScheduleTaskShade pTask : pTaskList) {
            try {
                ScheduleCron pScheduleCron = ScheduleFactory.parseFromJson(pTask.getScheduleConf());
                //执行时间
                String fatherLastJobCycTime = JobGraphUtils.getFatherLastJobBusinessDate(scheduleJob, pScheduleCron, scheduleCron);
                String pjobKey = JobGraphUtils.generateJobKey(keyPreStr, pTask.getId(), fatherLastJobCycTime);
                // BatchJob (cycTime 20191211000000 businessDate 20191210000000)  fatherLastJobCycTime 20191211000000
                //判断的时候需要拿执行时间判断

                DateTime fatherCycTime = new DateTime(DateUtil.getTimestamp(fatherLastJobCycTime, JobGraphUtils.dtfFormatString));
                //如果父任务在当前任务业务日期不同，则查询父任务是有已生成
                if (fatherCycTime.getDayOfYear() != jobCycTime.getDayOfYear()) {
                    //判断父任务是否生成
                    ScheduleJob pScheduleJob = batchJobService.getJobByJobKeyAndType(pjobKey, EScheduleType.NORMAL_SCHEDULE.getType());
                    if (pScheduleJob == null) {
                        logger.error("getExternalJobKeys ,but not found the parent job of " + pTask.getTaskId()
                                + " ,current job is " + scheduleJob.getJobId() + ", the pjobKey = " + pjobKey);
                        continue;
                    }
                }

                // 比如当天是2019.04.08,所依赖的父任务是月任务，每月20号执行，那找到的最近的父任务即为2019.03.20，但该父任务执行时间已过。
                jobKeyList.add(pjobKey);
            } catch (Exception e) {
                logger.error("getExternalJobKeys parse task" + pTask.getId() + " error", e);
            }
        }
        return jobKeyList;
    }

    private List<String> getJobKeys(List<ScheduleTaskShade> pTaskList, ScheduleJob scheduleJob, ScheduleCron scheduleCron, String keyPreStr) {
        List<String> jobKeyList = Lists.newArrayList();
        for (ScheduleTaskShade pTask : pTaskList) {
            try {
                ScheduleCron pScheduleCron = ScheduleFactory.parseFromJson(pTask.getScheduleConf());
                String pBusinessDate = JobGraphUtils.getFatherLastJobBusinessDate(scheduleJob, pScheduleCron, scheduleCron);
                String pjobKey = JobGraphUtils.generateJobKey(keyPreStr, pTask.getId(), pBusinessDate);
                jobKeyList.add(pjobKey);
            } catch (Exception e) {
                //FIXME 如果解析失败该任务是加入到队列里面还是提示直接不管该task
                logger.error("parse task" + pTask.getId() + " error", e);
            }
        }
        return jobKeyList;
    }


    public String getSelfDependencyJobKeys(ScheduleJob scheduleJob, ScheduleCron cron, String keyPreStr,Long scheduleTaskShadeId) {
        String preTriggerDateStr = JobGraphUtils.getPrePeriodJobTriggerDateStr(scheduleJob.getCycTime(), cron);
        //源逻辑是拿batchJob的taskId 作为key
        //现在task中 taskId + appType 才是唯一
        //现在采用taskShade表的id
//        ScheduleTaskShade shade = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
        if (Objects.nonNull(scheduleTaskShadeId)) {
            return JobGraphUtils.generateJobKey(keyPreStr, scheduleTaskShadeId, preTriggerDateStr);
        }
        return null;
    }


    public Map<String, ScheduleBatchJob> buildFillDataJobGraph(ArrayNode jsonObject, String fillJobName, boolean needFather,
                                                               String triggerDay, Long createUserId,
                                                               String beginTime, String endTime, Long projectId, Long tenantId, Boolean isRoot,Integer appType,Long fillId,Long dtuicTenantId) throws Exception {
        Map<String, ScheduleBatchJob> result = new HashMap<>();
        if (jsonObject != null && jsonObject.size() > 0) {
            for (JsonNode jsonNode : jsonObject) {
                Map<String, ScheduleBatchJob> stringScheduleBatchJobMap = buildFillDataJobGraph(jsonNode, fillJobName, needFather, triggerDay, createUserId, beginTime, endTime, projectId, tenantId, isRoot,appType,fillId,dtuicTenantId);
                result.putAll(stringScheduleBatchJobMap);
            }
        }
        return result;
    }


    public Map<String, ScheduleBatchJob> buildFillDataJobGraph(ArrayNode jsonObject, String fillJobName, boolean needFather,
                                                               String triggerDay, Long createUserId, Long projectId, Long tenantId, Boolean isRoot,Integer appType,Long fillId,Long dtuicTenantId) throws Exception {
        Map<String, ScheduleBatchJob> result = new HashMap<>();
        if (jsonObject != null && jsonObject.size() > 0) {
            for (JsonNode jsonNode : jsonObject) {
                Map<String, ScheduleBatchJob> stringScheduleBatchJobMap = buildFillDataJobGraph(jsonNode, fillJobName, needFather, triggerDay, createUserId, null, null, projectId, tenantId, isRoot,appType,fillId,dtuicTenantId);
                result.putAll(stringScheduleBatchJobMap);
            }
        }
        return result;
    }

    /**
     * 构建补数据的jobGraph
     *
     * @param jsonObject
     * @param triggerDay yyyy-MM-dd
     * @param projectId
     * @param tenantId
     * @param isRoot
     * @param fillId 补数据id
     * @return
     */
    public Map<String, ScheduleBatchJob> buildFillDataJobGraph(JsonNode jsonObject, String fillJobName, boolean needFather,
                                                               String triggerDay, Long createUserId,
                                                               String beginTime, String endTime, Long projectId, Long tenantId, Boolean isRoot,
                                                               @Param("appType") Integer appType,Long fillId,Long dtuicTenantId) throws Exception {

        if (!jsonObject.has("task")) {
            throw new RdosDefineException("can't get task field from jsonObject:" + jsonObject.toString(), ErrorCode.SERVER_EXCEPTION);
        }

        NumericNode fatherNode = (NumericNode) jsonObject.get("task");
        //生成jobList
        ScheduleTaskShade batchTask = batchTaskShadeService.getBatchTaskById(fatherNode.asLong(), appType);
        if (batchTask == null || batchTask.getIsDeleted().equals(Deleted.DELETED.getStatus())) {
            return null;
        }

        String preStr = FILL_DATA_TYPE + "_" + fillJobName;
        Map<String, ScheduleBatchJob> result = Maps.newLinkedHashMap();
        Map<String, String> flowJobId = Maps.newHashMap();
        List<ScheduleBatchJob> batchJobs;
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            batchJobs = buildJobRunBean(batchTask, preStr, EScheduleType.FILL_DATA, needFather,
                    true, triggerDay, fillJobName, createUserId, beginTime, endTime, projectId, tenantId);
        } else {
            batchJobs = buildJobRunBean(batchTask, preStr, EScheduleType.FILL_DATA, needFather,
                    true, triggerDay, fillJobName, createUserId, projectId, tenantId);
        }
        //针对专门补工作流子节点
        JobGraphUtils.doSetFlowJobIdForSubTasks(batchJobs, flowJobId);
        //工作流情况的处理
        if (batchTask.getTaskType().intValue() == EScheduleJobType.WORK_FLOW.getVal() ||
                batchTask.getTaskType().intValue() == EScheduleJobType.ALGORITHM_LAB.getVal()) {
            for (ScheduleBatchJob jobRunBean : batchJobs) {
                flowJobId.put(batchTask.getTaskId() + "_" + jobRunBean.getCycTime() + "_" + batchTask.getAppType(), jobRunBean.getJobId());
            }
            //将工作流下的子任务生成补数据任务实例
            List<ScheduleBatchJob> subTaskJobs = buildSubTasksJobForFlowWork(batchTask.getTaskId(), preStr, fillJobName, triggerDay, createUserId, beginTime, endTime, projectId, tenantId, appType);
            logger.info("buildFillDataJobGraph for flowTask with flowJobId map [{}]", flowJobId);
            JobGraphUtils.doSetFlowJobIdForSubTasks(subTaskJobs, flowJobId);
            batchJobs.addAll(subTaskJobs);
        }
        for (ScheduleBatchJob batchJob : batchJobs) {
            if (batchJob.getScheduleJob() != null) {
                batchJob.getScheduleJob().setFillId(fillId);
                batchJob.getScheduleJob().setDtuicTenantId(dtuicTenantId);
            }
            if(CollectionUtils.isNotEmpty(batchJob.getBatchJobJobList())){
                for (ScheduleJobJob scheduleJobJob : batchJob.getBatchJobJobList()) {
                    scheduleJobJob.setDtuicTenantId(dtuicTenantId);
                    scheduleJobJob.setAppType(appType);
                }
            }

            result.put(batchJob.getJobKey(), batchJob);
        }

        if (jsonObject.has("children")) {
            ArrayNode arrayNode = (ArrayNode) jsonObject.get("children");
            for (JsonNode node : arrayNode) {
                Map<String, ScheduleBatchJob> childNodeMap = buildFillDataJobGraph(node, fillJobName, true, triggerDay, createUserId, beginTime, endTime, projectId, tenantId, true,appType,fillId,dtuicTenantId);
                if (childNodeMap != null) {
                    result.putAll(childNodeMap);
                }
            }
        }

        return result;
    }

    // 虽然没用到，预留具体时间缺省时补工作流数据
    private List<ScheduleBatchJob> buildSubTasksJobForFlowWork(Long taskId, String preStr, String fillJobName, String triggerDay, Long createUserId, Long projectId, Long tenantId, Integer appType) throws Exception {
        return buildSubTasksJobForFlowWork(taskId, preStr, fillJobName, triggerDay, createUserId, null, null, projectId, tenantId, appType);
    }

    private List<ScheduleBatchJob> buildSubTasksJobForFlowWork(Long taskId, String preStr, String fillJobName,
                                                               String triggerDay, Long createUserId,
                                                               String beginTime, String endTime, Long projectId, Long tenantId, Integer appType) throws Exception {
        List<ScheduleBatchJob> result = Lists.newArrayList();
        //获取全部子任务
        List<ScheduleTaskShade> subTasks = batchTaskShadeService.getFlowWorkSubTasks(taskId, appType,null,null);
        for (ScheduleTaskShade taskShade : subTasks) {
            String subKeyPreStr = preStr;
            String subFillJobName = fillJobName;
            //子任务需添加依赖关系
            List<ScheduleBatchJob> batchJobs = buildJobRunBean(taskShade, subKeyPreStr, EScheduleType.FILL_DATA, true, true,
                    triggerDay, subFillJobName, createUserId, beginTime, endTime, projectId, tenantId);
            result.addAll(batchJobs);
        }

        return result;
    }

}
