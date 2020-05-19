package com.dtstack.engine.rdbs.oracle;

import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class OracleConnFactory extends AbstractConnFactory {

    public OracleConnFactory() {
        driverName = "oracle.jdbc.driver.OracleDriver";
        testSql = "select 1111 from dual";
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("create  procedure \"%s\" Authid Current_User as\n", procName);
    }

    @Override
    public boolean dealWithProcedure(String sql) {
        if (StringUtils.isBlank(sql)) {
            return true;
        }
        String[] sqls = sql.trim().split("\\s+", 1);
        if (sqls.length >= 2){
            if ("BEGIN".equalsIgnoreCase(sqls[0])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
    }
}
