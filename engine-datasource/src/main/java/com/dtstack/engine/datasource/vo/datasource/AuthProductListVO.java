package com.dtstack.engine.datasource.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("授权产品列表")
public class AuthProductListVO extends ProductListVO {

    @ApiModelProperty(value = "是否授权，0为未授权，1为已经授权", example = "0")
    private Integer isAuth;

    @ApiModelProperty(value = "是否被引用, 0-未引用 1-已被引用")
    private Integer isImport;
}
