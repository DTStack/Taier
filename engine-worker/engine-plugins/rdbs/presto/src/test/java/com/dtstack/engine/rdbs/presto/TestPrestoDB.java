package com.dtstack.engine.rdbs.presto;

import com.alibaba.fastjson.JSONObject;
import com.facebook.presto.jdbc.PrestoConnection;
import com.facebook.presto.jdbc.PrestoStatement;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TestPrestoDB {

    @Test
    public void testPrestoConn() throws Exception{
        String driver = "com.facebook.presto.jdbc.PrestoDriver";
        Class.forName(driver);
        String url = "jdbc:presto://172.16.100.168:8091/mysql";
        String user = "root";

        String sql = "select * from task3_copy01.console_engine";

        PrestoConnection conn = (PrestoConnection) DriverManager.getConnection(url, user, null);

        PrestoStatement stmt = (PrestoStatement)conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);

        List<JSONObject> results = new ArrayList<>();
        int count = resultSet.getMetaData().getColumnCount();
        String[] columns = new String[count];
        for (int i = 0; i < count; i++) {
            columns[i] = resultSet.getMetaData().getColumnName(i + 1);
        }
        while (resultSet.next()) {
            JSONObject jsonObject = new JSONObject();
            for (int j = 0; j < count; j++) {
                jsonObject.put(columns[j], resultSet.getString(j + 1));
            }
            results.add(jsonObject);
        }

        for (JSONObject jsonObject: results) {
            System.out.println(jsonObject.toString());
        }
    }
}
