package com.dtstack.engine.master.job.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.schedule.common.enums.DataBaseType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.schedule.common.jdbc.JdbcInfo;
import com.dtstack.schedule.common.jdbc.JdbcQuery;
import com.dtstack.schedule.common.jdbc.JdbcUrlPropertiesValue;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.utils.DtKerberosUtils;
import com.dtstack.engine.master.utils.DBUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yuemo
 * @company www.dtstack.com
 * @Date 2020-03-06
 */
@Service
public class HiveService {
    private static final Logger logger = LoggerFactory.getLogger(HiveService.class);

    @Autowired
    ClusterService clusterService;

    private volatile Map<String, ComboPooledDataSource> outerComboPooledDataSource = Maps.newConcurrentMap();
    private volatile Map<String, Object> parallelLockMap = Maps.newConcurrentMap();

    public List<List<Object>> executeQuery(Long dtuicTenantId, String database, String sql) throws Exception {
        JdbcInfo jdbcInfo = this.getJdbcInfo(dtuicTenantId);
        Connection connection;
        if (jdbcInfo.getUseConnectionPool()) {
            connection = this.getConnection(dtuicTenantId, database);
        } else {
            connection = this.getDirectConnection(dtuicTenantId, database);
        }

        return this.executeQuery(connection, database, dtuicTenantId, sql);
    }

    public List<List<Object>> executeQuery(Long dtuicTenantId, String jdbcUrl, String username, String password, String sql) throws Exception {
        JdbcInfo jdbcInfo = this.getJdbcInfo(dtuicTenantId);
        Connection connection;
        if (jdbcInfo.getUseConnectionPool()) {
            connection = this.getConnection(jdbcUrl, username, password, jdbcInfo.getKerberosConfig());
        } else {
            password = StringUtils.isBlank(username) ? jdbcInfo.getPassword() : password;
            username = StringUtils.isBlank(username) ? jdbcInfo.getUsername() : username;
            connection = DBUtil.getConnection(DataBaseType.HIVE, jdbcUrl, username, password, jdbcInfo.getKerberosConfig());
        }

        JdbcQuery jdbcQuery = new JdbcQuery(connection, "", dtuicTenantId, sql);
        return this.executeBaseQuery(jdbcQuery);
    }

    public Connection getConnection(String jdbcUrl, String username, String password, JSONObject kerberosConfig) throws PropertyVetoException, SQLException {
        jdbcUrl = this.login(kerberosConfig, jdbcUrl);
        ComboPooledDataSource c3p0 = (ComboPooledDataSource)this.outerComboPooledDataSource.get(jdbcUrl);
        if (c3p0 == null) {
            synchronized(this.getDatabaseLock(jdbcUrl)) {
                if (c3p0 == null) {
                    c3p0 = new ComboPooledDataSource();
                    c3p0.setDriverClass((String) JdbcUrlPropertiesValue.DRIVER_CLASS_NAME_MAP.get("hive"));
                    c3p0.setJdbcUrl(jdbcUrl);
                    c3p0.setUser(username);
                    c3p0.setPassword(password);
                    c3p0.setInitialPoolSize(5);
                    c3p0.setMaxPoolSize(20);
                    c3p0.setMinPoolSize(5);
                    c3p0.setCheckoutTimeout(60000);
                    c3p0.setMaxIdleTime(60);
                    c3p0.setTestConnectionOnCheckin(true);
                    c3p0.setIdleConnectionTestPeriod(60);
                    this.outerComboPooledDataSource.put(jdbcUrl, c3p0);
                }
            }
        }

        return c3p0.getConnection();
    }

    private Object getDatabaseLock(String lockKey) {
        Object lock = this;
        if (this.parallelLockMap != null) {
            Object newLock = new Object();
            lock = this.parallelLockMap.putIfAbsent(lockKey, newLock);
            if (lock == null) {
                lock = newLock;
            }
        }

        return lock;
    }

