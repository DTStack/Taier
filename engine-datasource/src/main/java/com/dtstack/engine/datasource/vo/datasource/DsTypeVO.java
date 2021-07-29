package com.dtstack.engine.datasource.vo.datasource;

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


}
