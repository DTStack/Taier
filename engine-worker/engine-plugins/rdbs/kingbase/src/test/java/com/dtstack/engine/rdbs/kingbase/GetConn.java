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

package com.dtstack.engine.rdbs.kingbase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 8:08 下午 2020/12/1
 */
public class GetConn {

    public Connection conn = null;
    public PreparedStatement pst = null;
//    public String url = "jdbc:kingbase8://172.16.100.181:54321/TEST";
//    public String user = "SYSTEM";
//    public String pass = "abc123";
      public String url = "jdbc:kingbase8://119.45.4.7:54321/TEST";
      public String user = "root";
      public String pass = "root";

    public GetConn(String sql) {
        try {
            Class.forName("com.kingbase8.Driver");//指南中的这个方法运行不成功
//            DriverManager.registerDriver(new com.kingbase.Driver());
            conn = DriverManager.getConnection(url, user, pass);//获取连接
            pst = conn.prepareStatement(sql);//准备执行语句
            System.out.println("yes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.conn.close();
            this.pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
