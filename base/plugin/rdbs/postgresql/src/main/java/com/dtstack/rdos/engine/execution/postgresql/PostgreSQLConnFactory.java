package com.dtstack.rdos.engine.execution.postgresql;


import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;

public class PostgreSQLConnFactory extends ConnFactory {

    public PostgreSQLConnFactory() {
        driverName = "org.postgresql.Driver";
        testSql = "select 1111";
    }


    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("CREATE FUNCTION %s() \n", procName);
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("select %s()", procName);
    }

    @Override
    public String getDropProc(String procName) {
        return String.format("drop function %s()", procName);
    }
}
