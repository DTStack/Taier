package com.dtstack.taier.scheduler.dto.fill;

import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:48 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataChooseTaskDTO {

    /**
     * 任务id
     */
    private Long taskId;


    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FillDataChooseTaskDTO that = (FillDataChooseTaskDTO) o;
        return Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
}
