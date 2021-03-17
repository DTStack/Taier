package com.dtstack.engine.rdbs.impala;

import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Properties;

public class ImpalaConnFactory extends AbstractConnFactory {

    public static String AUTHMECH = "AuthMech";

    public ImpalaConnFactory() {
        driverName = "com.cloudera.impala.jdbc41.Driver";
        testSql = "show tables";
    }

    @Override
    public void init(Properties props) throws ClassNotFoundException {
        super.init(props);
        if (StringUtils.isNotBlank(getUsername()) && !jdbcUrl.contains(AUTHMECH) && StringUtils.isNotBlank(getPassword())){
            jdbcUrl = jdbcUrl + ";AuthMech=3";
        } else if (StringUtils.isNotBlank(getUsername()) && !jdbcUrl.contains(AUTHMECH) && StringUtils.isBlank(getPassword())){
//            jdbcUrl = jdbcUrl + ";AuthMech=2";
            jdbcUrl = jdbcUrl;
        }
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
