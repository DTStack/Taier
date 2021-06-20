package com.dtstack.engine.datasource.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("数据源对应产品列表视图类")
public class DsAppListVO implements Serializable {

    @ApiModelProperty("产品type")
    private Integer appType;

    @ApiModelProperty("产品编码")
    private String appCode;

    @ApiModelProperty("产品名称")
    private String appName;


}
