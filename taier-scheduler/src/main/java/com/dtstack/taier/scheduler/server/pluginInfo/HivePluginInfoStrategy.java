package com.dtstack.taier.scheduler.server.pluginInfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;

public class HivePluginInfoStrategy implements ComponentPluginInfoStrategy {
    @Override
    public JSONObject convertPluginInfo(JSONObject clusterConfigJson, Long clusterId, Integer deployMode) {
        JSONObject computePluginInfo = clusterConfigJson.getJSONObject(EComponentType.HIVE_SERVER.getConfName());
        String jdbcUrl = computePluginInfo.getString(ConfigConstant.JDBCURL);
        //%s替换成默认的 供插件使用
        jdbcUrl = jdbcUrl.replace("/%s", "/default");
        computePluginInfo.put(ConfigConstant.JDBCURL, jdbcUrl);
        return computePluginInfo;
    }

    @Override
    public EComponentType getComponentTypeCode() {
        return EComponentType.HIVE_SERVER;
    }
}
