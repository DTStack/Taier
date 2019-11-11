package com.dtstack.engine.dtscript.execution.oracle;

import com.dtstack.engine.dtscript.execution.rdbs.executor.ConnFactory;

public class OracleConnFactory extends ConnFactory {

    public OracleConnFactory() {
        driverName = "oracle.jdbc.driver.OracleDriver";
        testSql = "select 1111 from dual";
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("create  procedure \"%s\" Authid Current_User as\n", procName);
    }
}
