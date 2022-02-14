package com.dtstack.taier.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * SLOGAN:改变世界！改变未来！
 *
 * @author 全阅
 * @Description:
 * @Date: 2021/3/9 11:19
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

    @ApiModelProperty(value = "schema名称，离线创建的meta数据源才有")
    private String schemaName;
}
