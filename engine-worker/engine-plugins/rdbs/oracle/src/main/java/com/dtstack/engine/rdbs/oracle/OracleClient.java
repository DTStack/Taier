package com.dtstack.engine.rdbs.oracle;


import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OracleClient extends AbstractRdbsClient {

    private static final String SHOW_COLUMN = "SELECT column_name,data_type FROM all_tab_columns where owner = upper('%s') and table_name = upper('%s')";

    public OracleClient() {
        this.dbType = "oracle";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new OracleConnFactory();
    }

    @Override
    public List<Column> getAllColumns(String tableName, String dbName) {

        AbstractConnFactory connFactory = getConnFactory();
        List<Column> columnList = new ArrayList<>();
        ResultSet res = null;
        try(
        Connection conn = connFactory.getConn();
        Statement statement = conn.createStatement()
        ){
            String sql = String.format(SHOW_COLUMN, dbName,tableName);
            res = statement.executeQuery(sql);
            while (res.next()) {
                Column column = new Column();
                column.setTable(tableName);
                column.setName(res.getString(1));
                column.setType(res.getString(2));
                columnList.add(column);
            }
        }catch (Exception e){
            throw new RdosDefineException("获取字段信息列表异常");
        }
        return columnList;
    }
}
