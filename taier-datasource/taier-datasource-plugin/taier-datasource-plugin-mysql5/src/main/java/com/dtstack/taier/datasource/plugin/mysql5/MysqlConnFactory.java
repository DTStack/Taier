package com.dtstack.taier.datasource.plugin.mysql5;

import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.SSLHandshakeException;
import java.sql.Connection;
import java.sql.Statement;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:11 2020/1/3
 * @Description：Mysql 连接
 */
public class MysqlConnFactory extends ConnFactory {
    public MysqlConnFactory() {
        driverName = DataBaseType.MySql.getDriverClassName();
        this.errorPattern = new MysqlErrorPattern();
    }

    @Override
    public Connection getConn(ISourceDTO sourceDTO) throws Exception {
        try {
            return super.getConn(sourceDTO);
        } catch (Exception e) {
            if (ExceptionUtils.getRootCause(e) instanceof SSLHandshakeException) {
                throw new SourceException(
                        "For jdk8 or above, please modify the disabled protocol in java.security, " +
                                "or add useSSL=false after the url. ", e);
            }else {
                throw new SourceException(e.getMessage(), e);
            }
        }
    }

    @Override
    protected String getCreateProcHeader(String procName) {
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
    public Connection setSchema(Connection conn, String schema) {
        if (StringUtils.isBlank(schema)) {
            return conn;
        }
        try(Statement statement = conn.createStatement()) {
            //选择schema
            String useSchema = String.format("USE %s", schema);
            statement.execute(useSchema);
        } catch (Exception e) {
            throw new SourceException(String.format("set schema [%s] error: %s", schema, e.getMessage()), e);
        }
        return conn;
    }
}
