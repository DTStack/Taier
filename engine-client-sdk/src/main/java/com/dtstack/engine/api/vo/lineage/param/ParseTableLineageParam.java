package com.dtstack.engine.api.vo.lineage.param;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname ParserTableLineageParam
 * @Description
 * @Date 2021/1/14 15:18
 * @Created chener@dtstack.com
 */
public class ParseTableLineageParam {
    @ApiModelProperty("uic租户id")
    private Long dtUicTenantId;

    @ApiModelProperty("应用类型")
    private Integer appType;

    @ApiModelProperty("需要解析的sql")
    private String sql;

    @ApiModelProperty("默认数据源")
    private String defaultDb;

    @ApiModelProperty("数据源类型")
    private Integer dataSourceType;

    @ApiModelProperty("血缘批次码")
    private String uniqueKey;

    @ApiModelProperty("引擎数据源id")
    private Long engineDataSourceId;

    /**任务类型，周期实例、临时运行**/
    private Integer type;

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getDefaultDb() {
        return defaultDb;
    }

    public void setDefaultDb(String defaultDb) {
        this.defaultDb = defaultDb;
    }

    public Integer getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(Integer dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Long getEngineDataSourceId() {
        return engineDataSourceId;
    }

    public void setEngineDataSourceId(Long engineDataSourceId) {
        this.engineDataSourceId = engineDataSourceId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
