package com.dtstack.engine.sql.node;

import com.dtstack.engine.sql.Column;
import org.dtstack.apache.calcite.sql.SqlDelete;
import org.dtstack.apache.calcite.sql.SqlIdentifier;
import org.dtstack.apache.calcite.sql.SqlNode;

import java.util.List;
import java.util.Map;

public class DeleteNode extends Node{

    /**
     * 操作的主表
     */
    private Identifier tableName;

    public DeleteNode(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb, tableColumnsMap);
    }

    @Override
    public Node parseSql(SqlNode node) {
         if (node instanceof SqlDelete){
             SqlNode targetTable = ((SqlDelete) node).getTargetTable();
             if (targetTable instanceof SqlIdentifier){
                 Identifier identifier = new Identifier(this.getDefaultDb(),getTableColumnMap());
                 identifier.setContext(Node.Context.IDENTIFIER_TABLE);
                 identifier.parseSql(targetTable);
                 this.tableName = identifier;
             }

         }

        return this ;
    }

    public Identifier getTableName() {
        return tableName;
    }

    public void setTableName(Identifier tableName) {
        this.tableName = tableName;
    }
}
