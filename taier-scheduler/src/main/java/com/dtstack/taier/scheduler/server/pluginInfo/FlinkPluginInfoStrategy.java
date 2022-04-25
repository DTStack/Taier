package com.dtstack.taier.scheduler.server.pluginInfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.TYPE_NAME;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.TYPE_NAME_KEY;

public class FlinkPluginInfoStrategy implements ComponentPluginInfoStrategy {

    @Override
    public JSONObject convertPluginInfo(JSONObject clusterConfigJson, Long clusterId, Integer deployMode) {
        //默认为session
        EDeployMode deploy = deployMode == null ? EDeployMode.SESSION : EDeployMode.getByType(deployMode);
        JSONObject confConfig = clusterConfigJson.getJSONObject(EComponentType.FLINK.getConfName());
        String typeName = confConfig.getString(TYPE_NAME);
        if (!StringUtils.isBlank(typeName)) {
            clusterConfigJson.put(TYPE_NAME_KEY, typeName);
        }
        confConfig = confConfig.getJSONObject(deploy.getMode());
        if (Objects.isNull(confConfig)) {
            throw new RdosDefineException(String.format("Flink Corresponding mode [%s] no information is configured", deploy.name()));
        }
        clusterConfigJson.putAll(confConfig);
        clusterConfigJson.remove(EComponentType.FLINK.getConfName());
        return clusterConfigJson;
    }

    @Override
    public EComponentType getComponentTypeCode() {
        return EComponentType.FLINK;
    }
}
