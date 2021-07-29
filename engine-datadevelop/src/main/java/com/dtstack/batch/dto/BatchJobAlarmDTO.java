package com.dtstack.batch.dto;

import lombok.Data;

/**
 * @author yuebai
 * @date 2020-03-10
 */
@Data
public class BatchJobAlarmDTO {
    private Long taskId;

    private Long engineJobId;

    private Integer status;

    public BatchJobAlarmDTO() {
    }

    public BatchJobAlarmDTO(Long taskId, Long engineJobId, Integer status) {
        this.taskId = taskId;
        this.engineJobId = engineJobId;
        this.status = status;
    }
}
