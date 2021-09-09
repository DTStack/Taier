package com.dtstack.engine.rdbs.mysql;


import com.dtstack.engine.pluginapi.util.DtStringUtil;
import com.dtstack.engine.pluginapi.util.MathUtil;
import com.dtstack.engine.rdbs.common.constant.ConfigConstant;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Properties;

public class MysqlConnFactory extends AbstractConnFactory {

    public static final String OCEANBASE_JDBC_PREFIX = "jdbc:oceanbase";

    public static final String MYSQL_JDBC_PREFIX = "jdbc:mysql";

    public MysqlConnFactory() {
        driverName = "com.mysql.jdbc.Driver";
        testSql = "select 1111";
    }

    @Override
    public void init(Properties properties) throws ClassNotFoundException {

        // to support jdbcUrl formatted like "jdbc:oceanbase://localhost:2883/def"
        String jdbcUrl = MathUtil.getString(properties.get(ConfigConstant.JDBCURL));
        if(StringUtils.startsWith(jdbcUrl, OCEANBASE_JDBC_PREFIX)){
            jdbcUrl = StringUtils.replaceOnce(jdbcUrl, OCEANBASE_JDBC_PREFIX, MYSQL_JDBC_PREFIX);
            properties.put(ConfigConstant.JDBCURL, jdbcUrl);
        }

        super.init(properties);
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("create procedure %s() \n", procName);
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("call %s()", procName);
    }

    @Override
    public String getDropProc(String procName) {
        return String.format("DROP PROCEDURE %s", procName);
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
    }

}
