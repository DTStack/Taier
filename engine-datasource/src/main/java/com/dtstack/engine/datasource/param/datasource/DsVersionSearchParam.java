package com.dtstack.engine.datasource.param.datasource;

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
@ApiModel("搜索数据源版本入参")
public class DsVersionSearchParam extends PubSvcBaseParam {

    @ApiModelProperty(value = "数据源类型编码", required = true)
    private String dataType;

}
