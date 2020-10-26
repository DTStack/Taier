package com.dtstack.engine.sql;

/**
 * @author chener
 * @Classname SqlInfo
 * @Description 解析sql基本信息对象
 * @Date 2020/10/15 10:58
 * @Created chener@dtstack.com
 */
public class SqlParseInfo extends BaseParseResult{
    /**
     * DDL语句中的表(DDL语句都是针对一张表的)
     */
    private Table mainTable;

    public Table getMainTable() {
        return mainTable;
    }

    public void setMainTable(Table mainTable) {
        this.mainTable = mainTable;
    }
}
