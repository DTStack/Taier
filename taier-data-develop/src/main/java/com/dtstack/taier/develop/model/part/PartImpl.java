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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentScheduleType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EDeployType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.ComponentConfig;
import com.dtstack.taier.develop.model.DataSource;
import com.dtstack.taier.develop.model.Part;
import com.dtstack.taier.develop.model.system.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PartImpl implements Part {

    protected Context context;
    protected DataSource dataSource;

    protected EComponentType type;
    protected String versionName;
    protected EComponentType storageType;
    protected EDeployType deployType;
    protected Map<EComponentScheduleType, List<Component>> componentScheduleGroup;
    protected PartImpl part = null;

    public PartImpl(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType,
            List<Component>> componentScheduleGroup, Context context, DataSource dataSource, EDeployType deployType) {
        this.type = componentType;
        this.versionName = versionName;
        this.storageType = storeType;
        this.context = context;
        this.dataSource = dataSource;
        this.componentScheduleGroup = componentScheduleGroup;
        this.deployType = deployType;
    }

    @Override
    public EComponentType getType() {
        return type;
    }

    @Override
    public String getVersionValue() {
        if (part == null) {
            initPartImpl();
        }
        return part.getVersionValue();
    }


    @Override
    public List<ComponentConfig> loadTemplate() {
        validDependOn();
        List<Long> allTemplateIds = new ArrayList<>();
        String pluginName = getPluginName();
        if (StringUtils.isBlank(pluginName)) {
            throw new TaierDefineException(String.format(ErrorCode.NOT_SUPPORT_COMPONENT.getMsg(), type.name(), versionName));
        }
        Long extraVersionParameters = getExtraVersionParameters();
        if (extraVersionParameters != null) {
            allTemplateIds.add(extraVersionParameters);
        }
        context.getBaseTemplateId(pluginName).ifPresent(allTemplateIds::add);
        return dataSource.listComponentConfig(allTemplateIds, true);
    }

    protected void validDependOn() {
        List<EComponentScheduleType> dependsOn = context.getDependsOn(type);
        if (CollectionUtils.isNotEmpty(dependsOn) && !EDeployType.STANDALONE.equals(deployType)) {
            for (EComponentScheduleType componentScheduleType : dependsOn) {
                if (!componentScheduleGroup.containsKey(componentScheduleType)) {
                    throw new TaierDefineException(ErrorCode.DEPEND_ON_COMPONENT_NOT_CONFIG);
                }
            }
        }
    }


    protected void validDeployType(EDeployType deployType) {
        if (EDeployType.YARN.equals(deployType) && !EComponentType.YARN.equals(getResourceType())) {
            throw new TaierDefineException(String.format(ErrorCode.RESOURCE_COMPONENT_NOT_SUPPORT_DEPLOY_TYPE.getMsg(), type, deployType));
        }
    }


    @Override
    public String getPluginName() {
        if (part == null) {
            initPartImpl();
        }
        return part.getPluginName();
    }

    private void initPartImpl() {
        EComponentScheduleType owner = context.getOwner(type);
        switch (owner) {
            case COMMON:
                part = new CommonPart(type, versionName, storageType, componentScheduleGroup, context, dataSource);
                break;
            case RESOURCE:
                part = new ResourcePart(type, versionName, storageType, componentScheduleGroup, context, dataSource);
                break;
            case STORAGE:
                part = new StoragePart(type, versionName, storageType, componentScheduleGroup, context, dataSource);
                break;
            case COMPUTE:
                if (EDeployType.STANDALONE.equals(deployType)) {
                    part = new StandaloneComputePart(type, versionName, storageType, componentScheduleGroup, context, dataSource, deployType);
                } else {
                    List<EComponentScheduleType> dependsOn = context.getDependsOn(type);
                    if (CollectionUtils.isNotEmpty(dependsOn)) {
                        part = new DependComputePart(type, versionName, storageType, componentScheduleGroup, context, dataSource, deployType);
                    } else {
                        part = new SingleComputePart(type, versionName, storageType, componentScheduleGroup, context, dataSource);
                    }
                }
                break;
            default:
                throw new TaierDefineException(ErrorCode.COMPONENT_INVALID);
        }
    }

    @Override
    public EComponentType getResourceType() {
        List<Component> components = componentScheduleGroup.get(EComponentScheduleType.RESOURCE);
        if (CollectionUtils.isEmpty(components)) {
            throw new TaierDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
        }
        Integer componentTypeCode = components.get(0).getComponentTypeCode();
        return EComponentType.getByCode(componentTypeCode);
    }

    @Override
    public Long getExtraVersionParameters() {
        return part == null ? null : part.getExtraVersionParameters();
    }

    /**
     * model 里面获取nameTemplate
     * 获取不到 在根据版本在modelConfig里面根据版本获取
     *
     * @return
     */
    protected String getPluginNameInModelOrByConfigVersion() {
        String nameTemplate = context.getComponentModel(type).getNameTemplate();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(nameTemplate)) {
            return nameTemplate;
        }
        //依赖resource 但是不依赖resource的类型拼接pluginName 如hive2
        Optional<JSONObject> modelConfig = context.getModelConfig(type, versionName);
        return modelConfig.map(model -> model.getString(versionName)).orElseThrow(() ->
                new TaierDefineException(String.format(ErrorCode.COMPONENT_CONFIG_NOT_SUPPORT_VERSION.getMsg(), type.name(), versionName))
        );
    }
}
