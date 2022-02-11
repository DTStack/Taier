package com.dtstack.taiga.develop.model.part;

import com.dtstack.taiga.common.enums.EComponentScheduleType;
import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.dao.domain.Component;
import com.dtstack.taiga.develop.model.DataSource;
import com.dtstack.taiga.develop.model.system.Context;

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
