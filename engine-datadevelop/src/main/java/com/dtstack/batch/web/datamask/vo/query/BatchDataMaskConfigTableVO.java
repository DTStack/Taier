package com.dtstack.batch.web.datamask.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脱敏获取表信息")
public class BatchDataMaskConfigTableVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "脱敏表id", example = "1", required = true)
    private Long tableId;
}
