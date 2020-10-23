package com.dtstack.engine.api.vo.lineage;


import com.dtstack.engine.api.pojo.lineage.Table;

/**
 * @author chener
 * @Classname SqlInfo
 * @Description 解析sql基本信息对象
 * @Date 2020/10/15 10:58
 * @Created chener@dtstack.com
 */
public class SqlParseInfo extends BaseParseResult{

    /**
     * 主数据库
     */
    private String mainDb;

    /**
     * DDL、DML操作的表对象
     */
    private Table mainTable;

    public String getMainDb() {
        return mainDb;
    }

    public void setMainDb(String mainDb) {
        this.mainDb = mainDb;
    }

    public Table getMainTable() {
        return mainTable;
    }

    public void setMainTable(Table mainTable) {
        this.mainTable = mainTable;
    }
}
