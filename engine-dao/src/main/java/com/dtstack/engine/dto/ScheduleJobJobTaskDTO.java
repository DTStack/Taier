package com.dtstack.engine.dto;

import org.springframework.beans.BeanUtils;
import com.dtstack.engine.domain.ScheduleJobJob;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class ScheduleJobJobTaskDTO extends ScheduleJobJob {

    private Long taskId;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public ScheduleJobJob toJobJob() {
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        BeanUtils.copyProperties(this, scheduleJobJob);
        return scheduleJobJob;
    }
}
