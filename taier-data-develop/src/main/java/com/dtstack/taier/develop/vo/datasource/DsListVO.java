package com.dtstack.taier.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * SLOGAN:改变世界！改变未来！
 *
 * @author 全阅
 * @Description:
 * @Date: 2021/3/9 11:19
 */
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

    @ApiModelProperty("数据源类型")
    private Integer dataTypeCode;

    public Integer getDataTypeCode() {
        return dataTypeCode;
    }

    public void setDataTypeCode(Integer dataTypeCode) {
        this.dataTypeCode = dataTypeCode;
    }

    public Long getDataInfoId() {
        return dataInfoId;
    }

    public void setDataInfoId(Long dataInfoId) {
        this.dataInfoId = dataInfoId;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
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

    public String getDataDesc() {
        return dataDesc;
    }

    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    public String getLinkJson() {
        return linkJson;
    }

    public void setLinkJson(String linkJson) {
        this.linkJson = linkJson;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsMeta() {
        return isMeta;
    }

    public void setIsMeta(Integer isMeta) {
        this.isMeta = isMeta;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsImport() {
        return isImport;
    }

    public void setIsImport(Integer isImport) {
        this.isImport = isImport;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}
