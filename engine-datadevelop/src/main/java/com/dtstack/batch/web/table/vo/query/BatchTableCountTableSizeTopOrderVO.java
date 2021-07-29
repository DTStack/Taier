package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表统计信息")
public class BatchTableCountTableSizeTopOrderVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "是否admin用户", hidden = true)
    private Boolean isAdmin;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "是否展示全部项目", example = "true",  required = true)
    private Boolean total;

    @ApiModelProperty(value = "展示条数", example = "5",  required = true)
    private Integer dataNum;

}
