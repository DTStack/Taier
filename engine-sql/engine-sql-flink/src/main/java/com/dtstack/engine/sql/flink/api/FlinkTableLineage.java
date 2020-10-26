package com.dtstack.engine.sql.flink.api;

import java.util.List;

/**
 * @author chener
 * @Classname TableLineage
 * @Description flinkSql解析血缘表结果
 * @Date 2020/10/20 15:44
 * @Created chener@dtstack.com
 */
public class FlinkTableLineage {
    private List<String> sourceTables;
    private List<String> sideTables;
    private List<String> sinkTables;

    public List<String> getSourceTables() {
        return sourceTables;
    }

    public void setSourceTables(List<String> sourceTables) {
        this.sourceTables = sourceTables;
    }

    public List<String> getSideTables() {
        return sideTables;
    }

    public void setSideTables(List<String> sideTables) {
        this.sideTables = sideTables;
    }

    public List<String> getSinkTables() {
        return sinkTables;
    }

    public void setSinkTables(List<String> sinkTables) {
        this.sinkTables = sinkTables;
    }

    @Override
    public String toString() {
        return "TableLineage{" +
                "sourceTables=" + sourceTables +
                ", sideTables=" + sideTables +
                ", sinkTables=" + sinkTables +
                '}';
    }
}
