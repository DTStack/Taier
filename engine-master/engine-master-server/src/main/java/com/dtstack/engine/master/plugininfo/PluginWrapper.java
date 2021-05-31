package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.utils.TaskParamsUtil;
import com.dtstack.schedule.common.enums.Deleted;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PluginWrapper{

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginWrapper.class);

    private static final String PARAMS_DELIM = "&";
    private static final String URI_PARAMS_DELIM = "?";
    private static final String LADP_USER_NAME = "ldapUserName";
    private static final String LADP_PASSWORD = "ldapPassword";
    private static final String DB_NAME = "dbName";
    private static final String CLUSTER = "cluster";
    private static final String DEPLOY_MODEL = "deployMode";
    private static final String QUEUE = "queue";
    private static final String NAMESPACE = "namespace";
    public static final String PLUGIN_INFO = "pluginInfo";

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleDictService scheduleDictService;

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
        if (null == deployMode && ScheduleEngineType.Flink.getEngineName().equalsIgnoreCase(engineType)) {
            //解析参数
            deployMode = TaskParamsUtil.parseDeployTypeByTaskParams(action.getTaskParams(),action.getComputeType(), EngineType.Flink.name()).getType();

        }
        // 需要传入组件版本,涉及Null值构建Map需要检验Null兼容
        JSONObject pluginInfoJson = clusterService.pluginInfoJSON(tenantId, engineType, action.getUserId(),deployMode,
                Collections.singletonMap(EngineTypeComponentType.engineName2ComponentType(engineType),action.getComponentVersion()));
        String groupName = ConfigConstant.DEFAULT_GROUP_NAME;
        action.setGroupName(groupName);
        if (null != pluginInfoJson && !pluginInfoJson.isEmpty()) {
            addParamsToJdbcUrl(actionParam, pluginInfoJson);
            addUserNameToHadoop(pluginInfoJson, ldapUserName);
            addUserNameToImpalaOrHive(pluginInfoJson, ldapUserName, ldapPassword, dbName, engineType);

            String clusterName = pluginInfoJson.getString(CLUSTER);
            String queue = pluginInfoJson.getString(QUEUE);
            String namespace = pluginInfoJson.getString(NAMESPACE);
            if (StringUtils.isNotEmpty(queue)) {
                groupName = String.format("%s_%s", clusterName, queue);
            } else if (StringUtils.isNotEmpty(namespace)) {
                groupName = String.format("%s_%s", clusterName, namespace);
            }
            action.setGroupName(groupName);
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
        Integer appType = MapUtils.getInteger(actionParam, "appType");
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId, Deleted.NORMAL.getStatus());
        if(null == scheduleJob || null == appType){
            LOGGER.info("dbUrl {} jobId {} appType or scheduleJob is null",dbUrl,jobId);
            return;
        }
        JSONObject info = JSONObject.parseObject(scheduleTaskShadeDao.getExtInfoByTaskId(scheduleJob.getTaskId(), appType));
        if(null == info){
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

        if (MultiEngineType.TIDB.getName().equalsIgnoreCase((String) actionParam.get("engineType"))) {
            //TiDB 没有currentSchema
            String currentSchema = paramsJson.getString("currentSchema");
            if(StringUtils.isBlank(currentSchema)){
                throw new RdosDefineException("tidb currentSchema Not allowed to be empty");
            }
            if (dbUrl.endsWith(currentSchema)) {
                pluginInfoJson.put("jdbcUrl", dbUrl);
                return;
            } else if (dbUrl.endsWith("/")) {
                pluginInfoJson.put("jdbcUrl", dbUrl + currentSchema);
                return;
            }

            throw new RdosDefineException("tidb jdbcUrl The parameter is invalid need / end");
        }

        if (MultiEngineType.PRESTO.getName().equalsIgnoreCase((String)actionParam.get("engineType"))){
            pluginInfoJson.put("jdbcUrl", dbUrl);
            return;
        }

        if (MultiEngineType.GREENPLUM.getName().equalsIgnoreCase((String)actionParam.get("engineType"))){
            pluginInfoJson.put("jdbcUrl", dbUrl);
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

        pluginInfoJson.put("username", userName);
        pluginInfoJson.put("password", password);
        pluginInfoJson.put("jdbcUrl", String.format(pluginInfoJson.getString("jdbcUrl"), dbName));
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

/*
    *//**
     * 回写任务执行的插件信息 到cache表
     * @param jobId
     * @param pluginInfo
     *//*
    public void savePluginInfoToDB(String jobId, String pluginInfo) {
        if (StringUtils.isEmpty(jobId) || StringUtils.isEmpty(pluginInfo)) {
            return;
        }
        EngineJobCache jobCache = engineJobCacheDao.getOne(jobId);
        if (null == jobCache) {
            return;
        }
        JSONObject dbPluginInfo = JSONObject.parseObject(jobCache.getJobInfo());
        if (null == dbPluginInfo) {
            dbPluginInfo = new JSONObject();
        }
        if (dbPluginInfo.containsKey(PLUGIN_INFO) && !dbPluginInfo.getJSONObject(PLUGIN_INFO).isEmpty()) {
            logger.info("jobId {} contains pluginInfo  {} not save", jobId, dbPluginInfo.getJSONObject(PLUGIN_INFO));
            return;
        }
        dbPluginInfo.put(PLUGIN_INFO, JSONObject.parseObject(pluginInfo,Map.class));
        engineJobCacheDao.updateJobInfo(dbPluginInfo.toJSONString(), jobId);
    }*/

    public String getPluginInfo(String taskParams, Integer computeType, String engineType, Long tenantId, Long userId, Map<String, String> pluginInfoCache) {
        try {
            Integer deployMode = null;
            if (ScheduleEngineType.Flink.getEngineName().equalsIgnoreCase(engineType)) {
                //解析参数
                deployMode = TaskParamsUtil.parseDeployTypeByTaskParams(taskParams, computeType,EngineType.Flink.name()).getType();
            }

            String cacheKey = String.format("%s.%s.%s.%s", tenantId, engineType, userId, deployMode);
            Integer finalDeployMode = deployMode;
            return pluginInfoCache.computeIfAbsent(cacheKey, (k) -> {
                JSONObject infoJSON = clusterService.pluginInfoJSON(tenantId, engineType, userId, finalDeployMode,null);
                if (Objects.nonNull(infoJSON)) {
                    return infoJSON.toJSONString();
                }
                return "";
            });

        } catch (Exception e) {
            LOGGER.error("getPluginInfo tenantId {} engineType {} error ", tenantId, engineType);
        }
        return "";
    }
}
