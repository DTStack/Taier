package com.dtstack.engine.datasource.param.datasource;

import com.dtstack.engine.datasource.param.PubSvcBaseParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("产品授权参数")
public class ProductAuthParam extends PubSvcBaseParam {

    @ApiModelProperty("数据源Id")
    private Long dataInfoId;

    @ApiModelProperty("产品类型")
    private List<Integer> appTypes;

    @ApiModelProperty(value = "是否授权，0为取消授权，1为授权", example = "0")
    private Integer isAuth;
}
