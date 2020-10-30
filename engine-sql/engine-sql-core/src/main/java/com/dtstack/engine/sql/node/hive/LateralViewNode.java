package com.dtstack.engine.sql.node.hive;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.node.Identifier;
import com.dtstack.engine.sql.node.Node;
import org.dtstack.apache.calcite.sql.SqlNode;

import java.util.List;
import java.util.Map;

/**
 * 用于hive侧视图的节点
 */
public class LateralViewNode extends Node {

    /**
     * 侧视图来源字段
     */
    private List<Identifier> comboList;

    public LateralViewNode(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb, tableColumnsMap);
    }


    @Override
    public Node parseSql(SqlNode node) {
        return null;
    }


    public List<Identifier> getComboList() {
        return comboList;
    }

    public void setComboList(List<Identifier> comboList) {
        this.comboList = comboList;
    }
}
