package com.dtstack.taier.develop.service.template.sqlserver;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.service.template.BaseReaderPlugin;
import com.dtstack.taier.develop.service.template.PluginName;

import java.util.List;

/**
 * Date: 2020/2/20
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class SqlServerCdcReader extends BaseReaderPlugin {
    private String username;
    private String password;
    private String url;
    private String databaseName;
    private List<String> tableList;
    private String cat;
    private Boolean pavingData;
    private Long pollInterval;
    private String lsn;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public List<String> getTableList() {
        return tableList;
    }

    public void setTableList(List<String> tableList) {
        this.tableList = tableList;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public Boolean getPavingData() {
        return pavingData;
    }

    public void setPavingData(Boolean pavingData) {
        this.pavingData = pavingData;
    }

    public Long getPollInterval() {
        return pollInterval;
    }

    public void setPollInterval(Long pollInterval) {
        this.pollInterval = pollInterval;
    }

    public String getLsn() {
        return lsn;
    }

    public void setLsn(String lsn) {
        this.lsn = lsn;
    }

    @Override
    public String pluginName() {
        return PluginName.SQLSERVER_CDC_R;
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
