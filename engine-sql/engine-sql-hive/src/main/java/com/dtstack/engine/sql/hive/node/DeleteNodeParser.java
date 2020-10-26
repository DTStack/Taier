package com.dtstack.engine.sql.hive.node;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.node.DeleteNode;
import com.dtstack.engine.sql.node.Identifier;
import com.dtstack.engine.sql.node.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteNodeParser extends NodeParser {
    @Override
    public Node parseSql(ASTNode node, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        DeleteNode deleteNode = new DeleteNode(defultDb,tableColumnsMap);
        if (HiveParser.TOK_DELETE_FROM == node.getType()){
            Identifier tableName = new Identifier(defultDb,tableColumnsMap);
            tableName.setContext(Node.Context.IDENTIFIER_TABLE);
            getTableName(tableName, (ASTNode) node.getChild(0),defultDb);
            deleteNode.setTableName(tableName);
        }
        return deleteNode;
    }

    @Override
    public void parseSqlTable(Node node, Set<Table> tables) {
        if (node instanceof DeleteNode){
            Identifier tableName = ((DeleteNode) node).getTableName();
            Table table = new Table(tableName.getDb(),tableName.getTable());
            tables.add(table);
        }
    }
}
