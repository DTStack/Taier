package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.ScheduleEngineProjectDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.domain.ScheduleEngineProject;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.executor.AbstractJobExecutor;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class BatchFlowWorkJobService {

    private final Logger logger = LoggerFactory.getLogger(AbstractJobExecutor.class);

    private final String LOG_TEM = "%s: %s(所属租户：%s,所属项目：%s)";

    /**
     * 任务状态从低到高排序
     */
    private final static List<Integer> sortedStatus = Lists.newArrayList(RdosTaskStatus.CANCELED.getStatus(), RdosTaskStatus.FAILED.getStatus(), RdosTaskStatus.KILLED.getStatus(), RdosTaskStatus.FROZEN.getStatus(), RdosTaskStatus.CANCELLING.getStatus()
            , RdosTaskStatus.PARENTFAILED.getStatus(), RdosTaskStatus.UNSUBMIT.getStatus(), RdosTaskStatus.SUBMITFAILD.getStatus(), RdosTaskStatus.SUBMITTING.getStatus(), RdosTaskStatus.SUBMITTED.getStatus(), RdosTaskStatus.WAITENGINE.getStatus()
            , RdosTaskStatus.ENGINEDISTRIBUTE.getStatus(), RdosTaskStatus.ENGINEACCEPTED.getStatus(), RdosTaskStatus.WAITCOMPUTE.getStatus(), RdosTaskStatus.CREATED.getStatus(), RdosTaskStatus.SCHEDULED.getStatus(), RdosTaskStatus.DEPLOYING.getStatus()
            , RdosTaskStatus.RESTARTING.getStatus(), RdosTaskStatus.RUNNING.getStatus(), RdosTaskStatus.MANUALSUCCESS.getStatus(), RdosTaskStatus.FINISHED.getStatus());

    @Autowired
    private ScheduleJobService batchJobService;

    @Autowired
    private ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private ScheduleEngineProjectDao scheduleEngineProjectDao;

    Predicate<Integer> isSpecialType = type ->  type.intValue() == EScheduleJobType.WORK_FLOW.getType() || type.intValue() == EScheduleJobType.ALGORITHM_LAB.getVal();

    /**
     * <br>1.工作流下无子任务更新为完成状态</br>
     * <br>2.工作流下任务都是完成状态，任务提交队列可以移除</br>
     * <br>3.同时更新工作流engine_job状态，工作流只有四种状态，成功/失败/取消/提交中</br>
     * <br>&nbsp;&nbsp;a.所有子任务状态为运行成功时，工作流状态更新为成功</br>
     * <br>&nbsp;&nbsp;a.工作流状态根据子任务的运行状态来确定，失败状态存在优先级：运行失败>提交失败>上游失败</br>
     * <br>&nbsp;&nbsp;b.子任务存在运行失败时，工作流状态更新为运行失败</br>
     * <br>&nbsp;&nbsp;b.子任务不存在运行失败时，存在提交失败，工作流状态更新为提交失败</br>
     * <br>&nbsp;&nbsp;b.子任务不存在运行失败时，不存在提交失败，存在上游失败时，工作流状态更新为上游失败</br>
     * <br>&nbsp;&nbsp;c.子任务存在取消状态时，工作流状态更新为取消</br>
     * <br>&nbsp;&nbsp;e.若子任务中同时存在运行失败或取消状态，工作流状态更新为失败状态</br>
     * <br>&nbsp;&nbsp;f.其他工作流更新为运行中状态</br>
     *
     * @param
     */
    public boolean checkRemoveAndUpdateFlowJobStatus(ScheduleBatchJob scheduleBatchJob) {
        String jobId = scheduleBatchJob.getJobId();
        List<ScheduleJob> subJobs = batchJobService.getSubJobsAndStatusByFlowId(jobId);
        boolean canRemove = false;
        Integer bottleStatus = null;
        //没有子任务
        if (CollectionUtils.isEmpty(subJobs)) {
            bottleStatus = RdosTaskStatus.FINISHED.getStatus();
            canRemove = true;
        } else {
            for (ScheduleJob scheduleJob : subJobs) {
                Integer status = scheduleJob.getStatus();
                // 工作流失败状态细化 优先级： 运行失败>提交失败>上游失败 > 取消（手动取消或者自动取消）
                if (RdosTaskStatus.FROZEN_STATUS.contains(status) || RdosTaskStatus.STOP_STATUS.contains(status)) {
                    if (!RdosTaskStatus.FAILED.getStatus().equals(bottleStatus) && !RdosTaskStatus.SUBMITFAILD.getStatus().equals(bottleStatus) && !RdosTaskStatus.PARENTFAILED.getStatus().equals(bottleStatus)) {
                        if (RdosTaskStatus.AUTOCANCELED.getStatus().equals(status)) {
                            bottleStatus = RdosTaskStatus.AUTOCANCELED.getStatus();
                        } else {
                            bottleStatus = RdosTaskStatus.CANCELED.getStatus();
                        }
                    }
                    canRemove = true;
                    continue;
                }
                if (RdosTaskStatus.PARENTFAILED_STATUS.contains(status)) {
                    if (!RdosTaskStatus.FAILED.getStatus().equals(bottleStatus) && !RdosTaskStatus.SUBMITFAILD.getStatus().equals(bottleStatus)){
                        bottleStatus = RdosTaskStatus.PARENTFAILED.getStatus();
                    }
                    canRemove = true;
                    continue;
                }
                if (RdosTaskStatus.SUBMITFAILD_STATUS.contains(status)) {
                    if (!RdosTaskStatus.FAILED.getStatus().equals(bottleStatus) ){
                        bottleStatus = RdosTaskStatus.SUBMITFAILD.getStatus();
                    }
                    canRemove = true;
                    continue;
                }

                if (RdosTaskStatus.RUN_FAILED_STATUS.contains(status)) {
                    bottleStatus = RdosTaskStatus.FAILED.getStatus();
                    canRemove = true;
                    break;
                }

            }
            //子任务不存在失败/取消的状态
            for (ScheduleJob scheduleJob : subJobs) {
                Integer status = scheduleJob.getStatus();
                //若存在子任务状态不是结束状态，工作流保持提交中状态
                if (!RdosTaskStatus.getStoppedStatus().contains(status)) {
                    canRemove = false;
                    bottleStatus = RdosTaskStatus.RUNNING.getStatus();
                    break;
                }
                canRemove = true;
                if (bottleStatus == null) {
                    bottleStatus = RdosTaskStatus.FINISHED.getStatus();
                }
            }
            //子任务全部为已冻结状态
            boolean flowJobAllFrozen = true;
            for (ScheduleJob scheduleJob : subJobs){
                Integer status = scheduleJob.getStatus();
                if (!RdosTaskStatus.FROZEN_STATUS.contains(status)){
                    flowJobAllFrozen = false;
                    break;
                }
            }
            if (flowJobAllFrozen){
                canRemove = true;
                bottleStatus = RdosTaskStatus.FROZEN.getStatus();
            }
        }
        Integer appType = scheduleBatchJob.getAppType();
        logger.info("jobId:{} bottleStatus:{}", jobId,bottleStatus);
        if (RdosTaskStatus.FINISHED.getStatus().equals(bottleStatus) || RdosTaskStatus.FAILED.getStatus().equals(bottleStatus)
                || RdosTaskStatus.PARENTFAILED.getStatus().equals(bottleStatus) || RdosTaskStatus.SUBMITFAILD.getStatus().equals(bottleStatus)) {
            //更新结束时间时间
            ScheduleJob updateJob = new ScheduleJob();
            updateJob.setJobId(jobId);
            updateJob.setStatus(bottleStatus);
            updateJob.setAppType(appType);
            updateJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
            updateJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
            batchJobService.updateStatusWithExecTime(updateJob);
        } else {
            //更新工作流状态
            batchJobService.updateStatusByJobId(jobId, bottleStatus,null);
        }

        if (TaskRuleEnum.STRONG_RULE.getCode().equals(scheduleBatchJob.getScheduleJob().getTaskRule())) {
            // 强规则任务,查询父任务
            handleTaskRule(scheduleBatchJob,bottleStatus);
        }

        Long id = scheduleBatchJob.getId();
        if (RdosTaskStatus.getStoppedStatus().contains(bottleStatus)) {
            logger.info("jobId:{} is WORK_FLOW or ALGORITHM_LAB son is execution complete update phaseStatus to execute_over.", jobId);
            batchJobService.updatePhaseStatusById(id, JobPhaseStatus.CREATE, JobPhaseStatus.EXECUTE_OVER);
        }
        return canRemove;
    }

    private void handleTaskRule(ScheduleBatchJob scheduleBatchJob,Integer bottleStatus) {
        String jobKey = scheduleBatchJob.getScheduleJob().getJobKey();
        // 查询当前任务的所有父任务的运行状态
        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByParentJobKey(jobKey);
        if (CollectionUtils.isNotEmpty(scheduleJobJobs)) {
            List<String> parentJobKeys = scheduleJobJobs.stream().map(ScheduleJobJob::getJobKey).collect(Collectors.toList());
            // 查询所有父任务
            List<ScheduleJob> scheduleJobs = batchJobService.listJobByJobKeys(parentJobKeys);
            // 查询所有父任务下的子任务关系
            Map<String,List<ScheduleJob>> parentAndSon = batchJobService.getParantJobKeyMap(parentJobKeys);

            for (ScheduleJob scheduleJob : scheduleJobs) {
                // 判断状态父任务的状态
                List<ScheduleJob> scheduleJobsSon = parentAndSon.get(scheduleJob.getJobKey());
                updateFatherStatus(scheduleJob,scheduleBatchJob.getScheduleJob(),scheduleJobsSon,bottleStatus);
            }

        }
    }

    private void updateFatherStatus(ScheduleJob fatherScheduleJob, ScheduleJob currentScheduleJob, List<ScheduleJob> sonScheduleJobs, Integer bottleStatus) {
        if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(fatherScheduleJob.getStatus()) && CollectionUtils.isNotEmpty(sonScheduleJobs)) {
            if (RdosTaskStatus.FAILED_STATUS.contains(bottleStatus)) {
                // 当前强任务执行失败，执行更新成失败
                getLog(fatherScheduleJob,currentScheduleJob);
                batchJobService.updateStatusAndLogInfoById(fatherScheduleJob.getJobId(), RdosTaskStatus.FAILED.getStatus(), "");
            } else if (RdosTaskStatus.FINISH_STATUS.contains(bottleStatus)) {
                // 当前任务执行成功,判断父任务下其他子任务是否有强规则任务
                List<ScheduleJob> jobs = sonScheduleJobs.stream().filter(job -> TaskRuleEnum.STRONG_RULE.getCode().equals(job.getTaskRule()) && job.getJobKey().equals(currentScheduleJob.getJobKey())).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(jobs)) {
                    List<ScheduleJob> noFinishJobs = jobs.stream().filter(job -> !RdosTaskStatus.FINISH_STATUS.contains(job.getStatus())).collect(Collectors.toList());

                    if (CollectionUtils.isEmpty(noFinishJobs)) {
                        // 为查到未完成的任务
                        batchJobService.updateStatusAndLogInfoById(fatherScheduleJob.getJobId(), RdosTaskStatus.FINISHED.getStatus(), "");
                    }
                } else {
                    batchJobService.updateStatusAndLogInfoById(fatherScheduleJob.getJobId(), RdosTaskStatus.FINISHED.getStatus(), "");
                }
            }
        }
    }

    private void getLog(ScheduleJob fatherScheduleJob,ScheduleJob currentScheduleJob) {
        String logInfo = fatherScheduleJob.getLogInfo();
        // %s: %s(所属租户：%s,所属项目：%s)
        String addLog = LOG_TEM;
        String nameByDtUicTenantId = tenantDao.getNameByDtUicTenantId(currentScheduleJob.getDtuicTenantId());
        ScheduleEngineProject project = scheduleEngineProjectDao.getProjectByProjectIdAndApptype(currentScheduleJob.getProjectId(),currentScheduleJob.getAppType());
        boolean isRule = Boolean.FALSE;
        if (EScheduleJobType.WORK_FLOW.getType().equals(currentScheduleJob.getTaskType())) {
            // 如果工作流任务，查询是否有null任务
            List<ScheduleJob> subJobsAndStatusByFlowId = batchJobService.getSubJobsAndStatusByFlowId(currentScheduleJob.getJobId());
            List<ScheduleJob> jobs = subJobsAndStatusByFlowId.stream().filter(job -> EScheduleJobType.NOT_DO_TASK.getType().equals(job.getTaskType())).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(jobs)) {
                // 有空任务
                for (ScheduleJob job : jobs) {
                    if (RdosTaskStatus.FAILED_STATUS.contains(job.getStatus())) {
                        // 存在空任务失败的情况
                        addLog = String.format(addLog, currentScheduleJob.getJobName(), "校验不通过", nameByDtUicTenantId, project.getProjectAlias());
                        isRule = Boolean.TRUE;
                        break;
                    }
                }


            }
        }

        if (!isRule) {
            addLog = String.format(addLog, currentScheduleJob.getJobName(), "运行失败", nameByDtUicTenantId, project.getProjectAlias());
        }

        
    }

    /**
     * 工作流自己为自动取消或冻结的状态的时候 直接把子任务状态全部更新 防止重复check
     * 出现工作流为自动取消 但是子任务为等待提交 需要经过下一轮check才变更为自动取消
     *
     * @param status
     */
    public void batchUpdateFlowSubJobStatus(ScheduleJob scheduleJob, Integer status) {
        if (null == scheduleJob || !isSpecialType.test(scheduleJob.getTaskType())) {
            return;
        }
        if (RdosTaskStatus.EXPIRE.getStatus().equals(status) || RdosTaskStatus.FROZEN.getStatus().equals(status)) {
            List<ScheduleJob> subJobs = batchJobService.getSubJobsAndStatusByFlowId(scheduleJob.getJobId());
            for (ScheduleJob subJob : subJobs) {
                logger.info("jobId:{} is WORK_FLOW or ALGORITHM_LAB son update status with flowJobId {} status {}", subJob.getJobId(), scheduleJob.getJobId(), status);
                batchJobService.updateStatusByJobId(subJob.getJobId(), status, null);
            }
        }
    }
}
