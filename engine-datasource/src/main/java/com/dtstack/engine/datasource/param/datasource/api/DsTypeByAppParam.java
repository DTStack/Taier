package com.dtstack.engine.datasource.param.datasource.api;

import com.dtstack.engine.datasource.param.PubSvcBaseParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("引入数据源下拉数据源类型列表入参")
public class DsTypeByAppParam extends PubSvcBaseParam {

    @ApiModelProperty(value = "产品type", required = true)
    private Integer appType;

}
