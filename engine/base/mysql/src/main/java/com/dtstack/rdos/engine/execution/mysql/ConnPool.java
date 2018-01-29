package com.dtstack.rdos.engine.execution.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 连接池---使用Druid
 * Date: 2018/1/29
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ConnPool {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConnPool.class);

    private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";

    /**sql执行超时时间-单位秒*/
    private int queryTimeOut = 5 * 60;

    /**初始连接池大小*/
    private int initialSize = 1;

    /**最小连接池大小*/
    private int minIdle = 1;

    /**最大连接池大小*/
    private int maxActive = 20;

    /**获取连接等待超时的时间*/
    private int maxWait = 60 * 1000;

    /**配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒*/
    private int timeBetweenEvictionRunsMillis = 60 * 1000;

    /**配置一个连接在池中最小生存的时间，单位是毫秒*/
    private int minEvictableIdleTimeMillis = 300 * 1000;

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
        init();
    }

    private void init(){

        //TODO 初始化datasource设置
        dataSource.setDriverClassName(DRIVER_NAME);
        dataSource.setUrl("");
        dataSource.setUsername("");
        dataSource.setPassword("");

        //TODO---其他设置eg:maxsize,timeout...
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
            DruidPooledConnection conn = dataSource.getConnection();
            conn.recycle();
        }catch (Exception e){
            LOG.error("", e);
            System.exit(-1);
        }

        LOG.warn("----init mysql conn pool success");
    }

    public static ConnPool getInstance(){
        return singleton;
    }

    public Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }

}
