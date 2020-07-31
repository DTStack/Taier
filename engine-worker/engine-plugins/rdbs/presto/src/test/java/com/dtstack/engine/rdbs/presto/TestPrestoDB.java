package com.dtstack.engine.rdbs.presto;

import com.facebook.presto.jdbc.PrestoConnection;
import com.facebook.presto.jdbc.PrestoStatement;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.ResultSet;

public class TestPrestoDB {

    @Test
    public void testPrestoConn() throws Exception{
        String driver = "com.facebook.presto.jdbc.PrestoDriver";
        Class.forName(driver);
        String url = "jdbc:hive2://node001:10004/data_science";
        String user = "";
        String pass = "";

        StringBuffer sb = new StringBuffer("select * from temp_mf_test limit 5");
        String createProc = sb.toString();

        PrestoConnection conn = (PrestoConnection) DriverManager.getConnection(url, user, pass);

        PrestoStatement stmt = (PrestoStatement)conn.createStatement();
        ResultSet res = stmt.executeQuery(createProc);
    }
}
