package com.dtstack.taier.develop.model.part;

import com.dtstack.taier.common.enums.EComponentScheduleType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.develop.model.DataSource;
import com.dtstack.taier.develop.model.system.Context;
import com.dtstack.taier.develop.model.system.config.ComponentModel;

import java.util.List;
import java.util.Map;

public class CommonPart extends PartImpl {

    public CommonPart(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup,
                      Context context, DataSource dataSource) {
        super(componentType, versionName, storeType, componentScheduleGroup, context, dataSource, null);
    }

    @Override
    public String getVersionValue() {
        return "";
    }

    @Override
    public String getPluginName() {
        ComponentModel componentModel = context.getComponentModel(type);
        return componentModel.getNameTemplate();
    }
}
