package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.domain.BatchJobJob;
import org.springframework.beans.BeanUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class BatchJobJobTaskDTO extends BatchJobJob {

    private Long taskId;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public BatchJobJob toJobJob() {
        BatchJobJob batchJobJob = new BatchJobJob();
        BeanUtils.copyProperties(this, batchJobJob);
        return batchJobJob;
    }
}
