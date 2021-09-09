package com.dtstack.engine.rdbs.kingbase;

import com.dtstack.engine.pluginapi.util.DtStringUtil;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

import java.util.List;

public class KingBaseConnFactory extends AbstractConnFactory {

    public KingBaseConnFactory() {
        driverName = "com.kingbase8.Driver";
        testSql = "select table_name from user_tables;";
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("create  procedure \"%s\" as \n", procName);
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
    }
}
