package com.dtstack.engine.rdbs.tidb;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TiDBClient extends AbstractRdbsClient {

    private static final String SHOW_COLUMN = "show columns from %s;";

    public TiDBClient() {
        this.dbType = "tidb";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new TiDBConnFactory();
    }


    @Override
    public List<Column> getAllColumns(String tableName, String dbName) {


        List<Column> columnList = new ArrayList<>();
        AbstractConnFactory connFactory = getConnFactory();

        try(Connection conn = connFactory.getConn();
            Statement statement = conn.createStatement()){
            String sql = String.format(SHOW_COLUMN, tableName);
            if (StringUtils.isNotBlank(dbName)) {
                statement.execute("use " + dbName);
            }
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                if("Field".equalsIgnoreCase(resultSet.getString(1))){
                    continue;
                }
                Column column = new Column();
                column.setTable(tableName);
                column.setName(resultSet.getString(1));
                column.setType(resultSet.getString(2));
                columnList.add(column);
            }
        }catch (Exception e){
            throw new RdosDefineException("获取字段列表异常");
        }

        return super.getAllColumns(tableName, dbName);
    }
}
