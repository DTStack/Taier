package com.dtstack.engine.rdbs.greenplum;


import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

import java.util.List;

public class GreenPlumConnFactory extends AbstractConnFactory {

    public GreenPlumConnFactory() {
        driverName = "com.pivotal.jdbc.GreenplumDriver";
        testSql = "select 1111";
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
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
