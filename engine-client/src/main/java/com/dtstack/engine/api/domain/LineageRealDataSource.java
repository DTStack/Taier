package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname RealDataSource
 * @Description TODO
 * @Date 2020/10/22 20:08
 * @Created chener@dtstack.com
 */
@ApiModel
public class LineageRealDataSource extends DtUicTenantEntity{

    @ApiModelProperty(notes = "数据源名称")
    private String sourceName;

    @ApiModelProperty(notes = "数据源key")
    private String sourceKey;

    @ApiModelProperty(notes = "数据源类型")
    private Integer sourceType;

    @ApiModelProperty(notes = "数据源配置信息")
    private String dataJson;

    @ApiModelProperty(notes = "数据源kerberos配置")
    private String kerberosConf;

    @ApiModelProperty(notes = "是否开启kerberos")
    private Integer openKerberos;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJason(String dataJason) {
        this.dataJson = dataJason;
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
}
