package com.dtstack.batch.web.table.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表血缘信息")
public class BatchTableBloodQueryDetailVO {

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "项目 ID", example = "1", required = true)
    private Long projectId;

    @ApiModelProperty(value = "所属项目 ID", example = "2", required = true)
    private Long belongProjectId;

    @ApiModelProperty(value = "表名", example = "dev", required = true)
    private String tableName;

    @ApiModelProperty(value = "表 ID", example = "1", required = true)
    private Long tableId;

    @ApiModelProperty(value = "数据源 ID", example = "1", required = true)
    private Long dataSourceId;

    @ApiModelProperty(value = "分页 条数", example = "10", required = true)
    private Integer pageSize = 0;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageIndex = 0;

}
