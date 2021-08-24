package com.dtstack.engine.rdbs.mysql;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TestMysql {
    public static void main(String[] args) throws Exception {
        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver);
        String url = "jdbc:mysql://172.16.10.45:3306/test";
        String user = "dtstack";
        String pass = "abc123";

        String xx = "update myresult_copy set channel='%s' where id=%s ";

        Connection conn = DriverManager.getConnection(url, user, pass);

        Statement statement = conn.createStatement();
        while (true) {
            for (int i = 1; i <= 150000; i++) {
                statement.execute(String.format(xx, i, i));
                if (i % 100 == 0) {
                    System.out.println(i);
                }
            }
        }
//        stmt = conn.prepareCall(callProc);
//        stmt.execute();

    }
}
