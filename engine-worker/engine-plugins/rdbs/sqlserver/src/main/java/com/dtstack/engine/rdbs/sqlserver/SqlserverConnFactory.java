package com.dtstack.engine.rdbs.sqlserver;


import com.dtstack.engine.pluginapi.util.DtStringUtil;
import com.dtstack.engine.pluginapi.util.MathUtil;
import com.dtstack.engine.rdbs.common.constant.ConfigConstant;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Properties;

public class SqlserverConnFactory extends AbstractConnFactory {

    public static final String SQLSERVER_JDBC_PREFIX = "jdbc:sqlserver";

    public static final String SQLSERVER_JTDS_JDBC_PREFIX = "jdbc:jtds:sqlserver";

    public SqlserverConnFactory() {
        driverName = "net.sourceforge.jtds.jdbc.Driver";
        testSql = "select 1111";
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("create procedure \"%s\" as\n", procName);
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("execute \"%s\"", procName);
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
    }

    @Override
    public void init(Properties properties) throws ClassNotFoundException {
        // to support jdbcUrl formatted like "jdbc:sqlserver://localhost:1433;databaseName=def"
        String jdbcUrl = MathUtil.getString(properties.get(ConfigConstant.JDBCURL));
        if(StringUtils.startsWith(jdbcUrl, SQLSERVER_JDBC_PREFIX)){
            jdbcUrl = StringUtils.replaceOnce(jdbcUrl, SQLSERVER_JDBC_PREFIX, SQLSERVER_JTDS_JDBC_PREFIX);
            properties.put(ConfigConstant.JDBCURL, jdbcUrl);
        }
        super.init(properties);
    }
}
