package com.dtstack.engine.datasource.vo.datasource.api;

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
@ApiModel("数据源中心迁移返回视图类")
public class DsShiftReturnVO implements Serializable {

    @ApiModelProperty("数据源实例主键")
    private Long dataInfoId;

    @ApiModelProperty("数据源实例名称")
    private String dataName;

}