    private String login(JSONObject kerberosConfig, String jdbcUrl) {
        try {
            Configuration conf = null;
            String principal = MapUtils.getString(kerberosConfig, "hive.server2.authentication.kerberos.principal");
            String path = MapUtils.getString(kerberosConfig, "hive.server2.authentication.kerberos.keytab");
            if (MapUtils.isNotEmpty(kerberosConfig) && StringUtils.isNotEmpty(principal) && StringUtils.isNotEmpty(path)) {
                conf = DtKerberosUtils.loginKerberos(kerberosConfig);
                logger.info("jdbcUrl:" + jdbcUrl);
                logger.info("principal:" + principal);
                jdbcUrl = DBUtil.concatHiveJdbcUrl(conf, jdbcUrl);
            }

            return jdbcUrl;
        } catch (Exception var6) {
            logger.error("Login kerberos error:{}", var6);
            throw new RdosDefineException("Login kerberos error:{}");
        }
    }

    public JdbcInfo getJdbcInfo(Long dtUicTenantId) {
        JdbcInfo jdbcInfo = null;
        if (dtUicTenantId != null) {
            jdbcInfo = hiveInfo(dtUicTenantId);
        }

        if (jdbcInfo == null) {
            throw new RdosDefineException("can't get hive jdbc conf from clusterService");
        } else {
            JdbcUrlPropertiesValue.setNullPropertiesToDefaultValue(jdbcInfo);
            return jdbcInfo;
        }
    }

    private JdbcInfo hiveInfo(Long dtUicTenantId) {
        Object data = clusterService.hiveInfo(dtUicTenantId,true);
        JdbcInfo JDBCInfo = null;
        if (data != null) {
            try {
                JDBCInfo = (JdbcInfo) PublicUtil.jsonStrToObject(data.toString(), JdbcInfo.class);
            } catch (IOException var5) {
                logger.error("", var5);
            }
        }

        return JDBCInfo;
    }

    public List<List<Object>> executeQuery(Connection connection, String database, Long dtuicTenantId, String sql) throws Exception {
        JdbcQuery jdbcQuery = new JdbcQuery(connection, database, dtuicTenantId, sql);
        return this.executeBaseQuery(jdbcQuery.done());
    }

    public Connection getDirectConnection(Long dtuicTenantId, String database) {
        return this.getDirectConnection(dtuicTenantId, (String)null, (String)null, database);
    }

    public Connection getDirectConnection(Long dtuicTenantId, String userName, String password, String database) {
        JdbcInfo jdbcInfo = this.getJdbcInfo(dtuicTenantId);
        password = StringUtils.isBlank(userName) ? jdbcInfo.getPassword() : password;
        userName = StringUtils.isBlank(userName) ? jdbcInfo.getUsername() : userName;
        return DBUtil.getConnection(DataBaseType.HIVE, String.format(jdbcInfo.getJdbcUrl(), database), userName, password, jdbcInfo.getKerberosConfig());
    }

    public Connection getConnection(Long dtuicTenantId, String database) {
        return this.getDirectConnection(dtuicTenantId, database);
    }

