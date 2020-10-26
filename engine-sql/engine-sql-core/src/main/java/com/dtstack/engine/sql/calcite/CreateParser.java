package com.dtstack.engine.sql.calcite;

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

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/30 14:22
 * @Description:
 */
public class CreateParser extends LineageParser {
    public static Logger LOG = LoggerFactory.getLogger(CreateParser.class);

    private Map<String, Node> getTableMap(CreateNode node){
        Map<String, Node> resMap = new HashMap<>(16);
        Node source = node.getQuery();
        if (source instanceof NodeList){
            for (Node nd : ((NodeList) source).getList()){
                if (nd instanceof SelectNode){
                    resMap.putAll(((SelectNode) nd).getTableMap());
                }
            }
        }else if (source instanceof SelectNode){
            resMap.putAll(((SelectNode) source).getTableMap());
        }
        return resMap;
    }

    @Override
    public List<Pair<Identifier, Identifier>> parseColumnLineage(Node createNode){
        if (!(createNode instanceof CreateNode)){
            throw new IllegalArgumentException("类型错误");
        }
        SelectSpecialParser selectParser = new SelectSpecialParser();
        selectParser.setTableMap(getTableMap((CreateNode) createNode));
        selectParser.setTableColumnMap(getTableColumnMap());
        CreateNode parseNode = (CreateNode) createNode;
        List<Pair<Identifier, Identifier>> resultList = Lists.newArrayList();
        Node source = parseNode.getQuery();
        if (source == null){
            return resultList;
        }
        Identifier tableName = parseNode.getName();

        List<Identifier> columnList = findColumnListFromQuery(source,tableName);

        for (int i = 0; i < columnList.size(); i++) {
            Identifier key = columnList.get(i);
            //union
            if (source instanceof NodeList){
                List<Node> list = ((NodeList) source).getList();
                for (Node nd : list){
                    if (nd instanceof SelectNode){
                        Identifier identifier = firstMatch(i, (SelectNode) nd);
                        //丢弃常量
                        if(identifier == null){
                            continue;
                        }
                        List<Identifier> sourceIdentifiers = selectParser.findSource(identifier,(SelectNode)nd);
                        if (CollectionUtils.isNotEmpty(sourceIdentifiers)){
                            for (Identifier id : sourceIdentifiers){
                                resultList.add(new Pair<>(key,id));
                            }
                        }
                    }else {
                        LOG.warn("这应该是不可能出现的");
                    }
                }
            }
            //select
            else if (source instanceof SelectNode){
                Identifier identifier = firstMatch(i, (SelectNode) source);
                //丢弃常量
                if(identifier == null){
                    continue;
                }

                List<Identifier> sourceIdentifiers = selectParser.findSource(identifier,(SelectNode)source);
                if (CollectionUtils.isNotEmpty(sourceIdentifiers)){
                    for (Identifier id : sourceIdentifiers){
                        resultList.add(new Pair<>(key,id));
                    }
                }
            }

        }
        return resultList;
    }

    @Override
    public List<Pair<Identifier, Identifier>> parseTableLineage(Node node) {
        List<Pair<Identifier, Identifier>> tableLineage = new ArrayList<>();
        CreateNode createNode  = (CreateNode) node;
        List<Identifier> tableList = getTableLineageByQuery(createNode.getQuery());
        if (CollectionUtils.isNotEmpty(tableList)){
            for (Identifier identifier : tableList) {
                tableLineage.add(new Pair<>(createNode.getName(), identifier));
            }
        }
        return tableLineage;
    }



    /**
     * insert初次匹配时，按照index
     * @param index
     * @param source
     * @return
     */
    private Identifier firstMatch(int index, SelectNode source){
        List<Node> selectList = source.getSelectList().getList();
        Node node = selectList.get(index);
        if (node instanceof LiteralIdentifier){
            return null;
        }
        else if (node instanceof Identifier){
            return (Identifier) node;
        }
        else if (node instanceof BasicCall){
            Identifier identifier = new Identifier(node.getDefaultDb(),getTableColumnMap());
            if (StringUtils.isNotEmpty(node.getAlias())){
                identifier.setColumn(node.getAlias());
            }else {
                identifier.setColumn(((BasicCall) node).getName());
            }
            return identifier;
        }
        else if (node instanceof SelectNode){
            //TODO 字段里是子查询的 那就拿到
            Identifier identifier= firstMatch(0,((SelectNode) node));
            identifier.setContext(Node.Context.SELECT_COLUMN_QUERY);
            return identifier;
        }
        return null;
    }

}
