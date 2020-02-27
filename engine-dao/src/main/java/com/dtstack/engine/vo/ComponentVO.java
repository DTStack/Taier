package com.dtstack.engine.vo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.engine.domain.Component;

import java.util.ArrayList;
import java.util.List;

public class ComponentVO {

    private String componentName;

    private Long componentId;

    private int componentTypeCode;

    private JSONObject config;

    public static ComponentVO toVO(Component component, boolean withKerberosCongfig){
        ComponentVO vo = new ComponentVO();
        PublicUtil.copyPropertiesIgnoreNull(component, vo);
        vo.setComponentId(component.getId());
        //前端默认不展示kerberosConfig
        JSONObject jsonObject = JSONObject.parseObject(component.getComponentConfig());
        if(!withKerberosCongfig) {
            if (component.getComponentTypeCode() == EComponentType.HDFS.getTypeCode() ||
                    component.getComponentTypeCode() == EComponentType.YARN.getTypeCode()) {
                jsonObject = jsonObject.fluentRemove("kerberosConfig").fluentRemove("openKerberos").fluentRemove("kerberosFile");
            } else {
                jsonObject = jsonObject.fluentRemove("kerberosConfig");
            }
        }
        vo.setConfig(jsonObject);
        return vo;
    }

    public static ComponentVO toVO(Component component){
        return toVO(component, false);
    }

    public static List<ComponentVO> toVOS(List<Component> components) {
        return toVOS(components, false);
    }

    public static List<ComponentVO> toVOS(List<Component> components, boolean withKerberosCongfig){
        List<ComponentVO> vos = new ArrayList<>();
        for (Component component : components) {
            vos.add(toVO(component, withKerberosCongfig));
        }
        return vos;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public int getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(int componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }
}

