package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class BatchFlowWorkJobService {

    /**
     * 任务状态从低到高排序
     */
    private final static List<Integer> sortedStatus = Lists.newArrayList(RdosTaskStatus.CANCELED.getStatus(), RdosTaskStatus.FAILED.getStatus(), RdosTaskStatus.KILLED.getStatus(), RdosTaskStatus.FROZEN.getStatus(), RdosTaskStatus.CANCELLING.getStatus()
            , RdosTaskStatus.PARENTFAILED.getStatus(), RdosTaskStatus.UNSUBMIT.getStatus(), RdosTaskStatus.SUBMITFAILD.getStatus(), RdosTaskStatus.SUBMITTING.getStatus(), RdosTaskStatus.SUBMITTED.getStatus(), RdosTaskStatus.WAITENGINE.getStatus()
            , RdosTaskStatus.ENGINEDISTRIBUTE.getStatus(), RdosTaskStatus.ENGINEACCEPTED.getStatus(), RdosTaskStatus.WAITCOMPUTE.getStatus(), RdosTaskStatus.CREATED.getStatus(), RdosTaskStatus.SCHEDULED.getStatus(), RdosTaskStatus.DEPLOYING.getStatus()
            , RdosTaskStatus.RESTARTING.getStatus(), RdosTaskStatus.RUNNING.getStatus(), RdosTaskStatus.MANUALSUCCESS.getStatus(), RdosTaskStatus.FINISHED.getStatus());

    @Autowired
    private ScheduleJobService batchJobService;

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
     * <br>&nbsp;&nbsp;e.若子任务中同时存在运行失败或取消状态，工作流状态更新为取消状态</br>
     * <br>&nbsp;&nbsp;f.其他工作流更新为运行中状态</br>
     *
     * @param jobId
     */
    @Forbidden
    public boolean checkRemoveAndUpdateFlowJobStatus(String jobId,Integer appType) {

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
                // 工作流失败状态细化 优先级： 运行失败>提交失败>上游失败
                if (RdosTaskStatus.PARENTFAILED_STATUS.contains(status)) {
                    if ( !RdosTaskStatus.CANCELED.getStatus().equals(bottleStatus) && !RdosTaskStatus.FAILED.getStatus().equals(bottleStatus) && !RdosTaskStatus.SUBMITFAILD.getStatus().equals(bottleStatus)){
                        bottleStatus = RdosTaskStatus.PARENTFAILED.getStatus();
                    }
                    canRemove = true;
                    continue;
                }
                if (RdosTaskStatus.SUBMITFAILD_STATUS.contains(status)) {
                    if (!RdosTaskStatus.CANCELED.getStatus().equals(bottleStatus) && !RdosTaskStatus.FAILED.getStatus().equals(bottleStatus) ){
                        bottleStatus = RdosTaskStatus.SUBMITFAILD.getStatus();
                    }
                    canRemove = true;
                    continue;
                }

                if (RdosTaskStatus.RUN_FAILED_STATUS.contains(status)) {
                    if (!RdosTaskStatus.CANCELED.getStatus().equals(bottleStatus) ){
                        bottleStatus = RdosTaskStatus.FAILED.getStatus();
                    }
                    canRemove = true;
                    continue;
                }
                if (RdosTaskStatus.FROZEN_STATUS.contains(status) || RdosTaskStatus.STOP_STATUS.contains(status)) {
                    bottleStatus = RdosTaskStatus.CANCELED.getStatus();
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
            batchJobService.updateStatusByJobId(jobId, bottleStatus);
        }
        return canRemove;
    }

}
