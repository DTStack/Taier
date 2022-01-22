package com.dtstack.taiga.develop.sql.hive.node;

import com.dtstack.taiga.develop.sql.Column;
import com.dtstack.taiga.develop.sql.Table;
import com.dtstack.taiga.develop.sql.TableOperateEnum;
import com.dtstack.taiga.develop.sql.node.CreateNode;
import com.dtstack.taiga.develop.sql.node.Identifier;
import com.dtstack.taiga.develop.sql.node.Node;
import org.apache.calcite.sql.SqlKind;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.*;

public class CreateNodeParser extends NodeParser {
    @Override
    public CreateNode parseSql(ASTNode root, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        CreateNode createNode = new CreateNode(defultDb,tableColumnsMap);
        //  再次校验一下
        if (root.getType() == HiveParser.TOK_CREATETABLE || root.getType() == HiveParser.TOK_CREATEVIEW){
            ArrayList<org.apache.hadoop.hive.ql.lib.Node> targetNode = root.getChildren();
            for (org.apache.hadoop.hive.ql.lib.Node node : targetNode ){
                if (((ASTNode)node).getType() == HiveParser.TOK_TABNAME){
                    // 目标表名
                    Identifier identifier = new Identifier(defultDb,tableColumnsMap);
                    getTableName(identifier, (ASTNode) node,defultDb);
                    createNode.setName(identifier);
                }else if (((ASTNode)node).getType() == HiveParser.TOK_QUERY){
                    SelectNodeParser selectNodeParser = new SelectNodeParser();
                    createNode.setQuery(selectNodeParser.parseSql((ASTNode) node,defultDb,tableColumnsMap,new HashMap<>()));
                    createNode.setSqlKind(SqlKind.SELECT);
                }else if (((ASTNode)node).getType() == HiveParser.TOK_LIKETABLE){
                    Identifier identifier = new Identifier(defultDb,tableColumnsMap);
                    if (node.getChildren() != null && node.getChildren().size()>0) {
                        getTableName(identifier, (ASTNode) ((ASTNode) node).getChild(0), defultDb);
                        createNode.setSqlKind(SqlKind.LIKE);
                    }else {
                        identifier = null;
                    }
                    createNode.setQuery(identifier);
                }
            }
        }
        if (createNode.getSqlKind() == null){
            createNode.setSqlKind(SqlKind.CREATE_TABLE);
        }
        return createNode;
    }

    @Override
    public void parseSqlTable(Node node, Set<Table> tables) {
        if (node instanceof CreateNode){
            CreateNode createNode = (CreateNode) node;
            Table table = new Table();
            table.setName(createNode.getName().getTable());
            table.setDb(createNode.getName().getDb());
            table.setOperate(TableOperateEnum.CREATE);
            tables.add(table);
            SelectNodeParser selectNodeParser = new SelectNodeParser();
            selectNodeParser.parseSqlTable(((CreateNode) node).getQuery(),tables);        }
    }
}
