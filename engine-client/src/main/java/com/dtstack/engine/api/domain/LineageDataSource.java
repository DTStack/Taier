package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageDataSource
 * @Description
 * @Date 2020/10/22 20:14
 * @Created chener@dtstack.com
 */
@ApiModel
public class LineageDataSource extends TenantEntity {

    @ApiModelProperty(notes = "真实数据源id")
    private Long realSourceId;

    @ApiModelProperty(notes = "数据源定位码")
    private String sourceKey;

    @ApiModelProperty(notes = "数据源名称")
    private String sourceName;

    @ApiModelProperty(notes = "应用类型")
    private Integer appType;

    @ApiModelProperty(notes = "数据源类型")
    private Integer sourceType;

    @ApiModelProperty(notes = "数据源配置信息")
    private String dataJason;

    @ApiModelProperty(notes = "数据源kerberos配置")
    private String kerberosConf;

    @ApiModelProperty(notes = "是否开启kerberos")
    private Integer openKerberos;

    @ApiModelProperty(notes = "应用数据源id")
    private Integer appSourceId;

    @ApiModelProperty(notes = "是否内部数据源")
    private Integer innerSource;

    @ApiModelProperty(notes = "组件id")
    private Integer componentId;

    public Long getRealSourceId() {
        return realSourceId;
    }

    public void setRealSourceId(Long realSourceId) {
        this.realSourceId = realSourceId;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getDataJason() {
        return dataJason;
    }

    public void setDataJason(String dataJason) {
        this.dataJason = dataJason;
    }

    public String getKerberosConf() {
        return kerberosConf;
    }

    public void setKerberosConf(String kerberosConf) {
        this.kerberosConf = kerberosConf;
    }

    public Integer getOpenKerberos() {
        return openKerberos;
    }

    public void setOpenKerberos(Integer openKerberos) {
        this.openKerberos = openKerberos;
    }

    public Integer getAppSourceId() {
        return appSourceId;
    }

    public void setAppSourceId(Integer appSourceId) {
        this.appSourceId = appSourceId;
    }

    public Integer getInnerSource() {
        return innerSource;
    }

    public void setInnerSource(Integer innerSource) {
        this.innerSource = innerSource;
    }

    public Integer getComponentId() {
        return componentId;
    }

    public void setComponentId(Integer componentId) {
        this.componentId = componentId;
    }
}
