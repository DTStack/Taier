package com.dtstack.engine.common.logstore.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * mysql 插件本身信息存储使用的连接
 * Date: 2018/1/29
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class MysqlDataConnPool {

    private static final Logger LOG = LoggerFactory.getLogger(MysqlDataConnPool.class);

    private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";

    private static Map<String, String> dbConfig;

    private String jdbcUrl;
    private String username;
    private String password;

    /**
     * sql执行超时时间-单位秒
     */
    private int queryTimeOut = 5 * 60;

    /**
     * 初始连接池大小
     */
    private int initialSize = 20;

    /**
     * 最小连接池大小
     */
    private int minIdle = 20;

    /**
     * 最大连接池大小
     */
    private int maxActive = 20;

    /**
     * 获取连接等待超时的时间
     */
    private int maxWait = 60 * 1000;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private long timeBetweenEvictionRunsMillis = 60 * 1000;

    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private long minEvictableIdleTimeMillis = 300 * 1000;

    /**
     * 连接检测的语句
     */
    private String validationQuery = "SELECT 1";

    /**
     * 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
     */
    private boolean testWhileIdle = true;

    /**
     * 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
     */
    private boolean testOnBorrow = false;

    /**
     * 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
     */
    private boolean testOnReturn = false;

    private DruidDataSource dataSource = new DruidDataSource();

    private static class SingletonHolder {
        private static MysqlDataConnPool instance = new MysqlDataConnPool();
    }

    private MysqlDataConnPool() {
        init();
    }

    private void init() {
        if (dbConfig == null) {
            LOG.error("dbConfig can not be null");
            throw new RuntimeException("dbConfig can not be null");
        }
        jdbcUrl = dbConfig.get(ConfigConstant.JDBCURL);
        username = dbConfig.get(ConfigConstant.USERNAME);
        password = dbConfig.get(ConfigConstant.PASSWORD);

        dataSource.setDriverClassName(DRIVER_NAME);
        dataSource.setUrl(jdbcUrl);

        if (!Strings.isNullOrEmpty(username)) {
            dataSource.setUsername(username);

        }

        if (!Strings.isNullOrEmpty(password)) {
            dataSource.setPassword(password);
        }

        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);

        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setValidationQuery(validationQuery);

        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setTestWhileIdle(testWhileIdle);

        dataSource.setQueryTimeout(queryTimeOut);

        try {
            //初始化的时候检测一次
            DruidPooledConnection conn = dataSource.getConnection();
            conn.recycle();
        } catch (Exception e) {
            LOG.error("", e);
            throw new RuntimeException(e);
        }

        LOG.warn("----init mysql conn pool success");
    }


    public static MysqlDataConnPool getInstance(Map<String, String> dbConfig) {
        MysqlDataConnPool.dbConfig = dbConfig;
        return SingletonHolder.instance;
    }

    public Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }

}
