package com.dtstack.engine.domain.po;

/**
 * @Auther: dazhi
 * @Date: 2020/12/15 4:20 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobCountPO {

    private Long projectId;

    private Integer status;

    private Integer count;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
