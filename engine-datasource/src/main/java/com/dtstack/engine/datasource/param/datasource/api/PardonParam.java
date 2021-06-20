package com.dtstack.engine.datasource.param.datasource.api;

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
@ApiModel("裸数组入参容器类")
public class PardonParam<T> extends PubSvcBaseParam {

    @ApiModelProperty("数组入参")
    private List<T> paramList;

}
