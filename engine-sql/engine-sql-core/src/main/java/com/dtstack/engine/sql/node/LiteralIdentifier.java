package com.dtstack.engine.sql.node;

import com.dtstack.engine.sql.Column;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;

import java.util.List;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/27 23:13
 * @Description:
 */
public class LiteralIdentifier extends Identifier {


    private static final String LITERAL = "_LITERAL_";

    private String name;

    public LiteralIdentifier(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb,tableColumnsMap);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Node parseSql(SqlNode node) {
        sqlLiteralCheckNode(node);
        this.setName(LITERAL);
        return null;
    }

    private SqlLiteral sqlLiteralCheckNode(SqlNode node) {
        if (!(node instanceof SqlLiteral)){
            throw new IllegalArgumentException("sqlNode类型不匹配");
        }
        return (SqlLiteral) node;
    }
}
