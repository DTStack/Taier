package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表基本信息")
public class BatchTableBaseVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "是否root用户", hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "表 ID", example = "3", required = true)
    private Long tableId;

    @ApiModelProperty(value = "表类别", example = "1", required = true)
    private Integer tableType;

    @ApiModelProperty(value = "表名", example = "dev", required = true)
    private String tableName;

    @ApiModelProperty(value = "删除表名",  example = "dev", required = true)
    private String dropTable;

    @ApiModelProperty(value = "表备注",  example = "用户基本信息表", required = true)
    private String tableDesc;

    @ApiModelProperty(value = "原数据源 ID",  example = "3", required = true)
    private Long originSourceId;

    @ApiModelProperty(value = "目标数据源 ID",  example = "11", required = true)
    private Long targetSourceId;

    @ApiModelProperty(value = "分区",  example = "/dev", required = true)
    private String partition;

    @ApiModelProperty(value = "生命周期",  example = "99", required = true)
    private Integer lifeDay;

    @ApiModelProperty(value = "引擎类别",  example = "1", required = true)
    private Integer engineType;

    @ApiModelProperty(value = "sql",  example = "select * from test", required = true)
    private String sql;

    @ApiModelProperty(value = "数据源id", example = "1", required = true)
    private Long sourceId;

    @ApiModelProperty(value = "原数据源 schema 信息", example = "schema")
    private String originSchema;

    @ApiModelProperty(value = "目标数据源 schema 信息", example = "schema")
    private String targetSchema;

}
