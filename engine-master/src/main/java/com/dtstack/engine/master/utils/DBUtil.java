package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.AddressUtil;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.schedule.common.constant.Constant;
import com.dtstack.schedule.common.enums.DataBaseType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.util.KerberosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DBUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DBUtil.class);

    public static final String SQLSTATE_USERNAME_PWD_ERROR = "28000";

    public static final String SQLSTATE_CANNOT_ACQUIRE_CONNECT = "08004";
    public static String JDBC_SPLIT = "/|;";
    public static final String HOST_KEY = "host";
    public static final String PORT_KEY = "port";
    public static final String DB_KEY = "db";
    public static final String PARAM_KEY = "param";

    public static Pattern JDBC_PATTERN = Pattern.compile("(?i)jdbc:[a-zA-Z0-9\\.]+://(?<host>[0-9a-zA-Z\\.-]+):(?<port>\\d+)/(?<db>[0-9a-zA-Z_%\\.]+)(?<param>[\\?;#].*)*");

    public static Pattern HIVE_JDBC_PATTERN = Pattern.compile("(?i)jdbc:hive2://(?<host>[0-9a-zA-Z\\-\\.]+):(?<port>\\d+)(/(?<db>[0-9a-z_%]+)*(?<param>[\\?;#].*)*)*");
    private static ReentrantLock lock = new ReentrantLock();

    private static final List<DataBaseType> DQ_SPARK = new ArrayList<>(Lists.newArrayList(
            DataBaseType.Spark,
            DataBaseType.HIVE));

    private DBUtil() {
    }

    public static Connection getConnection(final DataBaseType dataBaseType,
                                           final String jdbcUrl, final String username, final String password) {
        return DBUtil.getConnection(dataBaseType, jdbcUrl, username, password, null);
    }

    /**
     * 获取连接_kerberos认证
     */
    public static Connection getConnection(final DataBaseType dataBaseType,
                                           final String jdbcUrl, final String username, final String password, Map<String, Object> kerberosConfig) {

        try {
            Connection connection = RetryUtil.executeWithRetry(new Callable<Connection>() {
                @Override
                public Connection call() throws Exception {
                    return DBUtil.connect(dataBaseType, jdbcUrl, username,
                            password, String.valueOf(Constant.SOCKET_TIMEOUT_INSECOND * 1000), kerberosConfig);
                }
            }, 1, 1000L, false);
            return connection;
        } catch (RdosDefineException e) {
            throw e;
        } catch (Exception e1) {
            throw new RdosDefineException(String.format("连接：%s 时发生错误：%s.", jdbcUrl, e1.getMessage()),
                    ErrorCode.CONN_DB_ERROR, e1);
        }
    }

    public static Connection connect(DataBaseType dataBaseType,
                                     String url, String user, String pass, String socketTimeout, Map<String, Object> confMap) {
        String addr = parseIpAndPort(dataBaseType, url);
        if (!isAddressAvailable(addr)) {
            throw new RdosDefineException("连接信息：" + url, ErrorCode.IP_PORT_CONN_ERROR);
        }

        Properties prop = new Properties();
        prop.put("user", user);
        prop.put("password", pass);

        if (dataBaseType == DataBaseType.Oracle) {
            //oracle.net.READ_TIMEOUT for jdbc versions < 10.1.0.5 oracle.jdbc.ReadTimeout for jdbc versions >=10.1.0.5
            // unit ms
            prop.put("oracle.jdbc.ReadTimeout", socketTimeout);
            prop.put("remarksReporting", "true");

        }

        return connect(dataBaseType, url, prop, confMap);
    }

    public static String parseIpAndPort(DataBaseType dataBaseType, String url) {
        if(url.contains(";principal=")) {
            String[] jdbcStr = url.split(";");
            url = jdbcStr[0];
        }

        String addr;
        Matcher matcher;
        if(dataBaseType != DataBaseType.SQLServer && dataBaseType != DataBaseType.SQLSSERVER_2017_LATER) {
            if(dataBaseType == DataBaseType.HIVE) {
                matcher = HIVE_JDBC_PATTERN.matcher(url);
                if(matcher.find()) {
                    addr = matcher.group("host") + ":" + matcher.group("port");
                } else {
                    addr = url.substring(url.indexOf("//") + 2, url.lastIndexOf("/"));
                }
            } else {
                if(dataBaseType == DataBaseType.CarbonData) {
                    return parseAddress(url);
                }

                if(dataBaseType == DataBaseType.Impala) {
                    if(JDBC_PATTERN.matcher(url).find()) {
                        addr = url.substring(url.indexOf("//") + 2, url.lastIndexOf("/"));
                    } else if(url.indexOf(";") > -1) {
                        addr = url.substring(url.indexOf("//") + 2, url.indexOf(";"));
                    } else {
                        addr = url.substring(url.indexOf("//") + 2);
                    }
                } else if(url.contains("serverTimezone")) {
                    addr = url.substring(url.indexOf("//") + 2, url.lastIndexOf(":") + 5);
                } else {
                    addr = url.substring(url.indexOf("//") + 2, url.lastIndexOf("/"));
                }
            }
        } else {
            addr = url.substring(url.indexOf("//") + 2, url.lastIndexOf(";"));
        }

        return addr;
    }
    public static String parseAddress(String jdbcUrl) {
        String[] split = jdbcUrl.split(JDBC_SPLIT);
        if(split.length < 4) {
            throw new RdosDefineException(ErrorCode.CONN_DB_ERROR);
        } else {
            return split[2];
        }
    }
    public static boolean isAddressAvailable(String fullAddress) {
        boolean check = false;
        String[] split = fullAddress.split(",");
        for (String addr : split) {
            String[] addrs = addr.split(":");
            if (addrs.length == 1) {
                String ip = addrs[0].trim();
                check = AddressUtil.ping(ip);
            } else {
                String ip = addrs[0].trim();
                String port = addrs[1].trim();
                check = AddressUtil.telnet(ip, Integer.parseInt(port));
            }
            if (check) {
                //HA配置
                break;
            }
        }
        return check;
    }

    private static Connection connect(DataBaseType dataBaseType,
                                      String url, Properties prop, Map<String, Object> confMap) {
        try {
            lock.lock();
            Class.forName(dataBaseType.getDriverClassName());

            if (DQ_SPARK.contains(dataBaseType)) {
                DriverManager.setLoginTimeout(5 * 60);
                return getHiveConnection(url, prop, confMap);
            } else {
                DriverManager.setLoginTimeout(30);
                return DriverManager.getConnection(url, prop);
            }
        } catch (SQLException e) {
            LOG.info("LoginTimeOut: {}", DriverManager.getLoginTimeout());
            if (SQLSTATE_USERNAME_PWD_ERROR.equals(e.getSQLState())) {
                throw new RdosDefineException(ErrorCode.CONF_ERROR, e);
            } else if (SQLSTATE_CANNOT_ACQUIRE_CONNECT.equals(e.getSQLState())) {
                throw new RdosDefineException(ErrorCode.CONF_ERROR, e);
            } else {
                throw new RdosDefineException("连接信息：" + url + " 错误信息：" + e.getMessage(), ErrorCode.CONN_DB_ERROR, e);
            }
        } catch (RdosDefineException e) {
            throw e;
        } catch (Exception e1) {
            throw new RdosDefineException("连接信息：" + url + " 错误信息：" + e1.getMessage(), ErrorCode.CONN_DB_ERROR, e1);
        } finally {
            lock.unlock();
        }
    }

    public static String concatHiveJdbcUrl(Configuration conf, String jdbcUrl) throws Exception {

        if (jdbcUrl.contains(";principal=")) {
            return jdbcUrl;
        }

        String principal = conf.get(HadoopConfTool.BEELINE_PRINCIPAL);
        if (principal == null) {
            String host = conf.get(HadoopConfTool.HIVE_BIND_HOST);
            if (host == null) {
                Matcher matcher = DBUtil.HIVE_JDBC_PATTERN.matcher(jdbcUrl);
                if (matcher.find()) {
                    host = matcher.group(DBUtil.HOST_KEY);
                }
            }

            if (host != null) {
                UserGroupInformation loginUser = UserGroupInformation.getLoginUser();
                principal = String.format("%s/%s@%s", loginUser.getShortUserName(), host, KerberosUtil.getDefaultRealm());
            }
        }

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(principal) && !jdbcUrl.contains(";principal=")) {
            jdbcUrl = jdbcUrl + ";principal=" + principal;
        }

        return jdbcUrl;
    }

    /**
     * 获取hive连接
     *
     * @param url
     * @param prop
     * @return
     * @throws Exception
     */
    private static Connection getHiveConnection(String url, Properties prop, Map<String, Object> confMap) throws Exception {
        Configuration conf = null;
        if (MapUtils.isNotEmpty(confMap)) {
            String principalFile =(String) confMap.get("principalFile");
            LOG.info("getHiveConnection principalFile:{}",principalFile);

            conf = DtKerberosUtils.loginKerberos(confMap);
            //拼接URL
            //url = concatHiveJdbcUrl(conf, url);
        }

        Matcher matcher = HIVE_JDBC_PATTERN.matcher(url);
        String db = null;
        String host = null;
        String port = null;
        String param = null;
        if (matcher.find()) {
            host = matcher.group(HOST_KEY);
            port = matcher.group(PORT_KEY);
            db = matcher.group(DB_KEY);
            param = matcher.group(PARAM_KEY);
        }

        if (StringUtils.isNotEmpty(host) && StringUtils.isNotEmpty(db)) {
            param = param == null ? "" : param;
            url = String.format("jdbc:hive2://%s:%s/%s", host, port, param);
            Connection connection = DriverManager.getConnection(url, prop);
            if (StringUtils.isNotEmpty(db)) {
                try {
                    connection.createStatement().execute("use " + db);
                } catch (SQLException e) {
                    if (connection != null) {
                        connection.close();
                    }

                    if (e.getMessage().contains("NoSuchDatabaseException")) {
                        throw new RdosDefineException(ErrorCode.CONN_DB_ERROR, e);
                    } else {
                        throw e;
                    }
                }
            }

            return connection;
        }

        throw new RdosDefineException(ErrorCode.CONN_DB_ERROR);
    }

    public static boolean executeSqlWithoutResultSet(Connection connection, String sql) {
        boolean flag = true;
        Statement deleteStmt = null;
        try {
            deleteStmt = connection.createStatement();
            executeSqlWithoutResultSet(deleteStmt, sql);
        } catch (Exception e) {
            if (e.getMessage().contains("Table") && e.getMessage().contains("already exists")) {
                throw new RdosDefineException("表已经存在");
            }
            flag = false;
            LOG.error("execute sql:{}, errorMessage:[{}]", sql, e);
        } finally {
            DBUtil.closeDBResources(null, deleteStmt, null);
        }

        return flag;
    }

    private static void executeSqlWithoutResultSet(Statement stmt, String sql)
            throws SQLException {
        stmt.execute(sql);
    }

    public static void closeDBResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (null != rs) {
                rs.close();
            }

            if (null != stmt) {
                stmt.close();
            }

            if (null != conn) {
                conn.close();
            }
        } catch (Throwable t) {
            LOG.error("", t);
        }

    }

}
