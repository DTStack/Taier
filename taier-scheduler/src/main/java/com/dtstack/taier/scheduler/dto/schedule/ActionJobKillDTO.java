package com.dtstack.taier.scheduler.dto.schedule;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/29 11:30 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ActionJobKillDTO {

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 开始计划运行时间
     */
    private Long cycStartDay;

    /**
     * 结束计划运行时间
     */
    private Long cycEndTimeDay;

    /**
     * 任务类型
     */
    private Integer type;

    /**
     * 指定周期类型任务
     */
    private List<Integer> taskPeriodList;

    /**
     * 指定任务
     */
    private List<Long> taskIds;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCycStartDay() {
        return cycStartDay;
    }

    public void setCycStartDay(Long cycStartDay) {
        this.cycStartDay = cycStartDay;
    }

    public Long getCycEndTimeDay() {
        return cycEndTimeDay;
    }

    public void setCycEndTimeDay(Long cycEndTimeDay) {
        this.cycEndTimeDay = cycEndTimeDay;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Integer> getTaskPeriodList() {
        return taskPeriodList;
    }

    public void setTaskPeriodList(List<Integer> taskPeriodList) {
        this.taskPeriodList = taskPeriodList;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }
}
