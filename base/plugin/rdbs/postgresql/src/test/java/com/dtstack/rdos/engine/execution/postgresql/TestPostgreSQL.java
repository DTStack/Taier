package com.dtstack.rdos.engine.execution.postgresql;


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

        StringBuffer sb = new StringBuffer("CREATE FUNCTION proc() ");
        sb.append("begin\n");
        sb.append("\tinsert into my_time values (now());\n");
        sb.append("end\n");
        String createProc = sb.toString();
        String callProc = "call hyf_proc()";
        String deleteProc = "drop function hyf_proc";

        Connection conn = DriverManager.getConnection(url, user, pass);


        CallableStatement stmt = conn.prepareCall(deleteProc);
        stmt.execute();

        stmt = conn.prepareCall(createProc);
        stmt.execute();

        stmt = conn.prepareCall(callProc);
        stmt.execute();

    }
}
