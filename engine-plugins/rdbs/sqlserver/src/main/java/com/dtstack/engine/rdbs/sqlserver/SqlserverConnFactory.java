package com.dtstack.engine.rdbs.sqlserver;


import com.dtstack.engine.rdbs.common.executor.ConnFactory;

public class SqlserverConnFactory extends ConnFactory {

    public SqlserverConnFactory() {
        driverName = "net.sourceforge.jtds.jdbc.Driver";
        testSql = "select 1111";
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("create procedure \"%s\" as\n", procName);
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("execute \"%s\"", procName);
    }
}
