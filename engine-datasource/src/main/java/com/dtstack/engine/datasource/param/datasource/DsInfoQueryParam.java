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
@ApiModel("查询数据源详细信息入参")
public class DsInfoQueryParam extends PubSvcBaseParam {

    @ApiModelProperty(value = "数据源实例主键id", required = true)
    private Long dataInfoId;

}
