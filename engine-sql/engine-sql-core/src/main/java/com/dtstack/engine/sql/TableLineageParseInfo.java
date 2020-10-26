package com.dtstack.engine.sql;

import java.util.List;

/**
 * @author chener
 * @Classname TableLineageInfo
 * @Description 表级血缘解析结果对象
 * @Date 2020/10/15 11:05
 * @Created chener@dtstack.com
 */
public class TableLineageParseInfo extends SqlParseInfo {

    /**
     * 表级血缘  包含数据库 表
     */
    private List<TableLineage> tableLineages;

    /**
     * sql中涉及到的表
     */
    private List<Table> tables;

    public List<TableLineage> getTableLineages() {
        return tableLineages;
    }

    public void setTableLineages(List<TableLineage> tableLineages) {
        this.tableLineages = tableLineages;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
}
