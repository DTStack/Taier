package com.dtstack.engine.rdbs.tidb;

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
    public String url = "jdbc:kingbase8://172.16.100.181:54321/TEST";
    public String user = "SYSTEM";
    public String pass = "abc123";

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
