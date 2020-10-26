package com.dtstack.engine.sql.hive.node;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.node.DropNode;
import com.dtstack.engine.sql.node.Identifier;
import com.dtstack.engine.sql.node.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * drop语句 目前只解析出mainTable就可以了
 * shixi
 */
public class DropNodeParser extends NodeParser {
    @Override
    public Node parseSql(ASTNode node, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        DropNode dropNode = new DropNode(defultDb, tableColumnsMap);
        ArrayList<org.apache.hadoop.hive.ql.lib.Node> children = node.getChildren();
        for (org.apache.hadoop.hive.ql.lib.Node n : children) {
            if (((ASTNode) n).getType() == HiveParser.TOK_TABNAME) {
                Identifier identifier = new Identifier(defultDb, tableColumnsMap);
                getTableName(identifier, (ASTNode) n, defultDb);
                dropNode.setTargetTable(identifier);
                break;
            }
        }
        return dropNode;
    }

    @Override
    public void parseSqlTable(Node node, Set<Table> tables) {

    }
}
