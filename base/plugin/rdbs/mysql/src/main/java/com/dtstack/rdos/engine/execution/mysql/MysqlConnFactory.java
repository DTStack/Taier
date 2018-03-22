package com.dtstack.rdos.engine.execution.mysql;


import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;

public class MysqlConnFactory extends ConnFactory {

    public MysqlConnFactory() {
        driverName = "com.mysql.jdbc.Driver";
        testSql = "select 1111";
    }


    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("create procedure %s() \n", procName);
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("call %s()", procName);
    }

    @Override
    public String getDropProc(String procName) {
        return String.format("DROP PROCEDURE %s", procName);
    }
}
