package com.dtstack.engine.master.scheduler;

import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.util.DateUtil;
import com.dtstack.dtcenter.common.util.MathUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.domain.BatchEngineJob;
import com.dtstack.engine.domain.BatchJob;
import com.dtstack.engine.domain.BatchJobJob;
import com.dtstack.engine.domain.BatchTaskShade;
import com.dtstack.engine.domain.BatchTaskTaskShade;
import com.dtstack.engine.common.enums.DependencyType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.impl.*;
import com.dtstack.engine.master.parser.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.NumericNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private static final String NORMAL_TASK_FLOW_ID = "0";

    public static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EJobType.WORK_FLOW.getVal(), EJobType.ALGORITHM_LAB.getVal());

    private static final int TASK_BATCH_SIZE = 50;
    private static final int MAX_TASK_BUILD_THREAD = 10;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    @Autowired
    private BatchTaskShadeService batchTaskShadeService;

    @Autowired
    private BatchJobService batchJobService;

    @Autowired
    private BatchTaskTaskShadeService taskTaskShadeService;

    @Autowired
    private JobGraphTriggerService jobGraphTriggerService;

    @Autowired
    private BatchAlarmService batchAlarmService;

    @Autowired
    private BatchJobAlarmService batchJobAlarmService;

    @Autowired
    private ActionService actionService;

    private Lock lock = new ReentrantLock();

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
            //检查是否已经生成过
            String triggerTimeStr = triggerDay + " 00:00:00";
            Timestamp triggerTime = Timestamp.valueOf(triggerTimeStr);

            boolean hasBuild = jobGraphTriggerService.checkHasBuildJobGraph(triggerTime);
            if (hasBuild) {
                return;
            }

            int totalTask = batchTaskShadeService.countTaskByStatus(ESubmitStatus.SUBMIT.getStatus(), EProjectScheduleStatus.NORMAL.getStatus());
            logger.info("Counting task which status=SUBMIT scheduleStatus=NORMAL totalTask:{}", totalTask);

            if (totalTask <= 0) {
                return;
            }

            ExecutorService jobGraphBuildPool = new ThreadPoolExecutor(0, MAX_TASK_BUILD_THREAD, 10L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(1), new CustomThreadFactory(this.getClass().getSimpleName()));

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
                final List<BatchTaskShade> batchTaskShades = batchTaskShadeService.listTaskByStatus(startId, ESubmitStatus.SUBMIT.getStatus(), EProjectScheduleStatus.NORMAL.getStatus(), TASK_BATCH_SIZE);
                if (batchTaskShades.isEmpty()) {
                    break;
                }

                buildSemaphore.acquire();

                startId = batchTaskShades.get(batchTaskShades.size() - 1).getId();
                logger.info("batch-number:{} startId:{}", batchIdx, startId);
                jobGraphBuildPool.execute(() -> {
                    try {
                        for (BatchTaskShade task : batchTaskShades) {
                            try {
                                String cronJobName = CRON_JOB_NAME + "_" + task.getName();
                                List<ScheduleBatchJob> jobRunBeans = buildJobRunBean(task, CRON_TRIGGER_TYPE, EScheduleType.NORMAL_SCHEDULE,
                                        true, true, triggerDay, cronJobName, null, task.getProjectId(), task.getTenantId());

                                synchronized (allJobs) {
                                    allJobs.addAll(jobRunBeans);
                                }

                                if (SPECIAL_TASK_TYPES.contains(task.getTaskType())) {
                                    for (ScheduleBatchJob jobRunBean : jobRunBeans) {
                                        flowJobId.put(jobRunBean.getTaskId() + "_" + jobRunBean.getCycTime(), jobRunBean.getJobId());
                                    }
                                }
                            } catch (Exception e) {
                                logger.error("", e);
                            }
                        }
                        logger.info("batch-umber:{} done!!! allJobs size:{}", batchIdx, allJobs.size());
                    } catch (Throwable e) {
                        logger.error("{}", e);
                    } finally {
                        buildSemaphore.release();
                        ctl.countDown();
                    }
                });
            }
            ctl.await();
            jobGraphBuildPool.shutdown();

            doSetFlowJobIdForSubTasks(allJobs, flowJobId);

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
            logger.error("", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * <br>将工作流中的子任务flowJobId字段设置为所属工作流的实例id</br>
     * <br>用于BatchFlowWorkJobService中检查工作流子任务状态</br>
     *
     * @param jobList
     * @param flowJobId
     */
    private void doSetFlowJobIdForSubTasks(List<ScheduleBatchJob> jobList, Map<String, String> flowJobId) {
        for (ScheduleBatchJob job : jobList) {
            String flowIdKey = job.getBatchJob().getFlowJobId();
            job.getBatchJob().setFlowJobId(flowJobId.getOrDefault(flowIdKey, NORMAL_TASK_FLOW_ID));
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
        //需要保存BatchJob, BatchJobJob
        batchJobService.insertJobList(jobList, EScheduleType.NORMAL_SCHEDULE.getType());

        //添加到告警监控表里面
        saveJobAlarm(jobList);

        //记录当天job已经生成
        String triggerTimeStr = triggerDay + " 00:00:00";
        Timestamp timestamp = Timestamp.valueOf(triggerTimeStr);
        jobGraphTriggerService.addJobTrigger(timestamp);

        return true;
    }

    /**
     * 批量提交
     */
    public void saveJobAlarm(List<ScheduleBatchJob> jobList) {
        //先获取所有在用的告警
        List<Long> taskIdList = batchAlarmService.getAllNeedMonitorTaskId();
        if (CollectionUtils.isEmpty(taskIdList)) {
            return;
        }

        for (ScheduleBatchJob batchJob : jobList) {
            if (!taskIdList.contains(batchJob.getTaskId())) {
                continue;
            }

            batchJobAlarmService.saveBatchJobAlarm(batchJob.getBatchJob());
        }
    }

    public List<ScheduleBatchJob> buildJobRunBean(BatchTaskShade task, String keyPreStr, EScheduleType scheduleType,
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
    public List<ScheduleBatchJob> buildJobRunBean(BatchTaskShade task, String keyPreStr, EScheduleType scheduleType, boolean needAddFather,
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
            return jobList;
        }

        List<String> triggerDayList = scheduleCron.getTriggerTime(triggerDay);

        // 处理分钟粒度任务
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            dealConcreteTime(triggerDayList, triggerDay, beginTime, endTime);
            scheduleCron.setSelfReliance(DependencyType.NO_SELF_DEPENDENCY.getType());
        }

        for (int idx = 0; idx < triggerDayList.size(); idx++) {
            String triggerTime = triggerDayList.get(idx);
            String nextTriggerTime = null;
            if ((scheduleCron.getPeriodType() == ESchedulePeriodType.MIN.getVal() || scheduleCron.getPeriodType() == ESchedulePeriodType.HOUR.getVal())
                    && (idx < triggerDayList.size() - 1)) {
                nextTriggerTime = triggerDayList.get(idx + 1);
            }

            BatchJob batchJob = new BatchJob();
            ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(batchJob);

            triggerTime = DateUtil.getTimeStrWithoutSymbol(triggerTime);
            String jobKey = generateJobKey(keyPreStr, task.getId(), triggerTime);
            String targetJobName = jobName;
            if (scheduleType.equals(EScheduleType.NORMAL_SCHEDULE)) {
                targetJobName = targetJobName + "_" + triggerTime;
            } else if (scheduleType.equals(EScheduleType.FILL_DATA)) {
                //补数据的名称和后缀用‘-’分割开-->在查询的时候会用到
                targetJobName = targetJobName + "-" + task.getName() + "-" + triggerTime;
            }
            batchJob.setJobId(actionService.generateUniqueSign());
            batchJob.setJobKey(jobKey);
            batchJob.setJobName(targetJobName);
            batchJob.setPeriodType(scheduleCron.getPeriodType());
            batchJob.setTaskId(task.getTaskId());
            if (task.getFlowId() == 0) {  //普通任务
                batchJob.setFlowJobId(NORMAL_TASK_FLOW_ID);
            } else {
                String flowJobTime = triggerTime;
                //非 小时&分钟 任务
                if (scheduleCron.getPeriodType() != ESchedulePeriodType.MIN.getVal() && scheduleCron.getPeriodType() != ESchedulePeriodType.HOUR.getVal()) {
                    List<String> cycTime = getFlowWorkCycTime(task.getFlowId(), triggerDay, task.getAppType());
                    //其他类型的任务每天只会生成一个实例
                    if (CollectionUtils.isNotEmpty(cycTime)) {
                        flowJobTime = DateUtil.getTimeStrWithoutSymbol(cycTime.get(0));
                    }
                }
                batchJob.setFlowJobId(task.getFlowId() + "_" + flowJobTime);
            }

            batchJob.setGmtCreate(timestampNow);
            batchJob.setGmtModified(timestampNow);
            if (createUserId == null) {
                batchJob.setCreateUserId(task.getCreateUserId());
            } else {
                batchJob.setCreateUserId(createUserId);
            }
            //针对跨项目补数据，项目id需要随机应变
            batchJob.setTenantId(tenantId);
            batchJob.setProjectId(projectId);
            batchJob.setDtuicTenantId(task.getDtuicTenantId());
            batchJob.setAppType(task.getAppType());

            batchJob.setType(scheduleType.getType());
            batchJob.setCycTime(triggerTime);
            batchJob.setIsRestart(Restarted.NORMAL.getStatus());

            batchJob.setDependencyType(scheduleCron.getSelfReliance());

            batchJob.setStatus(TaskStatus.UNSUBMIT.getStatus());
            batchJob.setTaskType(task.getTaskType());
            batchJob.setMaxRetryNum(scheduleCron.getMaxRetryNum());
            batchJob.setVersionId(task.getVersionId());
            batchJob.setNextCycTime(nextTriggerTime);


            //执行时间 -1 天
            String businessDate = generateBizDateFromCycTime(triggerTime);
            batchJob.setBusinessDate(businessDate);

            //任务流中的子任务，起始节点将任务流节点作为父任务加入
            if (task.getFlowId() > 0 && !whetherHasParentTask(task.getId())) {
                List<String> keys = getJobKeys(Lists.newArrayList(task.getFlowId()), batchJob, scheduleCron, keyPreStr);
                scheduleBatchJob.addBatchJobJob(createNewJobJob(batchJob, jobKey, keys.get(0), timestampNow));
            }

            //获取依赖的父task 的 jobKey
            if (needAddFather) {
                List<String> fatherDependency = getDependencyJobKeys(scheduleType, batchJob, scheduleCron, keyPreStr);
                for (String dependencyJobKey : fatherDependency) {
                    scheduleBatchJob.addBatchJobJob(createNewJobJob(batchJob, jobKey, dependencyJobKey, timestampNow));
                }
            }

            if (needSelfDependency) {
                dealSelfDependency(scheduleCron.getSelfReliance(), batchJob, scheduleCron, isFirst, scheduleBatchJob, keyPreStr, scheduleType, jobKey, timestampNow);
            }

            jobList.add(scheduleBatchJob);
            isFirst = false;
        }

        return jobList;
    }

    private void dealConcreteTime(List<String> triggerDayList, String triggerDay, String beginTime, String endTime) {
        DateTimeFormatter ddd = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        beginTime = triggerDay + " " + beginTime + ":00";
        endTime = triggerDay + " " + endTime + ":00";

        DateTime begin = DateTime.parse(beginTime, ddd);
        DateTime end = DateTime.parse(endTime, ddd);

        List<String> remove = Lists.newArrayList();
        for (String cur : triggerDayList) {
            if (DateTime.parse(cur, ddd).isBefore(begin)) {
                remove.add(cur);
            } else {
                break;
            }
        }

        Collections.reverse(triggerDayList);
        for (String cur : triggerDayList) {
            if (DateTime.parse(cur, ddd).isAfter(end)) {
                remove.add(cur);
            } else {
                break;
            }
        }
        triggerDayList.removeAll(remove);
    }

    private List<String> getFlowWorkCycTime(Long flowId, String triggerDay, Integer appType) {
        BatchTaskShade flowWork = batchTaskShadeService.getBatchTaskById(flowId, appType);
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
     * @param batchJob
     * @param scheduleCron
     * @param isFirst
     * @param scheduleBatchJob
     * @param keyPreStr
     * @param scheduleType
     * @param jobKey
     * @param timestampNow
     * @throws ParseException
     */
    private void dealSelfDependency(Integer selfReliance, BatchJob batchJob, ScheduleCron scheduleCron, boolean isFirst,
                                    ScheduleBatchJob scheduleBatchJob, String keyPreStr, EScheduleType scheduleType, String jobKey,
                                    Timestamp timestampNow) {
        if (!DependencyType.SELF_DEPENDENCY_SUCCESS.getType().equals(selfReliance)
                && !DependencyType.SELF_DEPENDENCY_END.getType().equals(selfReliance)
                && !DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_SUCCESS.getType().equals(selfReliance)
                && !DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_END.getType().equals(selfReliance)) {
            return;
        }

        String preSelfJobKey = getSelfDependencyJobKeys(batchJob, scheduleCron, keyPreStr);
        if (preSelfJobKey != null) {
            if (isFirst) {//需要查库判断是否存在
                BatchJob dbBatchJob = batchJobService.getJobByJobKeyAndType(preSelfJobKey, scheduleType.getType());
                if (dbBatchJob != null) {
                    scheduleBatchJob.addBatchJobJob(createNewJobJob(batchJob, jobKey, preSelfJobKey, timestampNow));
                }
            } else {
                scheduleBatchJob.addBatchJobJob(createNewJobJob(batchJob, jobKey, preSelfJobKey, timestampNow));
            }
        }
    }

    public BatchEngineJob newInstance(Integer status, String jobId) {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        BatchEngineJob job = new BatchEngineJob();
        job.setStatus(status);
        job.setJobId(jobId);
        job.setGmtCreate(timestamp);
        job.setGmtModified(timestamp);
        return job;
    }

    public BatchJobJob createNewJobJob(BatchJob batchJob, String jobKey, String parentKey, Timestamp timestamp) {
        BatchJobJob jobJobJob = new BatchJobJob();
        jobJobJob.setTenantId(batchJob.getTenantId());
        jobJobJob.setProjectId(batchJob.getProjectId());
        jobJobJob.setDtuicTenantId(batchJob.getDtuicTenantId());
        jobJobJob.setAppType(batchJob.getAppType());
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
     * @param batchJob
     * @param scheduleCron
     * @return
     */
    public List<String> getDependencyJobKeys(EScheduleType scheduleType, BatchJob batchJob, ScheduleCron scheduleCron, String keyPreStr) {

        List<BatchTaskTaskShade> taskTasks = taskTaskShadeService.getAllParentTask(batchJob.getTaskId());
        List<Long> pIdList = Lists.newArrayList();
        for (BatchTaskTaskShade batchTaskTask : taskTasks) {
            if (batchTaskTask.getParentTaskId() != -1) {
                pIdList.add(batchTaskTask.getParentTaskId());
            }
        }

        //所有父任务的jobKey
        //return getJobKeys(pIdList, batchJob, scheduleCron, keyPreStr);
        //补数据运行时，需要所有周期实例立即运行
        if (EScheduleType.FILL_DATA.getType() == scheduleType.getType()) {
            return getJobKeys(pIdList, batchJob, scheduleCron, keyPreStr);
        }
        //假设2019年7月10号创建一个月调度周期实例，每月20日执行，子任务是天任务。这时，7月20日之前，父任务从未生成过实例，子任务都不能调度执行。
        return getExternalJobKeys(pIdList, batchJob, scheduleCron, keyPreStr);

    }


    /**
     * <p>
     * 获取任务外部父任务依赖，排除由于人为原因(创建时间、系统问题)导致的被依赖任务没生成的情况，比如
     * 日任务依赖周任务，日任务依赖月任务，周任务依赖月任务，但所被依赖的父任务没有生成graph，导致当前任务不能执行或者执行失败。
     * </p>
     *
     * @param taskShadeIds
     * @param batchJob
     * @param scheduleCron
     * @param keyPreStr
     * @return
     */
    private List<String> getExternalJobKeys(List<Long> taskShadeIds, BatchJob batchJob, ScheduleCron scheduleCron, String keyPreStr) {
        List<String> jobKeyList = Lists.newArrayList();
        List<BatchTaskShade> pTaskList = batchTaskShadeService.getTaskByIds(taskShadeIds, null);
        for (BatchTaskShade pTask : pTaskList) {
            try {
                ScheduleCron pScheduleCron = ScheduleFactory.parseFromJson(pTask.getScheduleConf());
                //执行时间
                String fatherLastJobCycTime = getFatherLastJobBusinessDate(batchJob, pScheduleCron, scheduleCron);
                String pjobKey = generateJobKey(keyPreStr, pTask.getId(), fatherLastJobCycTime);
                // BatchJob (cycTime 20191211000000 businessDate 20191210000000)  fatherLastJobCycTime 20191211000000
                //判断的时候需要拿执行时间判断
                DateTime jobCycTime = dtf.parseDateTime(batchJob.getCycTime());
                DateTime fatherCycTime = dtf.parseDateTime(fatherLastJobCycTime);
                //如果父任务在当前任务业务日期不同，则查询父任务是有已生成
                if (Objects.nonNull(jobCycTime) && Objects.nonNull(fatherCycTime) && fatherCycTime.getDayOfYear() != jobCycTime.getDayOfYear()) {
                    //判断父任务是否生成
                    BatchJob pBatchJob = batchJobService.getJobByJobKeyAndType(pjobKey, EScheduleType.NORMAL_SCHEDULE.getType());
                    if (pBatchJob == null) {
                        logger.error("getExternalJobKeys ,but not found the parent job of " + pTask.getTaskId()
                                + " ,current job is " + batchJob.getJobId() + ", the pjobKey = " + pjobKey);
                        continue;
                    }
                }

                // 比如当天是2019.04.08,所依赖的父任务是月任务，每月20号执行，那找到的最近的父任务即为2019.03.20，但该父任务执行时间已过。
                jobKeyList.add(pjobKey);
            } catch (Exception e) {
                logger.error("getExternalJobKeys parse task" + pTask.getId() + " error", e);
                continue;
            }
        }
        return jobKeyList;
    }


    /**
     * 是否有父任务依赖
     *
     * @param taskId
     * @return true-有父任务，false-无
     */
    private boolean whetherHasParentTask(Long taskId) {
        List<BatchTaskTaskShade> taskTasks = taskTaskShadeService.getAllParentTask(taskId);
        return CollectionUtils.isNotEmpty(taskTasks);
    }

    private List<String> getJobKeys(List<Long> taskShadeIds, BatchJob batchJob, ScheduleCron scheduleCron, String keyPreStr) {
        List<String> jobKeyList = Lists.newArrayList();
        List<BatchTaskShade> pTaskList = batchTaskShadeService.getTaskByIds(taskShadeIds, null);
        for (BatchTaskShade pTask : pTaskList) {
            try {
                ScheduleCron pScheduleCron = ScheduleFactory.parseFromJson(pTask.getScheduleConf());
                String pBusinessDate = getFatherLastJobBusinessDate(batchJob, pScheduleCron, scheduleCron);
                String pjobKey = generateJobKey(keyPreStr, pTask.getId(), pBusinessDate);
                jobKeyList.add(pjobKey);
            } catch (Exception e) {
                //FIXME 如果解析失败该任务是加入到队列里面还是提示直接不管该task
                logger.error("parse task" + pTask.getId() + " error", e);
                continue;
            }
        }
        return jobKeyList;
    }


    public String getSelfDependencyJobKeys(BatchJob batchJob, ScheduleCron cron, String keyPreStr) {
        String preTriggerDateStr = getPrePeriodJobTriggerDateStr(batchJob.getCycTime(), cron);
        //源逻辑是拿batchJob的taskId 作为key
        //现在task中 taskId + appType 才是唯一
        //现在采用taskShade表的id
        BatchTaskShade shade = batchTaskShadeService.getBatchTaskById(batchJob.getTaskId(), batchJob.getAppType());
        if (Objects.nonNull(shade)) {
            return generateJobKey(keyPreStr, shade.getId(), preTriggerDateStr);
        }
        return null;
    }

    /**
     * 返回上一个执行周期的触发时间
     * 返回的时间格式： yyyyMMddHHmmss
     *
     * @param batchJobCycTime
     * @param cron
     * @return
     */
    public static String getPrePeriodJobTriggerDateStr(String batchJobCycTime, ScheduleCron cron) {
        DateTime triggerDate = dtf.parseDateTime(batchJobCycTime);
        Date preTriggerDate = getPreJob(triggerDate.toDate(), cron);
        return dtf.print(preTriggerDate.getTime());
    }

    /**
     * @param triggerType
     * @param taskId
     * @param triggerTime 格式要求:yyyyMMddHHmmss
     * @return
     */
    public static String generateJobKey(String triggerType, long taskId, String triggerTime) {
        triggerTime = triggerTime.replace("-", "").replace(":", "").replace(" ", "");
        return triggerType + "_" + taskId + "_" + triggerTime;
    }

    /**
     * 获取上一个任务周期执行的时间
     *
     * @param currTriggerDate
     * @param cron
     * @return
     */
    public static Date getPreJob(Date currTriggerDate, ScheduleCron cron) {

        String[] timeFields = cron.getCronStr().split("\\s+");
        DateTime dateTime = new DateTime(currTriggerDate);

        if (timeFields.length != 6) {
            throw new RdosDefineException("illegal param of cron str:" + cron);
        }

        if (cron.getPeriodType() == ESchedulePeriodType.MONTH.getVal()) {
            //当前是当月执行的第一天的话则取上个周期的最后一天,其他的取当前的执行的前一个日期
            List<Integer> dayArr = getSortDayList(timeFields[3]);
            int currDay = dateTime.get(DateTimeFieldType.dayOfMonth());
            int index = dayArr.indexOf(currDay);

            if (index == -1) {//找不到该运行时间,不应该出现
                logger.error("can't find dayOfMonth:{} in cronStr:{}!", currDay, cron);
                return null;
            }

            if (index == 0) {//上个月的最后一天
                dateTime = dateTime.minusMonths(1);
                dateTime = dateTime.withDayOfMonth(dayArr.get(dayArr.size() - 1));
            } else {//上一天
                dateTime = dateTime.withDayOfMonth(dayArr.get(index - 1));
            }
        } else if (cron.getPeriodType() == ESchedulePeriodType.WEEK.getVal()) {
            //当前是当周执行的第一天的话则取上个周期的最后一天,其他的取当前的执行的前一个日期
            List<Integer> dayArr = getSortDayList(timeFields[5]);
            int currDay = dateTime.get(DateTimeFieldType.dayOfWeek());
            int index = dayArr.indexOf(currDay);
            if (index == -1) {//找不到该运行时间,不应该出现
                logger.error("can't find dayOfWeek:{} in cronStr:{}!", currDay, cron);
                return null;
            }

            if (index == 0) {//上周的最后一个执行天
                dateTime = dateTime.minusWeeks(1);
                dateTime = dateTime.withDayOfWeek(dayArr.get(dayArr.size() - 1));
            } else {//上一天
                dateTime = dateTime.withDayOfWeek(dayArr.get(index - 1));
            }

        } else if (cron.getPeriodType() == ESchedulePeriodType.DAY.getVal()) {
            //获取前一天的执行时间
            dateTime = dateTime.minusDays(1);
        } else if (cron.getPeriodType() == ESchedulePeriodType.HOUR.getVal()) {
            //如果是第一个小时--返回上一天的最后一个小时
            int firstHour = ((ScheduleCronHourParser) cron).getFirstHour();
            if (dateTime.getHourOfDay() == firstHour) {
                int lastHour = ((ScheduleCronHourParser) cron).getLastHour();
                dateTime = dateTime.minusDays(1);
                dateTime = dateTime.withHourOfDay(lastHour);
            } else {
                dateTime = dateTime.minusHours(((ScheduleCronHourParser) cron).getGapNum());
            }

        } else if (cron.getPeriodType() == ESchedulePeriodType.MIN.getVal()) {
            boolean isFirstOfDay = ((ScheduleCronMinParser) cron).isDayFirstTrigger(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
            if (isFirstOfDay) {
                int last = ((ScheduleCronMinParser) cron).getLastTriggerMinutes();
                int hour = last / 60;
                int minute = last % 60;

                dateTime = dateTime.minusDays(1);
                dateTime = dateTime.withHourOfDay(hour).withMinuteOfHour(minute);
            } else {
                dateTime = dateTime.minusMinutes(((ScheduleCronMinParser) cron).getGapNum());
            }

        } else {
            throw new RdosDefineException("not support of ESchedulePeriodType:" + cron.getPeriodType());
        }

        return dateTime.toDate();
    }

    /**
     * 返回有序的队列
     *
     * @param dayListStr
     * @return
     */
    public static List<Integer> getSortDayList(String dayListStr) {

        String[] dayArr = dayListStr.split(",");
        List<Integer> sortList = Lists.newArrayList();
        for (String dayStr : dayArr) {
            sortList.add(MathUtil.getIntegerVal(dayStr.trim()));
        }

        Collections.sort(sortList);
        return sortList;
    }

    /**
     * 返回有序的队列
     *
     * @param dayListStr
     * @return
     */
    public static List<Integer> getSortTimeList(String dayListStr, String hourStr, String minuteStr, String secondStr) {

        int hour = Integer.valueOf(hourStr.trim());
        int minute = Integer.valueOf(minuteStr.trim());
        int second = Integer.valueOf(secondStr);

        int suffix = hour * 10000 + minute * 100 + second;
        String[] dayArr = dayListStr.split(",");
        List<Integer> sortList = Lists.newArrayList();
        for (String dayStr : dayArr) {
            int dayInteger = MathUtil.getIntegerVal(dayStr.trim()) * 1000000 + suffix;
            sortList.add(dayInteger);
        }

        Collections.sort(sortList);
        return sortList;
    }

    /**
     * 返回父任务执行时间最靠近当前执行时间的
     * 如果父子任务都是天则返回父任务当天的key
     */
    public String getFatherLastJobBusinessDate(BatchJob childBatchJob, ScheduleCron fatherCron, ScheduleCron childCron) {
        DateTime dateTime = DateTime.parse(childBatchJob.getCycTime(), dtf);
        String pCronstr = fatherCron.getCronStr();


        String[] timeFields = pCronstr.split("\\s+");
        if (timeFields.length != 6) {
            throw new RdosDefineException("illegal param of cron str:" + pCronstr);
        }

        if (fatherCron.getPeriodType() == ESchedulePeriodType.MONTH.getVal()) {
            dateTime = getCloseInDateTimeOfMonth(timeFields, dateTime);
        } else if (fatherCron.getPeriodType() == ESchedulePeriodType.WEEK.getVal()) {
            dateTime = getCloseInDateTimeOfWeek(timeFields, dateTime);
        } else if (fatherCron.getPeriodType() == ESchedulePeriodType.DAY.getVal() && childCron.getPeriodType() != ESchedulePeriodType.DAY.getVal()) {
            dateTime = getCloseInDateTimeOfDay(dateTime, (ScheduleCronDayParser) fatherCron, false);
        } else if (fatherCron.getPeriodType() == ESchedulePeriodType.DAY.getVal() && childCron.getPeriodType() == ESchedulePeriodType.DAY.getVal()) {
            dateTime = getCloseInDateTimeOfDay(dateTime, (ScheduleCronDayParser) fatherCron, true);
        } else if (fatherCron.getPeriodType() == ESchedulePeriodType.HOUR.getVal()) {
            dateTime = getCloseInDateTimeOfHour(dateTime, (ScheduleCronHourParser) fatherCron);
        } else if (fatherCron.getPeriodType() == ESchedulePeriodType.MIN.getVal()) {
            dateTime = getCloseInDateTimeOfMin(dateTime, (ScheduleCronMinParser) fatherCron);
        } else {
            throw new RuntimeException("not support period type of " + fatherCron.getPeriodType());
        }

        return sdf.format(dateTime.toDate());
    }

    public DateTime getCloseInDateTimeOfMonth(String[] timeFields, DateTime dateTime) {
        String dayStr = timeFields[3];
        String hourStr = timeFields[2];
        String minStr = timeFields[1];
        String secStr = timeFields[0];
        List<Integer> timeList = getSortTimeList(dayStr, hourStr, minStr, secStr);
        int targetTime = dateTime.getDayOfMonth() * 1000000 + dateTime.getHourOfDay() * 10000 +
                dateTime.getMinuteOfHour() * 100 + dateTime.getSecondOfMinute();

        Integer dependencyTime = -1;
        for (int time : timeList) {
            if (targetTime < time) {
                break;
            }

            dependencyTime = time;
        }

        if (dependencyTime == -1) {//说明应该是上一个月的最后一个执行时间
            dependencyTime = timeList.get(timeList.size() - 1);
            dateTime = dateTime.minusMonths(1);
        }

        int day = dependencyTime / 1000000;
        dependencyTime = dependencyTime - day * 1000000;
        int hour = dependencyTime / 10000;
        dependencyTime = dependencyTime - hour * 10000;
        int min = dependencyTime / 100;
        int sec = dependencyTime % 100;

        dateTime = dateTime.withDayOfMonth(day).withTime(hour, min, sec, 0);
        return dateTime;
    }

    public DateTime getCloseInDateTimeOfWeek(String[] timeFields, DateTime dateTime) {
        String dayStr = timeFields[5];
        String hourStr = timeFields[2];
        String minStr = timeFields[1];
        String secStr = timeFields[0];
        List<Integer> timeList = getSortTimeList(dayStr, hourStr, minStr, secStr);
        int targetTime = dateTime.dayOfWeek().get() * 1000000 + dateTime.getHourOfDay() * 10000 +
                dateTime.getMinuteOfHour() * 100 + dateTime.getSecondOfMinute();

        Integer dependencyTime = -1;
        for (int time : timeList) {
            if (targetTime < time) {
                break;
            }

            dependencyTime = time;
        }

        if (dependencyTime == -1) {//说明应该是上周的最后一个执行时间
            dependencyTime = timeList.get(timeList.size() - 1);
            dateTime = dateTime.minusWeeks(1);
        }

        int day = dependencyTime / 1000000;
        dependencyTime = dependencyTime - day * 1000000;
        int hour = dependencyTime / 10000;
        dependencyTime = dependencyTime - hour * 10000;
        int min = dependencyTime / 100;
        int sec = dependencyTime % 100;

        dateTime = dateTime.withDayOfWeek(day).withTime(hour, min, sec, 0);
        return dateTime;
    }


    public DateTime getCloseInDateTimeOfDay(DateTime dateTime, ScheduleCronDayParser fatherCron, boolean isSamePeriod) {
        DateTime fatherCurrDayTime = dateTime.withTime(fatherCron.getHour(), fatherCron.getMinute(), 0, 0);
        if (fatherCurrDayTime.isAfter(dateTime) && !isSamePeriod) {//依赖昨天的
            fatherCurrDayTime = fatherCurrDayTime.minusDays(1);
        }

        return fatherCurrDayTime;
    }

    public DateTime getCloseInDateTimeOfHour(DateTime dateTime, ScheduleCronHourParser fatherCron) {
        int childTime = dateTime.getHourOfDay() * 100 + dateTime.getMinuteOfHour();
        int triggerTime = -1;

        for (int i = fatherCron.getBeginHour(); i <= fatherCron.getEndHour(); ) {
            int fatherTime = i * 100 + fatherCron.getBeginMinute();

            if (fatherTime > childTime) {
                break;
            }

            triggerTime = fatherTime;
            i += fatherCron.getGapNum();
        }

        if (triggerTime == -1) {//获取昨天最后一个执行时间
            dateTime = dateTime.minusDays(1);
            int i = fatherCron.getBeginHour();
            for (; i <= fatherCron.getEndHour(); ) {
                i += fatherCron.getGapNum();
            }
            triggerTime = i * 100 + fatherCron.getBeginMinute();
        }

        int hour = triggerTime / 100;
        int min = triggerTime % 100;
        dateTime = dateTime.withTime(hour, min, 0, 0);

        return dateTime;
    }

    public DateTime getCloseInDateTimeOfMin(DateTime dateTime, ScheduleCronMinParser fatherCron) {
        int childTime = dateTime.getHourOfDay() * 60 + dateTime.getMinuteOfHour();
        int triggerTime = -1;
        int begin = fatherCron.getBeginHour() * 60 + fatherCron.getBeginMin();
        int end = fatherCron.getEndHour() * 60 + fatherCron.getEndMin();

        if (end - begin < 0) {
            throw new RdosDefineException("illegal cron str :" + fatherCron.getCronStr());
        }

        for (int i = begin; i <= end; ) {
            if (i > childTime) {
                break;
            }
            triggerTime = i;
            i += fatherCron.getGapNum();
        }

        int hour = 0;
        int minute = 0;
        if (triggerTime == -1) {//获取昨天最后一个执行时间
            dateTime = dateTime.minusDays(1);
            int remainder = (end - begin) % fatherCron.getGapNum();
            //余数肯定不会超过59,所以直接减
            minute = fatherCron.getEndMin() - remainder;
            hour = fatherCron.getEndHour();

        } else {
            hour = triggerTime / 60;
            minute = triggerTime % 60;
        }
        dateTime = dateTime.withTime(hour, minute, 0, 0);

        return dateTime;
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
        BatchTaskShade batchTask = batchTaskShadeService.getBatchTaskById(fatherNode.asLong(), appType);
        if (batchTask == null || batchTask.getIsDeleted() == Deleted.DELETED.getStatus()) {
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
        doSetFlowJobIdForSubTasks(batchJobs, flowJobId);
        //工作流情况的处理
        if (batchTask.getTaskType().intValue() == EJobType.WORK_FLOW.getVal() ||
                batchTask.getTaskType().intValue() == EJobType.ALGORITHM_LAB.getVal()) {
            for (ScheduleBatchJob jobRunBean : batchJobs) {
                flowJobId.put(jobRunBean.getTaskId() + "_" + jobRunBean.getCycTime(), jobRunBean.getJobId());
            }
            //将工作流下的子任务生成补数据任务实例
            List<ScheduleBatchJob> subTaskJobs = buildSubTasksJobForFlowWork(batchTask.getTaskId(), preStr, fillJobName, triggerDay, createUserId, beginTime, endTime, projectId, tenantId, appType);
            logger.error("buildFillDataJobGraph for flowTask with flowJobId map [{}]", flowJobId);
            doSetFlowJobIdForSubTasks(subTaskJobs, flowJobId);
            batchJobs.addAll(subTaskJobs);
        }
        for (ScheduleBatchJob batchJob : batchJobs) {
            if (batchJob.getBatchJob() != null) {
                batchJob.getBatchJob().setFillId(fillId);
                batchJob.getBatchJob().setDtuicTenantId(dtuicTenantId);
            }
            if(CollectionUtils.isNotEmpty(batchJob.getBatchJobJobList())){
                for (BatchJobJob batchJobJob : batchJob.getBatchJobJobList()) {
                    batchJobJob.setDtuicTenantId(dtuicTenantId);
                    batchJobJob.setAppType(appType);
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
        List<BatchTaskShade> subTasks = batchTaskShadeService.getFlowWorkSubTasks(taskId, appType);
        for (BatchTaskShade taskShade : subTasks) {
            String subKeyPreStr = preStr;
            String subFillJobName = fillJobName;
            //子任务需添加依赖关系
            List<ScheduleBatchJob> batchJobs = buildJobRunBean(taskShade, subKeyPreStr, EScheduleType.FILL_DATA, true, true,
                    triggerDay, subFillJobName, createUserId, beginTime, endTime, projectId, tenantId);
            result.addAll(batchJobs);
        }

        return result;
    }

    /**
     * 根据cycTime计算bizTime
     *
     * @param cycTime cycTime格式必须是yyyyMMddHHmmss
     * @return
     */
    public String generateBizDateFromCycTime(String cycTime) {
        DateTime cycDateTime = DateTime.parse(cycTime, dtf);
        DateTime bizDate = cycDateTime.minusDays(1);
        return bizDate.toString(dtf);
    }

    //preKey_taskId_cyctime
    public static String parseCycTimeFromJobKey(String jobKey) {
        String[] strArr = jobKey.split("_");
        if (strArr.length < 1) {
            return null;
        }
        return strArr[strArr.length - 1];
    }

    public static EScheduleType parseScheduleTypeFromJobKey(String jobKey) {
        if (jobKey.startsWith(CRON_TRIGGER_TYPE)) {
            return EScheduleType.NORMAL_SCHEDULE;
        }

        return EScheduleType.FILL_DATA;
    }


}
