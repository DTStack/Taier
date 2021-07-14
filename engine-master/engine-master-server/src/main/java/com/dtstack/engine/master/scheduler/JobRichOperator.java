package com.dtstack.engine.master.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.scheduler.parser.ESchedulePeriodType;
import com.dtstack.engine.master.scheduler.parser.ScheduleCron;
import com.dtstack.engine.master.scheduler.parser.ScheduleFactory;
import com.dtstack.schedule.common.enums.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/30
 */
@Component
public class JobRichOperator {

    private static final Logger logger = LoggerFactory.getLogger(JobRichOperator.class);

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final long COUNT_BITS = Long.SIZE - 8;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleTaskShadeService shadeService;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    private ScheduleJobService batchJobService;

    @Autowired
    private ScheduleTaskShadeService batchTaskShadeService;

    /**
     * 判断任务是否可以执行
     * @param scheduleBatchJob
     * @param status 当前任务状态
     * @param scheduleType
     * @param notStartCache
     * @param errorJobCache
     * @param taskCache
     * @return
     * @throws ParseException
     */
    public JobCheckRunInfo checkJobCanRun(ScheduleBatchJob scheduleBatchJob, Integer status, Integer scheduleType,
                                          Set<String> notStartCache, Map<String, JobErrorInfo> errorJobCache,
                                          Map<Long, ScheduleTaskShade> taskCache) throws ParseException, IOException {

        ScheduleTaskShade batchTaskShade = getTaskShadeFromCache(taskCache,scheduleBatchJob.getAppType(), scheduleBatchJob.getTaskId());

        if (notStartCache == null) {
            notStartCache = Sets.newHashSet();
        }

        if (errorJobCache == null) {
            errorJobCache = Maps.newHashMap();
        }

        // 根据taskShade 的信息 校验出 未提交 冻结 过期这些状态
        JobCheckRunInfo checkRunInfo = this.checkStatusByTaskShade(scheduleBatchJob, status, scheduleType,batchTaskShade);

        if (Objects.nonNull(checkRunInfo)) {
            return checkRunInfo;
        }

        //重置为可以执行
        checkRunInfo = new JobCheckRunInfo();
        checkRunInfo.setStatus(JobCheckStatus.CAN_EXE);

        Integer dependencyType = scheduleBatchJob.getScheduleJob().getDependencyType();

        boolean hasFatherJobNotFinish = false;

        // 校验任务jobjob 中依赖条件是否满足
        for (ScheduleJobJob jobjob : scheduleBatchJob.getBatchJobJobList()) {
            checkRunInfo = checkJobJob(scheduleBatchJob, scheduleType, jobjob, notStartCache, errorJobCache, taskCache, dependencyType);
            //多个任务依赖的时候 如果有依赖任务还未运行完 需要check其他的依赖任务 以最后为准
            if(JobCheckStatus.FATHER_JOB_NOT_FINISHED.equals(checkRunInfo.getStatus())){
                hasFatherJobNotFinish = true;
                continue;
            }
            if (!JobCheckStatus.CAN_EXE.equals(checkRunInfo.getStatus())) {
                return checkRunInfo;
            }
        }

        if(hasFatherJobNotFinish){
            checkRunInfo.setStatus(JobCheckStatus.FATHER_JOB_NOT_FINISHED);
            return checkRunInfo;
        }

        boolean dependencyChildPrePeriod = DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_SUCCESS.getType().equals(dependencyType)
                || DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_END.getType().equals(dependencyType);

        // 校验任务依赖下游的上一周期 条件
        if (JobCheckStatus.CAN_EXE.equals(checkRunInfo.getStatus()) && dependencyChildPrePeriod) {//检测下游任务的上一个周期是否结束
            return checkChildTaskShadeStatus(scheduleBatchJob, batchTaskShade, dependencyType);
        }

        return checkRunInfo;
    }

