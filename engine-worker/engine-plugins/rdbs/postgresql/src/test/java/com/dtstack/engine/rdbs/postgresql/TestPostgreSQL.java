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

package com.dtstack.engine.rdbs.postgresql;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestPostgreSQL {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String driver = "org.postgresql.Driver";
        Class.forName(driver);
        String url = "jdbc:postgresql://172.16.8.190:54321/postgres";
        String user = "postgres";
        String pass = "password";

        StringBuffer sb = new StringBuffer("CREATE FUNCTION zhaojiemotest() RETURNS void AS $body$ ");
        sb.append(" begin ");
        sb.append(" insert into time_test values(now(),now(),now(),now()); ");
        sb.append(" end; ");
        sb.append("$body$ LANGUAGE PLPGSQL;");
        String createProc = sb.toString();
        String selectProc = "select zhaojiemotest()";
        String deleteProc = "drop  function if EXISTS zhaojiemotest()";

        Connection conn = DriverManager.getConnection(url, user, pass);


        CallableStatement stmt = conn.prepareCall(deleteProc);
        stmt.execute();

        stmt = conn.prepareCall(createProc);
        stmt.execute();

        stmt = conn.prepareCall(selectProc);
        stmt.execute();

    }
}
