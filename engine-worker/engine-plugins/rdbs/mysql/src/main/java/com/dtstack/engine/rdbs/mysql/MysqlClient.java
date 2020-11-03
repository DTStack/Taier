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

    private static final String TABLE_INFO_SQL = "select column_name , data_type, column_comment  from INFORMATION_SCHEMA.COLUMNS\n where table_name = ";

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new MysqlConnFactory();
    }


    @Override
    public List<Column> getAllColumns(String tableName, String dbName) {

        List<Column> columnList = new ArrayList<>();
        AbstractConnFactory connFactory = getConnFactory();

        try( Connection conn = connFactory.getConn();
            Statement statement = conn.createStatement()){

            statement.execute("use " + dbName);
            ResultSet res = statement.executeQuery(TABLE_INFO_SQL + tableName);
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
