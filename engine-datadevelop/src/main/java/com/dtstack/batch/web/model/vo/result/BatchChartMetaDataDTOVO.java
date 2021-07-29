package com.dtstack.batch.web.model.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("元数据信息")
public class BatchChartMetaDataDTOVO {

    @ApiModelProperty(value = "名称", example = "ruomu")
    private String name;

    @ApiModelProperty(value = "数据")
    private List<Object> data;
}
