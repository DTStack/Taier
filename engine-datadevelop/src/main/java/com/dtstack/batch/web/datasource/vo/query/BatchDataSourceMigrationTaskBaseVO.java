package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("整库同步任务信息")
public class BatchDataSourceMigrationTaskBaseVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "整库同步id", example = "1", required = true)
    private Long migrationId;

    @ApiModelProperty(value = "任务id", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "表名称", example = "table_name", required = true)
    private String tableName;

    @ApiModelProperty(value = "ide表名称", example = "ide_name", required = true)
    private String ideTableName;

    @ApiModelProperty(value = "同步状态", example = "1", required = true)
    private Integer status;

    @ApiModelProperty(value = "迁移报告", example = "report", required = true)
    private String report;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型 RDOS(1) DQ(2), API(3) TAG(4) MAP(5) CONSOLE(6) STREAM(7) DATASCIENCE(8)", example = "1", required = true)
    private Integer appType;

    @ApiModelProperty(value = "任务id", example = "1", required = true)
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55", required = true)
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55", required = true)
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1", required = true)
    private Integer isDeleted = 0;
}
