package com.dtstack.taier.develop.service.template.es;

import com.alibaba.fastjson.JSONObject;

/**
 * @author daojin
 */
public class EsReaderParam extends EsBaseParam {

    private JSONObject query;
    private String username;
    private String password;
    private SslConfig sslConfig;

    public JSONObject getQuery() {
        return query;
    }

    public void setQuery(JSONObject query) {
        this.query = query;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SslConfig getSslConfig() {
        return sslConfig;
    }

    public void setSslConfig(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
    }
}
