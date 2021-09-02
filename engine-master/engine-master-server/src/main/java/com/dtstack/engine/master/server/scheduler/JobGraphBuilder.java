package com.dtstack.engine.master.server.scheduler;

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
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.engine.dao.ScheduleEngineProjectDao;
import com.dtstack.engine.master.server.ScheduleBatchJob;
import com.dtstack.engine.master.druid.DtDruidRemoveAbandoned;
import com.dtstack.engine.master.impl.*;
import com.dtstack.engine.master.server.scheduler.parser.*;
import com.dtstack.schedule.common.enums.EProjectScheduleStatus;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.dtstack.schedule.common.enums.ESubmitStatus;
import com.dtstack.schedule.common.enums.Restarted;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.NumericNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.CronExpression;
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
import java.util.concurrent.atomic.AtomicInteger;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(JobGraphBuilder.class);

    /**
     * 系统调度的时候插入的默认batch_job名称
     */
    private static final String CRON_JOB_NAME = "cronJob";
    private static final String FILL_DATA_TYPE = "fillData";
    private static final String CRON_TRIGGER_TYPE = "cronTrigger";
    private static final String NORMAL_TASK_FLOW_ID = "0";

    public static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EScheduleJobType.WORK_FLOW.getVal(), EScheduleJobType.ALGORITHM_LAB.getVal());

    private static final int TASK_BATCH_SIZE = 50;
    private static final int JOB_BATCH_SIZE = 50;
    private static final int MAX_TASK_BUILD_THREAD = 20;

    private static String dtfFormatString = "yyyyMMddHHmmss";

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

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    @Autowired
    private ScheduleEngineProjectDao engineProjectDao;

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

        if (environmentContext.getJobGraphBuilderSwitch()) {
            return;
        }

        lock.lock();

        try {
            isBuildError = false;
            //检查是否已经生成过
            String triggerTimeStr = triggerDay + " 00:00:00";
            Timestamp triggerTime = Timestamp.valueOf(triggerTimeStr);

            boolean hasBuild = jobGraphTriggerService.checkHasBuildJobGraph(triggerTime);
            if (hasBuild) {
                LOGGER.info("trigger Day {} has build so break", triggerDay);
                return;
            }
            //清理周期实例脏数据
            cleanDirtyJobGraph(triggerDay);

            int totalTask = 0;
            Map<Integer, Set<Long>> projectAppMapping = getProjectWhiteListMapping();
            if (environmentContext.getJobGraphWhiteList() && MapUtils.isEmpty(projectAppMapping)) {
                return;
            }
            totalTask = getTotalTask(totalTask, projectAppMapping);
            LOGGER.info("Counting task which status=SUBMIT scheduleStatus=NORMAL totalTask:{}", totalTask);

            if (totalTask <= 0) {
                return;
            }

            ExecutorService jobGraphBuildPool = new ThreadPoolExecutor(MAX_TASK_BUILD_THREAD, MAX_TASK_BUILD_THREAD, 10L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(MAX_TASK_BUILD_THREAD), new CustomThreadFactory("JobGraphBuilder"));

            Map<String,List<String>> allFlowJobs = Maps.newHashMap();
            final Long[] minId = {0L};
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
            AtomicInteger count = new AtomicInteger();

            while (true) {
                final int batchIdx = ++i;
                if (batchIdx > totalBatch) {
                    break;
                }
                final List<ScheduleTaskShade> batchTaskShades = getScheduleTaskShades(projectAppMapping, startId);
                if (batchTaskShades.isEmpty()) {
                    break;
                }

                startId = batchTaskShades.get(batchTaskShades.size() - 1).getId();
                LOGGER.info("batch-number:{} startId:{}", batchIdx, startId);

                try {
                    buildSemaphore.acquire();
                    jobGraphBuildPool.execute(() -> {
                        try {
                            List<ScheduleBatchJob> allJobs = Lists.newArrayList();
                            for (ScheduleTaskShade task : batchTaskShades) {
                                try {
                                    List<ScheduleBatchJob> jobRunBeans = RetryUtil.executeWithRetry(() -> {
                                        String cronJobName = CRON_JOB_NAME + "_" + task.getName();
                                        return buildJobRunBean(task, CRON_TRIGGER_TYPE, EScheduleType.NORMAL_SCHEDULE,
                                                true, true, triggerDay, cronJobName, null, task.getProjectId(), task.getTenantId(),count);
                                    }, environmentContext.getBuildJobErrorRetry(), 200, false);
                                    if (CollectionUtils.isNotEmpty(jobRunBeans)) {
                                        jobRunBeans.forEach(job->{
                                            allJobs.add(job);
                                            if (minId[0] == 0L) {
                                                minId[0] = job.getJobExecuteOrder();
                                            } else if (minId[0] > job.getJobExecuteOrder()) {
                                                minId[0] = job.getJobExecuteOrder();
                                            }
                                        });
                                    }


                                    if (SPECIAL_TASK_TYPES.contains(task.getTaskType())) {
                                        for (ScheduleBatchJob jobRunBean : jobRunBeans) {
                                            flowJobId.put(this.buildFlowReplaceId(task.getTaskId(), jobRunBean.getCycTime(), task.getAppType()), jobRunBean.getJobId());
                                        }
                                    }
                                } catch (Throwable e) {
                                    LOGGER.error("build task failure taskId:{} apptype:{}",task.getTaskId(),task.getAppType(), e);
                                }
                            }
                            LOGGER.info("batch-number:{} done!!! allJobs size:{}", batchIdx, allJobs.size());

                            // 填充工作流任务
                            for (ScheduleBatchJob job : allJobs) {
                                String flowIdKey = job.getScheduleJob().getFlowJobId();
                                if (!NORMAL_TASK_FLOW_ID.equals(flowIdKey)) {
                                    // 说明是工作流子任务
                                    String flowId = flowJobId.get(flowIdKey);
                                    if (StringUtils.isBlank(flowId)) {
                                        // 后面更新
                                        synchronized (allFlowJobs) {
                                            List<String> jodIds = allFlowJobs.get(flowIdKey);
                                            if (CollectionUtils.isEmpty(jodIds)) {
                                                jodIds = Lists.newArrayList();
                                            }
                                            jodIds.add(job.getJobId());
                                            allFlowJobs.put(flowIdKey,jodIds);
                                        }
                                    } else {
                                        job.getScheduleJob().setFlowJobId(flowId);
                                    }
                                }
                            }

                            // 插入周期实例
                            batchJobService.insertJobList(allJobs, EScheduleType.NORMAL_SCHEDULE.getType());
                            LOGGER.info("batch-number:{} done!!! allFlowJobs size:{}", batchIdx, allFlowJobs.size());
                        } catch (Throwable e) {
                            LOGGER.error("!!! buildTaskJobGraph  build job error !!!", e);
                        } finally {
                            buildSemaphore.release();
                            ctl.countDown();
                        }
                    });
                } catch (Throwable e) {
                    LOGGER.error("[acquire pool error]:", e);
                    isBuildError = true;
                    throw new RdosDefineException(e);
                }
            }
            ctl.await();
            if (isBuildError) {
                LOGGER.info("buildTaskJobGraph happend error jobSize {}", allFlowJobs.size());
                return;
            }
            LOGGER.info("buildTaskJobGraph all done!!! allJobs size:{}", allFlowJobs.size());
            jobGraphBuildPool.shutdown();

            // 更新未填充的工作流
            for (Map.Entry<String, List<String>> listEntry : allFlowJobs.entrySet()) {
                String placeholder = listEntry.getKey();
                String flowJob = flowJobId.get(placeholder);

                if (StringUtils.isNotBlank(flowJob)) {
                    batchJobService.updateFlowJob(placeholder,flowJob);
                } else {
                    batchJobService.updateFlowJob(placeholder,NORMAL_TASK_FLOW_ID);
                }
            }

            //存储生成的jobRunBean
            jobGraphBuilder.saveJobGraph(triggerDay, minId[0]);
        } catch (Exception e) {
            LOGGER.error("buildTaskJobGraph ！！！", e);
        } finally {
            LOGGER.info("buildTaskJobGraph exit & unlock ...");
            lock.unlock();
        }
    }

    /**
     * get white list project
     * @return
     */
    private Map<Integer, Set<Long>> getProjectWhiteListMapping() {
        if (!environmentContext.getJobGraphWhiteList()) {
            return null;
        }
        List<ScheduleEngineProject> scheduleEngineProjects = engineProjectDao.listWhiteListProject();
        if (CollectionUtils.isEmpty(scheduleEngineProjects)) {
            LOGGER.info("Counting task which white list is empty");
            return null;
        }
        return scheduleEngineProjects.stream()
                .collect(Collectors.groupingBy(ScheduleEngineProject::getAppType,
                        Collectors.mapping(ScheduleEngineProject::getProjectId, Collectors.toSet())));
    }

    /**
     * get taskShade need to build job graph
     * @param projectAppMapping
     * @param startId
     * @return
     */
    private List<ScheduleTaskShade> getScheduleTaskShades(Map<Integer, Set<Long>> projectAppMapping, long startId) {
        if (!environmentContext.getJobGraphWhiteList()) {
            return batchTaskShadeService.listTaskByStatus(startId, ESubmitStatus.SUBMIT.getStatus(), EProjectScheduleStatus.NORMAL.getStatus(), TASK_BATCH_SIZE, null, null);
        } else {
            List<ScheduleTaskShade> whiteListTaskShade = new ArrayList<>();
            if (null != projectAppMapping) {
                for (Integer appType : projectAppMapping.keySet()) {
                    LOGGER.info("get white project {} appType {} ",projectAppMapping.get(appType),appType);
                    List<ScheduleTaskShade> taskShades = batchTaskShadeService.listTaskByStatus(startId, ESubmitStatus.SUBMIT.getStatus(), EProjectScheduleStatus.NORMAL.getStatus(),
                            TASK_BATCH_SIZE, projectAppMapping.get(appType), appType);
                    if (CollectionUtils.isNotEmpty(taskShades)) {
                        whiteListTaskShade.addAll(taskShades);
                    }
                }
            }
            return whiteListTaskShade;
        }
    }

    private int getTotalTask(int totalTask, Map<Integer, Set<Long>> projectAppMapping) {
        if (environmentContext.getJobGraphWhiteList()) {
            for (Integer appType : projectAppMapping.keySet()) {
                totalTask += batchTaskShadeService.countTaskByStatus(ESubmitStatus.SUBMIT.getStatus(), EProjectScheduleStatus.NORMAL.getStatus(),
                        projectAppMapping.get(appType), appType);
            }
        } else {
            totalTask = batchTaskShadeService.countTaskByStatus(ESubmitStatus.SUBMIT.getStatus(), EProjectScheduleStatus.NORMAL.getStatus(), null, null);
        }
        return totalTask;
    }


    /**
     * 使用taskID cycTime appType 生成展位Id
     * @param taskId
     * @param cycTime
     * @param appType
     * @return
     * @see  JobGraphBuilder#doSetFlowJobIdForSubTasks(java.util.List, java.util.Map)
     */
    public String buildFlowReplaceId(Long taskId, String cycTime, Integer appType) {
        return taskId + "_" + cycTime + "_" + appType ;
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
        LOGGER.info("Start cleaning dirty cron job graph,  totalJob:{}", totalJob);

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
            LOGGER.info("Start clean batchJobList, batch-number:{} startId:{}", batchIdx, startId);
            startId = scheduleJobList.get(scheduleJobList.size() - 1).getId();
            List<String> jobKeyList = new ArrayList<>();
            for ( ScheduleJob scheduleJob : scheduleJobList) {
                jobKeyList.add(scheduleJob.getJobKey());
            }
            batchJobService.deleteJobsByJobKey(jobKeyList);
            LOGGER.info("batch-number:{} done! Cleaning dirty jobs size:{}", batchIdx, scheduleJobList.size());
        }
    }

    /**
     * <br>将工作流中的子任务flowJobId字段设置为所属工作流的实例id</br>
     * <br>用于BatchFlowWorkJobService中检查工作流子任务状态</br>
     *
     * @param jobList
     * @param flowJobId
     */
    public void doSetFlowJobIdForSubTasks(List<ScheduleBatchJob> jobList, Map<String, String> flowJobId) {
        for (ScheduleBatchJob job : jobList) {
            String flowIdKey = job.getScheduleJob().getFlowJobId();
            job.getScheduleJob().setFlowJobId(flowJobId.getOrDefault(flowIdKey, NORMAL_TASK_FLOW_ID));
        }
    }


    /**
     * 保存生成的jobGraph记录
     *
     * @param triggerDay
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @DtDruidRemoveAbandoned
    public boolean saveJobGraph(String triggerDay,Long minJobId) {
        LOGGER.info("start saveJobGraph to db {}", triggerDay);
        //记录当天job已经生成
        String triggerTimeStr = triggerDay + " 00:00:00";
        Timestamp timestamp = Timestamp.valueOf(triggerTimeStr);
        try {
            RetryUtil.executeWithRetry(() -> {
                jobGraphTriggerService.addJobTrigger(timestamp,minJobId);
                return null;
            }, environmentContext.getBuildJobErrorRetry(), 200, false);
        } catch (Exception e) {
            LOGGER.error("addJobTrigger triggerTimeStr {} error ", triggerTimeStr,e);
            throw new RdosDefineException(e);
        }

        return true;
    }



    public List<ScheduleBatchJob> buildJobRunBean(ScheduleTaskShade task, String keyPreStr, EScheduleType scheduleType,
                                                  boolean needAddFather, boolean needSelfDependency, String triggerDay,
                                                  String jobName, Long createUserId, Long projectId, Long tenantId,AtomicInteger count) throws Exception {
        return buildJobRunBean(task, keyPreStr, scheduleType, needAddFather, needSelfDependency, triggerDay,
                jobName, createUserId, null, null, projectId, tenantId,count);
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
                                                  String beginTime, String endTime, Long projectId, Long tenantId,AtomicInteger count) throws Exception {

        String scheduleStr = task.getScheduleConf();
        ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleStr);
        List<ScheduleBatchJob> jobList = Lists.newArrayList();
        Timestamp timestampNow = Timestamp.valueOf(LocalDateTime.now());
        Timestamp jobBuildTime = Timestamp.valueOf(triggerDay + " 00:00:00");
        boolean isFirst = true;

        //正常调度生成job需要判断--任务有效时间范围
        if (scheduleType.equals(EScheduleType.NORMAL_SCHEDULE) &&
                (scheduleCron.getBeginDate().after(jobBuildTime) || scheduleCron.getEndDate().before(jobBuildTime))) {
            LOGGER.error("appType {} task {} out of normal schedule time " ,task.getTaskId(), task.getAppType());
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
            if (triggerDayList.size() > 1) {
                if ((idx < triggerDayList.size() - 1)) {
                    //当前不是最后一个
                    nextTriggerTime = triggerDayList.get(idx + 1);
                } else {
                    DateTime nextDayExecute = new DateTime(jobBuildTime.getTime()).plusDays(1);
                    //当前最后一个，则获取下个周期的第一个
                    List<String> nextTriggerDays = scheduleCron.getTriggerTime(nextDayExecute.toString(DateUtil.DATE_FORMAT));
                    if (CollectionUtils.isNotEmpty(nextTriggerDays)) {
                        nextTriggerTime = nextTriggerDays.get(0);
                    }
                }
            }

            ScheduleJob scheduleJob = new ScheduleJob();
            ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJob);
            triggerTime = DateUtil.getTimeStrWithoutSymbol(triggerTime);
            String jobKey = generateJobKey(keyPreStr, task.getId(), triggerTime);
            String targetJobName = jobName;
            if (scheduleType.equals(EScheduleType.NORMAL_SCHEDULE)) {
                targetJobName = targetJobName + "_" + triggerTime;
            } else if (scheduleType.equals(EScheduleType.FILL_DATA)) {
                //补数据的名称和后缀用‘-’分割开-->在查询的时候会用到
                targetJobName = targetJobName + "-" + task.getName() + "-" + triggerTime;
            }
            if(actionService == null) {
                String errorMsg = "actionService is null in JobGraphBuilder#buildJobRunBean";
                LOGGER.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            scheduleJob.setJobId(actionService.generateUniqueSign());
            scheduleJob.setJobKey(jobKey);
            scheduleJob.setJobName(targetJobName);
            scheduleJob.setPeriodType(scheduleCron.getPeriodType());
            scheduleJob.setTaskId(task.getTaskId());
            scheduleJob.setBusinessType(task.getBusinessType());
            scheduleJob.setComputeType(task.getComputeType());
            //普通任务
            if (task.getFlowId() == 0) {
                scheduleJob.setFlowJobId(NORMAL_TASK_FLOW_ID);
            } else {
                //工作流子节点
                ScheduleTaskShade flowTaskShade = batchTaskShadeService.getBatchTaskById(task.getFlowId(), task.getAppType());
                if (null == flowTaskShade) {
                    scheduleJob.setFlowJobId(NORMAL_TASK_FLOW_ID);
                } else {
                    //非 小时&分钟 任务
                    String flowJobTime = triggerTime;
                    if (triggerDayList.size() == 1) {
                        List<String> cycTime = getFlowWorkCycTime(flowTaskShade, triggerDay);
                        //其他类型的任务每天只会生成一个实例
                        if (CollectionUtils.isNotEmpty(cycTime)) {
                            flowJobTime = DateUtil.getTimeStrWithoutSymbol(cycTime.get(0));
                        }
                    }
                    scheduleJob.setFlowJobId(this.buildFlowReplaceId(flowTaskShade.getTaskId(), flowJobTime, flowTaskShade.getAppType()));
                }
            }

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
            scheduleJob.setJobExecuteOrder(buildJobExecuteOrder(triggerTime,count));

            scheduleJob.setIsRestart(Restarted.NORMAL.getStatus());

            scheduleJob.setDependencyType(scheduleCron.getSelfReliance());

            scheduleJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
            scheduleJob.setTaskType(task.getTaskType());
            scheduleJob.setMaxRetryNum(scheduleCron.getMaxRetryNum());
            scheduleJob.setVersionId(task.getVersionId());
            scheduleJob.setNextCycTime(nextTriggerTime);


            //业务时间等于执行时间 -1 天
            String businessDate = generateBizDateFromCycTime(triggerTime);
            scheduleJob.setBusinessDate(businessDate);
            scheduleJob.setTaskRule(task.getTaskRule());

            //任务流中的子任务且没有父任务依赖，起始节点将任务流节点作为父任务加入
            if (task.getFlowId() > 0 && !whetherHasParentTask(task.getTaskId(),task.getAppType())) {
                List<String> keys = getJobKeys(Lists.newArrayList(task.getFlowId()), scheduleJob, scheduleCron, keyPreStr);
                scheduleBatchJob.addBatchJobJob(createNewJobJob(scheduleJob, jobKey, keys.get(0), timestampNow,task.getAppType()));
            }

            //获取依赖的父task 的 jobKey
            if (needAddFather) {
                List<FatherDependency> fatherDependency = getDependencyJobKeys(scheduleType, scheduleJob, scheduleCron, keyPreStr);
                for (FatherDependency dependencyJobKey : fatherDependency) {
                    if(LOGGER.isDebugEnabled()){
                        LOGGER.debug("get Job {} Job key  {} cron {} cycTime {}", jobKey, dependencyJobKey, JSONObject.toJSONString(scheduleCron), scheduleJob.getCycTime());
                    }
                    scheduleBatchJob.addBatchJobJob(createNewJobJob(scheduleJob, jobKey, dependencyJobKey.getJobKey(), timestampNow,dependencyJobKey.getAppType()));
                }
            }

            if (needSelfDependency) {
                dealSelfDependency(scheduleCron.getSelfReliance(), scheduleJob, scheduleCron, isFirst, scheduleBatchJob, keyPreStr, scheduleType, jobKey, timestampNow);
            }

            jobList.add(scheduleBatchJob);
            isFirst = false;
        }

        return jobList;
    }

    private Long buildJobExecuteOrder(String triggerTime,AtomicInteger count) {
        if (StringUtils.isBlank(triggerTime)) {
            throw new RuntimeException("cycTime is not null");
        }

        // 时间格式 yyyyMMddHHmmss  截取 jobExecuteOrder = yyMMddHHmm +  9位的自增
        String substring = triggerTime.substring(2, triggerTime.length() - 2);
        String increasing = String.format("%09d", count.getAndIncrement());
        return Long.parseLong(substring+increasing);
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

    private List<String> getFlowWorkCycTime(ScheduleTaskShade flowTaskShade, String triggerDay) {
        List<String> triggerTime = Lists.newArrayList();
        if (flowTaskShade != null) {
            try {
                String scheduleStr = flowTaskShade.getScheduleConf();
                ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleStr);
                triggerTime = scheduleCron.getTriggerTime(triggerDay);
            } catch (Exception e) {
                LOGGER.error("getFlowWorkCycTime error with flowId: " + flowTaskShade.getTaskId(), e);
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
                                    Timestamp timestampNow) {

        // 修改 直接改成如果等于NO_SELF_DEPENDENCY就返回
        if(DependencyType.NO_SELF_DEPENDENCY.getType().equals(selfReliance)){
            return;
        }

        //获取上一个周期的jobKey
        String preSelfJobKey = getSelfDependencyJobKeys(scheduleJob, scheduleCron, keyPreStr);
        if (preSelfJobKey != null) {
            //需要查库判断是否存在
            if (isFirst) {
                ScheduleJob dbScheduleJob = batchJobService.getJobByJobKeyAndType(preSelfJobKey, scheduleType.getType());
                if (dbScheduleJob != null) {
                    scheduleBatchJob.addBatchJobJob(createNewJobJob(scheduleJob, jobKey, preSelfJobKey, timestampNow,scheduleJob.getAppType()));
                }
            } else {
                scheduleBatchJob.addBatchJobJob(createNewJobJob(scheduleJob, jobKey, preSelfJobKey, timestampNow,scheduleJob.getAppType()));
            }
        }
    }


    public ScheduleJobJob createNewJobJob(ScheduleJob scheduleJob, String jobKey, String parentKey, Timestamp timestamp,Integer parentAppType) {
        ScheduleJobJob jobJobJob = new ScheduleJobJob();
        jobJobJob.setTenantId(scheduleJob.getTenantId());
        jobJobJob.setProjectId(scheduleJob.getProjectId());
        jobJobJob.setDtuicTenantId(scheduleJob.getDtuicTenantId());
        jobJobJob.setAppType(scheduleJob.getAppType());
        jobJobJob.setJobKey(jobKey);
        jobJobJob.setParentJobKey(parentKey);
        jobJobJob.setParentAppType(parentAppType);
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
    public List<FatherDependency> getDependencyJobKeys(EScheduleType scheduleType, ScheduleJob scheduleJob, ScheduleCron scheduleCron, String keyPreStr) {

        List<ScheduleTaskTaskShade> taskTasks = taskTaskShadeService.getAllParentTask(scheduleJob.getTaskId(),scheduleJob.getAppType());

        // 所有父任务的jobKey
        // return getJobKeys(pIdList, batchJob, scheduleCron, keyPreStr);
        // 补数据运行时，需要所有周期实例立即运行
        if (EScheduleType.FILL_DATA.getType() == scheduleType.getType()) {
            return getJobKeysByTaskTasks(taskTasks, scheduleJob, scheduleCron, keyPreStr);
        }
        //假设2019年7月10号创建一个月调度周期实例，每月20日执行，子任务是天任务。这时，7月20日之前，父任务从未生成过实例，子任务都不能调度执行。
        return getExternalJobKeys(taskTasks, scheduleJob, scheduleCron, keyPreStr);

    }


    /**
     * <p>
     * 获取任务外部父任务依赖，排除由于人为原因(创建时间、系统问题)导致的被依赖任务没生成的情况，比如
     * 日任务依赖周任务，日任务依赖月任务，周任务依赖月任务，但所被依赖的父任务没有生成graph，导致当前任务不能执行或者执行失败。
     * </p>
     *
     * @param taskTasks
     * @param scheduleJob
     * @param scheduleCron
     * @param keyPreStr
     * @return
     */
    private List<FatherDependency> getExternalJobKeys(List<ScheduleTaskTaskShade> taskTasks, ScheduleJob scheduleJob, ScheduleCron scheduleCron, String keyPreStr) {
        List<FatherDependency> jobKeyList = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(taskTasks)) {
            Map<Integer, List<ScheduleTaskTaskShade>> taskMaps = taskTasks.stream().collect(Collectors.groupingBy(ScheduleTaskTaskShade::getParentAppType));

            for (Map.Entry<Integer, List<ScheduleTaskTaskShade>> entry : taskMaps.entrySet()) {
                List<ScheduleTaskTaskShade> taskTaskShades = entry.getValue();
                Integer appType = entry.getKey();
                List<Long> taskShadeIds = taskTaskShades.stream().map(ScheduleTaskTaskShade::getParentTaskId).collect(Collectors.toList());
                List<ScheduleTaskShade> pTaskList = batchTaskShadeService.getTaskByIds(taskShadeIds, appType);
                for (ScheduleTaskShade pTask : pTaskList) {
                    try {
                        ScheduleCron pScheduleCron = ScheduleFactory.parseFromJson(pTask.getScheduleConf());
                        //执行时间
                        String fatherLastJobCycTime = getFatherLastJobBusinessDate(scheduleJob, pScheduleCron, scheduleCron);
                        String pjobKey = generateJobKey(keyPreStr, pTask.getId(), fatherLastJobCycTime);
                        // BatchJob (cycTime 20191211000000 businessDate 20191210000000)  fatherLastJobCycTime 20191211000000
                        //判断的时候需要拿执行时间判断
                        DateTime jobCycTime = new DateTime(DateUtil.getTimestamp(scheduleJob.getCycTime(), dtfFormatString));
                        DateTime fatherCycTime = new DateTime(DateUtil.getTimestamp(fatherLastJobCycTime, dtfFormatString));


                        //如果父任务在当前任务业务日期不同，则查询父任务是有已生成
                        if (fatherCycTime.getDayOfYear() != jobCycTime.getDayOfYear()) {
                            //判断父任务是否生成
                            ScheduleJob pScheduleJob = batchJobService.getJobByJobKeyAndType(pjobKey, EScheduleType.NORMAL_SCHEDULE.getType());
                            if (pScheduleJob == null) {
                                LOGGER.error("getExternalJobKeys ,but not found the parent job of " + pTask.getTaskId()
                                        + " ,current job is " + scheduleJob.getJobId() + ", the pjobKey = " + pjobKey);
                                continue;
                            }
                        }

                        // 比如当天是2019.04.08,所依赖的父任务是月任务，每月20号执行，那找到的最近的父任务即为2019.03.20，但该父任务执行时间已过。
                        FatherDependency fatherDependency = new FatherDependency();

                        fatherDependency.setAppType(appType);
                        fatherDependency.setJobKey(pjobKey);
                        jobKeyList.add(fatherDependency);
                    } catch (Exception e) {
                        LOGGER.error("getExternalJobKeys parse task" + pTask.getId() + " error", e);
                        continue;
                    }
                }
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
    private boolean whetherHasParentTask(Long taskId,Integer appType) {
        List<ScheduleTaskTaskShade> taskTasks = taskTaskShadeService.getAllParentTask(taskId,appType);
        return CollectionUtils.isNotEmpty(taskTasks);
    }

    private List<String> getJobKeys(List<Long> taskShadeIds, ScheduleJob scheduleJob, ScheduleCron scheduleCron, String keyPreStr) {
        List<String> jobKeyList = Lists.newArrayList();
        List<ScheduleTaskShade> pTaskList = batchTaskShadeService.getTaskByIds(taskShadeIds, scheduleJob.getAppType());
        for (ScheduleTaskShade pTask : pTaskList) {
            try {
                ScheduleCron pScheduleCron = ScheduleFactory.parseFromJson(pTask.getScheduleConf());
                String pBusinessDate = getFatherLastJobBusinessDate(scheduleJob, pScheduleCron, scheduleCron);
                String pjobKey = generateJobKey(keyPreStr, pTask.getId(), pBusinessDate);
                jobKeyList.add(pjobKey);
            } catch (Exception e) {
                //FIXME 如果解析失败该任务是加入到队列里面还是提示直接不管该task
                LOGGER.error("parse task" + pTask.getId() + " error", e);
            }
        }
        return jobKeyList;
    }

    private List<FatherDependency> getJobKeysByTaskTasks(List<ScheduleTaskTaskShade> taskTasks, ScheduleJob scheduleJob, ScheduleCron scheduleCron, String keyPreStr) {
        List<FatherDependency> jobKeyList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(taskTasks)) {

            Map<Integer, List<ScheduleTaskTaskShade>> taskMaps = taskTasks.stream().collect(Collectors.groupingBy(ScheduleTaskTaskShade::getParentAppType));

            for (Map.Entry<Integer, List<ScheduleTaskTaskShade>> entry : taskMaps.entrySet()) {
                List<ScheduleTaskTaskShade> taskTaskShades = entry.getValue();
                Integer appType = entry.getKey();
                List<Long> taskShadeIds = taskTaskShades.stream().map(ScheduleTaskTaskShade::getParentTaskId).collect(Collectors.toList());
                List<ScheduleTaskShade> pTaskList = batchTaskShadeService.getTaskByIds(taskShadeIds, appType);
                for (ScheduleTaskShade pTask : pTaskList) {
                    try {
                        ScheduleCron pScheduleCron = ScheduleFactory.parseFromJson(pTask.getScheduleConf());
                        FatherDependency fatherDependency = new FatherDependency();

                        String pBusinessDate = getFatherLastJobBusinessDate(scheduleJob, pScheduleCron, scheduleCron);
                        String pjobKey = generateJobKey(keyPreStr, pTask.getId(), pBusinessDate);
                        fatherDependency.setJobKey(pjobKey);
                        fatherDependency.setAppType(appType);
                        jobKeyList.add(fatherDependency);
                    } catch (Exception e) {
                        //FIXME 如果解析失败该任务是加入到队列里面还是提示直接不管该task
                        LOGGER.error("parse task" + pTask.getId() + " error", e);
                    }
                }
            }
        }

        return jobKeyList;
    }


    public String getSelfDependencyJobKeys(ScheduleJob scheduleJob, ScheduleCron cron, String keyPreStr) {

        //获取上一个执行周期的触发时间
        String preTriggerDateStr = getPrePeriodJobTriggerDateStr(scheduleJob.getCycTime(), cron);
        //原逻辑是拿batchJob的taskId 作为key
        //现在task中 taskId + appType 才是唯一
        //现在采用taskShade表的id
        ScheduleTaskShade shade = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
        if (null != shade && StringUtils.isNotBlank(preTriggerDateStr)) {
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

        DateTime triggerDate = new DateTime(DateUtil.getTimestamp(batchJobCycTime, dtfFormatString));
        Date preTriggerDate = getPreJob(triggerDate.toDate(), cron);
        if(null == preTriggerDate){
            return null;
        }
        return DateUtil.getFormattedDate(preTriggerDate.getTime(), dtfFormatString);
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
            //找不到该运行时间,不应该出现
            if (index == -1) {
                LOGGER.error("can't find dayOfMonth:{} in cronStr:{}!", currDay, cron);
                return null;
            }
            //上个月的最后一天
            if (index == 0) {
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
            //找不到该运行时间,不应该出现
            if (index == -1) {
                LOGGER.error("can't find dayOfWeek:{} in cronStr:{}!", currDay, cron);
                return null;
            }
            //上周的最后一个执行天
            if (index == 0) {
                dateTime = dateTime.minusWeeks(1);
                dateTime = dateTime.withDayOfWeek(dayArr.get(dayArr.size() - 1));
            } else {
                //上一天
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

        }else if (cron.getPeriodType() == ESchedulePeriodType.CUSTOM.getVal()){
            CronExpression expression = null;
            try {
                expression = new CronExpression(cron.getCronStr());
            } catch (ParseException e) {
                throw new RdosDefineException("cron express is invalid:" + cron.getCronStr(), e);
            }
            dateTime = new DateTime(findLastDateBeforeCurrent(expression,currTriggerDate,
                    currTriggerDate.toInstant().atZone(DateUtil.DEFAULT_ZONE).toLocalDateTime(),0,true));
        }
        else {
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
    public String getFatherLastJobBusinessDate(ScheduleJob childScheduleJob, ScheduleCron fatherCron, ScheduleCron childCron) {
        DateTime dateTime = new DateTime(DateUtil.getTimestamp(childScheduleJob.getCycTime(), dtfFormatString));
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
        }else if (fatherCron.getPeriodType() == ESchedulePeriodType.CUSTOM.getVal()){
            CronExpression expression = null;
            try {
                expression = new CronExpression(fatherCron.getCronStr());
            } catch (ParseException e) {
                throw new RdosDefineException("cron express is invalid:" + fatherCron.getCronStr(), e);
            }
            // 父任务为自定义调度,则从当前子任务执行前寻找
            dateTime = new DateTime(findLastDateBeforeCurrent(expression,dateTime.toDate(),
                    dateTime.toDate().toInstant().atZone(DateUtil.DEFAULT_ZONE).toLocalDateTime(),0,false));
        } else {
            throw new RuntimeException("not support period type of " + fatherCron.getPeriodType());
        }

        return DateUtil.getFormattedDate(dateTime.getMillis(),dtfFormatString);
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
        //说明应该是上一个月的最后一个执行时间
        if (dependencyTime == -1) {
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
                                                               String beginTime, String endTime, Long projectId, Long tenantId, Boolean isRoot,Integer appType,Long fillId,Long dtuicTenantId,AtomicInteger count) throws Exception {
        Map<String, ScheduleBatchJob> result = new HashMap<>(16);
        if (jsonObject != null && jsonObject.size() > 0) {
            for (JsonNode jsonNode : jsonObject) {
                if (jsonNode.has("appType")) {
                    JsonNode node = jsonNode.get("appType");
                    appType = node.asInt();
                }

                Map<String, ScheduleBatchJob> stringScheduleBatchJobMap = buildFillDataJobGraph(jsonNode, fillJobName, needFather, triggerDay, createUserId, beginTime, endTime, projectId, tenantId, isRoot,appType,fillId,dtuicTenantId,count);
                result.putAll(stringScheduleBatchJobMap);
            }
        }
        return result;
    }


    public Map<String, ScheduleBatchJob> buildFillDataJobGraph(ArrayNode jsonObject, String fillJobName, boolean needFather,
                                                               String triggerDay, Long createUserId, Long projectId, Long tenantId, Boolean isRoot,Integer appType,Long fillId,Long dtuicTenantId,AtomicInteger count) throws Exception {
        Map<String, ScheduleBatchJob> result = new HashMap<>();
        if (jsonObject != null && jsonObject.size() > 0) {
            for (JsonNode jsonNode : jsonObject) {
                if (jsonNode.has("appType")) {
                    JsonNode node = jsonNode.get("appType");
                    appType = node.asInt();
                }

                Map<String, ScheduleBatchJob> stringScheduleBatchJobMap = buildFillDataJobGraph(jsonNode, fillJobName, needFather, triggerDay, createUserId, null, null, projectId, tenantId, isRoot,appType,fillId,dtuicTenantId,count);
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
                                                               @Param("appType") Integer appType,Long fillId,Long dtuicTenantId,AtomicInteger count) throws Exception {

        if (!jsonObject.has("task")) {
            throw new RdosDefineException("can't get task field from jsonObject:" + jsonObject.toString(), ErrorCode.SERVER_EXCEPTION);
        }

        NumericNode fatherNode = (NumericNode) jsonObject.get("task");
        //生成jobList
        ScheduleTaskShade batchTask = batchTaskShadeService.getBatchTaskById(fatherNode.asLong(), appType);
        String preStr = FILL_DATA_TYPE + "_" + fillJobName;
        Map<String, ScheduleBatchJob> result = Maps.newLinkedHashMap();
        Map<String, String> flowJobId = Maps.newHashMap();
        List<ScheduleBatchJob> batchJobs;
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            batchJobs = buildJobRunBean(batchTask, preStr, EScheduleType.FILL_DATA, needFather,
                    true, triggerDay, fillJobName, createUserId, beginTime, endTime, projectId, tenantId,count);
        } else {
            batchJobs = buildJobRunBean(batchTask, preStr, EScheduleType.FILL_DATA, needFather,
                    true, triggerDay, fillJobName, createUserId, projectId, tenantId,count);
        }
        //针对专门补工作流子节点
        doSetFlowJobIdForSubTasks(batchJobs, flowJobId);
        //工作流情况的处理
        if (batchTask.getTaskType().intValue() == EScheduleJobType.WORK_FLOW.getVal() ||
                batchTask.getTaskType().intValue() == EScheduleJobType.ALGORITHM_LAB.getVal()) {
            for (ScheduleBatchJob jobRunBean : batchJobs) {
                flowJobId.put(batchTask.getTaskId() + "_" + jobRunBean.getCycTime() + "_" + batchTask.getAppType(), jobRunBean.getJobId());
            }
            //将工作流下的子任务生成补数据任务实例
            List<ScheduleBatchJob> subTaskJobs = buildSubTasksJobForFlowWork(batchTask.getTaskId(), preStr, fillJobName, triggerDay, createUserId, beginTime, endTime, projectId, tenantId, appType);
            LOGGER.error("buildFillDataJobGraph for flowTask with flowJobId map [{}]", flowJobId);
            doSetFlowJobIdForSubTasks(subTaskJobs, flowJobId);
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

        Integer sonAppType = appType;
        if (jsonObject.has("children")) {
            ArrayNode arrayNode = (ArrayNode) jsonObject.get("children");
            for (JsonNode node : arrayNode) {
                if (node.has("appType")) {
                    JsonNode jsonNode = node.get("appType");
                    sonAppType = jsonNode.asInt();
                }
                Map<String, ScheduleBatchJob> childNodeMap = buildFillDataJobGraph(node, fillJobName, true, triggerDay, createUserId, beginTime, endTime, projectId, tenantId, true,appType,fillId,dtuicTenantId,count);
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
        List<ScheduleTaskShade> subTasks = batchTaskShadeService.getFlowWorkSubTasks(taskId, appType, null, null);
        AtomicInteger atomicInteger = new AtomicInteger();
        for (ScheduleTaskShade taskShade : subTasks) {
            String subKeyPreStr = preStr;
            String subFillJobName = fillJobName;
            //子任务需添加依赖关系
            List<ScheduleBatchJob> batchJobs = buildJobRunBean(taskShade, subKeyPreStr, EScheduleType.FILL_DATA, true, true,
                    triggerDay, subFillJobName, createUserId, beginTime, endTime, projectId, tenantId,atomicInteger);
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
        DateTime cycDateTime = new DateTime(DateUtil.getTimestamp(cycTime, dtfFormatString));
        DateTime bizDate = cycDateTime.minusDays(1);
        return bizDate.toString(dtfFormatString);
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


    public static Date findLastDateBeforeCurrent(CronExpression expression, Date currTriggerDate,
                                                 LocalDateTime findDate, int change, boolean sameTask){
        // 不同任务可能存在触发时间一样, 同个任务触发时间不可能相同, -1s防止下次执行时间刚好是本次
        findDate = sameTask ? findDate.plusSeconds(-1): findDate;
        if (change == 20){
            change = 0;
            findDate = findDate.plusDays(-1);
        }else if (change > 15){
            findDate = findDate.plusYears(-1L);
        }else if (change > 10){
            findDate =  findDate.plusMonths(-1L);
        }else if (change > 5){
            findDate = findDate.plusWeeks(-1L);
        }else {
            findDate = findDate.plusDays(-1L);
        }
        // 计算下次执行时间
        Date isLastDate = expression.getNextValidTimeAfter(new Date(findDate.toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli()));
        // 前一次执行时间在本次之前，需要确定是不是最后一次
        if (isLastDate.before(currTriggerDate)){
            Date lastDate = isLastDate;
            while ((isLastDate = expression.getNextValidTimeAfter(isLastDate)).before(currTriggerDate)){
                lastDate = isLastDate;
            }
            // 如果不是同一个任务，并且触发时间相同则返回这个触发时间否则返回上一个触发时间
            return !sameTask && !isLastDate.after(currTriggerDate)? isLastDate:lastDate;
        }else if (!sameTask && !isLastDate.after(currTriggerDate) ){
            return currTriggerDate;
        }

        return findLastDateBeforeCurrent(expression,currTriggerDate,sameTask?findDate.plusSeconds(1):findDate,change+1,sameTask);

    }

    /**
     * @Auther: dazhi
     * @Date: 2021/3/15 6:59 下午
     * @Email:dazhi@dtstack.com
     * @Description:
     */
    public static class FatherDependency {

        private String jobKey;

        private Integer appType;

        public String getJobKey() {
            return jobKey;
        }

        public void setJobKey(String jobKey) {
            this.jobKey = jobKey;
        }

        public Integer getAppType() {
            return appType;
        }

        public void setAppType(Integer appType) {
            this.appType = appType;
        }
    }
}
