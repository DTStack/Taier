package com.dtstack.engine.sql.hive.node;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.hive.until.ASTNodeFunctionEnum;
import com.dtstack.engine.sql.node.BasicCall;
import com.dtstack.engine.sql.node.Identifier;
import com.dtstack.engine.sql.node.JoinCall;
import com.dtstack.engine.sql.node.LiteralIdentifier;
import com.dtstack.engine.sql.node.Node;
import com.dtstack.engine.sql.node.NodeList;
import com.dtstack.engine.sql.node.SelectNode;
import com.dtstack.engine.sql.node.UnionCall;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class NodeParser {

    public static Logger LOG = LoggerFactory.getLogger(NodeParser.class);

    /**
     * 解析默认语法树 转化为自己的语法树
     *
     * @param node
     * @return
     */
    public abstract Node parseSql(ASTNode node, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable);

    /**
     * 解析表级血缘  默认已经转为自身的语法树
     *
     * @param node
     * @param tables
     */
    public abstract void parseSqlTable(Node node, Set<Table> tables);

    public Node parserFrom(ASTNode astNode, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        if (astNode.getType() == HiveParser.TOK_FROM) {
            Node targetTable = getFromTableNode((ASTNode) astNode.getChild(0), defultDb, tableColumnsMap, aliasToTable);
            return targetTable;

        }
        return null;
    }


    /**
     * 获取from表 节点的方法
     *  该递归方法不彻底，需要优化
     * @param targetTable
     * @param defultDb
     * @param tableColumnsMap
     * @return
     */
    public Node getFromTableNode(ASTNode targetTable, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
         if (targetTable.getType() == HiveParser.TOK_TABREF) {
            return getTableByTokTabref(defultDb, tableColumnsMap, targetTable, aliasToTable);
        } else if (targetTable.getType() == HiveParser.TOK_LEFTOUTERJOIN || targetTable.getType() == HiveParser.TOK_JOIN
                 || targetTable.getType() == HiveParser.TOK_RIGHTOUTERJOIN || targetTable.getType() == HiveParser.TOK_FULLOUTERJOIN
                 || targetTable.getType() == HiveParser.TOK_LEFTSEMIJOIN) {
            //说明是 多表连接
            JoinCall joinCall = new JoinCall(defultDb, tableColumnsMap);
            List<Node> comboList = new ArrayList<>();
            joinCall.setLeft(getFromTableNode((ASTNode) targetTable.getChild(0), defultDb, tableColumnsMap, aliasToTable));
            comboList.addAll(getComboList(joinCall.getLeft()));
            joinCall.setRight(getFromTableNode((ASTNode) targetTable.getChild(1), defultDb, tableColumnsMap, aliasToTable));
            comboList.addAll(getComboList(joinCall.getRight()));
            joinCall.setContext(Node.Context.CALL_JOIN);
            joinCall.setComboList(comboList);
            return joinCall;
        } else if (targetTable.getType() == HiveParser.TOK_SUBQUERY) {
            if ((targetTable.getChild(0)).getType() == HiveParser.TOK_QUERY) {
                SelectNode selectNode;
                SelectNodeParser selectNodeParser = new SelectNodeParser();
                selectNode = selectNodeParser.parseSql((ASTNode) targetTable.getChild(0), defultDb, tableColumnsMap, aliasToTable);
                selectNode.setAlias(targetTable.getChild(1).getText());
                selectNode.setContext(Node.Context.SELECT_SUB_QUERY);
                return selectNode;
            } else if ((targetTable.getChild(0)).getType() == HiveParser.TOK_UNIONALL) {
                UnionCall unionCall = new UnionCall(defultDb, tableColumnsMap);
                ASTNode tokUnionall = (ASTNode) targetTable.getChild(0);
                ArrayList<org.apache.hadoop.hive.ql.lib.Node> comblist = tokUnionall.getChildren();
                List<SelectNode> comboFromlist = new ArrayList<>();
                for (org.apache.hadoop.hive.ql.lib.Node union : comblist) {
                    if (((ASTNode) union).getType() == HiveParser.TOK_QUERY) {
                        SelectNodeParser selectNodeParser = new SelectNodeParser();
                        comboFromlist.add(selectNodeParser.parseSql((ASTNode) union, defultDb, tableColumnsMap, aliasToTable));
                    }else if (((ASTNode) union).getType() == HiveParser.TOK_UNIONALL){
                        getComboListFromUnionAll(comboFromlist,(ASTNode)union,defultDb, tableColumnsMap, aliasToTable);
                    }else {
                        LOG.info("未处理的类型"+((ASTNode) union).getType());
                    }
                }
                unionCall.setComboFromList(comboFromlist);
                unionCall.setContext(Node.Context.CALL_UNION);
                return unionCall;
            }
        } else if (targetTable.getType() == HiveParser.TOK_LATERAL_VIEW) {
            Map<String, List<Identifier>> lateralView = new HashMap<>();
            Node node = getNodeByparsetLateRelView(targetTable, lateralView, defultDb, tableColumnsMap, aliasToTable);
            node.setLateralView(lateralView);
            return node;
        } else {
            LOG.info("未处理的类型"+targetTable.getType());
        }

        return null;
    }

    private Node dealFrom(ASTNode targetTable, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable){
        if (targetTable.getType() == HiveParser.TOK_TABREF) {
            return getTableByTokTabref(defultDb, tableColumnsMap, targetTable, aliasToTable);
        } else if (targetTable.getType() == HiveParser.TOK_LEFTOUTERJOIN || targetTable.getType() == HiveParser.TOK_JOIN || targetTable.getType() == HiveParser.TOK_RIGHTOUTERJOIN) {
            return dealJoin(defultDb, tableColumnsMap, targetTable, aliasToTable);
        } else if (targetTable.getType() == HiveParser.TOK_SUBQUERY) {
            return dealSubQuery(defultDb, tableColumnsMap, targetTable, aliasToTable);
        } else if (targetTable.getType() == HiveParser.TOK_LATERAL_VIEW) {
            return dealLateralView(defultDb, tableColumnsMap, targetTable, aliasToTable);
        } else if (targetTable.getType() == HiveParser.TOK_LEFTSEMIJOIN) {
            return dealLeftSemiJoin(defultDb, tableColumnsMap, targetTable, aliasToTable);
        }else {
            LOG.info("未处理的类型{}",targetTable.getType());
        }
        return null;
    }

    /**
     * left semi join : select * from a where a.key in(select xxx)
     * 等于条件，不需要进行解析
     * @param defultDb
     * @param tableColumnsMap
     * @param targetTable
     * @param aliasToTable
     * @return
     */
    private Node dealLeftSemiJoin(String defultDb, Map<String, List<Column>> tableColumnsMap, ASTNode targetTable, Map<String, String> aliasToTable) {
        ArrayList<org.apache.hadoop.hive.ql.lib.Node> leftSemiJoin = targetTable.getChildren();
        for (org.apache.hadoop.hive.ql.lib.Node node : leftSemiJoin){
            if (((ASTNode) node).getType() == HiveParser.TOK_TABREF){
                return getTableByTokTabref(defultDb, tableColumnsMap, (ASTNode) node, aliasToTable);
            }else if (((ASTNode) node).getType() == HiveParser.TOK_SUBQUERY){
                //
                LOG.info("left semi join 在where条件中，一般不解析");
            }
        }
        return null;
    }

    private Node dealLateralView(String defultDb, Map<String, List<Column>> tableColumnsMap, ASTNode targetTable, Map<String, String> aliasToTable) {
        Map<String, List<Identifier>> lateralView = new HashMap<>();
        Node node = getNodeByparsetLateRelView(targetTable, lateralView, defultDb, tableColumnsMap, aliasToTable);
        node.setLateralView(lateralView);
        return node;
    }

    private Node dealSubQuery(String defultDb, Map<String, List<Column>> tableColumnsMap, ASTNode targetTable, Map<String, String> aliasToTable){
        if ((targetTable.getChild(0)).getType() == HiveParser.TOK_QUERY) {
            SelectNode selectNode;
            SelectNodeParser selectNodeParser = new SelectNodeParser();
            selectNode = selectNodeParser.parseSql((ASTNode) targetTable.getChild(0), defultDb, tableColumnsMap, aliasToTable);
            selectNode.setAlias(targetTable.getChild(1).getText());
            selectNode.setContext(Node.Context.SELECT_SUB_QUERY);
            return selectNode;
        } else if ((targetTable.getChild(0)).getType() == HiveParser.TOK_UNIONALL) {
            UnionCall unionCall = new UnionCall(defultDb, tableColumnsMap);
            ASTNode tokUnionall = (ASTNode) targetTable.getChild(0);
            ArrayList<org.apache.hadoop.hive.ql.lib.Node> comblist = tokUnionall.getChildren();
            List<SelectNode> comboFromlist = new ArrayList<>();
            for (org.apache.hadoop.hive.ql.lib.Node union : comblist) {
                if (((ASTNode) union).getType() == HiveParser.TOK_QUERY) {
                    SelectNodeParser selectNodeParser = new SelectNodeParser();
                    comboFromlist.add(selectNodeParser.parseSql((ASTNode) union, defultDb, tableColumnsMap, aliasToTable));
                }else if (((ASTNode) union).getType() == HiveParser.TOK_UNIONALL){
                    getComboListFromUnionAll(comboFromlist,(ASTNode)union,defultDb, tableColumnsMap, aliasToTable);
                }else {
                    LOG.info("未处理的类型"+((ASTNode) union).getType());
                }
            }
            unionCall.setComboFromList(comboFromlist);
            unionCall.setContext(Node.Context.CALL_UNION);
            return unionCall;
        } else {
            LOG.info("未处理的类型{}",targetTable.getChild(0).getType());
        }
        return null;
    }

    private Node dealJoin(String defultDb, Map<String, List<Column>> tableColumnsMap, ASTNode targetTable, Map<String, String> aliasToTable) {
        JoinCall joinCall = new JoinCall(defultDb, tableColumnsMap);
        List<Node> comboList = new ArrayList<>();
        joinCall.setLeft(dealFrom((ASTNode) targetTable.getChild(0), defultDb, tableColumnsMap, aliasToTable));
        comboList.addAll(getComboList(joinCall.getLeft()));
        joinCall.setRight(dealFrom((ASTNode) targetTable.getChild(1), defultDb, tableColumnsMap, aliasToTable));
        comboList.addAll(getComboList(joinCall.getRight()));
        joinCall.setContext(Node.Context.CALL_JOIN);
        joinCall.setComboList(comboList);
        return joinCall;
    }

    private void getComboListFromUnionAll(List<SelectNode> comboFromlist, ASTNode union,String defultDb,Map<String, List<Column>> tableColumnsMap,Map<String, String> aliasToTable) {
        if (((ASTNode) union).getType() == HiveParser.TOK_QUERY) {
            SelectNodeParser selectNodeParser = new SelectNodeParser();
            comboFromlist.add(selectNodeParser.parseSql((ASTNode) union, defultDb, tableColumnsMap, aliasToTable));
        }else if (((ASTNode) union).getType() == HiveParser.TOK_UNIONALL){
            ArrayList<org.apache.hadoop.hive.ql.lib.Node> comblist = union.getChildren();
            for (org.apache.hadoop.hive.ql.lib.Node node : comblist) {
                if (((ASTNode) node).getType() == HiveParser.TOK_QUERY) {
                    SelectNodeParser selectNodeParser = new SelectNodeParser();
                    comboFromlist.add(selectNodeParser.parseSql((ASTNode) node, defultDb, tableColumnsMap, aliasToTable));
                }else if (((ASTNode) node).getType() == HiveParser.TOK_UNIONALL){
                    getComboListFromUnionAll(comboFromlist,(ASTNode)node,defultDb, tableColumnsMap, aliasToTable);
                }
            }
        }
    }


    /**
     * 侧视图结构：一个select 将一个字段分解为多个
     * 一个fromClause(可能是子查询)
     * 对侧视图节点进行解析
     *
     * @return
     */
    private Node getNodeByparsetLateRelView(ASTNode astNode, Map<String, List<Identifier>> lateralView, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        Node selectNode = null;
        ArrayList<org.apache.hadoop.hive.ql.lib.Node> children = astNode.getChildren();
        if (children.size() == 2){
            org.apache.hadoop.hive.ql.lib.Node node0 = children.get(0);
            getLateralViewParserResult((ASTNode) node0, lateralView, defultDb, tableColumnsMap);
            org.apache.hadoop.hive.ql.lib.Node node1 = children.get(1);
            if (((ASTNode) node1).getType() == HiveParser.TOK_TABREF){
                selectNode = getFromTableNode((ASTNode) children.get(1), defultDb, tableColumnsMap, aliasToTable);
            }else if (((ASTNode) node1).getType() == HiveParser.TOK_LATERAL_VIEW){
                selectNode = getNodeByparsetLateRelView((ASTNode) node1, lateralView, defultDb, tableColumnsMap, aliasToTable);
            }else if (((ASTNode) node1).getType() == HiveParser.TOK_SUBQUERY){
                selectNode = dealSubQuery(defultDb,tableColumnsMap, (ASTNode) node1,aliasToTable);
            }
        }else{
            LOG.info("lateral view解析树异常");
        }
        return selectNode;
    }

    /**
     * 根据节点的类型 获取combolist或者本身
     *
     * @param node
     * @return
     */
    private List<? extends Node> getComboList(Node node) {
        List<Node> list = new ArrayList<>();
        if (node instanceof JoinCall) {
            return ((JoinCall) node).getComboList();
        } else if (node instanceof UnionCall) {
            return ((UnionCall) node).getComboFromList();
        } else if (node instanceof Identifier) {
            list.add(node);
        } else if (node instanceof SelectNode) {
            list.add(node);
        }
        return list;
    }

    public Identifier getTableByTokTabref(String defultDb, Map<String, List<Column>> tableColumnsMap, ASTNode targetTable, Map<String, String> aliasToTable) {
        Identifier identifier = new Identifier(defultDb, tableColumnsMap);
        if (targetTable.getChild(0).getType() == HiveParser.TOK_TABNAME) {
            getTableName(identifier, (ASTNode) targetTable.getChild(0), defultDb);
        }
        if (targetTable.getChildren().size() > 1) {
            //todo 说明有别名
            if (targetTable.getChild(1).getType() == HiveParser.Identifier) {
                identifier.setAlias(targetTable.getChild(1).getText());
            }
        }
        if (StringUtils.isBlank(identifier.getDb())) {
            identifier.setDb(identifier.getDefaultDb());
        }
        aliasToTable.put(StringUtils.isBlank(identifier.getAlias()) ? identifier.getTable() : identifier.getAlias(), identifier.getFullTable());
        return identifier;
    }

    public void getTableName(Identifier identifier, ASTNode targetTable, String defultDb) {
        ArrayList<org.apache.hadoop.hive.ql.lib.Node> table = targetTable.getChildren();
        if (table.size() > 1) {
            identifier.setDb(((ASTNode) table.get(0)).getText().replace("`", ""));
            identifier.setTable(((ASTNode) table.get(1)).getText().replace("`", ""));
        } else {
            identifier.setTable(((ASTNode) table.get(0)).getText().replace("`", ""));
            identifier.setDb(identifier.getDefaultDb());
        }
        identifier.setContext(Node.Context.IDENTIFIER_TABLE);
        // 对表别名进行一次填充 用于字段表别名进行填充
        identifier.setAlias(identifier.getTable());
        if (StringUtils.isBlank(identifier.getDb())) {
            identifier.setDb(defultDb);
        }
    }

    /**
     * 拿到插入的字段列表  tok_select   select 查询出来的字段
     *
     * @return
     */
    public List<Node> getColumnListByTokSelect(ASTNode node, String defultDb, Map<String, List<Column>> tableColumnsMap, Node fromNode) {
        List<Node> columns = new ArrayList<>();
        if (node.getType() == HiveParser.TOK_SELECT || HiveParser.TOK_SELECTDI == node.getType()) {
            ArrayList<org.apache.hadoop.hive.ql.lib.Node> selexprList = node.getChildren();
            for (org.apache.hadoop.hive.ql.lib.Node c : selexprList) {
                if (((ASTNode) c).getType() == HiveParser.TOK_SELEXPR) {
                    Identifier identifier = new Identifier(defultDb, tableColumnsMap);
                    ArrayList<org.apache.hadoop.hive.ql.lib.Node> columnList = ((ASTNode) c).getChildren();
                    if (columnList.size() > 1) {
                        //说明 属于id as id这种的
                        identifier.setAlias(((ASTNode) columnList.get(1)).getText());
                    }
                    if (((ASTNode) columnList.get(0)).getType() == HiveParser.Number || ((ASTNode) columnList.get(0)).getType() == HiveParser.StringLiteral
                            || ((ASTNode) columnList.get(0)).getType() == HiveParser.TOK_NULL) {
                        LiteralIdentifier l = new LiteralIdentifier(defultDb, tableColumnsMap);
                        if (((ASTNode) columnList.get(0)).getType() == HiveParser.TOK_NULL) {
                            l.setName("null");
                        } else {
                            l.setName(((ASTNode) columnList.get(0)).getText());
                        }
                        l.setAlias(identifier.getAlias());
                        columns.add(l);
                        continue;
                    } else if (((ASTNode) columnList.get(0)).getType() == HiveParser.DOT) {
                        // 说明属于 a.id这种的
                        ASTNode alays = (ASTNode) ((ASTNode) columnList.get(0)).getChild(0);
                        //获取db
                        identifier.setTable(((ASTNode) alays.getChildren().get(0)).getText());
                        identifier.setColumn(((ASTNode) columnList.get(0)).getChild(1).getText());
                    } else if (ASTNodeFunctionEnum.isFunction(((ASTNode) columnList.get(0)).getType())) {
                        BasicCall call = new BasicCall(defultDb, tableColumnsMap);
                        call.setComboList(getBasicCallByColumn((ASTNode) columnList.get(0), defultDb, tableColumnsMap));
                        // 对函数name属性进行处理
                        StringBuffer basicName = new StringBuffer();
                        basicName.append(((ASTNode) columnList.get(0)).getChild(0).getText());
                        call.getComboList().forEach(bean -> {
                            basicName.append("-").append(bean.getColumn());
                        });
                        call.setName(basicName.toString());
                        if (StringUtils.isNotBlank(identifier.getAlias())) {
                            call.setAlias(identifier.getAlias());
                        } else {
                            call.setAlias(basicName.toString());
                        }
                        columns.add(isLateralViewNode(call, fromNode));
                        continue;
                    } else if (((ASTNode) columnList.get(0)).getType() == HiveParser.TOK_TABLE_OR_COL) {
                        // 正常的简单字段
                        identifier.setColumn((((ASTNode) columnList.get(0)).getChild(0)).getText());
                    } else if (((ASTNode) columnList.get(0)).getType() == HiveParser.TOK_ALLCOLREF) {
                        // select *
                        ASTNode allcolref = ((ASTNode) columnList.get(0));
                        if (CollectionUtils.isNotEmpty(allcolref.getChildren())) {
                            ArrayList<org.apache.hadoop.hive.ql.lib.Node> allcolrefList = allcolref.getChildren();
                            for (org.apache.hadoop.hive.ql.lib.Node n : allcolrefList) {
                                if (((ASTNode) n).getType() == HiveParser.TOK_TABNAME) {
                                    identifier.setTable(((ASTNode) n).getChild(0).getText());
                                }
                            }
                        }
                        identifier.setColumn("*");
                    } else {
                        continue;
                    }
                    identifier.setContext(Node.Context.IDENTIFIER_COLUMN);
                    getDbByFromNode(identifier, fromNode, defultDb);
                    columns.add(isLateralViewNode(identifier, fromNode));
                }
            }
        }
        return columns;
    }

    /**
     * 根据fromNode节点 获取db
     *
     * @param identifier
     * @param fromNode
     * @param defultDb
     */
    private void getDbByFromNode(Identifier identifier, Node fromNode, String defultDb) {
        if (fromNode instanceof Identifier) {
            identifier.setDb(((Identifier) fromNode).getDb());
            if (StringUtils.isBlank(identifier.getTable())) {
                identifier.setTable(((Identifier) fromNode).getTable());
            }
        } else {
            if (StringUtils.isNotBlank(identifier.getTable())) {
                if (fromNode instanceof JoinCall) {
                    for (Node node : ((JoinCall) fromNode).getComboList()) {
                        if (identifier.getTable().equals(node.getAlias()) && node instanceof Identifier) {
                            identifier.setDb(((Identifier) node).getDb());
                        }
                    }
                }
            }
        }
        if (StringUtils.isBlank(identifier.getDb())) {
            identifier.setDb(defultDb);
        }
    }

    private List<Identifier> getBasicCallByColumn(ASTNode node, String defultDb, Map<String, List<Column>> tableColumnsMap) {
        List<Identifier> list = new ArrayList<>();
        ArrayList<org.apache.hadoop.hive.ql.lib.Node> basicList = node.getChildren();
        for (int i = 0; i < basicList.size(); i++) {
            ASTNode column = ((ASTNode) basicList.get(i));
            Identifier identifier = new Identifier(defultDb, tableColumnsMap);
            if (column.getType() == HiveParser.DOT) {
                identifier.setColumn((column.getChild(1)).getText());
                identifier.setTable(((column.getChild(0)).getChild(0)).getText());
            } else if (column.getType() == HiveParser.TOK_TABLE_OR_COL) {
                identifier.setColumn((column.getChild(0)).getText());
            } else if (ASTNodeFunctionEnum.isFunction(column.getType())) {
                list.addAll(getBasicCallByColumn(column, defultDb, tableColumnsMap));
                continue;
            } else {
                continue;
            }
            identifier.setContext(Node.Context.IDENTIFIER_COLUMN);
            list.add(identifier);

        }
        return list;
    }

    /**
     * 填充select *
     *
     * @param node
     * @param defultDb
     * @param tableColumnsMap
     */
    public void selectStarFill(SelectNode node, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        if (node.getSelectList() == null){
            return;
        }
        List<Node> columnList = node.getSelectList().getList();
        List<Node> starList = new ArrayList<>();
        List<Node> delColumn = new ArrayList<>();

        for (Node column : columnList) {
            if (column instanceof LiteralIdentifier) {
                continue;
            }
            if (column instanceof Identifier) {
                Identifier star = (Identifier) column;
                if (star.getColumn().equals("*")) {
                    delColumn.add(star);
                    //todo 说明有*  但是要判断是 *  还是 a.*
                    if (StringUtils.isNotBlank(star.getTable())) {
                        getColumnListByStarNode(defultDb, tableColumnsMap, aliasToTable, starList, column, node.getFromClause());
                    } else {
                        //todo 说明是*
                        Node fromNode = node.getFromClause();
                        getColumnListByStarNode(defultDb, tableColumnsMap, aliasToTable, starList, column, fromNode);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(starList)) {
            columnList.removeAll(delColumn);
        }
        columnList.addAll(starList);
    }

    private void getColumnListByTableMap(String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable, List<Node> starList, Node column, Identifier star) {
        String tableName = StringUtils.isNotBlank(star.getAlias()) ? star.getAlias() : star.getTable();
        List<Column> columns = tableColumnsMap.get(aliasToTable.get(tableName));
        if (CollectionUtils.isNotEmpty(columns)) {
            for (Column c : columns) {
                Identifier identifier = new Identifier(defultDb, tableColumnsMap);
                identifier.setColumn(StringUtils.isNotBlank(c.getAlias()) ? c.getAlias() : c.getName());
                identifier.setContext(Node.Context.IDENTIFIER_COLUMN);
                identifier.setTable(StringUtils.isNotBlank(star.getAlias()) ? star.getAlias() : star.getTable());
                identifier.setDb(aliasToTable.get(tableName).split("\\.")[0]);
                starList.add(identifier);
            }
        }
    }

    /**
     * 如果是select * from 这种 要判断from后面的是哪种
     *
     * @param defultDb
     * @param tableColumnsMap
     * @param aliasToTable
     * @param starList
     * @param column
     * @param fromNode
     */
    private void getColumnListByStarNode(String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable, List<Node> starList, Node column, Node fromNode) {
        if (fromNode instanceof Identifier) {
            getColumnListByTableMap(defultDb, tableColumnsMap, aliasToTable, starList, column, (Identifier) fromNode);
        } else if (fromNode instanceof SelectNode) {
            starList.addAll(changeDbByColumnList(((SelectNode) fromNode).getSelectList().getList(), column, defultDb, tableColumnsMap));
        } else if (fromNode instanceof JoinCall) {
            for (Node joinNode : ((JoinCall) fromNode).getComboList()) {
                getColumnListByStarNode(defultDb, tableColumnsMap, aliasToTable, starList, joinNode, joinNode);
            }
        } else if (fromNode instanceof UnionCall) {
            for (SelectNode selectNode : ((UnionCall) fromNode).getComboFromList()) {
                starList.addAll(changeDbByColumnList(selectNode.getSelectList().getList(), column, defultDb, tableColumnsMap));
                break;
            }
        }
    }

    /**
     * 填充*时 如果是从子查询中拿出来的字段 改变db
     *
     * @param columnList
     * @param star
     * @param defultDb
     * @param tableColumnsMap
     * @return
     */
    private List<Node> changeDbByColumnList(List<Node> columnList, Node star, String defultDb, Map<String, List<Column>> tableColumnsMap) {
        List<Node> list = new ArrayList<>();
        for (Node node : columnList) {
            if (node instanceof LiteralIdentifier) {
                list.add(node);
                continue;
            }
            Identifier identifier = new Identifier(defultDb, tableColumnsMap);
            identifier.setContext(Node.Context.IDENTIFIER_COLUMN);
            identifier.setAlias(node.getAlias());
            if (star instanceof Identifier) {
                identifier.setTable(((Identifier) star).getTable());
            } else {
                identifier.setTable(star.getAlias());
            }
            if (node instanceof Identifier) {
                identifier.setColumn(StringUtils.isNotBlank(((Identifier) node).getAlias()) ? ((Identifier) node).getAlias() : ((Identifier) node).getColumn());
                identifier.setDb(((Identifier) node).getDb());
            } else if (node instanceof BasicCall) {
                identifier.setColumn(node.getAlias());
                identifier.setDb(node.getDefaultDb());

            }
            list.add(identifier);
        }
        return list;
    }

    /**
     * 如果字段名没有别名  根据from 手动加入别名
     *
     * @param selectNode
     * @return
     */
    public void fillColumnTable(SelectNode selectNode) {
        List<Node> columnList = selectNode.getSelectList().getList();
        for (Node node : columnList) {
            if (node instanceof Identifier) {
                if (StringUtils.isBlank(((Identifier) node).getTable())) {
                    ((Identifier) node).setTable(getColumnAlias(selectNode.getFromClause(), ((Identifier) node).getColumn()));
                }
            } else if (node instanceof BasicCall) {
                List<Identifier> columns = ((BasicCall) node).getComboList();
                for (Identifier column : columns) {
                    if (StringUtils.isBlank(column.getTable())) {
                        column.setTable(getColumnAlias(selectNode.getFromClause(), column.getColumn()));
                    }
                }

            }
        }
    }

    /**
     * 填充字段时 查找字段的表别名
     *
     * @param fromNode
     * @param columnName
     * @return
     */
    public String getColumnAlias(Node fromNode, String columnName) {
        if (fromNode instanceof Identifier) {
            if (StringUtils.isNotBlank(fromNode.getAlias())) {
                return fromNode.getAlias();
            }
        } else if (fromNode instanceof JoinCall) {
            List<Node> nodes = ((JoinCall) fromNode).getComboList();
            Map<String, List<Column>> tableColumnsMap = fromNode.getTableColumnMap();
            for (Node node : nodes) {
                if (node instanceof Identifier) {
                    if (tableColumnsMap.containsKey(((Identifier) node).getFullTable())) {
                        List<String> colunmuNameList = tableColumnsMap.get(((Identifier) node).getFullTable()).stream().map(Column::getName).collect(Collectors.toList());
                        if (colunmuNameList.contains(columnName)) {
                            return node.getAlias();
                        }
                    }
                }
            }
        } else if (fromNode instanceof SelectNode) {
            if (StringUtils.isNotBlank(fromNode.getAlias())) {
                return fromNode.getAlias();
            }
        } else if (fromNode instanceof UnionCall) {
            if (StringUtils.isNotBlank(fromNode.getAlias())) {
                return fromNode.getAlias();
            }
        }
        return null;
    }

    /**
     * 对explode函数处理
     *
     * @param astNode
     * @param columnMap
     * @param defultDb
     * @param tableColumnsMap
     */
    private void getLateralViewParserResult(ASTNode astNode, Map<String, List<Identifier>> columnMap, String defultDb, Map<String, List<Column>> tableColumnsMap) {
        if (astNode.getType() == HiveParser.TOK_SELECT) {
            if (astNode.getChild(0).getType() == HiveParser.TOK_SELEXPR) {
                List<Identifier> targetColumn = null;
                String tableAlias = null;
                List<String> viewColumnList = new ArrayList<>();
                ArrayList<org.apache.hadoop.hive.ql.lib.Node> basicList = ((ASTNode) astNode.getChild(0)).getChildren();
                for (org.apache.hadoop.hive.ql.lib.Node node : basicList) {
                    if (((ASTNode) node).getType() == HiveParser.TOK_FUNCTION) {
                        //  如果是explode函数 那应该是只有一个值的
                        targetColumn = getBasicCallByColumn((ASTNode) node, defultDb, tableColumnsMap);
                    } else if (((ASTNode) node).getType() == HiveParser.Identifier) {
                        viewColumnList.add(((ASTNode) node).getText());
                    } else if (((ASTNode) node).getType() == HiveParser.TOK_TABALIAS) {
                        tableAlias = ((ASTNode) node).getChild(0).getText();
                    }
                }
                for (String viewColumn : viewColumnList) {
                    columnMap.put(String.format("%s.%s", tableAlias, viewColumn), targetColumn);
                }
            }
        }
    }

    /**
     * 侧视图特殊处理
     * 如果在字段里发现侧视图的字段 直接映射到真正字段上面
     *
     * @param columnNode
     * @param fromNode
     * @return
     */
    private Node isLateralViewNode(Node columnNode, Node fromNode) {
        if (fromNode == null) {
            return columnNode;
        }
        Map<String, List<Identifier>> lateralView = fromNode.getLateralView();
        if (lateralView == null || lateralView.isEmpty()) {
            return columnNode;
        }
        if (columnNode instanceof Identifier) {
            //如果是字段就封装成一个basicCall 这样 解析逻辑就不用变动了
            Identifier column = (Identifier) columnNode;
            if (lateralView.containsKey(String.format("%s.%s", column.getTable(), column.getColumn()))) {
                BasicCall basicCall = new BasicCall(columnNode.getDefaultDb(), columnNode.getTableColumnMap());
                basicCall.setAlias(StringUtils.isNotBlank(column.getAlias()) ? column.getAlias() : column.getColumn());
                basicCall.setName(String.format("lateraView_%s", basicCall.getAlias()));
                basicCall.setComboList(lateralView.get(String.format("%s.%s", column.getTable(), column.getColumn())));
                return basicCall;
            }
        } else if (columnNode instanceof BasicCall) {
            BasicCall basicCall = (BasicCall) columnNode;
            List<Identifier> combolist = basicCall.getComboList();
            List<Identifier> removeList = new ArrayList<>();
            List<Identifier> addNode = new ArrayList<>();
            for (Identifier column : combolist) {
                if (lateralView.containsKey(String.format("%s.%s", column.getTable(), column.getColumn()))) {
                    removeList.add(column);
                    addNode.addAll(lateralView.get(String.format("%s.%s", column.getTable(), column.getColumn())));
                }
            }
            basicCall.getComboList().removeAll(removeList);
            basicCall.getComboList().addAll(addNode);
            return basicCall;
        }
        return columnNode;
    }

    /**
     * 遍历当前树的所有from节点判断是否需要替换with节点
     *
     * @param node
     * @return
     */
    public SelectNode whihNodeReplace(Node node, Map<String, SelectNode> withMap, Map<String, String> aliasToTable) {
        if (null == node) {
            return null;
        }
        if (node instanceof NodeList) {
            NodeList nodeList = (NodeList) node;
            List<Node> remove = new ArrayList<>();
            List<Node> add = new ArrayList<>();
            for (Node n : nodeList.getList()) {
                Node newNode = whihNodeReplace(n, withMap,aliasToTable);
                if (null != newNode) {
                    remove.add(n);
                    add.add(newNode);
                }
            }
            if (CollectionUtils.isNotEmpty(remove)) {
                nodeList.getList().removeAll(remove);
                nodeList.getList().addAll(add);
            }
            return null;
        } else if (node instanceof SelectNode) {
            SelectNode selectNode = (SelectNode) node;
            Node newNode = whihNodeReplace(selectNode.getFromClause(), withMap,aliasToTable);
            if (null != newNode) {
                selectNode.setFromClause(newNode);
            }
            selectStarFill(selectNode, selectNode.getDefaultDb(), selectNode.getTableColumnMap(), aliasToTable);
            return null;
        } else if (node instanceof JoinCall) {
            JoinCall joinCall = (JoinCall) node;
            List<Node> remove = new ArrayList<>();
            List<Node> add = new ArrayList<>();
            Node newNode = whihNodeReplace(joinCall.getLeft(), withMap,aliasToTable);
            if (null != newNode) {
                remove.add(joinCall.getLeft());
                add.add(newNode);
                joinCall.setLeft(newNode);
            }
            newNode = whihNodeReplace(joinCall.getRight(), withMap,aliasToTable);
            if (null != newNode) {
                remove.add(joinCall.getRight());
                add.add(newNode);
                joinCall.setRight(newNode);
            }
            if (CollectionUtils.isNotEmpty(remove)) {
                joinCall.getComboList().removeAll(remove);
                joinCall.getComboList().addAll(add);
            }
            return null;

        } else if (node instanceof UnionCall) {
            UnionCall unionCall = (UnionCall) node;
            for (Node n : unionCall.getComboFromList()) {
                whihNodeReplace(n, withMap,aliasToTable);
            }
            return null;
        } else if (node instanceof Identifier) {
            Identifier n = (Identifier) node;
            if (withMap.containsKey(n.getTable())) {
                return withMap.get(n.getTable());
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * with语句解析
     *
     * @param defultDb
     * @param tableColumnsMap
     * @param aliasToTable
     * @param withMap
     * @param qNode
     */
    public void withNodeParser(String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable, Map<String, SelectNode> withMap, ASTNode qNode) {
        //with 语句解析 把with语句先当作 源数据的一部分存放起来
        List<Node> withList = new ArrayList<>();
        List<org.apache.hadoop.hive.ql.lib.Node> withNodeList = qNode.getChildren();
        for (org.apache.hadoop.hive.ql.lib.Node n : withNodeList) {
            List<org.apache.hadoop.hive.ql.lib.Node> nodeList = ((ASTNode) n).getChildren();
            SelectNodeParser selectNodeParser = new SelectNodeParser();
            SelectNode withNode = selectNodeParser.parseSql((ASTNode) nodeList.get(0), defultDb, tableColumnsMap, aliasToTable);
            withNode.setAlias(((ASTNode) nodeList.get(1)).getText());
            withMap.put(((ASTNode) nodeList.get(1)).getText(), withNode);
            withList.add(withNode);
            //with 可能内部嵌套 例如 with t1 as (select * from a),t2 as (select * from t1) select * from t1,t2;
            //所以优先处理一下 进行提前替换  但是要忽略互相嵌套 with t1 as (select * from t1),t2 as (select * from t1) select * from t1,t2
            if (withList.size() > 1) {
                SelectNode with = whihNodeReplace(withNode.getFromClause(), withMap,aliasToTable);
                if (null != with) {
                    withNode.setFromClause(with);
                    selectStarFill(withNode, defultDb, tableColumnsMap, aliasToTable);
                }
            }
        }
    }
}
