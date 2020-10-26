package com.dtstack.engine.sql.node;

import com.dtstack.engine.sql.Column;
import org.apache.calcite.sql.SqlNode;

import java.util.List;
import java.util.Map;

public class OtherNode extends Node {

    private Identifier table;


    public OtherNode(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb, tableColumnsMap);
    }

    @Override
    public Node parseSql(SqlNode node) {
        return null;
    }

    public Identifier getTable() {
        return table;
    }

    public void setTable(Identifier table) {
        this.table = table;
    }
}
