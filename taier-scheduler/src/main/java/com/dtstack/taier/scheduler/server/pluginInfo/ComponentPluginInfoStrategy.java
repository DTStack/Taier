package com.dtstack.taier.scheduler.server.pluginInfo;

import com.alibaba.fastjson.JSONObject;

public interface ComponentPluginInfoStrategy {

    JSONObject convertPluginInfo(JSONObject clusterConfigJson, Long clusterId, Integer deployMode);
}
