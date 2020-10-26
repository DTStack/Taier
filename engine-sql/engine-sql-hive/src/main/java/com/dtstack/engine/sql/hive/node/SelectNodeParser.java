package com.dtstack.engine.sql.hive.node;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.TableOperateEnum;
import com.dtstack.engine.sql.node.Identifier;
import com.dtstack.engine.sql.node.JoinCall;
import com.dtstack.engine.sql.node.Node;
import com.dtstack.engine.sql.node.NodeList;
import com.dtstack.engine.sql.node.SelectNode;
import com.dtstack.engine.sql.node.UnionCall;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectNodeParser extends NodeParser {

    public static Logger logger = LoggerFactory.getLogger(SelectNodeParser.class);

    @Override
    public SelectNode parseSql(ASTNode node, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        //用于存放with节点 用于整棵树构建之后 把节点填充
        Map<String, SelectNode> withMap = new HashMap<>();
        SelectNode selectNode = new SelectNode(defultDb, tableColumnsMap);
        selectNode.setTableMap(new HashMap<>());
        List<org.apache.hadoop.hive.ql.lib.Node> querNode = node.getChildren();
        if (CollectionUtils.isEmpty(querNode)){
            return null;
        }
        for (org.apache.hadoop.hive.ql.lib.Node qNode : querNode) {
            if (HiveParser.TOK_FROM == ((ASTNode) qNode).getType()) {
                selectNode.setFromClause(parserFrom((ASTNode) qNode, defultDb, tableColumnsMap, aliasToTable));
                //  判断一下  如果是union 会多一层  所以需要直接往上提一层
                if ((selectNode.getFromClause() instanceof SelectNode && ((SelectNode) selectNode.getFromClause()).getSelectList() == null)) {
                    ((SelectNode) selectNode.getFromClause()).getFromClause().setAlias(selectNode.getFromClause().getAlias());
                    selectNode.setFromClause(((SelectNode) selectNode.getFromClause()).getFromClause());
                }
                // 不能在整个树构建之后再去填充with节点  因为构建中间的特殊处理会被忽略。
                if (!withMap.isEmpty()) {
                    //说明有with语句 遍历当前树 进行节点替换
                    Node n = whihNodeReplace(selectNode.getFromClause(),withMap,aliasToTable);
                    if (null != n){
                        selectNode.setFromClause(n);
                    }
                }
            } else if (HiveParser.TOK_INSERT == ((ASTNode) qNode).getType()) {
                List<org.apache.hadoop.hive.ql.lib.Node> select = ((ASTNode) qNode).getChildren();
                for (org.apache.hadoop.hive.ql.lib.Node n : select) {
                    if (HiveParser.TOK_SELECT == ((ASTNode) n).getType() || HiveParser.TOK_SELECTDI == ((ASTNode) n).getType()) {
                        NodeList nodeList = new NodeList(defultDb, tableColumnsMap);
                        nodeList.setList(getColumnListByTokSelect((ASTNode) n, defultDb, tableColumnsMap, selectNode.getFromClause()));
                        selectNode.setSelectList(nodeList);
                        fillColumnTable(selectNode);
                        selectStarFill(selectNode, defultDb, tableColumnsMap, aliasToTable);
                    }else if (HiveParser.TOK_LIMIT == ((ASTNode) n).getType()){
                        List<Long> limitList = new ArrayList<>();
                        List<org.apache.hadoop.hive.ql.lib.Node> limitNode = ((ASTNode) n).getChildren();
                        for (org.apache.hadoop.hive.ql.lib.Node limitNum : limitNode){
                            limitList.add(Long.parseLong(((ASTNode)limitNum).getText()));
                        }
                        selectNode.setLimit(limitList);
                    }
                }
            } else if (HiveParser.TOK_CTE == ((ASTNode) qNode).getType()) {
                withNodeParser(defultDb, tableColumnsMap, aliasToTable, withMap, (ASTNode) qNode);
            }
        }
        return selectNode;
    }

    @Override
    public void parseSqlTable(Node node, Set<Table> tables) {
        if (node instanceof Identifier) {
            Table table = new Table();
            table.setName(((Identifier) node).getTable());
            table.setDb(((Identifier) node).getDb());
            table.setOperate(TableOperateEnum.SELECT);
            tables.add(table);
        } else if (node instanceof SelectNode) {
            parseSqlTable(((SelectNode) node).getFromClause(), tables);
        } else if (node instanceof JoinCall) {
            for (Node comboList : ((JoinCall) node).getComboList()) {
                parseSqlTable(comboList, tables);
            }
        } else if (node instanceof UnionCall) {
            for (Node comboList : ((UnionCall) node).getComboFromList()) {
                parseSqlTable(comboList, tables);
            }
        } else {
            logger.info("该类型未解析");
        }
    }



}
