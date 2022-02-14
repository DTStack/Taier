package com.dtstack.taier.develop.model;

import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.ComponentConfig;
import com.dtstack.taier.scheduler.service.ComponentConfigService;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@org.springframework.stereotype.Component
public class ComponentFacade {

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private ComponentService componentService;

    public List<Component> listAllByClusterId(Long clusterId) {
        Objects.requireNonNull(clusterId, "ClusterId is null.");
        return componentService.listAllComponents(clusterId);
    }

    public List<Component> listAllByClusterIdAndComponentType(Long clusterId, EComponentType type) {
        Objects.requireNonNull(clusterId, "ClusterId is null.");
        Objects.requireNonNull(type, "Type is null.");
        return componentService.listAllComponentsByComponent(clusterId, Lists.newArrayList(type.getTypeCode()));
    }

    public List<Component> listAllByClusterIdAndComponentTypes(Long clusterId, List<EComponentType> types) {
        Objects.requireNonNull(clusterId, "ClusterId is null.");
        if (types == null || types.isEmpty()) {
            throw new IllegalArgumentException("Empty types.");
        }
        List<Integer> codes = types.stream()
                .map(EComponentType::getTypeCode)
                .collect(Collectors.toList());
        return componentService.listAllComponentsByComponent(clusterId, codes);
    }


    public List<Component> listAllByClusterIdAndComponentTypeAndVersionName(
            Long clusterId, EComponentType type, String version) {
        Objects.requireNonNull(clusterId, "ClusterId is null.");
        Objects.requireNonNull(type, "Type is null.");
        Objects.requireNonNull(version, "Version is null.");
        return componentService.listAllByClusterIdAndComponentTypeAndVersionName(
                clusterId, type.getTypeCode(), version);
    }

    public List<ComponentConfig> listByComponentIds(List<Long> componentIds, boolean excludeCustom) {
        if (componentIds == null || componentIds.isEmpty()) {
            throw new IllegalArgumentException("Empty componentIds.");
        }
        return componentConfigService.listByComponentIds(componentIds, excludeCustom);
    }

}
