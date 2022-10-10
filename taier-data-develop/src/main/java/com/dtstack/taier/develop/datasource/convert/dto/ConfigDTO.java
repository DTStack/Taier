package com.dtstack.taier.develop.datasource.convert.dto;

import com.dtstack.taier.datasource.api.dto.SSLConfig;

import java.util.Map;

/**
 * datasourceX 配置类, 包括 kerberos、 sftp、ssl 等
 *
 * @author ：wangchuan
 * date：Created in 下午5:15 2022/2/28
 * company: www.dtstack.com
 */
public class ConfigDTO {

    /**
     * kerberos 配置
     */
    private Map<String, Object> kerberosConfig;

    /**
     * sftp 配置
     */
    private Map<String, String> sftpConf;

    /**
     * 扩展配置
     */
    private Map<String, Object> expendConfig;

    /**
     * ssl 配置
     */
    private SSLConfig sslConfig;

    /**
     * schema 信息
     */
    private String schema;

    public ConfigDTO() {
    }

    public Map<String, Object> getKerberosConfig() {
        return kerberosConfig;
    }

    public void setKerberosConfig(Map<String, Object> kerberosConfig) {
        this.kerberosConfig = kerberosConfig;
    }

    public Map<String, String> getSftpConf() {
        return sftpConf;
    }

    public void setSftpConf(Map<String, String> sftpConf) {
        this.sftpConf = sftpConf;
    }

    public Map<String, Object> getExpendConfig() {
        return expendConfig;
    }

    public void setExpendConfig(Map<String, Object> expendConfig) {
        this.expendConfig = expendConfig;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public SSLConfig getSslConfig() {
        return sslConfig;
    }

    public void setSslConfig(SSLConfig sslConfig) {
        this.sslConfig = sslConfig;
    }
}