    public List<List<Object>> executeBaseQuery(JdbcQuery jdbcQuery) throws Exception {
        List<List<Object>> result = Lists.newArrayList();
        Statement statement = null;
        ResultSet res = null;

        try {
            statement = jdbcQuery.getConnection().createStatement();
            statement.setMaxRows(jdbcQuery.getMaxRows());
            statement.setQueryTimeout(jdbcQuery.getQueryTimeout());
            if (StringUtils.isNotEmpty(jdbcQuery.getDatabase())) {
                statement.execute("use " + jdbcQuery.getDatabase());
            }

//            try {
//                if (StringUtils.isNotEmpty(jdbcQuery.getSql())) {
//                    SqlParserFactory instance = SqlParserFactory.getInstance();
//                    SqlParserImpl sqlParser = instance.getSqlParser(ETableType.HIVE);
//                    ParseResult parseResult = sqlParser.parseSql(jdbcQuery.getSql(), jdbcQuery.getDatabase(), (Map)null);
//                    if (null != parseResult && !SqlType.QUERY.equals(parseResult.getSqlType())) {
//                        statement.setMaxRows(0);
//                    }
//                }
//            } catch (Exception var19) {
//            }

            if (statement.execute(jdbcQuery.getSql())) {
                res = statement.getResultSet();
                int columns = res.getMetaData().getColumnCount();
                List<Object> cloumnName = Lists.newArrayList();
                int timeStamp = 0;
                SimpleDateFormat dateFormat = null;

                for(int i = 1; i <= columns; ++i) {
                    String name = res.getMetaData().getColumnName(i);
                    if (name.contains(".")) {
                        name = name.split("\\.")[1];
                    }

                    if ("current_timestamp()".equalsIgnoreCase(name)) {
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        timeStamp = i;
                    }

                    cloumnName.add(name);
                }

                result.add(cloumnName);

                while(res.next()) {
                    List<Object> objects = Lists.newArrayList();

                    for(int i = 1; i <= columns; ++i) {
                        if (i == timeStamp && Objects.nonNull(dateFormat)) {
                            objects.add(dateFormat.format(res.getObject(i)));
                        } else {
                            objects.add(res.getObject(i));
                        }
                    }

                    result.add(objects);
                }
            }
        } catch (Throwable var20) {
            if (var20.getMessage().contains("NoSuchTableException:")) {
                String message = var20.getMessage();
                throw new RdosDefineException(message.substring(message.indexOf("NoSuchTableException:") + "NoSuchTableException:".length()));
            }

            throw var20;
        } finally {
            try {
                if (res != null) {
                    res.close();
                }

                if (statement != null) {
                    statement.close();
                }

                if (jdbcQuery.getConnection() != null && !jdbcQuery.getMultiplex()) {
                    jdbcQuery.getConnection().close();
                    logger.info("jdbcQuery is closed");
                }
            } catch (Throwable var18) {
                logger.error("", var18);
            }

        }

        return result;
    }

    public List<List<Object>> executeQueryHiveServer(Long dtuicTenantId, String username, String password, String database, String sql) throws Exception {
        Connection connection = this.getHiveServerConnection(dtuicTenantId, username, password, database);
        JdbcQuery jdbcQuery = (new JdbcQuery(connection, database, dtuicTenantId, sql)).multiplex(false);
        return this.executeBaseQuery(jdbcQuery);
    }

    public Connection getHiveServerConnection(Long dtuicTenantId, String username, String password, String database) {
        JdbcInfo jdbcInfo = this.getHiveServerJdbcInfo(dtuicTenantId);
        password = StringUtils.isBlank(username) ? jdbcInfo.getPassword() : password;
        username = StringUtils.isBlank(username) ? jdbcInfo.getUsername() : username;
        return DBUtil.getConnection(DataBaseType.HIVE, String.format(jdbcInfo.getJdbcUrl(), database), username, password, jdbcInfo.getKerberosConfig());
    }

    public JdbcInfo getHiveServerJdbcInfo(Long dtUicTenantId) {
        JdbcInfo jdbcInfo = null;
        if (dtUicTenantId != null) {
            jdbcInfo = hiveServerInfo(dtUicTenantId);
        }

        if (jdbcInfo == null) {
            throw new RdosDefineException("can't get hive server jdbc conf from clusterService");
        } else {
            JdbcUrlPropertiesValue.setNullPropertiesToDefaultValue(jdbcInfo);
            return jdbcInfo;
        }
    }

    public JdbcInfo hiveServerInfo(Long dtUicTenantId) {
        Object data = clusterService.hiveServerInfo(dtUicTenantId,true);
        JdbcInfo jdbcInfo = null;
        if (data != null) {
            try {
                jdbcInfo = (JdbcInfo)PublicUtil.jsonStrToObject(data.toString(), JdbcInfo.class);
            } catch (IOException var5) {
                logger.error("", var5);
            }
        }

        return jdbcInfo;
    }


    public String getTableLocation(Long dtuicTenantId, String dbName, String tableName) throws Exception {
        return this.getTableLocation(dtuicTenantId, (String)null, (String)null, dbName, tableName);
    }

    public String getTableLocation(Long dtuicTenantId, String userName, String password, String dbName, String tableName) throws Exception {
        String location = null;
        List<List<Object>> result = this.executeQueryHiveServer(dtuicTenantId, userName, password, dbName, String.format("desc formatted %s", tableName));
        Iterator var8 = result.iterator();

        while(var8.hasNext()) {
            List<Object> objects = (List)var8.next();
            if (objects.get(0).toString().contains("Location:")) {
                location = objects.get(1).toString();
            }
        }

        return location;
    }
}
