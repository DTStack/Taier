package com.dtstack.engine.rdbs.postgresql;


import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class PostgreSQLConnFactory extends AbstractConnFactory {

    public PostgreSQLConnFactory() {
        driverName = "org.postgresql.Driver";
        testSql = "select 1111";
    }


    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("CREATE FUNCTION \"%s\"() RETURNS void AS $body$ ", procName);
    }

    @Override
    public String getCreateProcedureTailer() {
        return " $body$ LANGUAGE PLPGSQL; ";
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("select \"%s\"()", procName);
    }

    @Override
    public String getDropProc(String procName) {
        return String.format("drop function \"%s\"()", procName);
    }
}
