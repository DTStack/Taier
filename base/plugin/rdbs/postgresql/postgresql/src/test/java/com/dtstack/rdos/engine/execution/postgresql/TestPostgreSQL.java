package com.dtstack.rdos.engine.execution.mysql;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestMysql {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver);
        String url = "jdbc:mysql://172.16.10.61:3306/hyf";
        String user = "dtstack";
        String pass = "abc123";

        StringBuffer sb = new StringBuffer("create procedure hyf_proc() ");
        sb.append("begin\n");
        sb.append("\tinsert into my_time values (now());\n");
        sb.append("end\n");
        String createProc = sb.toString();
        String callProc = "call hyf_proc()";
        String deleteProc = "DROP PROCEDURE IF EXISTS hyf_proc";

        Connection conn = DriverManager.getConnection(url, user, pass);
        CallableStatement stmt = conn.prepareCall(deleteProc);
        stmt.execute();
//        stmt = conn.prepareCall(callProc);
//        stmt.execute();

    }
}
