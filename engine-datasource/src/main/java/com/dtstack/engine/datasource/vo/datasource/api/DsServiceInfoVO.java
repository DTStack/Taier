package com.dtstack.engine.datasource.vo.datasource.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("数据源实例详细信息视图类")
public class DsServiceInfoVO implements Serializable {

    @ApiModelProperty("数据源实例主键Id")
    private Long dataInfoId;

    @ApiModelProperty(value = "数据源类型", example = "MySQL, Hive")
    private String dataType;

    @ApiModelProperty("数据源版本")
    private String dataVersion;

    @ApiModelProperty(value = "数据源type", notes = "映射 com.dtstack.engine.datasource.common.enums.datasource.DataSourceTypeEnum val值")
    private Integer type;

    @ApiModelProperty("数据源名称")
    private String dataName;

    @ApiModelProperty("数据源简介")
    private String dataDesc;

    @ApiModelProperty("数据源连接信息 json, 默认为Base64密文")
    private String linkJson;

    @ApiModelProperty("数据源全部信息 json, 默认为Base64密文")
    private String dataJson;

    @ApiModelProperty("连接状态 0-连接失败, 1-正常")
    private Integer status;

    @ApiModelProperty("是否为默认数据源 0-否 1-是")
    private Integer isMeta;

    @ApiModelProperty("数据源创建时间")
    private Date gmtCreate;

    @ApiModelProperty("数据源最近修改时间")
    private Date gmtModified;

    @ApiModelProperty("kerberos路径")
    private String kerberosDir;

}
