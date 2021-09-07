package com.dtstack.engine.common.sftp;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/10/21
 */
public class SftpConfig {

    //SFTP主机地址
    private String host;
    //SFTP端口地址
    private Integer port;
    //主机用户名
    private String username;
    //主机文件路径
    private String path;
    /**
     * 连接池最多可获取的连接数量
     * 例：10个线程同时访问maxTotal设置为5的连接池，5个得到连接，其他等待。
     */
    private Integer maxTotal = 16;
    /**
     * 允许留存空闲的最大连接数量
     * 测试表明设置maxIdle=16也只能保留8个连接
     * 若设置maxIdle=0那么就不保留连接。
     */
    private Integer maxIdle = 16;
    /**
     * 允许留存空闲的最小连接数量
     * 实际保留的连接数量会在minIdle和maxIdle之间。
     */
    private Integer minIdle = 16;
    /**
     * 当minIdle < maxIdle产生作用，若超出timeout设置得时间，减少留存的空闲连接，但是不小于minIdle。
     */
    private Integer timeout = 0;
    //是否使用连接池
    private boolean isUsePool;
    private Long fileTimeout = 300000L;
    private Integer auth;
    private String password;
    private String rsaPath;
    private Long maxWaitMillis = 1000L * 60L * 60L;
    private Long minEvictableIdleTimeMillis = -1L;
    private Long softMinEvictableIdleTimeMillis = 1000L * 60L * 30L;
    private Long timeBetweenEvictionRunsMillis = 1000L * 60L * 5L;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SftpConfig that = (SftpConfig) o;

        if (isUsePool != that.isUsePool) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (port != null ? !port.equals(that.port) : that.port != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (maxTotal != null ? !maxTotal.equals(that.maxTotal) : that.maxTotal != null) return false;
        if (maxIdle != null ? !maxIdle.equals(that.maxIdle) : that.maxIdle != null) return false;
        if (minIdle != null ? !minIdle.equals(that.minIdle) : that.minIdle != null) return false;
        if (timeout != null ? !timeout.equals(that.timeout) : that.timeout != null) return false;
        if (fileTimeout != null ? !fileTimeout.equals(that.fileTimeout) : that.fileTimeout != null) return false;
        if (auth != null ? !auth.equals(that.auth) : that.auth != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (rsaPath != null ? !rsaPath.equals(that.rsaPath) : that.rsaPath != null) return false;
        if (maxWaitMillis != null ? !maxWaitMillis.equals(that.maxWaitMillis) : that.maxWaitMillis != null)
            return false;
        if (minEvictableIdleTimeMillis != null ? !minEvictableIdleTimeMillis.equals(that.minEvictableIdleTimeMillis) : that.minEvictableIdleTimeMillis != null)
            return false;
        if (softMinEvictableIdleTimeMillis != null ? !softMinEvictableIdleTimeMillis.equals(that.softMinEvictableIdleTimeMillis) : that.softMinEvictableIdleTimeMillis != null)
            return false;
        return timeBetweenEvictionRunsMillis != null ? timeBetweenEvictionRunsMillis.equals(that.timeBetweenEvictionRunsMillis) : that.timeBetweenEvictionRunsMillis == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (maxTotal != null ? maxTotal.hashCode() : 0);
        result = 31 * result + (maxIdle != null ? maxIdle.hashCode() : 0);
        result = 31 * result + (minIdle != null ? minIdle.hashCode() : 0);
        result = 31 * result + (timeout != null ? timeout.hashCode() : 0);
        result = 31 * result + (isUsePool ? 1 : 0);
        result = 31 * result + (fileTimeout != null ? fileTimeout.hashCode() : 0);
        result = 31 * result + (auth != null ? auth.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (rsaPath != null ? rsaPath.hashCode() : 0);
        result = 31 * result + (maxWaitMillis != null ? maxWaitMillis.hashCode() : 0);
        result = 31 * result + (minEvictableIdleTimeMillis != null ? minEvictableIdleTimeMillis.hashCode() : 0);
        result = 31 * result + (softMinEvictableIdleTimeMillis != null ? softMinEvictableIdleTimeMillis.hashCode() : 0);
        result = 31 * result + (timeBetweenEvictionRunsMillis != null ? timeBetweenEvictionRunsMillis.hashCode() : 0);
        return result;
    }
}