    /**
     * 校验当前任务本身是否满足执行条件
     *
     * @param scheduleBatchJob
     * @param status
     * @param scheduleType
     * @param batchTaskShade
     * @return
     */
    private JobCheckRunInfo checkStatusByTaskShade(ScheduleBatchJob scheduleBatchJob, Integer status, Integer scheduleType,ScheduleTaskShade batchTaskShade) throws IOException {
        if (batchTaskShade == null || batchTaskShade.getIsDeleted().equals(Deleted.DELETED.getStatus())) {
            return JobCheckRunInfo.createCheckInfo(JobCheckStatus.TASK_DELETE);
        }

        if(ComputeType.BATCH.getType().equals(scheduleBatchJob.getScheduleJob().getComputeType())){

            List<String> errorMessage = checkTaskResourceLimit(scheduleBatchJob, batchTaskShade);
            if(CollectionUtils.isNotEmpty(errorMessage)) {
                return JobCheckRunInfo.createCheckInfo(JobCheckStatus.RESOURCE_OVER_LIMIT,errorMessage.toString());
            }
        }
        if (!RdosTaskStatus.UNSUBMIT.getStatus().equals(status)) {
            return JobCheckRunInfo.createCheckInfo(JobCheckStatus.NOT_UNSUBMIT);
        }

        //正常调度---判断当前任务是不是处于暂停状态--暂停状态直接返回冻结
        if (scheduleType == EScheduleType.NORMAL_SCHEDULE.getType()
                && (EScheduleStatus.PAUSE.getVal().equals(batchTaskShade.getScheduleStatus()) ||
                EProjectScheduleStatus.PAUSE.getStatus().equals(batchTaskShade.getProjectScheduleStatus()))) {
            //查询缓存
            return JobCheckRunInfo.createCheckInfo(JobCheckStatus.TASK_PAUSE);
        }

        //判断执行时间是否到达
        String currStr = sdf.format(new Date());
        long currVal = Long.parseLong(currStr);
        long triggerVal = Long.parseLong(scheduleBatchJob.getCycTime());

        if (currVal < triggerVal) {
            return JobCheckRunInfo.createCheckInfo(JobCheckStatus.TIME_NOT_REACH);
        }

        JSONObject scheduleConf = JSONObject.parseObject(batchTaskShade.getScheduleConf());
        if(null == scheduleConf){
            return null;
        }
        Integer isExpire = scheduleConf.getInteger("isExpire");
        if(null == isExpire){
            return null;
        }
        //配置了允许过期才能
        if (Expired.EXPIRE.getVal() == isExpire && this.checkExpire(scheduleBatchJob, scheduleType, batchTaskShade)) {
            return validSelfWithExpire(scheduleBatchJob);
        }
        return null;
    }


