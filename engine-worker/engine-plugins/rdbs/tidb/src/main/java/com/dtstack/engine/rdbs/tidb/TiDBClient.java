package com.dtstack.engine.rdbs.tidb;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TiDBClient extends AbstractRdbsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TiDBClient.class);

    private static final String TABLE_INFO_SQL = "select column_name , data_type, column_comment  from INFORMATION_SCHEMA.COLUMNS where table_schema = '%s' and table_name = '%s' ";


    public TiDBClient() {
        this.dbType = "tidb";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new TiDBConnFactory();
    }


    @Override
    public List<Column> getAllColumns(String tableName,String schemaName, String dbName) {


        List<Column> columnList = new ArrayList<>();
        ResultSet res = null;
        try(Connection conn = this.connFactory.getConn();
            Statement statement = conn.createStatement()){
            String sql = String.format(TABLE_INFO_SQL,dbName,tableName);
            res = statement.executeQuery(sql);
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
            throw new RdosDefineException("getColumnsList exception");
        }finally {
            if(null != res){
                try {
                    res.close();
                } catch (SQLException e) {
                    LOGGER.error("close sqlResult exception:{}",e.getMessage());
                }
            }
        }
        return columnList;
    }
}
