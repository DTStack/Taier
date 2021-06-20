package com.dtstack.engine.datasource.param.datasource;

import com.alibaba.fastjson.JSONObject;
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
@ApiModel("数据源表单内容入参类")
public class DsFormContentParam implements Serializable {

    @ApiModelProperty("模版主键id")
    private Long templateId;

    @ApiModelProperty("数据源类型")
    private String dataType;

    @ApiModelProperty("数据源版本")
    private String dataVersion;

    @ApiModelProperty("数据源表单填写数据Json参数")
    private JSONObject dataJson;


}
