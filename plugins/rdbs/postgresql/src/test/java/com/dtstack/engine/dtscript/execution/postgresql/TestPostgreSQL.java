package com.dtstack.engine.dtscript.execution.postgresql;


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
