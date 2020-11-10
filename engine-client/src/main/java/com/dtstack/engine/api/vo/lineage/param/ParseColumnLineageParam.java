package com.dtstack.engine.api.vo.lineage.param;

import com.dtstack.engine.api.pojo.lineage.Column;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @author chener
 * @Classname ParseColumnLineageParam
 * @Description 查询字段血缘关系参数
 * @Date 2020/11/3 17:12
 * @Created chener@dtstack.com
 */
@ApiModel("查询字段血缘参数")
public class ParseColumnLineageParam {

    @ApiModelProperty("uic租户id")
    private Long dtUicTenantId;

    @ApiModelProperty("应用类型")
    private Integer appType;

    @ApiModelProperty("uic租户id")
    private Integer uicTenantId;

    @ApiModelProperty("需要解析的sql")
    private String sql;

    @ApiModelProperty("默认数据源")
    private String defaultDb;

    @ApiModelProperty("数据源类型")
    private Integer dataSourceType;

    @ApiModelProperty("引擎数据源id")
    private Long engineDataSourceId;

    @ApiModelProperty("表字段map")
    private Map<String, List<Column>> tableColumnsMap;

    @ApiModelProperty("血缘批次码")
    private String uniqueKey;

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Integer getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(Integer dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getUicTenantId() {
        return uicTenantId;
    }

    public void setUicTenantId(Integer uicTenantId) {
        this.uicTenantId = uicTenantId;
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

    public Map<String, List<Column>> getTableColumnsMap() {
        return tableColumnsMap;
    }

    public void setTableColumnsMap(Map<String, List<Column>> tableColumnsMap) {
        this.tableColumnsMap = tableColumnsMap;
    }

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public Long getEngineDataSourceId() {
        return engineDataSourceId;
    }

    public void setEngineDataSourceId(Long engineDataSourceId) {
        this.engineDataSourceId = engineDataSourceId;
    }
}
