package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.ScheduleJobJobDao;
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
import java.util.function.Predicate;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class BatchFlowWorkJobService {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractJobExecutor.class);

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
        LOGGER.info("flowId:{} checkRemoveAndUpdateFlowJobStatus ", jobId);
        if (CollectionUtils.isEmpty(subJobs)) {
            bottleStatus = RdosTaskStatus.FINISHED.getStatus();
            canRemove = true;
        } else {
            LOGGER.info("flowId:{} checkRemoveAndUpdateFlowJobStatus {}", jobId,JSON.toJSONString(subJobs));
            for (ScheduleJob scheduleJob : subJobs) {
                Integer status = scheduleJob.getStatus();
                LOGGER.info("flowId:{} status: {}", jobId,status);
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
                    LOGGER.info("flowId:{} status:{} update bottleStatus {}", jobId,status,RdosTaskStatus.FAILED.getStatus());
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
        LOGGER.info("jobId:{} bottleStatus:{}", jobId,bottleStatus);
        if (RdosTaskStatus.FINISHED.getStatus().equals(bottleStatus) || RdosTaskStatus.FAILED.getStatus().equals(bottleStatus)
                || RdosTaskStatus.PARENTFAILED.getStatus().equals(bottleStatus) || RdosTaskStatus.SUBMITFAILD.getStatus().equals(bottleStatus)) {
            //更新结束时间时间
            ScheduleJob updateJob = new ScheduleJob();
            updateJob.setJobId(jobId);
            if (RdosTaskStatus.FINISHED.getStatus().equals(bottleStatus) && batchJobService.hasTaskRule(scheduleBatchJob.getScheduleJob())) {
                updateJob.setStatus(RdosTaskStatus.RUNNING_TASK_RULE.getStatus());
            } else {
                updateJob.setStatus(bottleStatus);
            }
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
            batchJobService.handleTaskRule(scheduleBatchJob.getScheduleJob(),bottleStatus);
        }

        Long id = scheduleBatchJob.getId();
        if (RdosTaskStatus.getStoppedStatus().contains(bottleStatus)) {
            LOGGER.info("jobId:{} is WORK_FLOW or ALGORITHM_LAB son is execution complete update phaseStatus to execute_over.", jobId);
            batchJobService.updatePhaseStatusById(id, JobPhaseStatus.CREATE, JobPhaseStatus.EXECUTE_OVER);
        }
        return canRemove;
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
                LOGGER.info("jobId:{} is WORK_FLOW or ALGORITHM_LAB son update status with flowJobId {} status {}", subJob.getJobId(), scheduleJob.getJobId(), status);
                batchJobService.updateStatusByJobId(subJob.getJobId(), status, null);
            }
        }
    }
}
