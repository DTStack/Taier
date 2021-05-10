package com.dtstack.engine.api.vo.schedule.task.shade;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 2:08 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleTaskShadeCountTaskVO {

    private Integer deployCount;

    private String projectId;

    public Integer getDeployCount() {
        return deployCount;
    }

    public void setDeployCount(Integer deployCount) {
        this.deployCount = deployCount;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
