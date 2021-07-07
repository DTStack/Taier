package com.dtstack.batch.vo;

import lombok.Data;

@Data
public class BatchTaskRecordVO {

    private Long operateTime;
    private String operatorName;
    private String operateType;
}
