package com.dtstack.engine.api.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 组件多版本显示
 * @author xinge
 */
public class ComponentMultiVersionVO implements IComponentVO {

    /**
     * 组件标识
     * @see com.dtstack.engine.common.enums.EComponentType
     */
    private Integer componentTypeCode;
    /**
     * 组件的每个版本配置
     */
    private List<ComponentVO> multiVersion;

    @Override
    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public ComponentMultiVersionVO setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
        return this;
    }

    public List<ComponentVO> getMultiVersion() {
        return multiVersion;
    }

    public ComponentMultiVersionVO setMultiVersion(List<ComponentVO> multiVersion) {
        this.multiVersion = multiVersion;
        return this;
    }


    @Override
    public List<ComponentVO> loadComponents() {
        return this.multiVersion;
    }

    @Override
    public boolean multiVersion() {
        return true;
    }

    @Override
    public void addComponent(ComponentVO component) {
        this.multiVersion.add(component);
    }

    public static ComponentMultiVersionVO getInstanceWithCapacityAndType(Integer componentTypeCode, int capacity){
        return new ComponentMultiVersionVO().setComponentTypeCode(componentTypeCode)
                .setMultiVersion(new ArrayList<>(capacity));
    }


}
