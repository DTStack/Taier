package com.dtstack.engine.sql.calcite;

import com.dtstack.engine.sql.node.*;
import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/11/1 17:36
 * @Description: libra语法支持在子查询中使用外层查询定的表别名。
 */
public class SelectSpecialParser extends LineageParser {

    public static Logger LOG = LoggerFactory.getLogger(SelectSpecialParser.class);

    private Map<String, Node> tableMap;

    public Map<String, Node> getTableMap() {
        return tableMap;
    }

    public void setTableMap(Map<String, Node> tableMap) {
        this.tableMap = tableMap;
    }

    /**
     * @param identifier
     * @param fromClause
     * @return
     */
    public List<Identifier> findSource(Identifier identifier, SelectNode fromClause) {
        List<Identifier> resList = Lists.newArrayList();
        NodeList selectList = fromClause.getSelectList();
        Node tmpSourceNode = null;
        for (int i = 0; i < selectList.getList().size(); i++) {
            Node columnNode = selectList.getList().get(i);
            if (columnNode instanceof LiteralIdentifier) {
                LiteralIdentifier literalIdentifier = (LiteralIdentifier) columnNode;
                if (identifier.getColumn().equalsIgnoreCase(literalIdentifier.getAlias())) {
                    tmpSourceNode = literalIdentifier;
                    break;
                }
            } else if (columnNode instanceof BasicCall) {
                BasicCall basicCall = (BasicCall) columnNode;
                if (StringUtils.isNotEmpty(basicCall.getAlias())) {
                    if (identifier.getColumn().equalsIgnoreCase(basicCall.getAlias())) {
                        tmpSourceNode = basicCall;
                        break;
                    }
                } else if (identifier.getColumn().equalsIgnoreCase(basicCall.getName())) {
                    tmpSourceNode = basicCall;
                    break;
                }

            } else if (columnNode instanceof Identifier) {
                Identifier tmpSourceColumn = (Identifier) columnNode;
                String handledColumn = StringUtils.isEmpty(columnNode.getAlias()) ? tmpSourceColumn.getColumn() : columnNode.getAlias();
                if (identifier.equals(tmpSourceColumn)) {
                    tmpSourceNode = tmpSourceColumn;
                    break;
                }
                if (StringUtils.isNotBlank(identifier.getTable())) {
                    if (StringUtils.isNotBlank(fromClause.getAlias())) {
                        if (identifier.getColumn().equalsIgnoreCase(handledColumn) &&
                                identifier.getTable().equalsIgnoreCase(tmpSourceColumn.getTable())) {
                            tmpSourceNode = tmpSourceColumn;
                            break;
                        }
                    }
                } else {
                    if (identifier.getColumn().equalsIgnoreCase(handledColumn)) {
                        tmpSourceNode = tmpSourceColumn;
                        break;
                    }
                }
            } else if (columnNode instanceof SelectNode) {
                //TODO 如果是字段里面的子查询  取子查询中的字段名 只考虑一层嵌套
                Node node = ((SelectNode) columnNode).getSelectList().getList().get(0);
                String columnName = null;
                if (((SelectNode) columnNode).getFromClause() instanceof Identifier) ;
                String tableName = ((Identifier) ((SelectNode) columnNode).getFromClause()).getTable();
                if (node instanceof BasicCall) {
                    if (StringUtils.isNotBlank(node.getAlias())) {
                        columnName = ((BasicCall) node).getAlias();
                    } else {
                        columnName = ((BasicCall) node).getName();
                    }
                } else if (node instanceof Identifier) {
                    columnName = ((Identifier) node).getColumn();
                    tableName = ((Identifier) node).getTable();
                }
                if (StringUtils.isNotBlank(identifier.getTable())) {
                    if (identifier.getColumn().equals(columnName) && identifier.getTable().equalsIgnoreCase(tableName)) {
                        tmpSourceNode = columnNode;
                    }
                } else {
                    if (identifier.getColumn().equals(columnName)) {
                        tmpSourceNode = columnNode;
                    }
                }

            }

        }

        if (tmpSourceNode == null) {
            LOG.warn("identifier:{}未匹配到source", identifier);
            return resList;
        }
        if (tmpSourceNode instanceof LiteralIdentifier) {
            return resList;
        }
        //字段为identifier
        else if (tmpSourceNode instanceof Identifier){
            //找到source，判断是否是表字段，不是的话继续往下找
            if (isTableColumn((Identifier) tmpSourceNode)) {
                resList.add((Identifier) tmpSourceNode);
            }
            else {
                //不是表字段，说明是子查询字段或者别名表，继续往下找
                Node subFromClause = fromClause.getFromClause();
                if (subFromClause == null){
                    return new ArrayList<>();
                }
                if (StringUtils.isNotEmpty(subFromClause.getAlias())){
                    if (tableMap.get(subFromClause.getAlias())!=null){
                        subFromClause =  tableMap.get(subFromClause.getAlias());
                    }

                }
                //子查询
                if (subFromClause instanceof SelectNode) {
                    isRemoveTableAlias((Identifier) tmpSourceNode, subFromClause);
                    List<Identifier> sourceList = findSource((Identifier) tmpSourceNode, (SelectNode) subFromClause);
                    if (CollectionUtils.isNotEmpty(sourceList)) {
                        resList.addAll(sourceList);
                    }
                }
                //from表
                else if (subFromClause instanceof Identifier){
                    Identifier table = (Identifier) subFromClause;
                    String tableName = StringUtils.isEmpty(table.getAlias())?table.getTable():table.getAlias();
                    if (((Identifier)tmpSourceNode).getTable().equalsIgnoreCase(tableName)) {
                        ((Identifier)tmpSourceNode).setTable(table.getTable());
                        ((Identifier)tmpSourceNode).setDb(table.getDb());
                        if (isTableColumn(((Identifier)tmpSourceNode))) {
                            resList.add(((Identifier)tmpSourceNode));
                        }
                    }
                }
                //join查询
                else if (subFromClause instanceof JoinCall) {
                    JoinCall joinCall = (JoinCall) subFromClause;
                    for (Node nd : joinCall.getComboList()) {
                        //join table
                        if (nd instanceof Identifier) {
                            String targetTable = ((Identifier) tmpSourceNode).getTable();
                            //子查询没有写别名的情况
                            if (StringUtils.isEmpty(targetTable)){
                                continue;
                            }
                            if (((Identifier)tmpSourceNode).getTable().equalsIgnoreCase(nd.getAlias())) {
                                ((Identifier)tmpSourceNode).setTable(((Identifier) nd).getTable());
                                ((Identifier)tmpSourceNode).setDb(((Identifier) nd).getDb());
                                if (isTableColumn(((Identifier)tmpSourceNode))) {
                                    resList.add(((Identifier)tmpSourceNode));
                                    break;
                                }
                            }
                        }
                        //join 子查询
                        else if (nd instanceof SelectNode) {
                            isRemoveTableAlias((Identifier) tmpSourceNode, nd);
                            List<Identifier> sourceList = findSource((Identifier)tmpSourceNode, (SelectNode) nd);
                            if (CollectionUtils.isNotEmpty(sourceList)) {
                                resList.addAll(sourceList);
                                break;
                            }
                        }
                        else if (nd instanceof UnionCall) {
                            //TODO 血缘分叉
                            isRemoveTableAlias((Identifier) tmpSourceNode, nd);
                            unionParser(resList, (Identifier) tmpSourceNode, (UnionCall) nd);
                        }
                    }
                }
                //union查询
                else if (subFromClause instanceof UnionCall) {
                    //TODO 血缘分叉
                    isRemoveTableAlias((Identifier) tmpSourceNode, subFromClause);
                    unionParser(resList, (Identifier) tmpSourceNode, (UnionCall) subFromClause);
                }
            }
        }
        //函数处理后的字段
        else if (tmpSourceNode instanceof BasicCall){
            getColumnBloodByBasicCall(fromClause, resList, (BasicCall) tmpSourceNode);
        }
        else if (tmpSourceNode instanceof SelectNode){
            //TODO  如果字段是子查询
            Identifier iden = firstMatch(0,(SelectNode)tmpSourceNode);
            resList.addAll(findSource(iden,(SelectNode)tmpSourceNode));
        }
        return resList;
    }

