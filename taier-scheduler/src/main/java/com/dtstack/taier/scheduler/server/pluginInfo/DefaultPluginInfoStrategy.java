package com.dtstack.taier.scheduler.server.pluginInfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;

public class DefaultPluginInfoStrategy implements ComponentPluginInfoStrategy {

    private EComponentType componentType;

    public DefaultPluginInfoStrategy(EComponentType componentType) {
        this.componentType = componentType;
    }

    @Override
    public JSONObject convertPluginInfo(JSONObject clusterConfigJson, Long clusterId, Integer deployMode) {
        return clusterConfigJson;
    }

    @Override
    public EComponentType getComponentTypeCode() {
        return componentType;
    }
}
