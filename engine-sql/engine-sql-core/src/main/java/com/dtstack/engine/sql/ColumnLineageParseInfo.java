package com.dtstack.engine.sql;

import java.util.List;

/**
 * @author chener
 * @Classname ColumnLineageInfo
 * @Description 字段级血缘解析结果
 * @Date 2020/10/15 11:11
 * @Created chener@dtstack.com
 */
public class ColumnLineageParseInfo extends TableLineageParseInfo{
    /**
     * 字段血缘，包含数据库，表，字段三部分
     *
     */
    private List<ColumnLineage> columnLineages;

    public List<ColumnLineage> getColumnLineages() {
        return columnLineages;
    }

    public void setColumnLineages(List<ColumnLineage> columnLineages) {
        this.columnLineages = columnLineages;
    }
}
