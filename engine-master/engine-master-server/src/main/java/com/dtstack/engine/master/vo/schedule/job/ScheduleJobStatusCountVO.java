package com.dtstack.engine.master.vo.schedule.job;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 10:37 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobStatusCountVO {

    private String taskName;

    /**
     * 任务状态名称（替换taskName）
     */
    private String taskStatusName;

    private Integer count;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getTaskStatusName() {
        return taskStatusName;
    }

    public void setTaskStatusName(String taskStatusName) {
        this.taskStatusName = taskStatusName;
    }
}
