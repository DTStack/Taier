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

package com.dtstack.engine.rdbs.impala;

import com.dtstack.engine.pluginapi.pojo.Column;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImpalaClient extends AbstractRdbsClient {

    public ImpalaClient() {
        this.dbType = "impala";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ImpalaClient.class);

    public static final int MAX_ROWS = 5000;

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new ImpalaConnFactory();
    }

    @Override
    public List<Column> getAllColumns(String tableName,String schemaName, String dbName) {

        List<Column> columns = new ArrayList<>();
        ResultSet res = null;
        try(Connection conn = connFactory.getConn();
            Statement statement = conn.createStatement()){
            statement.setMaxRows(MAX_ROWS);
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
            throw new RdosDefineException("getColumnsList exception",e);
        }finally {
            if( null != res){
                try {
                    res.close();
                } catch (SQLException e) {
                    LOGGER.error("close result exception,e:{}",e.getMessage());
                }
            }
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
