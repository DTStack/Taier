/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taiga.scheduler.vo;

import com.dtstack.taiga.dao.domain.Component;
import org.apache.commons.lang3.BooleanUtils;
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
     * @param component 单个组件
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
            if (needDefault && BooleanUtils.isTrue(componentVO.getIsDefault())){
                return componentVO;
            }else if (StringUtils.isNotBlank(componentVersion) && componentVersion.equals(componentVO.getVersionName())){
                return componentVO;
            }
        }
        // 如果没有指定版本, 也没有找到默认版本, 说明构建组件出错
        throw new IllegalStateException(String.format("not found default component type = %s",getComponentTypeCode()));
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
