package com.dtstack.taier.develop.service.develop.savetask;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.mapstruct.vo.TaskMapstructTransfer;

/**
 * @Author: zhichen
 * @Date: 2022/05/29/4:55 PM
 */
public abstract class DevelopAddOrUpdateTaskTemplate {


    public abstract TaskResourceParam handleParam(TaskResourceParam taskResourceParam);

    public abstract EScheduleJobType getEScheduleJobType();

    /**
     * 任务编辑添加入口
     *
     * @param taskResourceParam
     * @return
     */
    public TaskVO addOrUpdate(TaskResourceParam taskResourceParam) {
        handleParam(taskResourceParam);
        TaskVO taskVO = TaskMapstructTransfer.INSTANCE.TaskResourceParamToTaskVO(taskResourceParam);
        if (taskVO.getId() != null && taskVO.getId() > 0) {//update
            updateTask(taskVO);
        } else {
            addTask(taskVO);
        }
        return taskVO;
    }

    public abstract void addTask(TaskVO taskVO);

    public abstract void updateTask(TaskVO taskVO);

}
