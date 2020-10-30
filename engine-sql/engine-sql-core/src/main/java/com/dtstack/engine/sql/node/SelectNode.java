package com.dtstack.engine.sql.node;


import com.dtstack.engine.sql.Column;
import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.dtstack.apache.calcite.sql.SqlBasicCall;
import org.dtstack.apache.calcite.sql.SqlIdentifier;
import org.dtstack.apache.calcite.sql.SqlJoin;
import org.dtstack.apache.calcite.sql.SqlKind;
import org.dtstack.apache.calcite.sql.SqlLiteral;
import org.dtstack.apache.calcite.sql.SqlNode;
import org.dtstack.apache.calcite.sql.SqlNodeList;
import org.dtstack.apache.calcite.sql.SqlOrderBy;
import org.dtstack.apache.calcite.sql.SqlSelect;
import org.dtstack.apache.calcite.sql.fun.SqlCase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/27 10:56
 * @Description: 子查询包括：where,having,from,select,exist子查询。其中需要关注from子查询select子查询。
 * select子查询在中原需求中基本没有，遇到再处理。
 */
public class SelectNode extends Node {

    public static Logger LOG = LoggerFactory.getLogger(SelectNode.class);

    /**
     * select的selectList最简单的形式是单一identifier，常常存在函数组合多个column identifier的形式。
     * 通常，类型为identifier或者call，也存在select 子查询的情况。eg:select id,name ,(select sex from b where b.id = a.id) from a;
     * selectItem类型常见：identifier,sqlBasicCall和sqlSelect
     * 如果selectItem是identifier需要考虑*的情况。此时分为两种情况：1:全* 2:a.*,b.*
     * 如果selectItem是sqlSelect需要将子select的selectList提出，并入上层select的selectList
     * 如果selectItem是sqlCall（常见sqlBasicCall和sqlCase），内部字段不能外提，但需要有组合字段列表
     */
    private NodeList selectList;

    /**
     * 目前只考虑子查询和join,union
     * <p>
     * from内可能存在的语法：
     * 1.开窗函数
     * 2.FOR SYSTEM_TIME AS OF
     * 3.MATCH_RECOGNIZE
     * 4.子查询
     * 5.UNNEST ... WITH ORDINALITY （postgreSql支持该语法）
     * 6.SELECT * FROM emp TABLESAMPLE SUBSTITUTE('medium')
     * 7.join
     * 8.union
     */
    private Node fromClause;

    /**
     * key：表/子查询别名
     * value：表/子查询
     * 由于libra在内层子查询中可以使用外层定义的别名
     */
    private Map<String, Node> tableMap;

    private Node where;

    private NodeList groupBy;

    private Node hiving;

    private NodeList windowDecls;

    private List<Long> limit;