    /**
     * 自依赖任务开启自动取消 还需要保证并发数为1 所以需要判断当前是否有运行中的任务
     * @param scheduleBatchJob
     * @return
     */
    private JobCheckRunInfo validSelfWithExpire(ScheduleBatchJob scheduleBatchJob) {
        ScheduleJob scheduleJob = scheduleBatchJob.getScheduleJob();
        if (!DependencyType.SELF_DEPENDENCY_END.getType().equals(scheduleJob.getDependencyType()) &&
                !DependencyType.SELF_DEPENDENCY_SUCCESS.getType().equals(scheduleJob.getDependencyType())) {
            return JobCheckRunInfo.createCheckInfo(JobCheckStatus.TIME_OVER_EXPIRE);
        }
        //查询当前自依赖任务 今天调度时间前是否有运行中或提交中的任务 如果有 需要等头部运行任务运行完成 才校验自动取消的逻辑
        String todayCycTime = DateTime.now().withTime(0, 0, 0, 0).toString("yyyyMMddHHmmss");
        List<Integer> checkStatus = new ArrayList<>(RdosTaskStatus.RUNNING_STATUS);
        checkStatus.addAll(RdosTaskStatus.WAIT_STATUS);
        checkStatus.addAll(RdosTaskStatus.SUBMITTING_STATUS);
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listIdByTaskIdAndStatus(scheduleJob.getTaskId(), checkStatus, scheduleJob.getAppType(), todayCycTime, EScheduleType.NORMAL_SCHEDULE.getType());
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
            ScheduleJob waitFinishJob = scheduleJobs.get(0);
            logger.info("jobId {} selfJob {}  has running status [{}],wait running job finish", scheduleJob.getJobId(), waitFinishJob.getJobId(), waitFinishJob.getStatus());
            return JobCheckRunInfo.createCheckInfo(JobCheckStatus.TIME_NOT_REACH);
        }
        return JobCheckRunInfo.createCheckInfo(JobCheckStatus.TIME_OVER_EXPIRE);
    }

    /**
    * @author zyd
    * @Description 校验当前任务的参数是否超出资源限制
    * @Date 3:25 下午 2020/10/15
    * @Param [batchTaskShade, tenantResource]
    * @retrun com.dtstack.engine.master.scheduler.JobCheckRunInfo
    **/
    private List<String> checkTaskResourceLimit(ScheduleBatchJob scheduleBatchJob ,ScheduleTaskShade batchTaskShade) throws IOException {

        //离线任务才需要校验资源
        //获取租户id
        Long dtuicTenantId = scheduleBatchJob.getScheduleJob().getDtuicTenantId();
        Integer taskType = scheduleBatchJob.getScheduleJob().getTaskType();
        String taskParams = batchTaskShade.getTaskParams();
        return shadeService.checkResourceLimit
                (dtuicTenantId, taskType, taskParams, batchTaskShade.getTaskId());
    }


    /**
     * 根据jobjob 表中的依赖关系 校验任务是否可以执行
     *
     * @param scheduleBatchJob
     * @param scheduleType
     * @param jobjob
     * @param notStartCache
     * @param errorJobCache
     * @param taskCache
     * @param dependencyType
     * @return
     */
    private JobCheckRunInfo checkJobJob(ScheduleBatchJob scheduleBatchJob, Integer scheduleType, ScheduleJobJob jobjob,
                                        Set<String> notStartCache, Map<String, JobErrorInfo> errorJobCache,
                                        Map<Long, ScheduleTaskShade> taskCache, Integer dependencyType) {
        JobCheckRunInfo checkRunInfo = new JobCheckRunInfo();
        checkRunInfo.setStatus(JobCheckStatus.CAN_EXE);
        checkRunInfo.setExtInfo("");
        if (notStartCache.contains(jobjob.getParentJobKey())) {
            notStartCache.add(jobjob.getJobKey());
            checkRunInfo.setStatus(JobCheckStatus.FATHER_JOB_NOT_FINISHED);
            checkRunInfo.setExtInfo("(父任务名称为:" + jobjob.getParentJobKey() + ")");
            return checkRunInfo;
        }

        Long dependencyTaskId = getJobTaskIdFromJobKey(jobjob.getParentJobKey());
        Boolean isSelfDependency = scheduleBatchJob.getTaskId().equals(dependencyTaskId);

        if (errorJobCache.containsKey(jobjob.getParentJobKey())) {
            if (checkDependEndStatus(dependencyType, isSelfDependency)) {
                return checkRunInfo;
            }

            errorJobCache.put(scheduleBatchJob.getJobKey(), createErrJobCacheInfo(scheduleBatchJob.getScheduleJob(), taskCache));
            if (isSelfDependency) {
                checkRunInfo.setStatus(JobCheckStatus.SELF_PRE_PERIOD_EXCEPTION);
                logger.error("job:{} 自依赖异常 job:{} error cache self_pre_period_exception", jobjob.getJobKey(), jobjob.getParentJobKey());
            } else {
                checkRunInfo.setStatus(JobCheckStatus.FATHER_JOB_EXCEPTION);
                JobErrorInfo fatherJobErrIfo = errorJobCache.get(jobjob.getParentJobKey());
                checkRunInfo.setExtInfo("(父任务名称为:" + fatherJobErrIfo.getTaskName() + ")");
                logger.error("job:{} 父任务异常 job:{} error cache father_job_exception", jobjob.getJobKey(), jobjob.getParentJobKey());
            }
            return checkRunInfo;
        }

        ScheduleJob dependencyJob = batchJobService.getJobByJobKeyAndType(jobjob.getParentJobKey(), scheduleType);
        if (dependencyJob == null) {//有可能任务已经失效.或者配置错误-->只有正常调度才可能存在
            if (scheduleType == EScheduleType.FILL_DATA.getType()) {
                return checkRunInfo;
            }

            logger.error("job:{} dependency job:{} not exists.", jobjob.getJobKey(), jobjob.getParentJobKey());
            String parentJobKey = jobjob.getParentJobKey();
            String parentTaskName = batchTaskShadeService.getTaskNameByJobKey(parentJobKey,scheduleBatchJob.getAppType());
            errorJobCache.put(scheduleBatchJob.getJobKey(), createErrJobCacheInfo(scheduleBatchJob.getScheduleJob(), taskCache));
            checkRunInfo.setStatus(JobCheckStatus.FATHER_NO_CREATED);
            checkRunInfo.setExtInfo("(父任务名称为:" + parentTaskName + ")");
            return checkRunInfo;
        }

        Integer dependencyJobStatus = batchJobService.getJobStatus(dependencyJob.getJobId());

        //工作中的起始子节点
        if (!StringUtils.equals("0", scheduleBatchJob.getScheduleJob().getFlowJobId())) {
            ScheduleTaskShade taskShade = getTaskShadeFromCache(taskCache,dependencyJob.getAppType(), dependencyJob.getTaskId());
            if (taskShade != null &&
                    (taskShade.getTaskType().intValue() == EScheduleJobType.WORK_FLOW.getVal() || taskShade.getTaskType().intValue() == EScheduleJobType.ALGORITHM_LAB.getVal())) {
                if (RdosTaskStatus.RUNNING.getStatus().equals(dependencyJobStatus)) {
                    return checkRunInfo;
                }
            }
        }

        //自依赖还需要判断二种情况
        //如果是依赖父任务成功 要判断父任务状态 走自依赖上一个周期异常
        //如果是依赖父任务结束 只要是满足结束条件的 这一周期可以执行
        if (checkDependEndStatus(dependencyType, isSelfDependency)) {
            if (isEndStatus(dependencyJobStatus)) {
                return checkRunInfo;
            } else {
                checkRunInfo.setStatus(JobCheckStatus.FATHER_JOB_NOT_FINISHED);
                return checkRunInfo;
            }
        }

        if (RdosTaskStatus.FAILED.getStatus().equals(dependencyJobStatus)
                || RdosTaskStatus.SUBMITFAILD.getStatus().equals(dependencyJobStatus)
                || RdosTaskStatus.PARENTFAILED.getStatus().equals(dependencyJobStatus)) {
            checkRunInfo.setStatus(JobCheckStatus.FATHER_JOB_EXCEPTION);
            if (isSelfDependency) {
                checkRunInfo.setStatus(JobCheckStatus.SELF_PRE_PERIOD_EXCEPTION);
                checkRunInfo.setExtInfo("(父任务名称为:" + jobjob.getParentJobKey() + ")");
                logger.error("job:{} 自依赖异常 job:{} self_pre_period_exception", jobjob.getJobKey(), jobjob.getParentJobKey());
            } else {//记录失败的父任务的名称
                JobErrorInfo errorInfo = createErrJobCacheInfo(dependencyJob, taskCache);
                errorJobCache.put(dependencyJob.getJobKey(), errorInfo);
                checkRunInfo.setExtInfo("(父任务名称为:" + errorInfo.getTaskName() + ")");
                logger.error("job:{} 父任务异常 taskName:{} error cache father_job_exception", dependencyJob.getJobKey(), errorInfo.getTaskName());
            }

            errorJobCache.put(scheduleBatchJob.getJobKey(), createErrJobCacheInfo(scheduleBatchJob.getScheduleJob(), taskCache));
            return checkRunInfo;
        } else if (RdosTaskStatus.FROZEN.getStatus().equals(dependencyJobStatus)) {
            if (!isSelfDependency) {
                checkRunInfo.setStatus(JobCheckStatus.DEPENDENCY_JOB_FROZEN);
                return checkRunInfo;
            } else {//自依赖的上游任务冻结不会影响当前任务的执行
                return checkRunInfo;
            }

        } else if (RdosTaskStatus.CANCELED.getStatus().equals(dependencyJobStatus)
                || RdosTaskStatus.KILLED.getStatus().equals(dependencyJobStatus)
                || RdosTaskStatus.AUTOCANCELED.getStatus().equals(dependencyJobStatus) ) {
            checkRunInfo.setStatus(JobCheckStatus.DEPENDENCY_JOB_CANCELED);
            checkRunInfo.setExtInfo("(父任务名称为:" + getTaskNameFromJobName(dependencyJob.getJobName(), dependencyJob.getType()) + ")");
            logger.error("job:{} dependency_job_canceled job:{} ", jobjob.getParentJobKey(), jobjob.getJobKey());
            return checkRunInfo;
        } else if (RdosTaskStatus.EXPIRE.getStatus().equals(dependencyJobStatus)) {
            checkRunInfo.setExtInfo("(父任务名称为:" + getTaskNameFromJobName(dependencyJob.getJobName(), dependencyJob.getType()) + ")");
            checkRunInfo.setStatus(JobCheckStatus.DEPENDENCY_JOB_EXPIRE);
            return checkRunInfo;
        } else if (!RdosTaskStatus.FINISHED.getStatus().equals(dependencyJobStatus) &&
                !RdosTaskStatus.MANUALSUCCESS.getStatus().equals(dependencyJobStatus)) {//系统设置完成或者手动设置为完成
            checkRunInfo.setStatus(JobCheckStatus.FATHER_JOB_NOT_FINISHED);
            notStartCache.add(jobjob.getJobKey());
            return checkRunInfo;
        }
        return checkRunInfo;
    }



    /**
     * 如果任务配置了依赖下游任务的上一周期
     * 去根据依赖关系 查询 是否满足执行条件
     *
     * @param scheduleBatchJob
     * @param batchTaskShade
     * @param dependencyType
     * @return
     * @throws ParseException
     */
    private JobCheckRunInfo checkChildTaskShadeStatus(ScheduleBatchJob scheduleBatchJob, ScheduleTaskShade batchTaskShade, Integer dependencyType) throws ParseException {
        JobCheckRunInfo jobCheckRunInfo = new JobCheckRunInfo();
        jobCheckRunInfo.setStatus(JobCheckStatus.CAN_EXE);
        jobCheckRunInfo.setExtInfo("");
        List<ScheduleJob> childPrePeriodList = scheduleBatchJob.getDependencyChildPrePeriodList();
        String jobKey = scheduleBatchJob.getScheduleJob().getJobKey();
        if (childPrePeriodList == null) {//获取子任务的上一个周期
            List<ScheduleJobJob> childJobJobList = scheduleJobJobDao.listByParentJobKey(jobKey);
            childPrePeriodList = getFirstChildPrePeriodBatchJobJob(childJobJobList);
            scheduleBatchJob.setDependencyChildPrePeriodList(childPrePeriodList);
        }
        String cycTime = JobGraphBuilder.parseCycTimeFromJobKey(jobKey);
        //如果没有下游任务 需要往前找到有下游任务周期
        if (CollectionUtils.isEmpty(childPrePeriodList)) {
            ScheduleCron scheduleCron = null;
            try {
                scheduleCron = ScheduleFactory.parseFromJson(batchTaskShade.getScheduleConf());
            } catch (IOException e) {
                logger.error("get {} parent pre pre error", scheduleBatchJob.getTaskId(), e);
            }

            List<ScheduleJob> parentPrePreJob = this.getParentPrePreJob(jobKey, scheduleCron, cycTime);
            if (CollectionUtils.isNotEmpty(parentPrePreJob)) {
                if (null == scheduleBatchJob.getDependencyChildPrePeriodList()) {
                    scheduleBatchJob.setDependencyChildPrePeriodList(new ArrayList<>());
                }
                scheduleBatchJob.getDependencyChildPrePeriodList().addAll(parentPrePreJob);
            }
        }
        for (ScheduleJob childJobPreJob : childPrePeriodList) {
            //子实例和 当前任务一致 直接运行
            if (jobKey.equals(childJobPreJob.getJobKey())) {
                continue;
            }
            Integer childJobStatus = batchJobService.getJobStatus(childJobPreJob.getJobId());
            boolean check;
            if (DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_SUCCESS.getType().equals(dependencyType)) {
                check = isSuccessStatus(childJobStatus);
                if (!check) {
                    //下游任务的上一周期已经结束(未成功状态 如手动取消 过期 kill等) 但是 配置条件的是依赖任务成功 需要跳出check 否则任务会一直是等待运行
                    if (isEndStatus(childJobStatus)) {
                        jobCheckRunInfo.setStatus(JobCheckStatus.CHILD_PRE_NOT_SUCCESS);
                        ScheduleTaskShade childPreTask = batchTaskShadeService.getBatchTaskById(childJobPreJob.getTaskId(),childJobPreJob.getAppType());
                        jobCheckRunInfo.setExtInfo(String.format("(依赖下游任务的上一周期(%s)",Objects.nonNull(childPreTask) ? childPreTask.getName() : ""));
                        logger.info("get JobKey {} child job {} prePeriod status is {}  but not success", jobKey, childJobPreJob.getJobId(), childJobStatus);
                        return jobCheckRunInfo;
                    }
                }
            } else {
                check = isEndStatus(childJobStatus);
            }

            if (!check) {
                jobCheckRunInfo.setStatus(JobCheckStatus.CHILD_PRE_NOT_FINISHED);
                return jobCheckRunInfo;
            }
        }
        return jobCheckRunInfo;
    }

    /**
     * 如果任务没有下游任务 需要找到往前 一直找到有下游任务的实例
     *
     * @param jobKey
     * @param scheduleCron
     * @param cycTime
     */
    private List<ScheduleJob> getParentPrePreJob(String jobKey, ScheduleCron scheduleCron, String cycTime) {
        if (StringUtils.isNotBlank(jobKey) && null != scheduleCron && StringUtils.isNotBlank(cycTime)) {
            String prePeriodJobTriggerDateStr = JobGraphBuilder.getPrePeriodJobTriggerDateStr(cycTime, scheduleCron);
            String prePeriodJobKey = jobKey.substring(0, jobKey.lastIndexOf("_") + 1) + prePeriodJobTriggerDateStr;
            EScheduleType scheduleType = JobGraphBuilder.parseScheduleTypeFromJobKey(jobKey);
            ScheduleJob dbBatchJob = batchJobService.getJobByJobKeyAndType(prePeriodJobKey, scheduleType.getType());
            //上一个周期任务为空 直接返回
            if (Objects.isNull(dbBatchJob)) {
                return null;
            }
            List<ScheduleJobJob> batchJobJobs = scheduleJobJobDao.listByParentJobKey(dbBatchJob.getJobKey());
            if (!CollectionUtils.isEmpty(batchJobJobs)) {
                //上一轮周期任务的下游任务不为空 判断下游任务的状态
                return scheduleJobDao.listJobByJobKeys(batchJobJobs.stream().map(ScheduleJobJob::getJobKey).collect(Collectors.toList()));
            }
            cycTime = JobGraphBuilder.parseCycTimeFromJobKey(prePeriodJobKey);
            //如果上一轮周期也没下游任务 继续找
            return this.getParentPrePreJob(prePeriodJobKey, scheduleCron, cycTime);
        }
        return null;

    }

    /**
     * 只要是结束状态 都可以运行
     * @param dependencyType
     * @param isSelfDependency
     * @return
     */
    private boolean checkDependEndStatus(Integer dependencyType, Boolean isSelfDependency) {
        if (isSelfDependency && (DependencyType.SELF_DEPENDENCY_END.getType().equals(dependencyType))) {
            return true;
        }
        if (isSelfDependency && DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_END.getType().equals(dependencyType)){
            return true;
        }
        return false;
    }



    private boolean checkExpire(ScheduleBatchJob scheduleBatchJob, Integer scheduleType, ScheduleTaskShade batchTaskShade) {
        //---正常周期任务超过当前时间则标记为过期
        //http://redmine.prod.dtstack.cn/issues/19917
        if (EScheduleType.NORMAL_SCHEDULE.getType() != scheduleType) {
            return false;
        }
        //分钟 小时任务 才有过期
        if (!batchTaskShade.getPeriodType().equals(ESchedulePeriodType.MIN.getVal())
                && !batchTaskShade.getPeriodType().equals(ESchedulePeriodType.HOUR.getVal())) {
            return false;
        }
        //重跑不走
        if (Objects.nonNull(scheduleBatchJob.getScheduleJob()) && Restarted.RESTARTED.getStatus() == scheduleBatchJob.getScheduleJob().getIsRestart()) {
            return false;
        }
        if (null != scheduleBatchJob.getScheduleJob() && !"0".equalsIgnoreCase(scheduleBatchJob.getScheduleJob().getFlowJobId())) {
            //工作流子任务不检验自动取消的逻辑
            return false;
        }
        //判断task任务是否配置了允许过期（暂时允许全部任务过期 不做判断）
        //超过时间限制
        String nextCycTime = scheduleBatchJob.getScheduleJob().getNextCycTime();
        if(StringUtils.isBlank(nextCycTime)){
            return false;
        }
        String scheduleConf = batchTaskShade.getScheduleConf();
        if(StringUtils.isBlank(scheduleConf)){
            return false;
        }
        LocalDateTime nextDateCycTime = LocalDateTime.parse(scheduleBatchJob.getScheduleJob().getNextCycTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        JSONObject jsonObject = JSON.parseObject(scheduleConf);
        Boolean isLastInstance = jsonObject.getBoolean("isLastInstance");
        if(null == isLastInstance){
            return nextDateCycTime.isBefore(LocalDateTime.now());
        }
        LocalDateTime cycDateTime = LocalDateTime.parse(scheduleBatchJob.getScheduleJob().getCycTime(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        if (isLastInstance) {
            // 判断当前实例是否是最后一个实例,且设置了执行最后一个任务
            if (nextDateCycTime.getDayOfMonth() != cycDateTime.getDayOfMonth()) {
                // cycTime 和 nextCycTime 是不是同一天 不是同一天 说明这个任务是今天执行的最后一个任务
                return false;
            }
            return nextDateCycTime.isBefore(LocalDateTime.now());
        } else {
            //延迟至第二天后自动取消
            if (nextDateCycTime.getDayOfMonth() == cycDateTime.getDayOfMonth()) {
                //不是当天最后一个任务
                return nextDateCycTime.isBefore(LocalDateTime.now());
            } else {
                //最后一个执行时间 20201105235800 nextCycTime为2020-11-06 23:48:00 当前时间2020-11-06 11:00:00 要过期
                return nextDateCycTime.getDayOfMonth() == LocalDateTime.now().getDayOfMonth();
            }
        }
    }

    private boolean isEndStatus(Integer jobStatus) {
        for (Integer status : RdosTaskStatus.getStoppedStatus()) {
            if (jobStatus.equals(status)) {
                return true;
            }
        }

        return false;
    }

    private boolean isSuccessStatus(Integer jobStatus) {
        for (Integer status : RdosTaskStatus.getFinishStatus()) {
            if (jobStatus.equals(status)) {
                return true;
            }
        }

        return false;
    }

    public ScheduleTaskShade getTaskShadeFromCache(Map<Long, ScheduleTaskShade> taskCache, Integer appType, Long taskId) {
        Long taskIdUnique = getTaskIdUnique(appType, taskId);
        return taskCache.computeIfAbsent(taskIdUnique,
                k -> {
                    ScheduleTaskShade taskShade = batchTaskShadeService.getBatchTaskById(taskId, appType);
                    if(Objects.nonNull(taskShade)){
                        //防止sqlText导致内存溢出
                        taskShade.setSqlText(null);
                    }
                    return taskShade;
                });
    }

    public JobErrorInfo createErrJobCacheInfo(ScheduleJob scheduleJob, Map<Long, ScheduleTaskShade> taskCache) {
        JobErrorInfo errorJobCacheInfo = new JobErrorInfo();
        errorJobCacheInfo.setJobKey(scheduleJob.getJobKey());

        ScheduleTaskShade batchTaskShade = getTaskShadeFromCache(taskCache, scheduleJob.getAppType(), scheduleJob.getTaskId());

        if (batchTaskShade == null) {
            errorJobCacheInfo.setTaskName("找不到对应的任务(id:" + scheduleJob.getTaskId() + ")");
        } else {
            errorJobCacheInfo.setTaskName(batchTaskShade.getName());
        }

        return errorJobCacheInfo;
    }



    public String getTaskNameFromJobName(String jobName, Integer scheduleType) {

        if (scheduleType == 1) {
            String[] arr = jobName.split("-");
            if (arr.length != 3) {
                return jobName;
            }

            return arr[1];
        } else {
            if (!jobName.contains("_")) {
                return jobName;
            }
            return jobName.substring(jobName.indexOf("_") + 1, jobName.lastIndexOf("_"));
        }

    }

    private List<ScheduleJob> getFirstChildPrePeriodBatchJobJob(List<ScheduleJobJob> jobJobList) {

        if (CollectionUtils.isEmpty(jobJobList)) {
            return Lists.newArrayList();
        }

        Map<Long, ScheduleJobJob> taskRefFirstJobMap = Maps.newHashMap();
        for (ScheduleJobJob scheduleJobJob : jobJobList) {
            String jobKey = scheduleJobJob.getJobKey();
            Long taskId = getJobTaskIdFromJobKey(jobKey);
            if (taskId == null) {
                continue;
            }

            ScheduleJobJob preJobJob = taskRefFirstJobMap.get(taskId);
            if (preJobJob == null) {
                taskRefFirstJobMap.put(taskId, scheduleJobJob);
            } else {
                String preJobTimeStr = getJobTriggerTimeFromJobKey(preJobJob.getJobKey());
                String currJobTimeStr = getJobTriggerTimeFromJobKey(scheduleJobJob.getJobKey());

                Long preJobTime = MathUtil.getLongVal(preJobTimeStr);
                Long currJobTime = MathUtil.getLongVal(currJobTimeStr);

                if (currJobTime < preJobTime) {
                    taskRefFirstJobMap.put(taskId, scheduleJobJob);
                }
            }
        }

        List<ScheduleJob> resultList = Lists.newArrayList();
        //计算上一个周期的key,并判断是否存在-->存在则添加依赖关系
        for (Map.Entry<Long, ScheduleJobJob> entry : taskRefFirstJobMap.entrySet()) {
            ScheduleJobJob scheduleJobJob = entry.getValue();
            Long taskId = entry.getKey();
            ScheduleTaskShade batchTaskShade = batchTaskShadeService.getBatchTaskById(taskId, scheduleJobJob.getAppType());
            if (batchTaskShade == null) {
                logger.error("can't find task by id:{}.", taskId);
                continue;
            }

            String jobKey = scheduleJobJob.getJobKey();
            String cycTime = JobGraphBuilder.parseCycTimeFromJobKey(jobKey);
            String scheduleConf = batchTaskShade.getScheduleConf();
            try {
                ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleConf);
                String prePeriodJobTriggerDateStr = JobGraphBuilder.getPrePeriodJobTriggerDateStr(cycTime, scheduleCron);
                String prePeriodJobKey = jobKey.substring(0, jobKey.lastIndexOf("_") + 1) + prePeriodJobTriggerDateStr;
                EScheduleType scheduleType = JobGraphBuilder.parseScheduleTypeFromJobKey(jobKey);
                ScheduleJob dbScheduleJob = batchJobService.getJobByJobKeyAndType(prePeriodJobKey, scheduleType.getType());
                if (dbScheduleJob != null) {
                    resultList.add(dbScheduleJob);
                }

            } catch (Exception e) {
                logger.error("", e);
                continue;
            }
        }

        return resultList;

    }

    private Long getJobTaskIdFromJobKey(String jobKey) {
        String[] fields = jobKey.split("_");
        if (fields.length < 3) {
            return null;
        }
        Long taskShadeId = MathUtil.getLongVal(fields[fields.length - 2]);
        ScheduleTaskShade batchTaskShade = batchTaskShadeService.getById(taskShadeId);
        return Objects.isNull(batchTaskShade) ? null : batchTaskShade.getTaskId();
    }

    private String getJobTriggerTimeFromJobKey(String jobKey) {
        String[] fields = jobKey.split("_");
        if (fields.length < 3) {
            return null;
        }

        return fields[fields.length - 1];
    }

    public Long getTaskIdUnique(int appType, long taskId) {
        return ((long) appType << COUNT_BITS) + taskId;
    }


    public Pair<String, String> getCycTimeLimit() {
        Integer dayGap = environmentContext.getCycTimeDayGap();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayGap-1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String startTime = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, dayGap+1);
        String endTime = sdf.format(calendar.getTime());
        return new ImmutablePair<>(startTime, endTime);
    }

    /**
     * getListMinId和listExecJob调用
     * @param isTrigger 普通周期调度任务
     * @param restart 重跑或补数据
     * @param mindJobId 查询最小id
     * @return
     */
    public Pair<String, String> getCycTimeLimitEndNow(Boolean isTrigger,Boolean restart,Boolean mindJobId) {
        // 当前时间
        Calendar calendar = Calendar.getInstance();
        String endTime = sdf.format(calendar.getTime());
        if(isTrigger && mindJobId){
            // 获得配置的前几天
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            // 获取minJobId的方法
            calendar.add(Calendar.HOUR,-environmentContext.getNormalScheduleCycTimeHourBefore());
        }else if(isTrigger){
            // 查询具体数据范围
            calendar.add(Calendar.DATE, -environmentContext.getCycTimeDayGap());
        }else{
            // 补数据或重跑
            calendar.add(Calendar.HOUR,restart ? -environmentContext.getRestartCycTimeHourBefore():
                    -environmentContext.getFillDataCycTimeHourGap());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String startTime = sdf.format(calendar.getTime());
        return new ImmutablePair<>(startTime, endTime);
    }

}
