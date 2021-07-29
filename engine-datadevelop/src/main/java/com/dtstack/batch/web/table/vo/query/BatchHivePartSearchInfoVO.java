package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分区信息")
public class BatchHivePartSearchInfoVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "项目 ID", required = true)
    private Long projectId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "表 ID", example = "1", required = true)
    private Long tableId;

    @ApiModelProperty(value = "分区名称 模糊查询", example = "desc")
    private String partitionName;

    @ApiModelProperty(value = "排序字段", example = "gmt_modified最后修改时间 file_count文件数量 store_size占用存储", required = true)
    private String sortColumn = "gmt_create";

    @ApiModelProperty(value = "排序方式", example = "desc", required = true)
    private String sort = "desc";

    @ApiModelProperty(value = "展示条数", example = "10", required = true)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "当前页数", example = "1", required = true)
    private Integer pageIndex = 1;
}
