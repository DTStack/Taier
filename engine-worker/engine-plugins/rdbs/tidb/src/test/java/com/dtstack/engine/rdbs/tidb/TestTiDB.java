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

package com.dtstack.engine.rdbs.tidb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TestTiDB {
    public static void main(String[] args) throws Exception {
        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver);
        String url = "jdbc:mysql://172.16.10.45:3306/test";
        String user = "dtstack";
        String pass = "abc123";

        String xx = "update myresult_copy set channel='%s' where id=%s ";

        Connection conn = DriverManager.getConnection(url, user, pass);

        Statement statement = conn.createStatement();
    }
}
