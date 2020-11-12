package com.dtstack.engine.rdbs.sqlserver;


import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlserverClient extends AbstractRdbsClient {

    public static final String TABLE_INFO_SQL = "select a.name as column_name, b.name as data_type,g.value as column_comment\n" +
            " FROM  syscolumns a \n" +
            " left join systypes b on a.xtype=b.xusertype  \n" +
            " inner join sysobjects d on a.id=d.id and d.xtype='U' \n" +
            " left join sys.extended_properties g on a.id=g.major_id AND a.colid=g.minor_id\n" +
            " left join sys.objects h on a.id= h.object_id\n" +
            " left join sys.schemas i on h.schema_id=i.schema_id\n" +
            "where b.name is not null\n" +
            "and d.name='%s'   --如果只查询指定表名称,加上此条件；视图名改为视图名称\n" +
            "and i.name = '%s' --根据schema进行查询，可删除";

    public SqlserverClient() {
        this.dbType = "sqlserver";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new SqlserverConnFactory();
    }


    @Override
    public List<Column> getAllColumns(String tableName,String schemaName, String dbName) {
        List<Column> columnList = new ArrayList<>();
        try(Connection conn = this.connFactory.getConn();
            Statement statement = conn.createStatement()){
            //选择数据库
            statement.execute(String.format("use \"%s\" ",dbName));
            String sql = String.format(TABLE_INFO_SQL,tableName,schemaName);
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