    public SelectNode(String defaultDb,Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb,tableColumnsMap);
    }

    public NodeList getSelectList() {
        return selectList;
    }

    public void setSelectList(NodeList selectList) {
        this.selectList = selectList;
    }

    public Node getFromClause() {
        return fromClause;
    }

    public void setFromClause(Node fromClause) {
        this.fromClause = fromClause;
    }

    public Map<String, Node> getTableMap() {
        return tableMap;
    }

    public void setTableMap(Map<String, Node> tableMap) {
        this.tableMap = tableMap;
    }

    public List<Long> getLimit() {
        return limit;
    }

    public void setLimit(List<Long> limit) {
        this.limit = limit;
    }

    @Override
    public Node parseSql(SqlNode node) {
        SqlSelect sqlSelect = checkNode(node);
        SqlNodeList selectList = sqlSelect.getSelectList();
        handleSelectList(selectList);
        SqlNode from = sqlSelect.getFrom();
        handleFrom(from);
        List<Node> columnList= new ArrayList<>();
        if (selectStarHandle(this.selectList,this.fromClause,columnList)) {
            getSelectList().getList().addAll(columnList);
        }
        handleTableMap();
        return null;
    }

    /**
     * select * 处理器
     */
    private Boolean selectStarHandle(NodeList selectList, Node fromClause, List<Node> columns){
        List<Node>  removeNode = new ArrayList<>();
        Boolean flag =false;
        for (Node sn : selectList.getList()) {
            if (sn instanceof Identifier){
                Identifier identifier = (Identifier) sn;
                if (identifier.getContext()!=null && identifier.getContext().equals(Context.IDENTIFIER_COLUMN ) && identifier.isSelectStarFromTable()){
                    if (StringUtils.isBlank(identifier.getTable())){
                        getColumnByNodeSelectAll(fromClause,columns);
                    }else {
                        getSelectStar(identifier,fromClause,columns);
                    }
                    removeNode.add(sn);
                    flag=true;
                }else {
                    columns.add(sn);
                }
            }else {
                columns.add(sn);
            }
        }
        selectList.getList().removeAll(removeNode);
        return flag;
    }

    private void getSelectStar(Identifier identifier, Node fromClause, List<Node> columns){
        //说明直接是表 或者是子查询
        if (getTableColumnMap().containsKey(identifier.getFullTable())){
            List<Column> columnList=getTableColumnMap().get(identifier.getFullTable());
            columnList.forEach(c->{
                Identifier i = new Identifier(identifier.getDefaultDb(),getTableColumnMap());
                i.setColumn(c.getName());
                i.setTable(identifier.getTable());
                i.setAlias(c.getAlias());
                i.setDb(identifier.getDb());
                i.setContext(Context.IDENTIFIER_COLUMN);
                columns.add(i);
            });
        }else if (this.getTableMap()!=null && this.getTableMap().containsKey(identifier.getTable())){
            Node columnNode =getTableMap().get(identifier.getTable());
            getColumnByNode(identifier,columnNode,columns);
        }else {
            getColumnByNode(identifier,fromClause,columns);
        }
    }
    private void getColumnByNode(Identifier identifier, Node subFromClause, List<Node> columns){
        //子查询
        if (subFromClause instanceof SelectNode) {
            if (identifier.getTable().equalsIgnoreCase(subFromClause.getAlias())) {
                selectStarHandle(((SelectNode) subFromClause).getSelectList(), ((SelectNode) subFromClause).getFromClause(), columns);
            }
        }
        //from表
        else if (subFromClause instanceof Identifier){
            if (identifier.getTable().equalsIgnoreCase(subFromClause.getAlias())){
                List<Column> columnList=getTableColumnMap().get(((Identifier)subFromClause).getFullTable());
                needAddColumns((Identifier) subFromClause, columns, columnList);
            }
        }
        //join查询
        else if (subFromClause instanceof JoinCall) {
            JoinCall joinCall = (JoinCall) subFromClause;
            for (Node nd : joinCall.getComboList()) {
                if (!identifier.getTable().equalsIgnoreCase(nd.getAlias())){
                    continue;
                }
                //join table
                if (nd instanceof Identifier) {
                    List<Column> columnList=getTableColumnMap().get(((Identifier)subFromClause).getFullTable());
                    needAddColumns((Identifier) subFromClause, columns, columnList);
                }
                //join 子查询
                else if (nd instanceof SelectNode) {
                    selectStarHandle(((SelectNode)subFromClause).getSelectList(),((SelectNode) subFromClause).getFromClause(),columns);
                }
                break;
            }
        }
        //union查询
        else if (subFromClause instanceof UnionCall) {
            //TODO 血缘分叉
            UnionCall unionCall = (UnionCall) subFromClause;
            for (SelectNode sn : unionCall.getComboFromList()) {
                if (!identifier.getTable().equalsIgnoreCase(sn.getAlias())){
                    continue;
                }
                selectStarHandle(sn.getSelectList(),sn.getFromClause(),columns);
                break;
            }
        }
    }

    private void getColumnByNodeSelectAll(Node subFromClause, List<Node> columns){
        //子查询
        if (subFromClause instanceof SelectNode) {
            selectStarHandle(((SelectNode)subFromClause).getSelectList(),((SelectNode) subFromClause).getFromClause(),columns);
        }
        //from表
        else if (subFromClause instanceof Identifier){
            List<Column> columnList=getTableColumnMap().get(((Identifier)subFromClause).getFullTable());
            needAddColumns((Identifier) subFromClause, columns, columnList);
        }
        //join查询
        else if (subFromClause instanceof JoinCall) {
            JoinCall joinCall = (JoinCall) subFromClause;
            for (Node nd : joinCall.getComboList()) {
                //join table
                if (nd instanceof Identifier) {
                    List<Column> columnList=getTableColumnMap().get(((Identifier)nd).getFullTable());
                    needAddColumns((Identifier) nd, columns, columnList);
                }
                //join 子查询
                else if (nd instanceof SelectNode) {
                    selectStarHandle(((SelectNode)nd).getSelectList(),((SelectNode) nd).getFromClause(),columns);
                }
            }
        }
        //union查询
        else if (subFromClause instanceof UnionCall) {
            //TODO 血缘分叉
            UnionCall unionCall = (UnionCall) subFromClause;
            for (SelectNode sn : unionCall.getComboFromList()) {
                selectStarHandle(sn.getSelectList(),sn.getFromClause(),columns);
            }
        }
    }

    private void needAddColumns(Identifier subFromClause, List<Node> columns, List<Column> columnList) {
        columnList.forEach(c->{
            Identifier i = new Identifier(subFromClause.getDefaultDb(),getTableColumnMap());
            i.setColumn(c.getName());
            i.setTable(subFromClause.getTable());
            i.setAlias(c.getAlias());
            i.setDb(subFromClause.getDb());
            i.setContext(Context.IDENTIFIER_COLUMN);
            columns.add(i);
        });
    }

    private void handleTableMap() {
        if(tableMap == null){
            tableMap = new HashMap<>();
        }
        if (fromClause != null){
            addTableMap(fromClause.getAlias(),fromClause);
        }
    }

    private void handleFrom(SqlNode from) {
        Pair<String, SqlNode> sqlNodePair = removeAs(from);
        String alias = null;
        SqlNode handledNode = from;
        String db = null;
        String table = null;
        if (sqlNodePair != null) {
            alias = sqlNodePair.getKey();
            handledNode = sqlNodePair.getValue();
            //填充selectList中的每一项identifier的table
            table = alias;
        }
        //from 表
        if (handledNode instanceof SqlIdentifier) {
            Identifier identifier = new Identifier(getDefaultDb(),getTableColumnMap());
            identifier.setAlias(alias);
            identifier.setContext(Context.IDENTIFIER_TABLE);
            identifier.parseSql(handledNode);
            setFromClause(identifier);
            db = identifier.getDb();
            table = identifier.getTable();
        }
        //join
        else if (handledNode instanceof SqlJoin) {
            JoinCall joinCall = new JoinCall(getDefaultDb(),getTableColumnMap());
            joinCall.setContext(Context.CALL_JOIN);
            joinCall.setAlias(alias);
            joinCall.parseSql(handledNode);
            setFromClause(joinCall);
        }
        //union
        else if (handledNode instanceof SqlBasicCall && handledNode.getKind() == SqlKind.UNION) {
            UnionCall unionCall = new UnionCall(getDefaultDb(),getTableColumnMap());
            unionCall.setContext(Context.CALL_UNION);
            unionCall.setAlias(alias);
            unionCall.parseSql(handledNode);
            setFromClause(unionCall);
        }
        //子查询
        else if (handledNode instanceof SqlSelect) {
            SelectNode selectNode = new SelectNode(getDefaultDb(),getTableColumnMap());
            selectNode.setContext(Context.FROM_SUB_QUERY);
            selectNode.setAlias(alias);
            selectNode.parseSql(handledNode);
            setFromClause(selectNode);
        } //orderBy子查询
        else if (handledNode instanceof SqlOrderBy) {
            SelectNode selectNode = new SelectNode(getDefaultDb(),getTableColumnMap());
            selectNode.setContext(Context.FROM_SUB_QUERY);
            selectNode.setAlias(alias);
            selectNode.parseSql(((SqlOrderBy) handledNode).getOperandList().get(0));
            setFromClause(selectNode);
        }
        fillColumnTable(db,table);
    }

    private void addTableMap(String alias, Node table){
        if (table instanceof Identifier){
            if(StringUtils.isEmpty(alias)){
                return;
            }
            tableMap.put(alias,table);
        }else if (table instanceof JoinCall){
            //暂时不添加子查询别名
            if (StringUtils.isNotEmpty(table.getAlias())){
                tableMap.put(table.getAlias(),table);
            }
            JoinCall jtb = (JoinCall) table;
            List<Node> comboList = jtb.getComboList();
            for (Node cb : comboList){
                addTableMap(cb.getAlias(),cb);
            }
        }else if (table instanceof UnionCall){
            if (StringUtils.isNotEmpty(table.getAlias())){
                tableMap.put(table.getAlias(),table);
            }
            UnionCall utb = (UnionCall) table;
            List<SelectNode> comboFromList = utb.getComboFromList();
            for (Node cb : comboFromList){
                addTableMap(cb.getAlias(),cb);
            }
        }else if (table instanceof SelectNode){
            if (StringUtils.isNotEmpty(table.getAlias())){
                tableMap.put(table.getAlias(),table);
            }
            SelectNode stb = (SelectNode) table;
            Node fromClause = stb.getFromClause();
            if(Objects.nonNull(fromClause)){
                addTableMap(fromClause.getAlias(),fromClause);
            }
        }
    }

    /**
     * 如果from语句有别名是一张表，填充selectList中的字段中表信息
     *
     * @param alias
     */
    private void fillColumnTable(String db, String alias) {
        List<Node> list = selectList.getList();
        for (Node node : list) {
            if (node instanceof Identifier) {
                if (StringUtils.isEmpty(((Identifier) node).getTable())) {
                    ((Identifier) node).setTable(alias);
                }
                if (StringUtils.isNotEmpty(db)) {
                    ((Identifier) node).setDb(db);
                }

            }
            //函数字段，包括case
            else if (node instanceof BasicCall) {
                List<Identifier> comboList = ((BasicCall) node).getComboList();
                for (Identifier id : comboList) {
                    if (StringUtils.isEmpty(id.getTable())) {
                        id.setTable(alias);
                    }
                    if(StringUtils.isNotBlank(db)){
                        // 以from 解析出来为准
                        id.setDb(db);
                    }
                }
            }
            //selectList中的子查询
            else if (node instanceof SelectNode) {
                //FIXME selectList中的select暂未处理  不需要处理
            }
            //union
            else if (node instanceof UnionCall) {
                //FIXME union暂未处理  在解析时特殊处理了 这里不需要处理了
            }

        }
    }

    private void handleSelectList(SqlNodeList selectList) {
        NodeList nodeList = new NodeList(getDefaultDb(),getTableColumnMap());
        List<Node> list = Lists.newArrayList();
        for (SqlNode sn : selectList.getList()) {
            //合并as
            Pair<String, SqlNode> sqlNodePair = removeAs(sn);
            SqlNode handledNode = sn;
            String alias = null;
            if (sqlNodePair != null) {
                handledNode = sqlNodePair.getValue();
                alias = sqlNodePair.getKey();
            }
            if (handledNode instanceof SqlIdentifier) {
                Identifier identifier = new Identifier(getDefaultDb(),getTableColumnMap());
                identifier.setAlias(alias);
                identifier.setContext(Context.IDENTIFIER_COLUMN);
                identifier.parseSql(handledNode);
                list.add(identifier);
            }
            //常量
            else if (handledNode instanceof SqlLiteral) {
                LiteralIdentifier literalIdentifier = new LiteralIdentifier(getDefaultDb(),getTableColumnMap());
                literalIdentifier.setAlias(alias);
                literalIdentifier.parseSql(handledNode);
                list.add(literalIdentifier);
            }
            //selectList中的子查询
            else if (handledNode instanceof SqlSelect) {
                SelectNode selectNode = new SelectNode(getDefaultDb(),getTableColumnMap());
                selectNode.setAlias(alias);
                selectNode.setContext(Context.SELECT_SUB_QUERY);
                selectNode.parseSql(handledNode);
                list.add(selectNode);
            }
            //函数
            else if (handledNode instanceof SqlBasicCall) {
                BasicCall basicCall = new BasicCall(getDefaultDb(),getTableColumnMap());
                basicCall.setAlias(alias);
                basicCall.setContext(Context.CALL_IN_COLUMN);
                basicCall.parseSql(handledNode);
                list.add(basicCall);
            }
            //case when
            else if (handledNode instanceof SqlCase) {
                BasicCall basicCall = new BasicCall(getDefaultDb(),getTableColumnMap());
                basicCall.setAlias(alias);
                basicCall.setContext(Context.CASE_IN_COLUMN);
                basicCall.parseSql(handledNode);
                list.add(basicCall);
            } else {
                LOG.warn("未处理的sql类型:{}", handledNode.getKind());
            }

        }
        nodeList.setList(list);
        this.setSelectList(nodeList);
    }


    private SqlSelect checkNode(SqlNode node) {
        if (!(node instanceof SqlSelect)) {
            throw new IllegalStateException("不匹配的sqlNode类型");
        }
        return (SqlSelect) node;
    }
}
