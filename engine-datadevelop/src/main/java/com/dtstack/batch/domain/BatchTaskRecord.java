package com.dtstack.batch.domain;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class BatchTaskRecord extends TenantProjectEntity {
    Integer recordType;
    Long taskId;
    Long operatorId;
    Timestamp operateTime;
}
