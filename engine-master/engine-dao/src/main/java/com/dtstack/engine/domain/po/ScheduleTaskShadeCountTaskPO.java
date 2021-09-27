package com.dtstack.engine.domain.po;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 2:08 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleTaskShadeCountTaskPO {

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
