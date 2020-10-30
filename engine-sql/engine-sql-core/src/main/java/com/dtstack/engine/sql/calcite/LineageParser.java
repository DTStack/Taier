package com.dtstack.engine.sql.calcite;


import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.node.*;
import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.dtstack.apache.calcite.sql.SqlBasicCall;
import org.dtstack.apache.calcite.sql.SqlDelete;
import org.dtstack.apache.calcite.sql.SqlInsert;
import org.dtstack.apache.calcite.sql.SqlKind;
import org.dtstack.apache.calcite.sql.SqlNode;
import org.dtstack.apache.calcite.sql.SqlSelect;
import org.dtstack.apache.calcite.sql.ddl.SqlCreateTable;
import org.dtstack.apache.calcite.sql.ddl.SqlCreateView;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/27 16:39
 * @Description:
 */
public abstract class LineageParser {

    public static Logger LOG = LoggerFactory.getLogger(LineageParser.class);

    protected Map<String, List<Column>> tableColumnMap;

    public void setTableColumnMap(Map<String, List<Column>> tableColumnMap) {
        this.tableColumnMap = tableColumnMap;
    }

    Map<String, List<Column>> getTableColumnMap() {
        return tableColumnMap;
    }

    /**
     * 将calcite解析树转化为自己的sql树结构
     *
     * @param rootNode
     * @param defaultDb
     * @return
     */
    public Node parseSql(SqlNode rootNode, String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        Node node = null;
        if (rootNode instanceof SqlSelect) {
            node = new SelectNode(defaultDb,tableColumnsMap);
            node.parseSql(rootNode);
        } else if (rootNode instanceof SqlInsert) {
            node = new InsertNode(defaultDb,tableColumnsMap);
            node.parseSql(rootNode);
        } else if (rootNode instanceof SqlCreateTable || rootNode instanceof SqlCreateView) {
            node = new CreateNode(defaultDb,tableColumnsMap);
            node.parseSql(rootNode);
        } else if (rootNode instanceof SqlBasicCall && SqlKind.UNION == ((SqlBasicCall) rootNode).getOperator().getKind()) {
            node = new UnionCall(defaultDb,tableColumnsMap);
            node.parseSql(rootNode);
        } else if (rootNode instanceof SqlDelete){
            node = new DeleteNode(defaultDb,tableColumnsMap);
            node.parseSql(rootNode);
        }
        return node;
    }

    /**
     * 解析字段血缘
     *
     * @param node
     * @return
     */
    public abstract List<Pair<Identifier, Identifier>> parseColumnLineage(Node node);

    public abstract List<Pair<Identifier, Identifier>> parseTableLineage(Node node);

