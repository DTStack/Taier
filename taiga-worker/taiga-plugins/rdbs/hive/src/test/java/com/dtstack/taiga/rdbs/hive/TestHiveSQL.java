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

package com.dtstack.taiga.rdbs.hive;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.hive.jdbc.HiveConnection;
import org.apache.hive.jdbc.HiveStatement;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TestHiveSQL {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String driver = "org.apache.hive.jdbc.HiveDriver";
        Class.forName(driver);
        String url = "jdbc:hive2://node001:10004/data_science";
        String user = "";
        String pass = "";

        StringBuffer sb = new StringBuffer("select * from temp_mf_test limit 5");
        String createProc = sb.toString();

        HiveConnection conn = (HiveConnection) DriverManager.getConnection(url, user, pass);


        HiveStatement stmt = (HiveStatement)conn.createStatement();
        ResultSet res = stmt.executeQuery(createProc);

        List<Map<String, Object>> result = Lists.newArrayList();
        try {
            int columns = res.getMetaData().getColumnCount();
            List<String> columnName = Lists.newArrayList();
            for (int i = 0; i < columns; i++) {
                columnName.add(res.getMetaData().getColumnName(i + 1));
            }

            while (res.next()) {
                Map<String, Object> row = Maps.newLinkedHashMap();;
                for (int i = 0; i < columns; i++) {
                    row.put(columnName.get(i), res.getObject(i + 1));
                }
                result.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("SQL 执行异常");
        } finally {
        }
        System.out.println(result);

    }
}
