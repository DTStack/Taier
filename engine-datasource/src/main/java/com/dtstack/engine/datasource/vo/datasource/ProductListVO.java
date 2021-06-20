package com.dtstack.engine.datasource.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
/**
 *
 * @author 全阅
 * @Description:
 * @Date: 2021/3/9 14:22
 */
@ApiModel("产品列表信息")
@Data
public class ProductListVO {

    @ApiModelProperty(value = "产品type")
    private Integer appType;

    @ApiModelProperty(value = "产品名称", example = "产品名称，如离线开发，实时开发")
    private String appName;
}