    boolean isTableColumn(Identifier identifier) {
        String fullTable = identifier.getFullTable();
        if (Objects.nonNull(tableColumnMap) && tableColumnMap.keySet().contains(fullTable)) {
            List<Column> columns = tableColumnMap.get(fullTable);
            for (Column column : columns) {
                if (Objects.nonNull(column.getName()) && column.getName().equalsIgnoreCase(identifier.getColumn())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 侧视图专用方法
     * @param identifier
     * @param fromTable
     * @return
     */
    List<Identifier> isTableColumn(Identifier identifier, Identifier fromTable) {
        String fullTable = identifier.getFullTable();
        List<Identifier> identifiers = new ArrayList<>();
        if (tableColumnMap.keySet().contains(fullTable)) {
            List<Column> columns = tableColumnMap.get(fullTable);
            for (Column column : columns) {
                if (Objects.nonNull(column.getName()) && column.getName().equalsIgnoreCase(identifier.getColumn())) {
                    identifiers.add(identifier);
                }
            }
        }
        //todo  判断侧视图
        if (fromTable.getLateralView().containsKey(identifier.getColumn())){
                return  fromTable.getLateralView().get(identifier.getColumn());
        }
        return null;
    }

    protected List<Identifier> findColumnListFromQuery(Node source, Identifier tableName) {
        List<Identifier> resultList = Lists.newArrayList();
        SelectNode targetNode = null;
        //union查询
        if (source instanceof NodeList){
            NodeList sourceList = (NodeList) source;
            targetNode = (SelectNode) sourceList.getList().get(0);
        }
        //select
        else if (source instanceof SelectNode){
            targetNode = (SelectNode) source;
        }else {
            LOG.warn("这是不可能的:{}",source);
            return new ArrayList<>();
        }
        NodeList selectList = targetNode.getSelectList();
        for (int i = 0; i < selectList.getList().size(); i++) {
            Node column = selectList.getList().get(i);
            //常量
            if (column instanceof LiteralIdentifier){
                Identifier identifier = new Identifier(tableName.getDefaultDb(),getTableColumnMap());
                identifier.setDb(tableName.getDb());
                identifier.setTable(tableName.getTable());
                identifier.setColumn(column.getAlias());
                resultList.add(identifier);
            }
            //字段
            else if (column instanceof Identifier){
                Identifier identifier = new Identifier(tableName.getDefaultDb(),getTableColumnMap());
                identifier.setDb(tableName.getDb());
                identifier.setTable(tableName.getTable());
                String columnStr = StringUtils.isEmpty(column.getAlias())?((Identifier) column).getColumn():column.getAlias();
                identifier.setColumn(columnStr);
                resultList.add(identifier);
            }
            //函数字段
            else if (column instanceof BasicCall){
                Identifier identifier = new Identifier(tableName.getDefaultDb(),getTableColumnMap());
                identifier.setDb(tableName.getDb());
                identifier.setTable(tableName.getTable());
                if (StringUtils.isEmpty(column.getAlias())){
                    identifier.setColumn(((BasicCall) column).getName());
                }else {
                    identifier.setColumn(column.getAlias());
                }
                resultList.add(identifier);
            }
            //selectList中的子查询
            else if (column instanceof SelectNode){
                //TODO selectList 中子查询  只考虑一层嵌套
                String columnName = null;
                Identifier identifier = new Identifier(tableName.getDefaultDb(),getTableColumnMap());
                identifier.setDb(tableName.getDb());
                identifier.setTable(tableName.getTable());
                Node node =((SelectNode)column).getSelectList().getList().get(0);
                //todo 如果子查询最外面有别名的话 就拿最外面的
                if (StringUtils.isNotBlank(column.getAlias())){
                    columnName=column.getAlias();
                }else {
                    if (node instanceof BasicCall){
                        if (StringUtils.isNotBlank(node.getAlias())){
                            columnName=node.getAlias();
                        }else {
                            columnName=((BasicCall) node).getName();
                        }
                    }else if (node instanceof Identifier){
                        columnName=((Identifier) node).getColumn();
                    }
                }
                identifier.setColumn(columnName);
                resultList.add(identifier);
            }
        }
        return resultList;
    }

    /**
     * 获取一个血缘解析器
     * 只解析create和insert语句血缘关系
     */
    public static class ParserProxy {
        public static LineageParser getParser(SqlNode rootNode, String defaultDb, Map<String,List<Column>> metaDataMap){
            LineageParser parser = null;
            if (rootNode instanceof SqlInsert){
                parser = new InsertParser();
            }else if (rootNode instanceof SqlCreateTable || rootNode instanceof SqlCreateView){
                parser = new CreateParser();
            }else if (rootNode instanceof SqlDelete){
                parser = new DeleteParser();
            }else {
                return null;
            }
            parser.setTableColumnMap(metaDataMap);
            return parser;
        }
    }

    /**
     * 获取一个hive血缘解析器
     * 只解析create和insert语句血缘关系
     */
    public static class HiveParserProxy {
        public static LineageParser getParser(Node rootNode, String defaultDb, Map<String,List<Column>> metaDataMap){
            LineageParser parser = null;
            if (rootNode instanceof InsertNode){
                parser = new InsertParser();
            }else if (rootNode instanceof CreateNode){
                parser = new CreateParser();
            }else {
                return null;
            }
            parser.setTableColumnMap(metaDataMap);
            return parser;
        }
    }

    /**
     * 获取node下面所有的表
     * @param node
     * @return
     */
    public List<Identifier> getTableLineageByQuery(Node node){
        List<Identifier> tables = new ArrayList<>();
        if (null == node){
            return tables;
        }
        if (node instanceof NodeList){
            NodeList nodeList = (NodeList) node;
            for (Node n : nodeList.getList()){
                tables.addAll(getTableLineageByQuery(n));
            }
        }else if (node instanceof SelectNode){
            SelectNode selectNode  = (SelectNode) node;
            tables.addAll(getTableLineageByQuery(selectNode.getFromClause()));
        }else if (node instanceof JoinCall){
            JoinCall joinCall = (JoinCall)node;
            for (Node n : joinCall.getComboList()){
                tables.addAll(getTableLineageByQuery(n));
            }

        }else if (node instanceof UnionCall){
            UnionCall unionCall = (UnionCall)node;
            for (Node n : unionCall.getComboFromList()){
                tables.addAll(getTableLineageByQuery(n));
            }
        }else if (node instanceof Identifier){
            tables.add((Identifier) node);
        }
        return tables;
    }
}
