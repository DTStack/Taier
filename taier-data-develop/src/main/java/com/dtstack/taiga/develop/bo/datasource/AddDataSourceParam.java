package com.dtstack.taiga.develop.bo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 新增数据源整体入参
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
@Data
@ApiModel("新增数据源整体入参")
public class AddDataSourceParam extends PubSvcBaseParam {

    @ApiModelProperty("数据源主键id")
    private Long id = 0L;

    @ApiModelProperty(value = "数据源类型", required = true)
    private String dataType;

    @ApiModelProperty("数据源版本")
    private String dataVersion;

    @ApiModelProperty(value = "数据源名称", required = true)
    private String dataName;

    @ApiModelProperty(value = "数据源描述", required = true)
    private String dataDesc;

    @ApiModelProperty(value = "数据源表单填写数据JsonString", required = true)
    private String dataJsonString;

}
