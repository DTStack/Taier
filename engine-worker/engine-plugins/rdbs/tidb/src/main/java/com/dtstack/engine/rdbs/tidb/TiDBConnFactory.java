package com.dtstack.engine.rdbs.tidb;

import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

import java.util.List;

public class TiDBConnFactory extends AbstractConnFactory {

    public TiDBConnFactory() {
        driverName = "com.mysql.jdbc.Driver";
        testSql = "select 1111";
    }

    @Override
    public boolean supportProcedure(String sql) {
        return false;
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
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
