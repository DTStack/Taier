package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Forbidden;

public interface BatchFlowWorkJobService {
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
    public boolean checkRemoveAndUpdateFlowJobStatus(String jobId,Integer appType);
}
