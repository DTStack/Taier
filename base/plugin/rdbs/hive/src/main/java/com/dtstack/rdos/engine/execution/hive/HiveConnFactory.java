package com.dtstack.rdos.engine.execution.hive;


import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;

public class HiveConnFactory extends ConnFactory {

    public HiveConnFactory() {
        driverName = "org.apache.hive.jdbc.HiveDriver";
        testSql = "show tables";
    }

    @Override
    public boolean supportProcedure() {
        return false;
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCallProc(String procName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDropProc(String procName) {
        throw new UnsupportedOperationException();
    }
}
