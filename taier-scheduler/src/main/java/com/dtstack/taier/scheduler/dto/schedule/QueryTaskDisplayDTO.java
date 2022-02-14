package com.dtstack.taier.scheduler.dto.schedule;

/**
 * @Auther: dazhi
 * @Date: 2021/12/26 11:30 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryTaskDisplayDTO {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 查询层级
     */
    private Integer level;

    /**
     * 方向
     */
    private Integer directType;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getDirectType() {
        return directType;
    }

    public void setDirectType(Integer directType) {
        this.directType = directType;
    }
}
