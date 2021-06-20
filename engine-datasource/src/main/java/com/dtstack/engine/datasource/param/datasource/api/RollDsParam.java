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
@ApiModel("回滚数据源入参")
public class RollDsParam extends PubSvcBaseParam {

    @ApiModelProperty(value = "数据源主键id", required = true)
    private Long dataInfoId;

    @ApiModelProperty(value = "产品type", required = true)
    private Integer appType;

    @ApiModelProperty(value = "创建数据源的dtuic 租户id", required = true)
    private Long dsDtuicTenantId;

    @ApiModelProperty(value = "创建数据源的租户主键id", required = true)
    private Long dsTenantId;


}
