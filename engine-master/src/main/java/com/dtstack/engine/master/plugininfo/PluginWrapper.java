package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.EngineType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ClusterService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PluginWrapper{

    private static final Logger logger = LoggerFactory.getLogger(PluginWrapper.class);

    private static final String PARAMS_DELIM = "&";
    private static final String URI_PARAMS_DELIM = "?";
    private static final String TENANT_ID = "tenantId";
    private static final String ENGINE_TYPE = "engineType";
    private static final String DEFAULT_GROUP_NAME = "default_default";
    private static final String LADP_USER_NAME = "ldapUserName";
    private static final String LADP_PASSWORD = "ldapPassword";
    private static final String DB_NAME = "dbName";
    private static final String CLUSTER = "cluster";
    private static final String PLUGIN_INFO = "pluginInfo";
    private static final String QUEUE = "queue";
    private static final String GROUP_NAME = "groupName";

    @Autowired
    private ClusterService clusterService;

    public Map<String, Object> wrapperPluginInfo(Map<String, Object> actionParam){

        String ldapUserName = MapUtils.getString(actionParam, LADP_USER_NAME);
        String ldapPassword = MapUtils.getString(actionParam, LADP_PASSWORD);
        String dbName = MapUtils.getString(actionParam, DB_NAME);

        if (StringUtils.isNotBlank(ldapUserName)) {
            actionParam.remove(LADP_USER_NAME);
            actionParam.remove(LADP_PASSWORD);
            actionParam.remove(DB_NAME);
        }

        Long tenantId = MapUtils.getLong(actionParam, TENANT_ID);
        String engineType = MapUtils.getString(actionParam, ENGINE_TYPE);
        JSONObject pluginInfoJson = clusterService.pluginInfoJSON(tenantId, engineType);
        String groupName = DEFAULT_GROUP_NAME;
        if (pluginInfoJson == null) {
            throw new RdosDefineException("pluginInfo not be null");
        }
        if (pluginInfoJson.isEmpty()) {
            addParamsToJdbcUrl(actionParam, pluginInfoJson);
            addUserNameToHadoop(pluginInfoJson, ldapUserName);
            addUserNameToImpalaOrHive(pluginInfoJson, ldapUserName, ldapPassword, dbName, engineType);
            actionParam.put(PLUGIN_INFO, pluginInfoJson);
            groupName = pluginInfoJson.getString(CLUSTER) + "_" + pluginInfoJson.getString(QUEUE);
            actionParam.put(GROUP_NAME, groupName);
        }

        actionParam.put(PLUGIN_INFO, pluginInfoJson);
        actionParam.put(GROUP_NAME, groupName);

        return actionParam;
    }

    private void addParamsToJdbcUrl(Map<String, Object> actionParam, JSONObject pluginInfoJson){
        if(pluginInfoJson == null || actionParam == null){
            return;
        }

        String dbUrl = pluginInfoJson.getString("dbUrl");
        if(org.apache.commons.lang3.StringUtils.isEmpty(dbUrl)){
            return;
        }

        String paramsJsonStr = MapUtils.getString(actionParam, "jdbcUrlParams");
        JSONObject paramsJson = JSONObject.parseObject(paramsJsonStr);
        if(paramsJson == null || paramsJson.isEmpty()){
            return;
        }

        String paramsStr = getParamsString(dbUrl, paramsJson);

        if(dbUrl.contains(URI_PARAMS_DELIM)){
            dbUrl = dbUrl.split("\\?")[0];
        }

        dbUrl = dbUrl + URI_PARAMS_DELIM + paramsStr;
        pluginInfoJson.put("dbUrl", dbUrl);
    }

    private void addUserNameToImpalaOrHive(JSONObject pluginInfoJson, String userName, String password, String dbName, String engineType) {
        if(pluginInfoJson == null || org.apache.commons.lang3.StringUtils.isBlank(userName) || (!EngineType.IMPALA.getEngineName().equals(engineType) && !EngineType.HIVE.getEngineName().equals(engineType))){
            return;
        }

        pluginInfoJson.put("userName", userName);
        pluginInfoJson.put("pwd", password);
        pluginInfoJson.put("dbUrl", String.format(pluginInfoJson.getString("dbUrl"), dbName));
    }


    private void addUserNameToHadoop(JSONObject pluginInfoJson, String userName){
        if(pluginInfoJson == null || org.apache.commons.lang3.StringUtils.isBlank(userName)){
            return;
        }

        JSONObject hadoopConfig = pluginInfoJson.getJSONObject("hadoopConf");
        if(hadoopConfig == null){
            return;
        }

        hadoopConfig.put("hadoop.user.name", userName);
    }

    private String getParamsString(String dbUrl, JSONObject paramsJson){
        if(dbUrl.contains(URI_PARAMS_DELIM)){
            String paramsStr = dbUrl.split("\\?")[1];
            for (String param : paramsStr.split(PARAMS_DELIM)) {
                String[] paramsSplit = param.split("=");
                if(!paramsJson.containsKey(paramsSplit[0])){
                    paramsJson.put(paramsSplit[0], paramsSplit[1]);
                }
            }
        }

        List<String> paramsList = new ArrayList<>();
        paramsJson.forEach((key,value) -> paramsList.add(String.format("%s=%s", key, value)));
        return org.apache.commons.lang3.StringUtils.join(paramsList, PARAMS_DELIM);
    }
}
