package com.dtstack.taier.develop.datasource.convert.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * @author ：nanqi
 * date：Created in 下午5:15 2021/7/29
 * company: www.dtstack.com
 */
public class PluginInfoDTO implements Serializable {
    /**
     * JDBC 地址
     */
    private String jdbcUrl;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * Kerberos 参数
     */
    private JSONObject kerberosConfig;

    /**
     * 存储类型
     */
    private Integer storeType;

    /**
     * HDFS 高可用配置
     */
    private String hadoopConfig;

    /**
     * defaultFs 信息
     */
    private String defaultFs;

    /**
     * 版本
     */
    private String version;

    /**
     * 查询最大条数
     */
    private Integer maxRows;

    /**
     * 查询超时时间
     */
    private Integer queryTimeout;

    /**
     * ssl信息
     */
    private JSONObject sslClient;

    @JSONField(name = "hive.proxy.enable")
    private boolean hiveProxyEnable;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public JSONObject getKerberosConfig() {
        return kerberosConfig;
    }

    public void setKerberosConfig(JSONObject kerberosConfig) {
        this.kerberosConfig = kerberosConfig;
    }

    public Integer getStoreType() {
        return storeType;
    }

    public void setStoreType(Integer storeType) {
        this.storeType = storeType;
    }

    public String getHadoopConfig() {
        return hadoopConfig;
    }

    public void setHadoopConfig(String hadoopConfig) {
        this.hadoopConfig = hadoopConfig;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
    }

    public Integer getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(Integer queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public String getDefaultFs() {
        return defaultFs;
    }

    public void setDefaultFs(String defaultFs) {
        this.defaultFs = defaultFs;
    }

    public JSONObject getSslClient() {
        return sslClient;
    }

    public void setSslClient(JSONObject sslClient) {
        this.sslClient = sslClient;
    }

    public boolean getHiveProxyEnable() {
        return hiveProxyEnable;
    }

    public void setHiveProxyEnable(boolean hiveProxyEnable) {
        this.hiveProxyEnable = hiveProxyEnable;
    }
}
