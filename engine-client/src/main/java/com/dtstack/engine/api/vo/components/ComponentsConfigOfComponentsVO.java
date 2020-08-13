package com.dtstack.engine.api.vo.components;

/**
 * @Auther: dazhi
 * @Date: 2020/7/29 5:35 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ComponentsConfigOfComponentsVO {

    private Integer componentTypeCode;

    private String componentConfig;

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public String getComponentConfig() {
        return componentConfig;
    }

    public void setComponentConfig(String componentConfig) {
        this.componentConfig = componentConfig;
    }
}
