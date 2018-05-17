package com.dtstack.rdos.engine.execution.inceptor;

import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;

public class InceptorConnFactory extends ConnFactory {

    public InceptorConnFactory() {
        driverName = "org.apache.hive.jdbc.HiveDriver";
        testSql = "show tables";
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
