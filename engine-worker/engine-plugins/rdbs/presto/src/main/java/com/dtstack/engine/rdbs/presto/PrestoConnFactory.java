package com.dtstack.engine.rdbs.presto;

import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class PrestoConnFactory extends AbstractConnFactory {

    public PrestoConnFactory() {
        driverName = "com.facebook.presto.jdbc.PrestoDriver";
        testSql = "select 1111";
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportTransaction() {
        return false;
    }
}
