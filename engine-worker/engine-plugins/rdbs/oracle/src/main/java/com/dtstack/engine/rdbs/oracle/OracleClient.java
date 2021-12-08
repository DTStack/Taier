/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.rdbs.oracle;


import com.dtstack.engine.pluginapi.pojo.Column;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OracleClient extends AbstractRdbsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleClient.class);

    private static final String SHOW_COLUMN = "SELECT column_name,data_type FROM all_tab_columns where owner = upper('%s') and table_name = upper('%s')";

    private static final String SHOW_COMMENT = "select comments from all_col_comments  where owner = upper('%s') and table_name = upper('%s')";
    public OracleClient() {
        this.dbType = "oracle";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new OracleConnFactory();
    }

    @Override
    public List<Column> getAllColumns(String tableName,String schemaName, String dbName) {

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
            String commentSql = String.format(SHOW_COMMENT,dbName,tableName);
            res = statement.executeQuery(commentSql);
            List<String> commentList = new ArrayList<>();
            while (res.next()){
                commentList.add(res.getString(1));
            }
            columnList = convertColumnList(columnList, commentList);
        }catch (Exception e){
            throw new RdosDefineException("getColumnsList exception");
        }finally {
            if( null != res){
                try {
                    res.close();
                } catch (SQLException e) {
                    LOGGER.error("close sqlResult exception,e:{}",e.getMessage());
                }
            }
        }
        return columnList;
    }


    private List<Column> convertColumnList(List<Column> columnList, List<String> commentList) {

        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < columnList.size(); i++) {
            Column column = new Column();
            column.setTable(columnList.get(i).getTable());
            column.setName(columnList.get(i).getName());
            column.setType(columnList.get(i).getType());
            column.setComment(commentList.get(i));
            columns.add(column);
        }
        return columns;
    }
}
