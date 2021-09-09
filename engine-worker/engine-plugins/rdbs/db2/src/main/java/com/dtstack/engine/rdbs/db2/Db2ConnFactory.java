package com.dtstack.engine.rdbs.db2;


import com.dtstack.engine.pluginapi.util.DtStringUtil;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

import java.util.List;

public class Db2ConnFactory extends AbstractConnFactory {

    public Db2ConnFactory() {
        driverName = "com.ibm.db2.jcc.DB2Driver";
        // todo: find the best sql for check
        testSql = "SELECT * FROM SYSCAT.TABLES FETCH FIRST 1 ROWS ONLY";
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("CREATE PROCEDURE %s() \n", procName);
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
