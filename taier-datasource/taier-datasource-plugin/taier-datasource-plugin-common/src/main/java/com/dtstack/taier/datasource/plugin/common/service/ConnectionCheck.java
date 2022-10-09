package com.dtstack.taier.datasource.plugin.common.service;

import java.sql.Connection;

/**
 * connection check
 *
 * @author ：wangchuan
 * date：Created in 14:38 2022/9/27
 * company: www.dtstack.com
 */
public class ConnectionCheck {

    /**
     * conn
     */
    private Connection connection;

    /**
     * 放入的时间戳
     */
    private long timestamp;

    /**
     * 是否外部获取
     */
    private boolean externalObtain;

    /**
     * 过期时间
     */
    private long expireTime;

    /**
     * 数据源类型
     */
    private Integer sourceType;

    public ConnectionCheck() {
    }

    public ConnectionCheck(Connection connection, long timestamp,
                           boolean externalObtain, long expireTime, Integer sourceType) {
        this.connection = connection;
        this.timestamp = timestamp;
        this.externalObtain = externalObtain;
        this.expireTime = expireTime;
        this.sourceType = sourceType;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isExternalObtain() {
        return externalObtain;
    }

    public void setExternalObtain(boolean externalObtain) {
        this.externalObtain = externalObtain;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }
}
