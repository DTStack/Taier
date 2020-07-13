package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.domain.ScheduleJobJob;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.BeanUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@ApiModel
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
