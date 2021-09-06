package com.dtstack.batch.web.datamask.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脱敏开启/关闭上下游信息")
public class BatchDataMaskConfigChainVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "脱敏id", example = "1", required = true)
    private Long configId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "脱敏表id", example = "1", required = true)
    private Long tableId;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "表字段名称", example = "col_name", required = true)
    private String column;

    @ApiModelProperty(value = "开启/关闭脱敏 0-正常 1-禁用", example = "0", required = true)
    private Integer enable = 0;

    @ApiModelProperty(value = "op类型", example = "0", required = true)
    private Integer opType;
}
