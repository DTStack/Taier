package com.dtstack.engine.sql.calcite;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.node.*;
import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.apache.commons.collections.CollectionUtils;
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
 * @Date: 2019/10/30 00:13
 * @Description:
 */
public class InsertParser extends LineageParser {

    public static Logger LOG = LoggerFactory.getLogger(InsertParser.class);

    private Map<String, Node> getTableMap(InsertNode node) {
        Map<String, Node> resMap = new HashMap<>();
        Node source = node.getSource();
        if (source instanceof NodeList) {
            for (Node nd : ((NodeList) source).getList()) {
                if (nd instanceof SelectNode) {
                    resMap.putAll(((SelectNode) nd).getTableMap());
                }
            }
        } else if (source instanceof SelectNode) {
            resMap.putAll(((SelectNode) source).getTableMap());
        }
        return resMap;
    }

    @Override
    public List<Pair<Identifier, Identifier>> parseColumnLineage(Node insertNode) {
        if (!(insertNode instanceof InsertNode)) {
            throw new IllegalArgumentException("类型错误");
        }
        SelectSpecialParser selectParser = new SelectSpecialParser();
        selectParser.setTableMap(getTableMap((InsertNode) insertNode));
        selectParser.setTableColumnMap(getTableColumnMap());
        InsertNode parseNode = (InsertNode) insertNode;
        List<Pair<Identifier, Identifier>> resultList = Lists.newArrayList();
        Node source = parseNode.getSource();
        if (source == null) {
            return resultList;
        }
        List<Identifier> columnList = null;

        columnList = fullColumnByTableMap(parseNode);
        for (int i = 0; i < columnList.size(); i++) {
            Identifier key = columnList.get(i);
            if (source instanceof NodeList) {
                List<Node> list = ((NodeList) source).getList();
                for (Node nd : list) {
                    if (nd instanceof SelectNode) {
                        Identifier identifier = firstMatch(i, (SelectNode) nd);
                        if (identifier == null) {
                            continue;
                        }
                        List<Identifier> sourceIdentifiers = selectParser.findSource(identifier, (SelectNode) nd);
                        if (CollectionUtils.isNotEmpty(sourceIdentifiers)) {
                            for (Identifier id : sourceIdentifiers) {
                                resultList.add(new Pair<>(key, id));
                            }
                        }
                    } else {
                        LOG.warn("这应该是不可能出现的");
                    }
                }
            }
            //select
            else if (source instanceof SelectNode) {
                Identifier identifier = firstMatch(i, (SelectNode) source);
                if (identifier == null) {
                    continue;
                }
                List<Identifier> sourceIdentifiers = selectParser.findSource(identifier, (SelectNode) source);
                if (CollectionUtils.isNotEmpty(sourceIdentifiers)) {
                    for (Identifier id : sourceIdentifiers) {
                        resultList.add(new Pair<>(key, id));
                    }
                }
            }

        }
        return resultList;
    }

    /**
     * 如果insert 语句没有写入字段信息 那就从源数据中获取
     * @param parseNode
     * @return
     */
    private List<Identifier> fullColumnByTableMap(InsertNode parseNode) {
        List<Identifier> columnList;
        if (CollectionUtils.isEmpty(parseNode.getColumnList()) && Objects.nonNull(getTableColumnMap())) {
            List<Column> columns = getTableColumnMap().get(parseNode.getTargetTable().getFullTable());
            if (CollectionUtils.isNotEmpty(columns)) {
                List<Identifier> identifiers = new ArrayList<>();
                columns.forEach(c -> {
                    Identifier i = new Identifier(parseNode.getTargetTable().getDb(), getTableColumnMap());
                    i.setColumn(c.getName());
                    i.setTable(parseNode.getTargetTable().getTable());
                    i.setAlias(c.getAlias());
                    i.setDb(parseNode.getTargetTable().getDb());
                    i.setContext(Node.Context.IDENTIFIER_COLUMN);
                    identifiers.add(i);
                });
                columnList = identifiers;
            } else {
                columnList = new ArrayList<>();
            }

        } else {
            columnList = parseNode.getColumnList();
        }
        return columnList;
    }

    @Override
    public List<Pair<Identifier, Identifier>> parseTableLineage(Node node) {
        List<Pair<Identifier, Identifier>> tableLineage = new ArrayList<>();
        InsertNode insertNode  = (InsertNode) node;
        List<Identifier> tableList = getTableLineageByQuery(insertNode.getSource());
        if (CollectionUtils.isNotEmpty(tableList)){
            for (Identifier identifier : tableList) {
                tableLineage.add(new Pair<>(insertNode.getTargetTable(), identifier));
            }
        }
        return tableLineage;
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
        if(index >= selectList.size()){
            return null;
        }
        Node node = selectList.get(index);
        if (node instanceof LiteralIdentifier) {
            return null;
        } else if (node instanceof Identifier) {
            return (Identifier) node;
        } else if (node instanceof BasicCall) {
            Identifier identifier = new Identifier(node.getDefaultDb(), getTableColumnMap());
            if (StringUtils.isNotEmpty(node.getAlias())) {
                identifier.setColumn(node.getAlias());
            } else {
                identifier.setColumn(((BasicCall) node).getName());
            }
            return identifier;
        } else if (node instanceof SelectNode) {
            // 如果是子查询的话 如果有别名 寻找别名 没有别名拿到第一个
            SelectNode selectNode = (SelectNode) node;
            Identifier identifier ;
            if (StringUtils.isNotBlank(selectNode.getAlias())){
                identifier = new Identifier(node.getDefaultDb(), getTableColumnMap());
                identifier.setColumn(selectNode.getAlias());
            }else {
                identifier = firstMatch(0, ((SelectNode) node));
            }
            identifier.setContext(Node.Context.SELECT_COLUMN_QUERY);
            return identifier;
        }
        return null;
    }

}
