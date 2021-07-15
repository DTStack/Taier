package com.dtstack.batch.web.dirtydata.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("脏数据产生趋势结果信息")
public class BatchDirtyDateTrendResultVO {
    @ApiModelProperty(value = "x轴数据")
    private BatchChartMetaDataDTO x;

    @ApiModelProperty(value = "类型,对应图上的多条线")
    private BatchChartMetaDataDTO type;

    @ApiModelProperty(value = "y轴数据")
    private List<BatchChartMetaDataDTO> y;
}