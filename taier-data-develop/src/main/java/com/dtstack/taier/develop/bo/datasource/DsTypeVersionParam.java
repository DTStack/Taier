package com.dtstack.taier.develop.bo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 数据源类型和版本统一入参
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
@ApiModel("数据源类型和版本统一入参")
public class DsTypeVersionParam extends PubSvcBaseParam {

    @ApiModelProperty("数据源类型 如MySql, Oracle")
    private String dataType;

    @ApiModelProperty("数据源版本, 可为空")
    private String dataVersion;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }
}
