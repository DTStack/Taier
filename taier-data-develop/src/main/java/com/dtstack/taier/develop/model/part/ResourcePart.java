package com.dtstack.taier.develop.model.part;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentScheduleType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.develop.model.DataSource;
import com.dtstack.taier.develop.model.system.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResourcePart extends PartImpl {

    public ResourcePart(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup, Context context, DataSource dataSource) {
        super(componentType, versionName, storeType, componentScheduleGroup, context, dataSource, null);
    }

    @Override
    public String getVersionValue() {
        return context.getComponentModel(type).getVersionValue(versionName);
    }

    @Override
    public String getPluginName() {
        Optional<JSONObject> versionModelConfig = context.getModelConfig(type, versionName);
        return versionModelConfig.map(config -> config.getString(type.name())).orElse(null);
    }

    @Override
    public Long getExtraVersionParameters() {
        Optional<JSONObject> modelExtraVersionParameters = context.getModelExtraVersionParameters(type, versionName);
        return modelExtraVersionParameters.map(parameters -> parameters.getLong(type.name())).orElse(null);
    }
}
