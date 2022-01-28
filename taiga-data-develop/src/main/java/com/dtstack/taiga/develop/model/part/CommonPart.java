package com.dtstack.taiga.develop.model.part;

import com.dtstack.taiga.common.enums.EComponentScheduleType;
import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.dao.domain.Component;
import com.dtstack.taiga.develop.model.DataSource;
import com.dtstack.taiga.develop.model.system.Context;
import com.dtstack.taiga.develop.model.system.config.ComponentModel;

import java.util.List;
import java.util.Map;

public class CommonPart extends PartImpl {

    public CommonPart(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup,
                      Context context, DataSource dataSource) {
        super(componentType, versionName, storeType, componentScheduleGroup, context, dataSource,null);
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
