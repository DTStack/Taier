package com.dtstack.engine.sql.node;

import com.dtstack.engine.sql.Column;
import org.apache.calcite.sql.SqlNode;

import java.util.List;
import java.util.Map;

public class DropNode extends Node {
    /**
     * 被删除的表
     */
    private Identifier targetTable;

    public DropNode(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb, tableColumnsMap);
    }

    @Override
    public Node parseSql(SqlNode node) {
        return null;
    }

    public Identifier getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(Identifier targetTable) {
        this.targetTable = targetTable;
    }
}
