package com.dtstack.taier.scheduler.server.pluginInfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.RdosDefineException;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.TYPE_NAME;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.TYPE_NAME_KEY;

public class ScriptPluginInfoStrategy implements ComponentPluginInfoStrategy {

    @Override
    public JSONObject convertPluginInfo(JSONObject clusterConfigJson, Long clusterId, Integer deployMode) {
        JSONObject confConfig = clusterConfigJson.getJSONObject(EComponentType.SCRIPT.getConfName());
        String typeName = confConfig.getString(TYPE_NAME);
        if (!StringUtils.isBlank(typeName)) {
            clusterConfigJson.put(TYPE_NAME_KEY, typeName);
        }
        if (Objects.isNull(confConfig)) {
            throw new RdosDefineException(String.format("scriptConf is not configured"));
        }
        clusterConfigJson.remove(EComponentType.SCRIPT.getConfName());
        // put flat all script config
        clusterConfigJson.putAll(confConfig);
        return clusterConfigJson;
    }

    @Override
    public EComponentType getComponentTypeCode() {
        return EComponentType.SCRIPT;
    }
}