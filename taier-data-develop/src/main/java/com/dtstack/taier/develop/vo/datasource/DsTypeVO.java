package com.dtstack.taier.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 数据源类型视图类
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
@ApiModel("数据源类型视图类")
public class DsTypeVO implements Serializable {

    @ApiModelProperty("数据源类型主键id")
    private Long typeId;

    @ApiModelProperty("数据源类型唯一编码")
    private String dataType;

    @ApiModelProperty("数据源图片url")
    private String imgUrl;

    @ApiModelProperty("该数据源是否含有版本")
    private Boolean haveVersion;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Boolean getHaveVersion() {
        return haveVersion;
    }

    public void setHaveVersion(Boolean haveVersion) {
        this.haveVersion = haveVersion;
    }
}
