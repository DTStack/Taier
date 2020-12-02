package com.dtstack.engine.rdbs.tidb;

import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

import java.util.List;

public class KingBaseConnFactory extends AbstractConnFactory {

    public KingBaseConnFactory() {
        driverName = "com.kingbase8.Driver";
        testSql = "select table_name from user_tables;";
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("create  procedure \"%s\" Authid Current_User as\n", procName);
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
    }
}
