package com.dtstack.taier.datasource.plugin.postgresql;

import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.source.DataBaseType;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 15:53 2020/1/7
 * @Description：
 */
public class PostgresqlConnFactory extends ConnFactory {
    public PostgresqlConnFactory() {
        this.driverName = DataBaseType.PostgreSQL.getDriverClassName();
        this.errorPattern = new PostgreSqlErrorPattern();
    }

    @Override
    protected String getCreateProcHeader(String procName) {
        return String.format("CREATE FUNCTION \"%s\"() RETURNS void AS $body$", procName);
    }

    @Override
    public String getCreateProcTail() {
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
}
