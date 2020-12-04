package com.dtstack.sql.flink.api;


import java.util.List;

/**
 * @author chener
 * @Classname TableLineage
 * @Description flinkSql解析血缘表结果
 * @Date 2020/10/20 15:44
 * @Created chener@dtstack.com
 */
public class FlinkTableLineage {
    private List<TableMata> sourceTables;
    private List<TableMata> sideTables;
    private List<TableMata> sinkTables;

    public List<TableMata> getSourceTables() {
        return sourceTables;
    }

    public void setSourceTables(List<TableMata> sourceTables) {
        this.sourceTables = sourceTables;
    }

    public List<TableMata> getSideTables() {
        return sideTables;
    }

    public void setSideTables(List<TableMata> sideTables) {
        this.sideTables = sideTables;
    }

    public List<TableMata> getSinkTables() {
        return sinkTables;
    }

    public void setSinkTables(List<TableMata> sinkTables) {
        this.sinkTables = sinkTables;
    }
}
