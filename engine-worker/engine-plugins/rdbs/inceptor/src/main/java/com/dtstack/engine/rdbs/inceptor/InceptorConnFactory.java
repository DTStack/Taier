package com.dtstack.engine.rdbs.inceptor;

import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * @program: engine-all
 * @author: wuren
 * @create: 2021/04/06
 **/
public class InceptorConnFactory extends AbstractConnFactory {

    public static final String USER = "user";
    public static final String PASSWORD = "password";

    private static final String HIVE_CONF_PREFIX = "hiveconf:";

    private static final String KERBEROS_URL_FORMAT =
            "%s;" + "authentication=kerberos;" + "kuser=%s;" + "keytab=%s;" + "krb5conf=%s;";

    public InceptorConnFactory() {
        driverName = "org.apache.hive.jdbc.HiveDriver";
        testSql = "show tables";
    }

    @Override
    public Connection getConnByTaskParams(String taskParams, String jobName) throws ClassNotFoundException, SQLException, IOException {
        Properties properties =  new Properties();

        if (StringUtils.isNotEmpty(taskParams)) {
            for (String line : taskParams.split("\n")) {
                line = StringUtils.trim(line);
                if (StringUtils.isEmpty(line) || line.startsWith("#")) {
                    continue;
                }

                String[] keyAndVal = line.split("=");
                if (keyAndVal.length > 1) {
                    String newKey = keyAndVal[0].startsWith(HIVE_CONF_PREFIX) ? keyAndVal[0] : HIVE_CONF_PREFIX + keyAndVal[0];
                    String newValue = keyAndVal[1];
                    properties.setProperty(newKey, newValue);
                }
            }
        }

        Connection conn;
        try {
            if (baseConfig.isOpenKerberos()) {
                String[] paths = KerberosUtils.getKerberosFile(baseConfig, null);
                String keytabPath = paths[0];
                String krb5confPath = paths[1];
                String kerberosUrl = String.format(KERBEROS_URL_FORMAT,
                        jdbcUrl, baseConfig.getPrincipal(), keytabPath, krb5confPath);
                conn = DriverManager.getConnection(kerberosUrl);
            } else {
                if (getUsername() == null) {
                    conn = DriverManager.getConnection(jdbcUrl, properties);
                } else {
                    properties.setProperty(USER, getUsername());
                    properties.setProperty(PASSWORD, getPassword());
                    conn = DriverManager.getConnection(jdbcUrl, properties);
                }
            }
        } catch (Exception e) {
            throw new RdosDefineException("get connection by taskParams error", e);
        }
        return conn;
    }

    @Override
    public Connection getConn() throws SQLException {
        Connection conn;
        if (baseConfig.isOpenKerberos()) {
            String[] paths = KerberosUtils.getKerberosFile(baseConfig, null);
            String keytabPath = paths[0];
            String krb5confPath = paths[1];
            String kerberosUrl = String.format(KERBEROS_URL_FORMAT,
                    jdbcUrl, baseConfig.getPrincipal(), keytabPath, krb5confPath);
            conn = DriverManager.getConnection(kerberosUrl);
        } else {
            conn = DriverManager.getConnection(jdbcUrl);
        }
        return conn;
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
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
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
