package com.dtstack.engine.sql.calcite;

import com.dtstack.engine.sql.node.*;
import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/29 20:25
 * @Description:
 */
public class SelectParser extends LineageParser {

    public static Logger LOG = LoggerFactory.getLogger(SelectParser.class);

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
            if (columnNode instanceof LiteralIdentifier){
                LiteralIdentifier literalIdentifier = (LiteralIdentifier) columnNode;
                if (identifier.getColumn().equalsIgnoreCase(literalIdentifier.getAlias())){
                    tmpSourceNode = literalIdentifier;
                    break;
                }
            }
            else if (columnNode instanceof BasicCall){
                BasicCall basicCall = (BasicCall) columnNode;
                if (StringUtils.isNotEmpty(basicCall.getAlias())){
                    if (identifier.getColumn().equalsIgnoreCase(basicCall.getAlias())){
                        tmpSourceNode = basicCall;
                        break;
                    }
                }else if (identifier.getColumn().equalsIgnoreCase(basicCall.getName())){
                    tmpSourceNode = basicCall;
                    break;
                }

            }
            else if (columnNode instanceof Identifier){
                Identifier tmpSourceColumn = (Identifier) columnNode;
                String handledColumn = StringUtils.isEmpty(columnNode.getAlias()) ? tmpSourceColumn.getColumn() : columnNode.getAlias();
                if (identifier.equals(tmpSourceColumn)){
                    tmpSourceNode = tmpSourceColumn;
                    break;
                }
                if (identifier.getColumn().equalsIgnoreCase(handledColumn)){
                    tmpSourceNode = tmpSourceColumn;
                    break;
                }
            }
            else if (tmpSourceNode instanceof SelectNode){
                //TODO
            }

        }

        if (tmpSourceNode == null){
            LOG.warn("identifier:{}未匹配到source",identifier);
            return resList;
        }
        if (tmpSourceNode instanceof LiteralIdentifier){
            return resList;
        }
        //字段为identifier
        else if (tmpSourceNode instanceof Identifier){
            //找到source，判断是否是表字段，不是的话继续往下找
            if (isTableColumn((Identifier) tmpSourceNode)) {
                resList.add((Identifier) tmpSourceNode);
            }
            //不是表字段，说明是子查询字段，继续往下找
            else {
                Node subFromClause = fromClause.getFromClause();
                //子查询
                if (subFromClause instanceof SelectNode) {
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
                                }
                            }
                        }
                        //join 子查询
                        else if (nd instanceof SelectNode) {
                            List<Identifier> sourceList = findSource((Identifier)tmpSourceNode, (SelectNode) nd);
                            if (CollectionUtils.isNotEmpty(sourceList)) {
                                resList.addAll(sourceList);
                            }
                        }
                    }
                }
                //union查询
                else if (subFromClause instanceof UnionCall) {
                    //TODO 血缘分叉
                    UnionCall unionCall = (UnionCall) subFromClause;
                    for (SelectNode sn : unionCall.getComboFromList()) {

                    }
                }
            }
        }
        //函数处理后的字段
        else if (tmpSourceNode instanceof BasicCall){
            BasicCall tmpBasicCall = (BasicCall) tmpSourceNode;
            for (Identifier comboIdentifier : tmpBasicCall.getComboList()){
                if (comboIdentifier instanceof LiteralIdentifier){
                    continue;
                }
                if (comboIdentifier instanceof Identifier){
                    Node subFromClause = fromClause.getFromClause();
                    //找到source，判断是否是表字段，不是的话继续往下找
                    if (isTableColumn(comboIdentifier)) {
                        resList.add(comboIdentifier);
                    }
                    //from表
                    else if (subFromClause instanceof Identifier){
                        Identifier table = (Identifier) subFromClause;
                        String tableName = StringUtils.isEmpty(table.getAlias())?table.getTable():table.getAlias();
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
                                if (StringUtils.isEmpty(comboIdentifier.getTable())){
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
                                List<Identifier> sourceList = findSource(comboIdentifier, (SelectNode) nd);
                                if (CollectionUtils.isNotEmpty(sourceList)) {
                                    resList.addAll(sourceList);
                                }
                            }
                        }
                    }
                    //from子查询
                    else if (subFromClause instanceof SelectNode){
                        List<Identifier> sourceList = findSource(comboIdentifier, (SelectNode) subFromClause);
                        if (CollectionUtils.isNotEmpty(sourceList)) {
                            resList.addAll(sourceList);
                        }
                    }
                    //from union查询
                    else if (subFromClause instanceof UnionCall) {
                        //TODO 血缘分叉
                        UnionCall unionCall = (UnionCall) subFromClause;
                        for (SelectNode sn : unionCall.getComboFromList()) {

                        }
                    }
                }
            }
        }
        else if (tmpSourceNode instanceof SelectNode){
            //TODO
        }
        return resList;
    }


    @Override
    public List<Pair<Identifier, Identifier>> parseColumnLineage(Node node) {
        throw new IllegalStateException("select无血缘关系");
    }

    @Override
    public List<Pair<Identifier, Identifier>> parseTableLineage(Node node) {
        return null;
    }
}
