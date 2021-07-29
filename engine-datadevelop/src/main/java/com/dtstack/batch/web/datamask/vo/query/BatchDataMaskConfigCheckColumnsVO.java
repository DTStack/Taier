package com.dtstack.batch.web.datamask.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脱敏检查上游表字段信息")
public class BatchDataMaskConfigCheckColumnsVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "表id", example = "1", required = true)
    private Long tableId;

    @ApiModelProperty(value = "表名称", example = "test", required = true)
    private String tableName;

    @ApiModelProperty(value = "表所属项目id", example = "1", required = true)
    private Long belongProjectId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "表字段名称", example = "col_name", required = true)
    private String column;
}
