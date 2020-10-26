package com.dtstack.engine.sql.rdb;

import com.dtstack.engine.sql.ParseResult;
import org.apache.calcite.sql.SqlNode;

import java.sql.SQLException;

/**
 * SqlNode 解析接口
 *
 * @author jiangbo
 * @date 2019/5/21
 */
public interface SqlNodeParserImpl {

    /**
     * 解析SqlNode
     *
     * @param node
     * @return
     */
    void parseSqlNode(SqlNode node, ParseResult parseResult) throws SQLException;
}
