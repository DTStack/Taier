package com.dtstack.engine.rdbs.greenplum;

import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import com.google.common.base.Strings;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GreenPlumClient extends AbstractRdbsClient {

    private static final String CHG_SCHEMA_TMPL = "SET search_path TO %s";
    private static final String QUERY_COMMENT_INFO = "SELECT col_description ( A.attrelid, A.attnum ) AS COMMENT,format_type ( A.atttypid, A.atttypmod ) AS TYPE,A.attname AS NAME,A.attnotnull AS NOTNULL FROM pg_class AS C,pg_attribute AS A WHERE C.relname = '%s' and c.relnamespace = (select OID from pg_namespace where nspname = '%s') AND A.attrelid = C.oid AND A.attnum > 0";
    private static final String QUERY_COLUMN = "select * from %s limit 0";


    public GreenPlumClient() {
        this.dbType = "greenplum";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new GreenPlumConnFactory();
    }


    @Override
    public List<Column> getAllColumns(String tableName,String schemaName, String dbName) {

        List<Column> columns = new ArrayList<>();
        try(Connection conn = connFactory.getConn();
            Statement statement = conn.createStatement()){
            if (!Strings.isNullOrEmpty(dbName)) {
                String chgSchema = String.format(CHG_SCHEMA_TMPL, dbName);
                statement.execute(chgSchema);
            }
            String commentSql = String.format(QUERY_COMMENT_INFO,tableName,dbName);
            ResultSet commentResult = statement.executeQuery(commentSql);
            Map<String,String> columnCommentMap = new HashMap<>(16);
            while (commentResult.next()) {
                String comment = commentResult.getString(1);
                String columnName = commentResult.getString(3);
                columnCommentMap.put(columnName,comment);
            }
            String querySql = String.format(QUERY_COLUMN, tableName);
            ResultSet resultSet = statement.executeQuery(querySql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                Column column = new Column();
                String columnName = metaData.getColumnName(i + 1);
                column.setName(columnName);
                String columnTypeName = metaData.getColumnTypeName(i + 1);
                if ("NUMERIC".equalsIgnoreCase(columnTypeName)) {
                    int scale = metaData.getScale(i + 1);
                    int precision = metaData.getPrecision(i + 1);
                    columnTypeName = columnTypeName + String.format("(%s,%s)", precision, scale);
                } else if ("VARCHAR".equalsIgnoreCase(columnTypeName) || "CHAR".equalsIgnoreCase(columnTypeName) || "BPCHAR".equalsIgnoreCase(columnTypeName)
                        || "NCHAR".equalsIgnoreCase(columnTypeName)) {
                    int precision = metaData.getPrecision(i + 1);
                    //CHAR VARCHAR 不设置 范围的时候需要默认最大
                    columnTypeName = columnTypeName + String.format("(%s)", precision);
                } else {
                    columnTypeName = metaData.getColumnTypeName(i + 1);
                }
                column.setType(columnTypeName);
                column.setIndex(i);
                column.setComment(columnCommentMap.get(columnName));
                columns.add(column);
            }
        }catch (Exception e){
            throw new RdosDefineException("执行sql异常");
        }
        return columns;
    }
}
