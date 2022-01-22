package com.dtstack.taiga.develop.bo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 基础服务入参基类
 * @description:
 * @author: liuxx
 * @date: 2021/3/18
 */
@Data
@ApiModel("基础服务入参基类")
public class PubSvcBaseParam {

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private String dtToken;

}
