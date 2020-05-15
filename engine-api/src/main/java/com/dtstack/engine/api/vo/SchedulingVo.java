package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.Component;

import java.util.List;

/**
 * @author yuebai
 * @date 2020-05-11
 */
public class SchedulingVo {
    private int schedulingCode;

    private String SchedulingName;

    private List<Component> components;

    public int getSchedulingCode() {
        return schedulingCode;
    }

    public void setSchedulingCode(int schedulingCode) {
        this.schedulingCode = schedulingCode;
    }

    public String getSchedulingName() {
        return SchedulingName;
    }

    public void setSchedulingName(String schedulingName) {
        SchedulingName = schedulingName;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }
}
