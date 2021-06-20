package com.dtstack.engine.datasource.vo.datasource.api;

import com.dtstack.fasterxml.jackson.annotation.JsonFormat;
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
@ApiModel("外部产品引入数据源列表视图类")
public class DsServiceListVO implements Serializable {

    @ApiModelProperty("数据源主键id")
    private Long dataInfoId;

    @ApiModelProperty(value = "数据源名称", example = "mysql")
    private String dataName;

    @ApiModelProperty(value = "数据源type", notes = "映射 com.dtstack.engine.datasource.common.enums.datasource.DataSourceTypeEnum val值")
    private Integer type;

    @ApiModelProperty("数据源类型")
    private String dataType;

    @ApiModelProperty("数据源版本")
    private String dataVersion;

    @ApiModelProperty(value = "数据源描述")
    private String dataDesc;

    @ApiModelProperty("数据源连接信息 json")
    private String linkJson;

    @ApiModelProperty("是否有meta标志 0-否 1-是")
    private Integer isMeta;

//    @ApiModelProperty("数据源所有连接信息")
//    private String dataJson;

    @ApiModelProperty("连接状态 0-连接失败, 1-正常")
    private Integer status;

    @ApiModelProperty("是否开启kerberos ")
    private Boolean openKerberos;

    @ApiModelProperty("最近修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;

}
