package com.dtstack.taier.develop.service.template.clickhouse;

import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.rdbms.RDBWriter;

import java.util.List;

/**
 * <a href="https://github.com/DTStack/chunjun/blob/master/chunjun-examples/json/clickhouse/clickhouse.json">...</a>
 *
 * @author leon
 * @date 2022-10-12 14:54
 **/
public class ClickHouseWriter extends RDBWriter {

    private List<String> fullColumnName;
    private List<String> getFullColumnType;

    @Override
    public String pluginName() {
        return PluginName.Clichhouse_W;
    }

    public List<String> getFullColumnName() {
        return fullColumnName;
    }

    public void setFullColumnName(List<String> fullColumnName) {
        this.fullColumnName = fullColumnName;
    }

    public List<String> getGetFullColumnType() {
        return getFullColumnType;
    }

    public void setGetFullColumnType(List<String> getFullColumnType) {
        this.getFullColumnType = getFullColumnType;
    }
}
