package com.dtstack.taier.develop.service.template.clickhouse;

import com.dtstack.taier.develop.dto.devlop.ColumnDTO;

import java.util.List;

/**
 * @author leon
 * @date 2022-10-11 19:57
 **/
public class ClickHouseReaderParam {

    private Integer sourceId;

    private List<ColumnDTO> column;

    private String split;

    private String where;

    private String table;

    private String extralConfig;

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public List<ColumnDTO> getColumn() {
        return column;
    }

    public void setColumn(List<ColumnDTO> column) {
        this.column = column;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getExtralConfig() {
        return extralConfig;
    }

    public void setExtralConfig(String extralConfig) {
        this.extralConfig = extralConfig;
    }
}
