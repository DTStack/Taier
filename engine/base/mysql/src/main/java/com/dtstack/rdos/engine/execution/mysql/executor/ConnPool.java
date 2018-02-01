package com.dtstack.rdos.engine.execution.mysql.executor;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.mysql.constant.ConfigConstant;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 连接池---使用Druid
 * Date: 2018/1/29
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ConnPool {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConnPool.class);

    private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";

    private String dbUrl;

    private String dbUserName;

    private String dbPwd;

    /**sql执行超时时间-单位秒*/
    private int queryTimeOut = 5 * 60;

    /**初始连接池大小*/
    private int initialSize = 2;

    /**最小连接池大小*/
    private int minIdle = 5;

    /**最大连接池大小*/
    private int maxActive = 20;

    /**获取连接等待超时的时间*/
    private int maxWait = 60 * 1000;

    /**配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒*/
    private long timeBetweenEvictionRunsMillis = 60 * 1000;

    /**配置一个连接在池中最小生存的时间，单位是毫秒*/
    private long minEvictableIdleTimeMillis = 300 * 1000;

    /**连接检测的语句*/
    private String validationQuery = "SELECT 1";

    /**申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。*/
    private boolean testWhileIdle = true;

    /**申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能*/
    private boolean testOnBorrow = false;

    /**归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能*/
    private boolean testOnReturn = false;

    private DruidDataSource dataSource = new DruidDataSource();

    private static ConnPool singleton = new ConnPool();

    private ConnPool(){
    }

    public void init(Properties properties){

        try{
            parseConfig(properties);
        }catch (Exception e){
            LOG.error("mysql 插件配置异常", e);
            System.exit(-1);
        }

        dataSource.setDriverClassName(DRIVER_NAME);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUserName);
        dataSource.setPassword(dbPwd);

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

        try{
            //初始化的时候检测一次
            DruidPooledConnection conn = dataSource.getConnection();
            conn.recycle();
        }catch (Exception e){
            LOG.error("", e);
            System.exit(-1);
        }

        LOG.warn("----init mysql conn pool success");
    }

    private void parseConfig(Properties properties){
        dbUrl = Preconditions.checkNotNull(properties.getProperty(ConfigConstant.DB_URL), "mysql 插件必须设置DBURL");
        dbUserName = Preconditions.checkNotNull(properties.getProperty(ConfigConstant.USER_NAME), "mysql 插件用户名必须设置");
        dbPwd = Preconditions.checkNotNull(properties.getProperty(ConfigConstant.PWD), "mysql 插件密码必须设置");

        queryTimeOut = MathUtil.getIntegerVal(properties.get(ConfigConstant.QUERY_TIMEOUT), queryTimeOut);
        initialSize = MathUtil.getIntegerVal(properties.get(ConfigConstant.INITIAL_SIZE), initialSize);
        minIdle = MathUtil.getIntegerVal(properties.get(ConfigConstant.MIN_IDLE), minIdle);
        maxActive = MathUtil.getIntegerVal(properties.get(ConfigConstant.MAX_ACTIVE), maxActive);
        maxWait = MathUtil.getIntegerVal(properties.get(ConfigConstant.MAX_WAIT), maxWait);
        timeBetweenEvictionRunsMillis = MathUtil.getLongVal(properties.get(ConfigConstant.TIME_BETWEEN_EVICTIONRUNS_MILLIS), timeBetweenEvictionRunsMillis);
        minEvictableIdleTimeMillis = MathUtil.getLongVal(properties.get(ConfigConstant.MIN_EVICTABLE_IDLETIME_MILLIS), minEvictableIdleTimeMillis);
        validationQuery = MathUtil.getString(properties.get(ConfigConstant.VALIDATION_QUERY), validationQuery);
        testWhileIdle = MathUtil.getBoolean(properties.get(ConfigConstant.TEST_WHILE_IDLE), testWhileIdle);
        testOnBorrow = MathUtil.getBoolean(properties.get(ConfigConstant.TEST_ON_BORROW), testOnBorrow);
        testOnReturn = MathUtil.getBoolean(properties.get(ConfigConstant.TEST_ON_RETURN), testOnReturn);
    }



    public static ConnPool getInstance(){
        return singleton;
    }

    public Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }

}
