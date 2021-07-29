package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表基本信息")
public class BatchTableColumnInfoVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "表名", example = "dev", required = true)
    private String tableName;

    @ApiModelProperty(value = "表唯一标识", example = "dev", required = true)
    private String projectIdentifier;

    @ApiModelProperty(value = "表类别", example = "1", required = true)
    private Integer tableType;

    @ApiModelProperty(value = "表 ID", example = "13", required = true)
    private Long tableId;

    @ApiModelProperty(value = "表生命周期", example = "99", required = true)
    private Integer lifeDay;

}
