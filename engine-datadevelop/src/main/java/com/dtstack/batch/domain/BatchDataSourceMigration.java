package com.dtstack.batch.domain;

import lombok.Data;

@Data
public class BatchDataSourceMigration extends TenantProjectEntity {

    private Long dataSourceId;
    private String scheduleConf;
    private Integer syncType;
    private String timeFieldIdentifier;
    private Integer parallelType;
    private String parallelConf;
    private String transformFieldConfig;
    private Long createUserId;

}