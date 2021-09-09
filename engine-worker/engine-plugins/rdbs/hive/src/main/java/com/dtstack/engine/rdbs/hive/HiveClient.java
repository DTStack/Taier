package com.dtstack.engine.rdbs.hive;

import com.dtstack.engine.common.pojo.Column;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HiveClient extends AbstractRdbsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HiveClient.class);

    public HiveClient() {
        this.dbType = "hive";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new HiveConnFactory();
    }


    @Override
    public List<Column> getAllColumns(String tableName, String schemaName, String dbName) {

        List<List<Object>> result = new ArrayList<>();
        ResultSet res = null;
        try(Connection conn = connFactory.getConn();
            Statement statement = conn.createStatement()){
            String descSql = "desc " + tableName;
            if (StringUtils.isNotEmpty(dbName)) {
                statement.execute("use " + dbName);
            }
            if (statement.execute(descSql)) {
                res = statement.getResultSet();
                int columns = res.getMetaData().getColumnCount();
                List<Object> cloumnName = Lists.newArrayList();
                int timeStamp  = 0;
                SimpleDateFormat dateFormat = null;
                for (int i = 1; i <= columns; i++) {
                    String name = res.getMetaData().getColumnName(i);
                    if (name.contains(".")) {
                        name = name.split("\\.")[1];
                    }
                    if("current_timestamp()".equalsIgnoreCase(name)){
                        //current_timestamp() 需要转为 时间
                        dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        timeStamp= i;
                    }
                    cloumnName.add(name);
                }
                result.add(cloumnName);

                while (res.next()) {
                    List<Object> objects = Lists.newArrayList();
                    for (int i = 1; i <= columns; i++) {
                        if (i == timeStamp) {
                            if (Objects.nonNull(dateFormat)) {
                                objects.add(dateFormat.format(res.getObject(i)));
                                continue;
                            }
                        }
                        objects.add(res.getObject(i));
                    }
                    result.add(objects);
                }
            }
        }catch (Exception e){
            throw new RdosDefineException("getColumnsList exception");
        }finally {
            if(null != res) {
                try {
                    res.close();
                } catch (SQLException e) {
                    LOGGER.error("close sqlResult exception,e:{}", e.getMessage());
                }
            }
        }
        return parseColumnInfo(result, tableName);
    }

    private List<Column> parseColumnInfo(List<List<Object>> result, String tableName) {

        List<Column> columns = new ArrayList<>();
        List<Column> part = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(result)) {
            result.remove(0);
            boolean isPart = false;
            Column column;
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).get(0) == null || org.apache.commons.lang.StringUtils.isEmpty(result.get(i).get(0).toString().trim())
                        || result.get(i).get(0).toString().contains("# Partition Information")) {
                    continue;
                }
                column = new Column();
                column.setTable(tableName);
                column.setName(String.valueOf(result.get(i).get(0)));
                column.setType(String.valueOf(result.get(i).get(1)));
                if (result.get(i).get(2) != null) {
                    column.setComment(String.valueOf(result.get(i).get(2)));
                }

                if (!isPart) {
                    if (String.valueOf(result.get(i).get(0)).contains("# col_name")) {
                        isPart = true;
                        continue;
                    }

                    column.setIndex(columns.size());
                    columns.add(column);
                } else {
                    column.setIndex(part.size());
                    part.add(column);
                }
            }

            if (part.size() > 0) {
                columns = columns.subList(0, columns.size() - part.size());
            }
        }
        return columns;
    }

}
