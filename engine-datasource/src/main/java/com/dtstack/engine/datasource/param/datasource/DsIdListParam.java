package com.dtstack.engine.datasource.param.datasource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
public class DsIdListParam implements Serializable {

    @ApiModelProperty("数据源Id列表")
    private List<Long> dataInfoIdList;
}
