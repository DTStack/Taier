package com.dtstack.taier.develop.bo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 搜索数据源版本入参
 * @description:
 * @author: liuxx
 * @date: 2021/3/18
 */
@Data
@ApiModel("搜索数据源版本入参")
public class DsVersionSearchParam extends PubSvcBaseParam {

    @ApiModelProperty(value = "数据源类型编码", required = true)
    private String dataType;

}
