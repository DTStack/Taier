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

//        sql = "CREATE TABLE\"tengzhen_test\"(\n" +
//                "\"name\" VARCHAR (100) NOT NULL ,\n" +
//                "\"value\" VARCHAR (11) NOT NULL\n" +
//                ");";

                sql = "CREATE TABLE temp_data_tengzhen3(\n" +
                "name VARCHAR (100) NOT NULL ,\n" +
                "var INTEGER NOT NULL\n" +
                ");";

//        sql = "CREATE TABLE \"temp_data_tengzhen2\"(\n" +
//                "\"name\" VARCHAR(100) NOT NULL,\n" +
//                "\"var\" INT(10) NOT NULL\n" +
//                ");";
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

        sql = " create  procedure \"prod_1d0eb7hcg\" as \n" +
                "begin \n" +
                " create table temp_data_tengzhen4( name varchar(100) not null,var integer not null);\n" +
                " insert into temp_data_tengzhen4(name,var) values('lisi',1); \n" +
                " end;";
        conn = new GetConn(sql);//
        try {
            Connection conn = TestKingBase.conn.conn;
            Statement statement = conn.createStatement();
            statement.execute(sql);
            CallableStatement callableStatement = conn.prepareCall("call prod_1d0eb7hcg()");
            callableStatement.execute();

            TestKingBase.conn.close();//关闭连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
