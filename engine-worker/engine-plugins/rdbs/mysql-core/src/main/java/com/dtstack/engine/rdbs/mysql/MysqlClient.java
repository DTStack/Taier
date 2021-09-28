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

package com.dtstack.engine.rdbs.mysql;


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

public class MysqlClient extends AbstractRdbsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlClient.class);

    private static final String TABLE_INFO_SQL = "select column_name , data_type, column_comment  from INFORMATION_SCHEMA.COLUMNS where table_schema = '%s' and table_name = '%s' ";

    public MysqlClient() {
        this.dbType = "mysql";
    }

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new MysqlConnFactory();
    }

    @Override
    public List<Column> getAllColumns(String tableName, String schemaName, String dbName) {

        List<Column> columnList = new ArrayList<>();
        ResultSet res = null;
        try(Connection conn = this.connFactory.getConn();
            Statement statement = conn.createStatement()){
            String sql = String.format(TABLE_INFO_SQL, dbName, tableName);
            res = statement.executeQuery(sql);
            while (res.next()){
                Column column = new Column();
                column.setName(res.getString("column_name"));
                column.setType(res.getString("data_type"));
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
                    LOGGER.error("close sqlResult exception,e: ", e);
                }
            }
        }
        return columnList;
    }
}
