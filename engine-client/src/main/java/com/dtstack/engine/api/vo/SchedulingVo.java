package com.dtstack.engine.api.vo;

import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * @author yuebai
 * @date 2020-05-11
 */
@ApiModel
public class SchedulingVo {
    private int schedulingCode;

    private String SchedulingName;

    private List<IComponentVO> components;

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

    public List<IComponentVO> getComponents() {
        return components;
    }

    public void setComponents(List<IComponentVO> components) {
        this.components = components;
    }
}
