package com.dtstack.engine.datasource.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("数据源列表信息")
public class DsListVO {

    @ApiModelProperty("数据源Id")
    private Long dataInfoId;

    @ApiModelProperty(value = "数据源名称", example = "mysql")
    private String dataName;

    @ApiModelProperty(value = "数据源类型")
    private String dataType;

    @ApiModelProperty(value = "数据源版本号")
    private String dataVersion;

//    @ApiModelProperty(value = "数据源类型加版本号", example = "Hive1.x")
//    private String dataTypeName;

    @ApiModelProperty("已授权产品")
    private String appNames;

    @ApiModelProperty("数据源描述")
    private String dataDesc;

    @ApiModelProperty("数据源连接信息")
    private String linkJson;

    @ApiModelProperty("连接状态 0-连接失败, 1-正常")
    private Integer status;

    @ApiModelProperty("是否有meta标志 0-否 1-是")
    private Integer isMeta;

    @ApiModelProperty(value = "最近修改时间")
    private Date gmtModified;

    @ApiModelProperty(value = "是否应用，0为未应用，1为已应用", example = "0")
    private Integer isImport;
}
