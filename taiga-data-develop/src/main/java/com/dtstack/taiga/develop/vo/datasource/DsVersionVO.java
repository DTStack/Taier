package com.dtstack.taiga.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据源版本视图类
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
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
