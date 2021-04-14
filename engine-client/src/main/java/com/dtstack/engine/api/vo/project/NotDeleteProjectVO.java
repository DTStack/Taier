package com.dtstack.engine.api.vo.project;

import com.dtstack.engine.api.vo.task.NotDeleteTaskVO;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/3/22 9:51 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NotDeleteProjectVO {

    private String taskName;

    private List<NotDeleteTaskVO> notDeleteTaskVOList;
    
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<NotDeleteTaskVO> getNotDeleteTaskVOList() {
        return notDeleteTaskVOList;
    }

    public void setNotDeleteTaskVOList(List<NotDeleteTaskVO> notDeleteTaskVOList) {
        this.notDeleteTaskVOList = notDeleteTaskVOList;
    }
}
