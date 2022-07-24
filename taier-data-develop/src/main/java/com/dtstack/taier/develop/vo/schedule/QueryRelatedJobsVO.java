package com.dtstack.taier.develop.vo.schedule;

import com.dtstack.taier.develop.dto.devlop.TaskVO;

import java.util.List;

/**
 * @Author: 安陌
 * @CreateTime: 2022-07-23  17:47
 * @Description: 工作流返回实例
 * @Version: 1.0
 */
public class QueryRelatedJobsVO {


    private TaskVO taskVOList;

    private ReturnJobListVO returnJobListVOS;

    public TaskVO getTaskVOList() {
        return taskVOList;
    }

    public void setTaskVOList(TaskVO taskVOList) {
        this.taskVOList = taskVOList;
    }

    public ReturnJobListVO getReturnJobListVOS() {
        return returnJobListVOS;
    }

    public void setReturnJobListVOS(ReturnJobListVO returnJobListVOS) {
        this.returnJobListVOS = returnJobListVOS;
    }
}
