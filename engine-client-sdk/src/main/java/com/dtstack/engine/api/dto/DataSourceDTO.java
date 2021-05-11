package com.dtstack.engine.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 11:09 上午 2020/10/29
 */
@ApiModel
public class DataSourceDTO {

    @ApiModelProperty(value = "数据源id")
    private Long dataSourceId;

    @ApiModelProperty(value = "数据源名称",required = true)
    private String sourceName;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "schemaName")
    private String schemaName;

    @ApiModelProperty(value = "数据源配置")
    private String dataJson;

    @ApiModelProperty(value = "kerberos配置")
    private String kerberosConf;

    @ApiModelProperty(value = "数据源类型")
    private Integer sourceType;

    @ApiModelProperty(value = "应用类型")
    private Integer appType;

    @ApiModelProperty(value = "dtuic租户id")
    private Long dtUicTenantId;

    @ApiModelProperty(value = "平台sourceId")
    private Long sourceId;

    @ApiModelProperty(value = "是否是默认数据源")
    private Integer isDefault;


    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public String getKerberosConf() {
        return kerberosConf;
    }

    public void setKerberosConf(String kerberosConf) {
        this.kerberosConf = kerberosConf;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }
}
