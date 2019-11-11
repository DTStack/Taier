package com.dtstack.engine.dtscript.execution.hive;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.hive.jdbc.HiveConnection;
import org.apache.hive.jdbc.HiveStatement;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TestHiveSQL {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String driver = "org.apache.hive.jdbc.HiveDriver";
        Class.forName(driver);
        String url = "jdbc:hive2://node001:10004/data_science";
        String user = "";
        String pass = "";

        StringBuffer sb = new StringBuffer("select * from temp_mf_test limit 5");
        String createProc = sb.toString();

        HiveConnection conn = (HiveConnection) DriverManager.getConnection(url, user, pass);


        HiveStatement stmt = (HiveStatement)conn.createStatement();
        ResultSet res = stmt.executeQuery(createProc);

        List<Map<String, Object>> result = Lists.newArrayList();
        try {
            int columns = res.getMetaData().getColumnCount();
            List<String> columnName = Lists.newArrayList();
            for (int i = 0; i < columns; i++) {
                columnName.add(res.getMetaData().getColumnName(i + 1));
            }

            while (res.next()) {
                Map<String, Object> row = Maps.newLinkedHashMap();;
                for (int i = 0; i < columns; i++) {
                    row.put(columnName.get(i), res.getObject(i + 1));
                }
                result.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("SQL 执行异常");
        } finally {
        }
        System.out.println(result);

    }
}
