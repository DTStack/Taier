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

package com.dtstack.taier.scheduler.vo;

import com.dtstack.taier.dao.domain.Component;
import io.swagger.annotations.ApiModel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApiModel
public class ComponentVO extends Component implements IComponentVO{

    private String clusterName;

    private String componentConfig;

    private String componentTemplate;

    private Integer deployType;

    public Integer getDeployType() {
        return deployType;
    }

    public void setDeployType(Integer deployType) {
        this.deployType = deployType;
    }

    public String getComponentConfig() {
        return componentConfig;
    }

    public void setComponentConfig(String componentConfig) {
        this.componentConfig = componentConfig;
    }

    public String getComponentTemplate() {
        return componentTemplate;
    }

    public void setComponentTemplate(String componentTemplate) {
        this.componentTemplate = componentTemplate;
    }

    private String principals;

    private String principal;

    private String mergeKrb5Content;

    public String getMergeKrb5Content() {
        return mergeKrb5Content;
    }

    public void setMergeKrb5Content(String mergeKrb5Content) {
        this.mergeKrb5Content = mergeKrb5Content;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }


    @Override
    public List<ComponentVO> loadComponents() {
        return Collections.singletonList(this);
    }

    public static List<ComponentVO> toVOS(List<Component> components) {
        List<ComponentVO> vos = new ArrayList<>();
        if (CollectionUtils.isEmpty(components)) {
            return vos;
        }
        for (Component component : components) {
            ComponentVO vo = new ComponentVO();
            BeanUtils.copyProperties(component, vo);
            vos.add(vo);
        }
        return vos;
    }

    public static ComponentVO getInstance(){
        return new ComponentVO();
    }

    public static ComponentVO toVO(Component component) {
        ComponentVO vo = new ComponentVO();
        BeanUtils.copyProperties(component, vo);
        return vo;
    }
}

