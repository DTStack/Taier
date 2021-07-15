package com.dtstack.batch.web.dirtydata.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("脏数据图表元数据信息")
public class BatchChartMetaDataDTO {
    @ApiModelProperty(value = "元数据名称", example = "data_name")
    private String name;

    @ApiModelProperty(value = "元数据列表")
    private List<Object> data;
}