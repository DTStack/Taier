package com.dtstack.taier.develop.bo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 搜索数据源版本入参
 * @description:
 * @author: liuxx
 * @date: 2021/3/18
 */
@ApiModel("搜索数据源版本入参")
public class DsVersionSearchParam extends PubSvcBaseParam {

    @ApiModelProperty(value = "数据源类型编码", required = true)
    private String dataType;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
