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

package com.dtstack.taier.develop.model.part;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentScheduleType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EDeployType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.develop.model.DataSource;
import com.dtstack.taier.develop.model.system.Context;
import com.dtstack.taier.develop.model.system.config.ComponentModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class DependComputePart extends PartImpl {

    public DependComputePart(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup,
                             Context context, DataSource dataSource, EDeployType deployType) {
        super(componentType, versionName, storeType, componentScheduleGroup, context, dataSource, deployType);
    }

    @Override
    public String getPluginName() {
        validDeployType(deployType);
        if (null == storageType) {
            throw new RdosDefineException(ErrorCode.STORE_COMPONENT_NOT_CONFIG);
        }
        List<Component> components = componentScheduleGroup.get(EComponentScheduleType.RESOURCE);
        if (CollectionUtils.isEmpty(components)) {
            throw new RdosDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
        }
        Component resourceComponent = components.get(0);
        String resourceVersion = resourceComponent.getVersionName();
        EComponentType resourceType = EComponentType.getByCode(resourceComponent.getComponentTypeCode());
        Optional<JSONObject> resourceModelConfig = context.getModelConfig(resourceType, resourceVersion);
        if (!resourceModelConfig.isPresent()) {
            throw new RdosDefineException(Strings.format(ErrorCode.RESOURCE_NOT_SUPPORT_COMPONENT_VERSION.getMsg(), resourceType, type, versionName));
        }
        //唯一的pluginName
        return getValueInConfigWithResourceStore(resourceModelConfig.get(), resourceComponent, this::getPluginNameInModelOrByConfigVersion);
    }


    public Long getExtraVersionParameters() {
        Component resourceComponent = componentScheduleGroup.get(EComponentScheduleType.RESOURCE).get(0);
        String resourceVersion = resourceComponent.getVersionName();
        EComponentType resourceType = EComponentType.getByCode(resourceComponent.getComponentTypeCode());
        Optional<JSONObject> resourceModelExtraConfig = context.getModelExtraVersionParameters(resourceType, resourceVersion);
        String extraTemplateId = null;
        if (resourceModelExtraConfig.isPresent()) {
            extraTemplateId = getValueInConfigWithResourceStore(resourceModelExtraConfig.get(), resourceComponent, null);
            if (StringUtils.isNotBlank(extraTemplateId)) {
                return Long.parseLong(extraTemplateId);
            }
        }
        //依赖resource 但是不依赖resource的类型拼接额外参数信息 如hive2
        extraTemplateId = context.getModelExtraVersionParameters(type, versionName).map((extraConfig) -> extraConfig.getString(versionName)).orElse(null);
        if (StringUtils.isNotBlank(extraTemplateId)) {
            return Long.parseLong(extraTemplateId);
        }
        return null;
    }


    private String getValueInConfigWithResourceStore(JSONObject resourceConfig, Component resourceComponent, Supplier<String> specialSupplier) {
        JSONObject storageConfig = resourceConfig.getJSONObject(storageType.name());
        if (storageConfig == null) {
            throw new RdosDefineException(ErrorCode.STORE_COMPONENT_CONFIG_NULL);
        }
        if (StringUtils.isNotBlank(storageConfig.getString(type.name().toUpperCase()))) {
            //model config 已经定义了pluginName
            if (storageConfig.get(type.name().toUpperCase()) instanceof List) {
                return getValueWithKey(storageConfig.getJSONArray(type.name().toUpperCase()))
                        .orElseThrow(() -> new RdosDefineException(Strings.format(ErrorCode.RESOURCE_NOT_SUPPORT_COMPONENT_VERSION.getMsg(),
                                resourceComponent.getComponentName(), type.name(), versionName)));
            }
            return storageConfig.getString(type.name().toUpperCase());
        } else if (null != specialSupplier) {
            return specialSupplier.get();
        }
        return null;
    }

    private Optional<String> getValueWithKey(JSONArray computeVersionModelConfig) {
        for (int i = 0; i < computeVersionModelConfig.size(); i++) {
            if (StringUtils.isNotBlank(computeVersionModelConfig.getJSONObject(i).getString(getVersionValue()))) {
                return Optional.ofNullable(computeVersionModelConfig.getJSONObject(i).getString(getVersionValue()));
            }
        }
        return Optional.empty();
    }

    @Override
    public String getVersionValue() {
        if (StringUtils.isBlank(versionName)) {
            return Strings.EMPTY;
        }
        ComponentModel componentModel = context.getComponentModel(type);
        if (null != componentModel) {
            return componentModel.getVersionValue(versionName);
        }
        return Strings.EMPTY;
    }


}
