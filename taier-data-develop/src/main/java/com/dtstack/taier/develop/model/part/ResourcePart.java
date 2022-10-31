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
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.develop.model.DataSource;
import com.dtstack.taier.develop.model.system.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResourcePart extends PartImpl {

    public ResourcePart(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup, Context context, DataSource dataSource) {
        super(componentType, versionName, storeType, componentScheduleGroup, context, dataSource, null);
    }

    @Override
    public String getVersionValue() {
        return context.getComponentModel(type).getVersionValue(versionName);
    }

    @Override
    public String getPluginName() {
        Optional<JSONObject> versionModelConfig = context.getModelConfig(type, versionName);
        return versionModelConfig.map(config -> config.getString(type.name())).orElse(null);
    }

    @Override
    public Long getExtraVersionParameters() {
        Optional<JSONObject> modelExtraVersionParameters = context.getModelExtraVersionParameters(type, versionName);
        return modelExtraVersionParameters.map(parameters -> parameters.getLong(type.name())).orElse(null);
    }
}
