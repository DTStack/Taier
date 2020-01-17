package com.dtstack.task.server.impl;

import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.engine.domain.BatchJob;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private final static List<Integer> sortedStatus = Lists.newArrayList(TaskStatus.CANCELED.getStatus(), TaskStatus.FAILED.getStatus(), TaskStatus.KILLED.getStatus(), TaskStatus.FROZEN.getStatus(), TaskStatus.CANCELING.getStatus()
            , TaskStatus.PARENTFAILED.getStatus(), TaskStatus.UNSUBMIT.getStatus(), TaskStatus.SUBMITFAILD.getStatus(), TaskStatus.SUBMITTING.getStatus(), TaskStatus.SUBMITTED.getStatus(), TaskStatus.WAITENGINE.getStatus()
            , TaskStatus.ENGINEDISTRIBUTE.getStatus(), TaskStatus.ENGINEACCEPTED.getStatus(), TaskStatus.WAITCOMPUTE.getStatus(), TaskStatus.CREATED.getStatus(), TaskStatus.SCHEDULED.getStatus(), TaskStatus.DEPLOYING.getStatus()
            , TaskStatus.RESTARTING.getStatus(), TaskStatus.RUNNING.getStatus(), TaskStatus.MANUALSUCCESS.getStatus(), TaskStatus.FINISHED.getStatus());

    @Autowired
    private BatchJobService batchJobService;

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
    public boolean checkRemoveAndUpdateFlowJobStatus(String jobId) {

        List<BatchJob> subJobs = batchJobService.getSubJobsAndStatusByFlowId(jobId);
        boolean canRemove = false;
        Integer bottleStatus = null;
        //没有子任务
        if (CollectionUtils.isEmpty(subJobs)) {
            bottleStatus = TaskStatus.FINISHED.getStatus();
            canRemove = true;
        } else {
            for (BatchJob batchJob : subJobs) {
                Integer status = batchJob.getStatus();
                // 工作流失败状态细化 优先级： 运行失败>提交失败>上游失败
                if (TaskStatusConstrant.PARENTFAILED_STATUS.contains(status)) {
                    if ( !TaskStatus.CANCELED.getStatus().equals(bottleStatus) && !TaskStatus.FAILED.getStatus().equals(bottleStatus) && !TaskStatus.SUBMITFAILD.getStatus().equals(bottleStatus)){
                        bottleStatus = TaskStatus.PARENTFAILED.getStatus();
                    }
                    canRemove = true;
                    continue;
                }
                if (TaskStatusConstrant.SUBMITFAILD_STATUS.contains(status)) {
                    if (!TaskStatus.CANCELED.getStatus().equals(bottleStatus) && !TaskStatus.FAILED.getStatus().equals(bottleStatus) ){
                        bottleStatus = TaskStatus.SUBMITFAILD.getStatus();
                    }
                    canRemove = true;
                    continue;
                }

                if (TaskStatusConstrant.RUN_FAILED_STATUS.contains(status)) {
                    if (!TaskStatus.CANCELED.getStatus().equals(bottleStatus) ){
                        bottleStatus = TaskStatus.FAILED.getStatus();
                    }
                    canRemove = true;
                    continue;
                }
                if (TaskStatusConstrant.FROZEN_STATUS.contains(status) || TaskStatusConstrant.STOP_STATUS.contains(status)) {
                    bottleStatus = TaskStatus.CANCELED.getStatus();
                    canRemove = true;
                    break;
                }
            }
            //子任务不存在失败/取消的状态
            for (BatchJob batchJob : subJobs) {
                Integer status = batchJob.getStatus();
                //若存在子任务状态不是结束状态，工作流保持提交中状态
                if (!TaskStatusConstrant.endStatusList.contains(status)) {
                    canRemove = false;
                    bottleStatus = TaskStatus.RUNNING.getStatus();
                    break;
                }
                canRemove = true;
                if (bottleStatus == null) {
                    bottleStatus = TaskStatus.FINISHED.getStatus();
                }
            }
            //子任务全部为已冻结状态
            boolean flowJobAllFrozen = true;
            for (BatchJob batchJob : subJobs){
                Integer status = batchJob.getStatus();
                if (!TaskStatusConstrant.FROZEN_STATUS.contains(status)){
                    flowJobAllFrozen = false;
                    break;
                }
            }
            if (flowJobAllFrozen){
                canRemove = true;
                bottleStatus = TaskStatus.FROZEN.getStatus();
            }
        }
        //更新工作流状态
        batchJobService.updateStatusByJobId(jobId, bottleStatus);
        return canRemove;
    }

}
