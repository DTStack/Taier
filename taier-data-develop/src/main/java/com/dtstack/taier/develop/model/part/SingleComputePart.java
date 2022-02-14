package com.dtstack.taier.develop.model.part;

import com.dtstack.taier.common.enums.EComponentScheduleType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.develop.model.DataSource;
import com.dtstack.taier.develop.model.system.Context;

import java.util.List;
import java.util.Map;

public class SingleComputePart extends PartImpl {

    public SingleComputePart(EComponentType componentType, String versionName, EComponentType storeType,
                             Map<EComponentScheduleType, List<Component>> componentScheduleGroup, Context context, DataSource dataSource) {
        super(componentType, versionName, storeType, componentScheduleGroup, context, dataSource,null);
    }

    @Override
    public String getPluginName() {
        return getPluginNameInModelOrByConfigVersion();
    }

    @Override
    public String getVersionValue() {
        return context.getComponentModel(type).getVersionValue(versionName);
    }
}
