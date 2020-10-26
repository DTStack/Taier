package com.dtstack.engine.sql.hive.node;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.TableOperateEnum;
import com.dtstack.engine.sql.node.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InsertNodeParser extends NodeParser {

    @Override
    public Node parseSql(ASTNode node, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        //用于存放with节点 用于整棵树构建之后 把节点填充
        Map<String, SelectNode> withMap = new HashMap<>();
        InsertNode insertNode = new InsertNode(defultDb, tableColumnsMap);
        SelectNode source = new SelectNode(defultDb, tableColumnsMap);
        source.setTableMap(new HashMap<>());
        if (node.getType() == HiveParser.TOK_QUERY) {
            ArrayList<org.apache.hadoop.hive.ql.lib.Node> querNode = node.getChildren();
            for (org.apache.hadoop.hive.ql.lib.Node qNode : querNode) {
                if (((ASTNode) qNode).getType() == HiveParser.TOK_FROM) {
                    source.setFromClause(parserFrom((ASTNode) qNode, defultDb, tableColumnsMap, aliasToTable));
                    //  判断一下  如果是union 会多一层  所以需要直接往上提一层
                    if (source.getFromClause() instanceof SelectNode && ((SelectNode) source.getFromClause()).getSelectList() == null) {
                        String alias = source.getFromClause().getAlias();
                        source.setFromClause(((SelectNode) source.getFromClause()).getFromClause());
                        source.getFromClause().setAlias(alias);
                    }
                    if (!withMap.isEmpty()) {
                        //说明有with语句 遍历当前树 进行节点替换
                        SelectNode n = whihNodeReplace(source, withMap,aliasToTable);
                        if (null != n) {
                            source = n;
                        }
                    }
                } else if (((ASTNode) qNode).getType() == HiveParser.TOK_INSERT) {
                    ArrayList<org.apache.hadoop.hive.ql.lib.Node> insert = ((ASTNode) qNode).getChildren();
                    NodeList nodeList = new NodeList(defultDb, tableColumnsMap);
                    nodeList.setList(getColumnListByTokSelect((ASTNode) insert.get(1), defultDb, tableColumnsMap, source.getFromClause()));
                    source.setSelectList(nodeList);
                    // 填充 侧视图字段映射
                    // 对没有表别名的字段 进行填充表别名
                    fillColumnTable(source);
                    // 填充select *
                    selectStarFill(source, defultDb, tableColumnsMap, aliasToTable);
                    insertNode.setSource(source);
                    InsertNode tarInsertNode = getNodeByTokInsertInto((ASTNode) insert.get(0), defultDb, tableColumnsMap, aliasToTable);
                    insertNode.setColumnList(tarInsertNode.getColumnList());
                    insertNode.setTargetTable(tarInsertNode.getTargetTable());
                } else if (HiveParser.TOK_CTE == ((ASTNode) qNode).getType()) {
                    withNodeParser(defultDb, tableColumnsMap, aliasToTable, withMap, (ASTNode) qNode);
                }
            }
        }

        return insertNode;
    }

    @Override
    public void parseSqlTable(Node node, Set<Table> tables) {
        if (node instanceof InsertNode) {
            Table table = new Table();
            table.setName(((InsertNode) node).getTargetTable().getTable());
            table.setDb(((InsertNode) node).getTargetTable().getDb());
            table.setOperate(TableOperateEnum.INSERT);
            tables.add(table);
            SelectNodeParser selectNodeParser = new SelectNodeParser();
            selectNodeParser.parseSqlTable(((InsertNode) node).getSource(), tables);
        }
    }


    private void getColumnList(ASTNode node, String defultDb, Map<String, List<Column>> tableColumnsMap, List<Identifier> columns) {
        //todo  这个是insert into 没有插入字段
        if (node == null) {
            return;
        }
        ArrayList<org.apache.hadoop.hive.ql.lib.Node> columnList = node.getChildren();
        for (org.apache.hadoop.hive.ql.lib.Node c : columnList) {
            if (((ASTNode) c).getType() == HiveParser.Identifier) {
                Identifier identifier = new Identifier(defultDb, tableColumnsMap);
                identifier.setColumn(((ASTNode) c).getText());
                identifier.setContext(Node.Context.IDENTIFIER_COLUMN);
                columns.add(identifier);
            }
        }
    }

    /**
     * 获取 insert语句的 插入的表名  插入的字段列表
     *
     * @param astNode
     * @param defultDb
     * @param tableColumnsMap
     * @return
     */
    private InsertNode getNodeByTokInsertInto(ASTNode astNode, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        InsertNode insertNode = new InsertNode(defultDb, tableColumnsMap);
        if (astNode.getType() == HiveParser.TOK_INSERT_INTO) {
            ASTNode table = (ASTNode) astNode.getChild(0);
            Identifier identifier = new Identifier(defultDb, tableColumnsMap);
            getTableName(identifier, (ASTNode) table.getChild(0), defultDb);
            if (StringUtils.isBlank(identifier.getDb())) {
                identifier.setDb(identifier.getDefaultDb());
            }
            insertNode.setTargetTable(identifier);
            //todo 获取insert字段
            ASTNode column = (ASTNode) astNode.getChild(1);
            List<Identifier> columnList = new ArrayList<>();
            getColumnList(column, defultDb, tableColumnsMap, columnList);
            for (Identifier i : columnList) {
                i.setTable(insertNode.getTargetTable().getTable());
                i.setDb(identifier.getDb());
            }
            insertNode.setColumnList(columnList);
        } else if (astNode.getType() == HiveParser.TOK_DESTINATION) {
            //insert overwirt
            Identifier identifier = new Identifier(defultDb, tableColumnsMap);
            getTableName(identifier, (ASTNode) astNode.getChild(0).getChild(0), defultDb);
            if (StringUtils.isBlank(identifier.getDb())) {
                identifier.setDb(identifier.getDefaultDb());
            }
            insertNode.setTargetTable(identifier);
        }
        return insertNode;
    }


}
