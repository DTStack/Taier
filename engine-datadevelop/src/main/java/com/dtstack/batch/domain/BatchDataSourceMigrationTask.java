package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

@Data
public class BatchDataSourceMigrationTask extends TenantProjectEntity {

    private Long migrationId;
    private Long taskId;
    private String tableName;
    private String ideTableName;
    private Integer status;
    private String report;

}