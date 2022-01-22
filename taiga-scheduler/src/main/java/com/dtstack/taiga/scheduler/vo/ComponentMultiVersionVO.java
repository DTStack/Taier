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

import com.dtstack.taiga.common.enums.EComponentType;

import java.util.ArrayList;
import java.util.List;

/**
 * 组件多版本显示
 * @author xinge
 */
public class ComponentMultiVersionVO implements IComponentVO {

    /**
     * 组件标识
     * @see EComponentType
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
