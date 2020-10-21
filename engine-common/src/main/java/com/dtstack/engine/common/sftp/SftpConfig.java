package com.dtstack.engine.common.sftp;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/10/21
 */
public class SftpConfig {

    private String host;
    private Integer port;
    private String username;
    private String path;
    private Integer maxTotal = 16;
    private Integer maxIdle = 16;
    private Integer minIdle = 16;
    private Integer timeout = 0;
    private boolean isUsePool;
    private Long fileTimeout = 300000L;
    private Integer auth;
    private String password;
    private String rsaPath;
    private Long maxWaitMillis = 1000L * 60L * 60L;
    private Long minEvictableIdleTimeMillis =  -1L;
    private Long softMinEvictableIdleTimeMillis =  1000L * 60L * 30L;
    private Long timeBetweenEvictionRunsMillis =  1000L * 60L * 5L;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public boolean getIsUsePool() {
        return isUsePool;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public void setIsUsePool(boolean isUsePool) {
        this.isUsePool = isUsePool;
    }

    public Long getFileTimeout() {
        return fileTimeout;
    }

    public void setFileTimeout(Long fileTimeout) {
        this.fileTimeout = fileTimeout;
    }

    public Integer getAuth() {
        return auth;
    }

    public void setAuth(Integer auth) {
        this.auth = auth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRsaPath() {
        return rsaPath;
    }

    public void setRsaPath(String rsaPath) {
        this.rsaPath = rsaPath;
    }

    public boolean isUsePool() {
        return isUsePool;
    }

    public void setUsePool(boolean usePool) {
        isUsePool = usePool;
    }

    public Long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(Long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public Long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(Long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public Long getSoftMinEvictableIdleTimeMillis() {
        return softMinEvictableIdleTimeMillis;
    }

    public void setSoftMinEvictableIdleTimeMillis(Long softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }

    public Long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(Long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

}
