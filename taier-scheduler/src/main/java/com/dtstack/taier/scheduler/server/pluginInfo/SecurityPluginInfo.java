package com.dtstack.taier.scheduler.server.pluginInfo;

import com.alibaba.fastjson.JSONObject;

public interface SecurityPluginInfo {

    JSONObject configSecurity(JSONObject clusterConfigJson, Long clusterId, Integer deployMode);
}
