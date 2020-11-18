package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.handler.HiveUglySqlHandler;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author chener
 * @Classname HiveOrangeTest
 * @Description TODO
 * @Date 2020/9/9 15:16
 * @Created chener@dtstack.com
 */
public class HiveOrangeTest {

    @Test
    public void testComplicated() throws Exception {
        String sqlFile = getClass().getClassLoader().getResource("hive.sql").getFile();
        String sql = readFile(sqlFile);
        SqlParserImpl sqlParser = new AstNodeParser(new HiveUglySqlHandler());
        List<Table> tables = sqlParser.parseTables("default", sql);
        StringBuilder builder = new StringBuilder();
        for (Table table:tables){
            builder.append(table.getDb())
                    .append(".")
                    .append(table.getName())
                    .append("\n");
        }
        System.out.println(builder);
    }


    @Test
    public void testComplicated2() throws Exception {
        String sqlFile = getClass().getClassLoader().getResource("hive2.sql").getFile();
        String sql = readFile(sqlFile);
        SqlParserImpl sqlParser = new AstNodeParser(new HiveUglySqlHandler());
        List<Table> tables = sqlParser.parseTables("default", sql);
        StringBuilder builder = new StringBuilder();
        for (Table table:tables){
            builder.append(table.getDb())
                    .append(".")
                    .append(table.getName())
                    .append("\n");
        }
        System.out.println(builder);
    }

    @Test
    public void testComplicated3() throws Exception {
        String sqlFile = getClass().getClassLoader().getResource("hive3.sql").getFile();
        String sql = readFile(sqlFile);
        SqlParserImpl sqlParser = new AstNodeParser(new HiveUglySqlHandler());
        List<Table> tables = sqlParser.parseTables("default", sql);
        StringBuilder builder = new StringBuilder();
        for (Table table:tables){
            builder.append(table.getDb())
                    .append(".")
                    .append(table.getName())
                    .append("\n");
        }
        System.out.println(builder);
    }

    @Test
    public void testComplicated4() throws Exception {
        String sqlFile = getClass().getClassLoader().getResource("hive4.sql").getFile();
        String sql = readFile(sqlFile);
        SqlParserImpl sqlParser = new AstNodeParser(new HiveUglySqlHandler());
        List<Table> tables = sqlParser.parseTables("default", sql);
        StringBuilder builder = new StringBuilder();
        for (Table table:tables){
            builder.append(table.getDb())
                    .append(".")
                    .append(table.getName())
                    .append("\n");
        }
        System.out.println(builder);
    }

    public static String readFile(String path){
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF8");
            reader = new BufferedReader(inputStreamReader);
            String tmpStr = null;
            while ((tmpStr = reader.readLine()) != null){
                builder.append(tmpStr);
                builder.append("\n");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
}
