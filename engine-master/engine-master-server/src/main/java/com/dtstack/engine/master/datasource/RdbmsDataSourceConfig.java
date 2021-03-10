package com.dtstack.engine.master.datasource;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.master.utils.JdbcUrlUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author chener
 * @Classname RdbmsDataSourceConfig
 * @Description 关系型数据源配置信息(jdbc)
 * @Date 2020/10/23 15:13
 * @Created chener@dtstack.com
 */
public class RdbmsDataSourceConfig extends DataSourceConfig {

    private String jdbc;

    private String user;

    private String pass;

    public String getJdbc() {
        return jdbc;
    }

    public void setJdbc(String jdbc) {
        this.jdbc = jdbc;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String getConfigJson() {
        Map<String,String> configMap = new HashedMap();
        configMap.put("jdbc",getJdbc());
        configMap.put("user",getUser());
        configMap.put("pass",getPass());
        return JSONObject.toJSONString(configMap);
    }

    @Override
    public String generateRealSourceKey() {
        if (StringUtils.isEmpty(jdbc)){
            return null;
        }
        UrlInfo urlInfo = JdbcUrlUtil.getUrlInfo(jdbc);
        return String.format("%s#%s",urlInfo.getHost(),urlInfo.getPort());
    }
}
