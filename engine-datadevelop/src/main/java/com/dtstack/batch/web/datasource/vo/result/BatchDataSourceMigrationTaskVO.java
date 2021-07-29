package com.dtstack.batch.web.datasource.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("整库同步任务信息")
public class BatchDataSourceMigrationTaskVO {
    @ApiModelProperty(value = "整库同步id", example = "1")
    private Long migrationId;

    @ApiModelProperty(value = "任务id", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "表名称", example = "table_name")
    private String tableName;

    @ApiModelProperty(value = "ide表名称", example = "ide_name")
    private String ideTableName;

    @ApiModelProperty(value = "同步状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "迁移报告", example = "report")
    private String report;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;
}