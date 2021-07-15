package com.dtstack.batch.web.datasource.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("整库同步-表列表结果信息")
public class BatchDataSourceMigrationTableListResultVO {
    @ApiModelProperty(value = "数据源ID", example = "1")
    private Long sourceId;

    @ApiModelProperty(value = "原始表名", example = "original_name")
    private String originalTableName;

    @ApiModelProperty(value = "修改后表名", example = "modify_name")
    private String modifyTableName;

    @ApiModelProperty(value = "任务状态", example = "1")
    private Integer taskStatus;

    @ApiModelProperty(value = "任务失败描述", example = "1")
    private String taskReport;

    @ApiModelProperty(value = "创建人id", example = "1")
    private Long createUserId;

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