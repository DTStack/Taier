package com.dtstack.taiga.develop.model;

import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.dao.domain.Component;
import com.dtstack.taiga.dao.domain.ComponentConfig;

import java.util.List;

/**
 *
 * All input versionName parameters should normalize.
 */
public interface DataSource {
    List<ComponentConfig> listComponentConfig(List<Long> componentId, boolean excludeCustom);
    List<Component> getComponents(EComponentType type, String versionName);
    List<Component> getComponents(EComponentType type);
    List<Component> listAllByClusterId();
    List<Component> getComponents(List<EComponentType> types);
}