    private void unionParser(List<Identifier> resList, Identifier tmpSourceNode, UnionCall subFromClause) {
        UnionCall unionCall = subFromClause;
        for (SelectNode sn : unionCall.getComboFromList()) {
            List<Identifier> sourceList = findSource(tmpSourceNode, sn);
            for (Identifier i : sourceList) {
                if (!resList.contains(i)){
                    resList.add(i);
                }
            }
        }
    }

    /**
     * insert初次匹配时，按照index
     *
     * @param index
     * @param source
     * @return
     */
    private Identifier firstMatch(int index, SelectNode source) {
        List<Node> selectList = source.getSelectList().getList();
        Node node = selectList.get(index);
        if (node instanceof LiteralIdentifier) {
            return null;
        } else if (node instanceof Identifier) {
            return (Identifier) node;
        } else if (node instanceof BasicCall) {
            Identifier identifier = new Identifier(node.getDefaultDb(),getTableColumnMap());
            if (StringUtils.isNotEmpty(node.getAlias())) {
                identifier.setColumn(node.getAlias());
            } else {
                identifier.setColumn(((BasicCall) node).getName());
            }
            return identifier;
        } else if (node instanceof SelectNode) {
            //TODO
            Identifier identifier= firstMatch(0,((SelectNode) node));
            identifier.setContext(Node.Context.SELECT_COLUMN_QUERY);
            return identifier;
        }
        return null;
    }

