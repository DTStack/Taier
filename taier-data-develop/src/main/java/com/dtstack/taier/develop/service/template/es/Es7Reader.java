package com.dtstack.taier.develop.service.template.es;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.service.template.BaseReaderPlugin;
import com.dtstack.taier.develop.service.template.PluginName;

import java.util.List;

/**
 * @author daojin
 */
public class Es7Reader extends BaseReaderPlugin {

    private List<String> hosts;
    private String username;
    private String password;
    private JSONObject query;
    private String index;
    private List column;
    private Integer batchSize;
    private SslConfig sslConfig;
    private List<Long> sourceIds;

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
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

    public JSONObject getQuery() {
        return query;
    }

    public void setQuery(JSONObject query) {
        this.query = query;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public List getColumn() {
        return column;
    }

    public void setColumn(List column) {
        this.column = column;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public SslConfig getSslConfig() {
        return sslConfig;
    }

    public void setSslConfig(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
    }

    public List<Long> getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(List<Long> sourceIds) {
        this.sourceIds = sourceIds;
    }

    @Override
    public String pluginName() {
        return PluginName.ES7_R;
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
