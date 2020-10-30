package com.dtstack.engine.sql.node;

import com.dtstack.engine.sql.Column;
import org.dtstack.apache.calcite.sql.SqlNode;
import org.dtstack.apache.calcite.sql.SqlNodeList;

import java.util.List;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/27 11:22
 * @Description:
 */
public class NodeList extends Node {
    private List<Node> list;

    public NodeList(String defaultDb, Map<String, List<Column>> tableColumnsMap){
        super(defaultDb,tableColumnsMap);
    }

    public List<Node> getList() {
        return list;
    }

    public void setList(List<Node> list) {
        this.list = list;
    }

    @Override
    public Node parseSql(SqlNode node) {
        SqlNodeList sqlNodes = checkNode(node);
        for (SqlNode item : sqlNodes.getList()) {
            //TODO
        }
        return null;
    }

    private SqlNodeList checkNode(SqlNode node){
        if (!(node instanceof SqlNodeList)){
            throw new IllegalStateException("sqlNode类型不匹配");
        }
        return (SqlNodeList) node;
    }
}