    /**
     * 寻找子查询中的血缘
     *
     * @param tmpSourceNode
     * @param fromClause
     * @param resList
     */
    private void getColumnBloodBySqlSelect(SelectNode tmpSourceNode, SelectNode fromClause, List<Identifier> resList) {
        Node sonSelect = (tmpSourceNode).getSelectList().getList().get(0);
        if (sonSelect instanceof BasicCall) {
            getColumnBloodByBasicCall(fromClause, resList, (BasicCall) sonSelect);
        } else if (sonSelect instanceof Identifier) {
            resList.add((Identifier) sonSelect);
        } else if (sonSelect instanceof SelectNode) {
            getColumnBloodBySqlSelect((SelectNode) sonSelect, (SelectNode) sonSelect, resList);
        }
    }

    /**
     * 寻找函数中的血缘信息
     *
     * @param fromClause
     * @param resList
     * @param tmpSourceNode
     */
    private void getColumnBloodByBasicCall(SelectNode fromClause, List<Identifier> resList, BasicCall tmpSourceNode) {
        BasicCall tmpBasicCall = tmpSourceNode;
        for (Identifier comboIdentifier : tmpBasicCall.getComboList()) {
            if (comboIdentifier instanceof LiteralIdentifier) {
                continue;
            }
            if (comboIdentifier instanceof Identifier) {
                Node subFromClause = fromClause.getFromClause();
                if (StringUtils.isNotEmpty(subFromClause.getAlias()) && (!subFromClause.getContext().equals(Node.Context.SELECT_COLUMN_QUERY))) {
                    if (tableMap.get(subFromClause.getAlias()) != null) {
                        subFromClause = tableMap.get(subFromClause.getAlias());
                    }
                }
                //找到source，判断是否是表字段，不是的话继续往下找
                if (isTableColumn(comboIdentifier)) {
                    resList.add(comboIdentifier);
                }
                //from表
                else if (subFromClause instanceof Identifier) {
                    Identifier table = (Identifier) subFromClause;
                    String tableName = StringUtils.isEmpty(table.getAlias()) ? table.getTable() : table.getAlias();
                    if ((comboIdentifier).getTable().equalsIgnoreCase(tableName)) {
                        (comboIdentifier).setTable(table.getTable());
                        (comboIdentifier).setDb(table.getDb());
                        if (isTableColumn(comboIdentifier)) {
                            resList.add(comboIdentifier);
                        }
                    }
                }
                //from join查询
                else if (subFromClause instanceof JoinCall) {
                    JoinCall joinCall = (JoinCall) subFromClause;
                    for (Node nd : joinCall.getComboList()) {
                        //join table
                        if (nd instanceof Identifier) {
                            if (StringUtils.isEmpty(comboIdentifier.getTable())) {
                                continue;
                            }
                            if (comboIdentifier.getTable().equalsIgnoreCase(nd.getAlias())) {
                                comboIdentifier.setTable(((Identifier) nd).getTable());
                                comboIdentifier.setDb(((Identifier) nd).getDb());
                                if (isTableColumn(comboIdentifier)) {
                                    resList.add(comboIdentifier);
                                }
                            }
                        }
                        //join 子查询
                        else if (nd instanceof SelectNode) {
                            isRemoveTableAlias(comboIdentifier, nd);
                            List<Identifier> sourceList = findSource(comboIdentifier, (SelectNode) nd);
                            if (CollectionUtils.isNotEmpty(sourceList)) {
                                resList.addAll(sourceList);
                            }
                        }
                    }
                }
                //from子查询
                else if (subFromClause instanceof SelectNode) {
                    isRemoveTableAlias(comboIdentifier, subFromClause);
                    List<Identifier> sourceList = findSource(comboIdentifier, (SelectNode) subFromClause);
                    if (CollectionUtils.isNotEmpty(sourceList)) {
                        resList.addAll(sourceList);
                    }
                }
                //from union查询
                else if (subFromClause instanceof UnionCall) {
                    this.isRemoveTableAlias(comboIdentifier,subFromClause);
                    //TODO 血缘分叉
                    UnionCall unionCall = (UnionCall) subFromClause;
                    for (SelectNode sn : unionCall.getComboFromList()) {
                        List<Identifier> sourceList = findSource(comboIdentifier, sn);
                        if (CollectionUtils.isNotEmpty(sourceList)) {
                            resList.addAll(sourceList);
                        }
                    }
                }
            }
        }
    }

    /**
     * 如果 要判断的 Identifier 表别名和 node的别名相同 就不用判断 表别名了
     * @param comboIdentifier
     * @param nd
     */
    private void isRemoveTableAlias(Identifier comboIdentifier, Node nd) {
        if (StringUtils.isNotBlank(comboIdentifier.getTable()) && StringUtils.isNotBlank(nd.getAlias())) {
            if (comboIdentifier.getTable().equalsIgnoreCase(nd.getAlias())) {
                comboIdentifier.setTable(null);
            }
        }
    }


    @Override
    public List<Pair<Identifier, Identifier>> parseColumnLineage(Node node) {
        throw new IllegalStateException("select无血缘关系");
    }

    @Override
    public List<Pair<Identifier, Identifier>> parseTableLineage(Node node) {
        throw new IllegalStateException("select无血缘关系");
    }
}
