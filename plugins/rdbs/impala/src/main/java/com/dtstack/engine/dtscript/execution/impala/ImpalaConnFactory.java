package com.dtstack.engine.dtscript.execution.impala;

import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.dtscript.execution.base.util.DtStringUtil;
import com.dtstack.engine.dtscript.execution.rdbs.executor.ConnFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Properties;

public class ImpalaConnFactory extends ConnFactory {

    public static String AUTHMECH = "AuthMech";

    public ImpalaConnFactory() {
        driverName = "com.cloudera.impala.jdbc41.Driver";
        testSql = "show tables";
    }

    @Override
    public void init(Properties props) throws ClassNotFoundException {
        super.init(props);
        if (StringUtils.isNotBlank(getUserName()) && !dbURL.contains(AUTHMECH) && StringUtils.isNotBlank(getPwd())){
            dbURL = dbURL + ";AuthMech=3";
        } else if (StringUtils.isNotBlank(getUserName()) && !dbURL.contains(AUTHMECH) && StringUtils.isBlank(getPwd())){
            dbURL = dbURL + ";AuthMech=2";
        }
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
    }

    @Override
    public boolean supportProcedure() {
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
