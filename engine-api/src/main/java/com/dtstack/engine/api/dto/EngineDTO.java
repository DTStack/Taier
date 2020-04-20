package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.Engine;

import java.util.List;

public class EngineDTO extends Engine {

    private List<Component> componentList;

    private List<Integer> componentTypeCodeList;

    public List<Integer> getComponentTypeCodeList() {
        return componentTypeCodeList;
    }

    public void setComponentTypeCodeList(List<Integer> componentTypeCodeList) {
        this.componentTypeCodeList = componentTypeCodeList;
    }

    public List<Component> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<Component> componentList) {
        this.componentList = componentList;
    }
}
