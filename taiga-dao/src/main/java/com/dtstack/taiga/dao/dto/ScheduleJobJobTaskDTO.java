package com.dtstack.taiga.dao.dto;

import com.dtstack.taiga.dao.domain.ScheduleJobJob;
import org.springframework.beans.BeanUtils;

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
