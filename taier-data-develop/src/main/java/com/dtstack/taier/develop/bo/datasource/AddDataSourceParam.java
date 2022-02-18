package com.dtstack.taier.develop.bo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 新增数据源整体入参
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    public String getDataJsonString() {
        return dataJsonString;
    }

    public void setDataJsonString(String dataJsonString) {
        this.dataJsonString = dataJsonString;
    }
}
