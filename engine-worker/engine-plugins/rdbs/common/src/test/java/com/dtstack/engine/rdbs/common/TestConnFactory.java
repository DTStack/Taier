package com.dtstack.engine.rdbs.common;

import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

public class TestConnFactory extends AbstractConnFactory {

    public TestConnFactory() {
        driverName = "com.mysql.jdbc.Driver";
        testSql = "select 1111";
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("create procedure %s() \n", procName);
    }
}