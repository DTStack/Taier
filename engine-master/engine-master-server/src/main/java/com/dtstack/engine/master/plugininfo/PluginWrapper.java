package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.enums.ScheduleEngineType;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class PluginWrapper{

    private static final Logger logger = LoggerFactory.getLogger(PluginWrapper.class);

    private static final String PARAMS_DELIM = "&";
    private static final String URI_PARAMS_DELIM = "?";
    private static final String DEFAULT_GROUP_NAME = "default_default";
    private static final String LADP_USER_NAME = "ldapUserName";
    private static final String LADP_PASSWORD = "ldapPassword";
    private static final String DB_NAME = "dbName";
    private static final String CLUSTER = "cluster";
    private static final String PLUGIN_INFO = "pluginInfo";
    private static final String DEPLOY_MODEL = "deployMode";
    private static final String QUEUE = "queue";
    private static final String GROUP_NAME = "groupName";

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    public Map<String, Object> wrapperPluginInfo(ParamAction action) throws Exception{

        Map actionParam = PublicUtil.objectToMap(action);

        String ldapUserName = MapUtils.getString(actionParam, LADP_USER_NAME);
        String ldapPassword = MapUtils.getString(actionParam, LADP_PASSWORD);
        Integer deployMode = MapUtils.getInteger(actionParam,DEPLOY_MODEL);
        String dbName = MapUtils.getString(actionParam, DB_NAME);

        if (StringUtils.isNotBlank(ldapUserName)) {
            actionParam.remove(LADP_USER_NAME);
            actionParam.remove(LADP_PASSWORD);
            actionParam.remove(DB_NAME);
        }

        Long tenantId = action.getTenantId();
        String engineType = action.getEngineType();
        if(Objects.isNull(deployMode) && ScheduleEngineType.Flink.getEngineName().equalsIgnoreCase(engineType)){
            //解析参数
            deployMode = scheduleJobService.parseDeployTypeByTaskParams(action.getTaskParams()).getType();
        }
        JSONObject pluginInfoJson = clusterService.pluginInfoJSON(tenantId, engineType, action.getUserId(),deployMode);
        action.setGroupName(DEFAULT_GROUP_NAME);
        if (Objects.nonNull(pluginInfoJson) && !pluginInfoJson.isEmpty()) {
            addParamsToJdbcUrl(actionParam, pluginInfoJson);
            addUserNameToHadoop(pluginInfoJson, ldapUserName);
            addUserNameToImpalaOrHive(pluginInfoJson, ldapUserName, ldapPassword, dbName, engineType);
            action.setGroupName(pluginInfoJson.getString(CLUSTER) + "_" + pluginInfoJson.getString(QUEUE));
        }

        return pluginInfoJson;
    }

    private void addParamsToJdbcUrl(Map<String, Object> actionParam, JSONObject pluginInfoJson){
        if(pluginInfoJson == null || actionParam == null){
            return;
        }

        String dbUrl = pluginInfoJson.getString("jdbcUrl");
        if(org.apache.commons.lang3.StringUtils.isEmpty(dbUrl)){
            return;
        }

        String jobId = (String)actionParam.get("taskId");
        String appType = (String)actionParam.getOrDefault("appType", AppType.RDOS.getType());
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId, Deleted.NORMAL.getStatus());
        if(Objects.isNull(scheduleJob)){
            return;
        }
        if(Objects.isNull(appType)){
            appType = AppType.RDOS.getType() + "";
        }
        JSONObject info = JSONObject.parseObject(scheduleTaskShadeDao.getExtInfoByTaskId(scheduleJob.getTaskId(), Integer.valueOf(appType)));
        if(Objects.isNull(info)){
            return;
        }

        JSONObject paramsJson = info.getJSONObject("info").getJSONObject("jdbcUrlParams");
        if(paramsJson == null || paramsJson.isEmpty()){
            return;
        }

        String paramsStr = getParamsString(dbUrl, paramsJson);

        if(MultiEngineType.ORACLE.getName().equalsIgnoreCase((String)actionParam.get("engineType"))){
            //oracle不包含schema
            pluginInfoJson.put("jdbcUrl", dbUrl);
            return;
        }

        if(MultiEngineType.TIDB.getName().equalsIgnoreCase((String)actionParam.get("engineType"))){
            //TiDB 没有currentSchema
            pluginInfoJson.put("jdbcUrl", dbUrl  + paramsJson.getString("currentSchema"));
            return;
        }

        if (MultiEngineType.GREENPLUM.getName().equalsIgnoreCase((String)actionParam.get("engineType"))){
            pluginInfoJson.put("dbUrl", dbUrl);
            return;
        }

        if(dbUrl.contains(URI_PARAMS_DELIM)){
            dbUrl = dbUrl.split("\\?")[0];
        }

        dbUrl = dbUrl + URI_PARAMS_DELIM + paramsStr;
        pluginInfoJson.put("jdbcUrl", dbUrl);
    }

    private void addUserNameToImpalaOrHive(JSONObject pluginInfoJson, String userName, String password, String dbName, String engineType) {
        if(pluginInfoJson == null || org.apache.commons.lang3.StringUtils.isBlank(userName) || (!ScheduleEngineType.IMPALA.getEngineName().equals(engineType) && !ScheduleEngineType.HIVE.getEngineName().equals(engineType))){
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

    private String getParamsString(String dbUrl, Map paramsJson){
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
