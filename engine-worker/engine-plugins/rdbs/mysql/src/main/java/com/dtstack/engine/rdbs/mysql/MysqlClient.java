package com.dtstack.engine.rdbs.mysql;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MysqlClient extends AbstractRdbsClient {

    public MysqlClient() {
        this.dbType = "mysql";
    }

    private static final String TABLE_INFO_SQL = "select column_name , data_type, column_comment  from INFORMATION_SCHEMA.COLUMNS where table_schema = '%s' and table_name = '%s' ";

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new MysqlConnFactory();
    }


    @Override
    public List<Column> getAllColumns(String tableName, String dbName) {

        List<Column> columnList = new ArrayList<>();
        try(Connection conn = this.connFactory.getConn();
            Statement statement = conn.createStatement()){
            String sql = String.format(TABLE_INFO_SQL,dbName,tableName);
            ResultSet res = statement.executeQuery(sql);
            while (res.next()){
                Column column = new Column();
                String name = res.getString("column_name");
                column.setName(name);
                String type = res.getString("data_type");
                column.setType(type);
                column.setComment(res.getString("column_comment"));
                columnList.add(column);
            }
        }catch (Exception e){
            throw new RdosDefineException("获取字段列表异常");
        }
        return columnList;
    }
}
