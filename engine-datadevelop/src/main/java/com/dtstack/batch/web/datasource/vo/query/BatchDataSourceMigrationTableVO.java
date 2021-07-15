package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("整库同步表信息")
public class BatchDataSourceMigrationTableVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "数据源id", example = "1", required = true)
    private Long sourceId;

    @ApiModelProperty(value = "是否为系统用户", example = "false", required = true)
    private Boolean isSys = false;

    @ApiModelProperty(value = "当前页数", example = "1", required = true)
    private Integer currentPage;

    @ApiModelProperty(value = "展示条数", example = "10", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "是否为first", example = "false", required = true)
    private Boolean isFirst = false;
}
