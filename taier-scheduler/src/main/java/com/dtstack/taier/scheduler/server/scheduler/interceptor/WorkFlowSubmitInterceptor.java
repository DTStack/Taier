package com.dtstack.taier.scheduler.server.scheduler.interceptor;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.enums.JobPhaseStatus;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public class WorkFlowSubmitInterceptor extends SubmitInterceptorAdapter {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    public Integer getSort() {
        return 4;
    }

    @Override
    public Boolean beforeSubmit(ScheduleJobDetails scheduleJobDetails) {
        return Boolean.TRUE;
    }

    @Override
    public void afterSubmit(ScheduleJobDetails scheduleJobDetails) {
        if (!TaskStatus.UNSUBMIT.getStatus().equals(scheduleJobDetails.getScheduleJob().getStatus()) &&
                EScheduleJobType.WORK_FLOW.getType().equals(scheduleJobDetails.getScheduleTaskShade().getTaskType())) {
            checkRemoveAndUpdateFlowJobStatus(scheduleJobDetails.getScheduleJob());
        }
    }

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
    public boolean checkRemoveAndUpdateFlowJobStatus(ScheduleJob workFlowJob) {
        String jobId = workFlowJob.getJobId();
        List<ScheduleJob> subJobs = scheduleJobService.getWorkFlowSubJobs(jobId);
        boolean canRemove = false;
        Integer bottleStatus = null;
        //没有子任务
        if (CollectionUtils.isEmpty(subJobs)) {
            bottleStatus = TaskStatus.FINISHED.getStatus();
            canRemove = true;
        } else {
            for (ScheduleJob scheduleJob : subJobs) {
                Integer status = scheduleJob.getStatus();
                LOGGER.info("flowId:{}, subJobId:{}, subJob status: {}", jobId, scheduleJob.getJobId(), status);
                // 工作流失败状态细化 优先级： 运行失败>提交失败>上游失败 > 取消（手动取消或者自动取消）
                if (TaskStatus.FROZEN_STATUS.contains(status) || TaskStatus.STOP_STATUS.contains(status)) {
                    if (!TaskStatus.FAILED.getStatus().equals(bottleStatus) && !TaskStatus.SUBMITFAILD.getStatus().equals(bottleStatus) && !TaskStatus.PARENTFAILED.getStatus().equals(bottleStatus)) {
                        if (TaskStatus.AUTOCANCELED.getStatus().equals(status)) {
                            bottleStatus = TaskStatus.AUTOCANCELED.getStatus();
                        } else {
                            bottleStatus = TaskStatus.CANCELED.getStatus();
                        }
                    }
                    canRemove = true;
                    continue;
                }
                if (TaskStatus.PARENTFAILED_STATUS.contains(status)) {
                    if (!TaskStatus.FAILED.getStatus().equals(bottleStatus) && !TaskStatus.SUBMITFAILD.getStatus().equals(bottleStatus)) {
                        bottleStatus = TaskStatus.PARENTFAILED.getStatus();
                    }
                    canRemove = true;
                    continue;
                }
                if (TaskStatus.SUBMITFAILD_STATUS.contains(status)) {
                    if (!TaskStatus.FAILED.getStatus().equals(bottleStatus)) {
                        bottleStatus = TaskStatus.SUBMITFAILD.getStatus();
                    }
                    canRemove = true;
                    continue;
                }

                if (TaskStatus.RUN_FAILED_STATUS.contains(status)) {
                    bottleStatus = TaskStatus.FAILED.getStatus();
                    LOGGER.info("flowId:{}, subJobId:{}, subJob status:{}, update bottleStatus {}", jobId, scheduleJob.getJobId(), status, bottleStatus);
                    canRemove = true;
                    break;
                }

            }

            if (bottleStatus == null) {
                //子任务不存在失败/取消的状态
                boolean isFinished = Boolean.TRUE;
                for (ScheduleJob scheduleJob : subJobs) {
                    Integer status = scheduleJob.getStatus();
                    //若存在子任务状态不是结束状态，工作流保持提交中状态
                    if (!TaskStatus.getStoppedStatus().contains(status)) {
                        isFinished = Boolean.FALSE;
                        break;
                    }
                }

                if (isFinished) {
                    bottleStatus = TaskStatus.FINISHED.getStatus();
                } else {
                    bottleStatus = TaskStatus.RUNNING.getStatus();
                }
            }
        }

        LOGGER.info("jobId:{}, bottleStatus:{}", jobId, bottleStatus);
        if (TaskStatus.FINISHED.getStatus().equals(bottleStatus) || TaskStatus.FAILED.getStatus().equals(bottleStatus)
                || TaskStatus.PARENTFAILED.getStatus().equals(bottleStatus) || TaskStatus.SUBMITFAILD.getStatus().equals(bottleStatus)
                || TaskStatus.CANCELED.getStatus().equals(bottleStatus) || TaskStatus.AUTOCANCELED.getStatus().equals(bottleStatus)) {
            //更新结束时间时间
            ScheduleJob updateJob = new ScheduleJob();
            updateJob.setJobId(jobId);
            updateJob.setStatus(bottleStatus);
            updateJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
            updateJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
            scheduleJobService.updateStatusWithExecTime(updateJob);
        } else {
            //更新工作流状态
            scheduleJobService.updateJobStatusByJobIds(Lists.newArrayList(jobId), bottleStatus, null);
        }

        if (TaskStatus.getStoppedStatus().contains(bottleStatus)) {
            LOGGER.info("jobId:{} is WORK_FLOW or ALGORITHM_LAB son is execution complete update phaseStatus to execute_over.", jobId);
            scheduleJobService.updatePhaseStatusById(workFlowJob.getId(), JobPhaseStatus.CREATE, JobPhaseStatus.EXECUTE_OVER);
        }
        return canRemove;
    }
}
