package com.dtstack.engine.rdbs.tidb;

import java.sql.*;

public class TestKingBase {

    static String sql = null;
    static GetConn conn = null;
    static ResultSet ret = null;

    public static void main(String[] args) throws Exception {

//        query();
//        add();
//        createTable();
//        delete();
//        testConn();
        createProcedure();
    }


    public static void  query(){

        //要执行的SQL语句，改成自己的表什么的
        sql = "select  * from  \"tengzhen_test\";";

        //获取 预编译对象
        conn = new GetConn(sql);
        try {
            //执行语句，ret是结果
            ret = conn.pst.executeQuery();
            while (ret.next()) {
                System.out.println(ret.getString(1)+": "+ret.getString(2) );
            }//显示数据
            ret.close();
            conn.close();//关闭连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void  add(){

        sql = "insert into \"tengzhen_test\" (\"name\",\"value\") values(?,?);";
        conn = new GetConn(sql);//创建数据库对象
        PreparedStatement preparedStatement = null;
        try {
            conn.pst.setString(1,"yuebai");
            conn.pst.setInt(2,25);
            int count = conn.pst.executeUpdate();
            if (count>0){
                System.out.println("添加数据成功");
            }
            else {
                System.out.println("添加数据失败");
            }
            conn.close();//关闭连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void  createTable(){

        sql = "CREATE TABLE\"tengzhen_test\"(\n" +
                "\"name\" VARCHAR (100) NOT NULL ,\n" +
                "\"value\" VARCHAR (11) NOT NULL\n" +
                ");";
        conn = new GetConn(sql);//创建数据库对象
        try {
            int count = conn.pst.executeUpdate();
            if (count>0){
                System.out.println("创建成功");
            }
            else {
                System.out.println("创建失败");
            }
            conn.close();//关闭连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(){
        sql = " delete from \"tengzhen_test\" where \"name\"= 'yuebai' ";
        conn = new GetConn(sql);//创建数据库对象
        try {
            int i1 = conn.pst.executeUpdate();
            if (i1>0){
                System.out.println(i1+"个删除成功");
            }
            else {
                System.out.println("删除失败");
            }
            conn.close();//关闭连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void testConn(){
        sql = "select * from SYS_NAMESPACE;";
        conn = new GetConn(sql);
        try {
            ret = conn.pst.executeQuery();
            while (ret.next()){
                String string = ret.getString(1);
                System.out.println(string);
            }
            conn.close();//关闭连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void  createProcedure(){

        sql = "CREATE OR REPLACE  PROCEDURE PUBLIC.TEMP_EMP_CREATETAB() \n" +
                "\n" +
                "// 使用的是plsql 语言\n" +
                " LANGUAGE PLSQL\n" +
                "\n" +
                "//创建变量创建的表名\n" +
                "AS     tabName VARCHAR2(200);\n" +
                "\n" +
                "//统计数量\n" +
                "      tabCount NUMBER;\n" +
                "\n" +
                "//创建表的语句\n" +
                "      createTableSql VARCHAR2(300);\n" +
                " BEGIN " +
                "//为表名赋值\n" +
                "    tabName := 'TEMP_EMP_DATA_' || TO_CHAR(sysdate, 'yyyyMMdd');\n" +
                "//查询是否有同名的表    \n" +
                "\n" +
                "SELECT COUNT(1) INTO tabCount FROM user_tables WHERE TABLE_NAME = tabName;\n" +
                "//如果没有则继续    \n" +
                "\n" +
                "if tabCount = 0 THEN\n" +
                "\n" +
                "//创建表的语句\n" +
                "    createTableSql := 'CREATE TABLE '||tabName||'(\n" +
                "        \"ID\" VARCHAR(64 byte) NOT NULL   \n" +
                "    )';\n" +
                "\n" +
                "//执行建表语句\n" +
                "    EXECUTE createTableSql;\n" +
                "   END IF;\n" +
                "END;";
        conn = new GetConn(sql);//
        try {
            int count = conn.pst.executeUpdate();
            if (count>0){
                System.out.println("创建成功");
            }
            else {
                System.out.println("创建失败");
            }
            conn.close();//关闭连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
