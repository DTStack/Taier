package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel("数据源字段信息")
public class BatchDataSourceColumnsVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "字段信息", required = true)
    private Map<String, String> columns;
}
