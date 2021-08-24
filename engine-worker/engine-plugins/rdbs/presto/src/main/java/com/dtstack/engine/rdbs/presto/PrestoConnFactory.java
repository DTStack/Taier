package com.dtstack.engine.rdbs.presto;

import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

import java.util.List;


public class PrestoConnFactory extends AbstractConnFactory {

    public PrestoConnFactory() {
        driverName = "com.facebook.presto.jdbc.PrestoDriver";
        testSql = "select 1111";
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
    }

    @Override
    public boolean supportTransaction() {
        return false;
    }

    @Override
    public boolean supportProcedure(String sql) {
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
