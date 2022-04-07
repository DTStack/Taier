package com.dtstack.taier.develop.bo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("数据源l类型列表查询参数")
public class DsTypeListParam extends PubSvcBaseParam {
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @NotNull(message = "type not null")
    @ApiModelProperty(value = "data_source_code")
    private Integer type;
}