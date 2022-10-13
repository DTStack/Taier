package com.dtstack.taier.develop.model.part;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentScheduleType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.develop.model.DataSource;
import com.dtstack.taier.develop.model.system.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StoragePart extends PartImpl {

    public StoragePart(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup, Context context, DataSource dataSource) {
        super(componentType, versionName, storeType, componentScheduleGroup, context, dataSource, null);
    }

    @Override
    public String getPluginName() {
        List<Component> components = componentScheduleGroup.get(EComponentScheduleType.RESOURCE);
        if (CollectionUtils.isEmpty(components)) {
            throw new RdosDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
        }
        Component resourceComponent = components.get(0);
        String resourceVersion = resourceComponent.getVersionName();
        EComponentType resourceType = EComponentType.getByCode(resourceComponent.getComponentTypeCode());
        Optional<JSONObject> resourceModelConfig = context.getModelConfig(resourceType, resourceVersion);
        JSONObject storageModelConfig = resourceModelConfig.map(res -> res.getJSONObject(type.name())).orElseThrow(() ->
                new RdosDefineException(Strings.format(ErrorCode.RESOURCE_NOT_SUPPORT_COMPONENT_VERSION.getMsg(), resourceComponent.getComponentName(), type.name(), versionName))
        );
        return storageModelConfig.getString(type.name());
    }

    @Override
    public String getVersionValue() {
        //special hdfs same as yarn
        if (StringUtils.isBlank(versionName) && EComponentType.HDFS.equals(type)) {
            Component resourceComponent = componentScheduleGroup.get(EComponentScheduleType.RESOURCE).get(0);
            versionName = resourceComponent.getVersionName();
        }
        return context.getComponentModel(type).getVersionValue(versionName);
    }

    @Override
    public Long getExtraVersionParameters() {
        Component resourceComponent = componentScheduleGroup.get(EComponentScheduleType.RESOURCE).get(0);
        if (null == resourceComponent) {
            throw new RdosDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
        }
        String resourceVersion = resourceComponent.getVersionName();
        EComponentType resourceType = EComponentType.getByCode(resourceComponent.getComponentTypeCode());
        Optional<JSONObject> resourceModelExtraVersionParameters = context.getModelExtraVersionParameters(resourceType, resourceVersion);
        if (resourceModelExtraVersionParameters.isPresent()) {
            return resourceModelExtraVersionParameters.map(res -> res.getJSONObject(type.name())).orElse(new JSONObject()).getLong(type.name());
        }
        return null;
    }
}
