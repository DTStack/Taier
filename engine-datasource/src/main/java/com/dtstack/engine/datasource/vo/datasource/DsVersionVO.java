package com.dtstack.engine.datasource.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("数据源版本视图类")
public class DsVersionVO implements Serializable {

    @ApiModelProperty("数据源类型")
    private String dataType;

    @ApiModelProperty("数据源版本")
    private String dataVersion;

    @ApiModelProperty("排序字段")
    private Integer sorted;



}
