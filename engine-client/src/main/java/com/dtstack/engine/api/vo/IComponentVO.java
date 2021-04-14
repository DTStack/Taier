package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.Component;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author xinge
 */
public interface IComponentVO {


    /**
     * 组件所对应的类型
     * @return 获取组件类型
     */
    Integer getComponentTypeCode();

    /**
     * 是否多版本实现
     * @return true 多版本
     */
    default boolean multiVersion(){
      return false;
    }

    /**
     * 添加一个确定版本组件
     * 无多版本选项默认空调用
     * @param component
     */
    default void addComponent(ComponentVO component){ }

    /**
     * 组件的所有版本
     * @return 组件的所有版本
     */
    List<ComponentVO> loadComponents();

    /**
     * 获取指定版本的组件,如果不存在返回默认组件
     * @param componentVersion 组件版本
     * @return 默认组件或者对应版本组件
     */
    default ComponentVO getComponent(String componentVersion){
        boolean needDefault= StringUtils.isBlank(componentVersion);
        List<ComponentVO> componentVOList = loadComponents();
        for (ComponentVO componentVO : componentVOList) {
            if (needDefault && componentVO.getIsDefault()){
                return componentVO;
            }else if (componentVersion.equals(componentVO.getHadoopVersion())){
                return componentVO;
            }
        }
        //
        throw new IllegalStateException();
    }

    /**
     * 获取一个具体版本组件实例
     * @param componentVO
     * @param component
     * @return
     */
    static ComponentVO getComponentVo(IComponentVO componentVO, Component component){
        if (!(componentVO instanceof ComponentVO)){
           componentVO=new ComponentVO();
        }
        BeanUtils.copyProperties(component,componentVO);
        return (ComponentVO) componentVO;
    }


}
