package com.dtstack.batch.dto;

import lombok.Data;

@Data
public class BatchDirtyDataDTO {

    private String taskName;

    private String timeFlag;

    private Long dirtyDataNum;

}
