package com.dtstack.engine.sql.node;


import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.calcite.SqlNodeType;
import com.google.common.collect.Lists;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSelect;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/27 10:35
 * @Description:
 */
public class InsertNode extends Node {

    public static Logger LOG = LoggerFactory.getLogger(InsertNode.class);

    /**
     * insert的columnList基本上都是identifier
     */
    private List<Identifier> columnList;
    /**
     * 通常都是插入表
     */
    private Identifier targetTable;


    /**
     * 通常为一个selectNode。union的情况下是一个NodeList，包含List<selectNode>
     */
    private Node source;

    private SqlNodeType type;

    public InsertNode(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb,tableColumnsMap);
    }

    public List<Identifier> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Identifier> columnList) {
        this.columnList = columnList;
    }

    public Identifier getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(Identifier targetTable) {
        this.targetTable = targetTable;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public SqlNodeType getType() {
        return type;
    }

    public void setType(SqlNodeType type) {
        this.type = type;
    }

    @Override
    public Node parseSql(SqlNode node) {
        SqlInsert sqlInsert = checkNode(node);
        SqlNodeList targetColumnList = sqlInsert.getTargetColumnList();

        handleColumnList(targetColumnList);

        handleTargetTable(sqlInsert.getTargetTable());

        handleSource(sqlInsert.getSource());
        return null;
    }

    private void handleSource(SqlNode source) {
        //insert into values语法,operator为  SqlValuesOperator
        //union
        if (source instanceof SqlBasicCall){
            SqlOperator operator = ((SqlBasicCall) source).getOperator();
            if (SqlKind.UNION == operator.kind){
                UnionCall unionCall = new UnionCall(getDefaultDb(),getTableColumnMap());
                unionCall.parseSql(source);
                NodeList nodeList = new NodeList(getDefaultDb(),getTableColumnMap());
                nodeList.setContext(Context.INSERT_FROM_UNION);
                List<Node> nodes = Lists.newArrayList();
                nodes.addAll(unionCall.getComboFromList());
                nodeList.setList(nodes);
                this.source = nodeList;
            }
        }else if (source instanceof SqlSelect){
            SelectNode selectNode = new SelectNode(getDefaultDb(),getTableColumnMap());
            selectNode.parseSql(source);
            this.source = selectNode;
        }
    }

    /**
     *
     * @param targetTable
     */
    private void handleTargetTable(SqlNode targetTable) {
        //insert的target通常都是表
        if (targetTable instanceof SqlIdentifier){
            Identifier identifier = new Identifier(getDefaultDb(),getTableColumnMap());
            identifier.setContext(Context.IDENTIFIER_TABLE);
            identifier.parseSql(targetTable);
            this.targetTable = identifier;
            fillColumnDbAndTable(identifier.getDb(),identifier.getTable());
        }
    }

    /**
     * 将columnList中的identifier的db和table信息填充
     * @param db
     * @param table
     */
    private void fillColumnDbAndTable(String db, String table) {
        for (Identifier identifier:columnList){
            if (StringUtils.isNotEmpty(db)){
                identifier.setDb(db);
            }
            if (StringUtils.isEmpty(identifier.getTable())){
                identifier.setTable(table);
            }
        }
    }

    /**
     *
     * @param targetColumnList
     */
    private void handleColumnList(SqlNodeList targetColumnList) {
        if (CollectionUtils.isEmpty(columnList)){
            columnList = Lists.newArrayList();
        }
        if (targetColumnList == null){
            // insert into select 语句
            return;
        }
        for (SqlNode node : targetColumnList.getList()){
            Identifier identifier = new Identifier(getDefaultDb(),getTableColumnMap());
            identifier.setContext(Context.IDENTIFIER_COLUMN);
            identifier.parseSql(node);
            columnList.add(identifier);
        }
    }

    private SqlInsert checkNode(SqlNode node){
        if (!(node instanceof SqlInsert)){
            throw new IllegalStateException("sqlNode类型不匹配");
        }
        return (SqlInsert) node;
    }
}
