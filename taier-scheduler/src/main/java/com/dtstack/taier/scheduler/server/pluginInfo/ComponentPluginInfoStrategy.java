package com.dtstack.taier.scheduler.server.pluginInfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;

public interface ComponentPluginInfoStrategy {

    JSONObject convertPluginInfo(JSONObject clusterConfigJson, Long clusterId, Integer deployMode);

    EComponentType getComponentTypeCode();
}
