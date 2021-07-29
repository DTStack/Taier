package com.dtstack.batch.web.model.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("字段不规范趋势返回结果")
public class BatchChartDataResultVO {

    @ApiModelProperty(value = "x轴数据")
    protected BatchChartMetaDataDTOVO x;

    @ApiModelProperty(value = "类型，对应图上的多条线")
    protected BatchChartMetaDataDTOVO type;

    @ApiModelProperty(value = "y轴数据")
    protected List<BatchChartMetaDataDTOVO> y;
}
