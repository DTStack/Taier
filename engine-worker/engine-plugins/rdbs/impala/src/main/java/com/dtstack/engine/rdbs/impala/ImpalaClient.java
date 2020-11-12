package com.dtstack.engine.rdbs.impala;

import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import com.dtstack.schedule.common.jdbc.JdbcUrlPropertiesValue;
import com.google.common.collect.Lists;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImpalaClient extends AbstractRdbsClient {

    public ImpalaClient() {
        this.dbType = "impala";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new ImpalaConnFactory();
    }

    @Override
    public List<Column> getAllColumns(String tableName,String schemaName, String dbName) {

        List<Column> columns = new ArrayList<>();
        int maxRows = JdbcUrlPropertiesValue.MAX_ROWS;
        ResultSet res = null;
        try(Connection conn = connFactory.getConn();
           Statement statement = conn.createStatement()){
            statement.setMaxRows(maxRows);
            statement.execute("use "+ dbName);
            //首先判断是否是kudu表 是kudu表直接用主键代替 isPart
            res = statement.executeQuery("DESCRIBE " + tableName);
            int columnCnt = res.getMetaData().getColumnCount();
            // kudu表
            if (columnCnt > 3) {
                while (res.next()) {
                    columns.add(dealResult(res));
                }
                return columns;
            }
            //hive表 继续获取分区字段 先关闭之前的 rs
            res.close();
            res = statement.executeQuery("DESCRIBE formatted " + tableName);
            while (res.next()) {
                String colName = res.getString("name").trim();

                if (StringUtils.isEmpty(colName)) {
                    continue;
                }
                if (colName.startsWith("#") && colName.contains("col_name")) {
                    continue;

                }
                if (colName.startsWith("#") || colName.contains("Partition Information")) {
                    break;
                }
                if (StringUtils.isNotBlank(colName)) {
                    columns.add(dealResult(res));
                }
            }
        }catch (Exception e){
            throw new RdosDefineException("获取字段列表异常",e);
        }
        return columns;
    }

    private static Column dealResult(ResultSet resultSet) throws SQLException {
        Column column = new Column();
        column.setName(resultSet.getString("name"));
        column.setType(resultSet.getString("type"));
        column.setComment("comment");
        return column;
    }
}
