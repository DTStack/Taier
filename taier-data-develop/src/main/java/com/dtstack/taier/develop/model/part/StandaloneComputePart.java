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
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.develop.model.DataSource;
import com.dtstack.taier.develop.model.system.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StandaloneComputePart extends PartImpl {
    public StandaloneComputePart(EComponentType componentType, String versionName, EComponentType storageType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup,
                                 Context context, DataSource dataSource, EDeployType deployType) {
        super(componentType, versionName, storageType, componentScheduleGroup, context, dataSource, deployType);
    }

    @Override
    public String getPluginName() {
        Optional<JSONObject> modelConfig = context.getModelConfig(type, versionName);
        return modelConfig.map(config -> config.getString(versionName)).orElse(null);
    }

    @Override
    public String getVersionValue() {
        return context.getComponentModel(type).getVersionValue(versionName);
    }

    @Override
    public Long getExtraVersionParameters() {
        Optional<JSONObject> modelConfig = context.getModelExtraVersionParameters(type, versionName);
        return modelConfig.map(config -> config.getLong(versionName)).orElse(null);
    }
}
