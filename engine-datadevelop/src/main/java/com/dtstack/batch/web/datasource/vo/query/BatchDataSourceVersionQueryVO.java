package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据同步-数据源 信息查询")
public class BatchDataSourceVersionQueryVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "数据源ID", example = "1", required = true)
    private Long dataSourceId;

}
