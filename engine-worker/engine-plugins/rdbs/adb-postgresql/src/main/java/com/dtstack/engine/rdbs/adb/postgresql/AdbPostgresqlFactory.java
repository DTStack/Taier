package com.dtstack.engine.rdbs.adb.postgresql;

import com.dtstack.engine.pluginapi.util.DtStringUtil;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

import java.util.List;

/**
 * @author tiezhu
 * @since 2021/5/18 6:59 下午
 */
public class AdbPostgresqlFactory extends AbstractConnFactory {

    public AdbPostgresqlFactory() {
        driverName = "org.postgresql.Driver";
        testSql = "select 1";
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("CREATE FUNCTION \"%s\"() RETURNS void AS $body$", procName);
    }

    @Override
    public String getCreateProcedureTailer() {
        return " $body$ LANGUAGE PLPGSQL; ";
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("SELECT \"%s\"()", procName);
    }

    @Override
    public String getDropProc(String procName) {
        return String.format("DROP FUNCTION \"%s\"()", procName);
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
    }

}
