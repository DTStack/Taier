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

package com.dtstack.taier.scheduler.server.pluginInfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.TaierDefineException;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.TYPE_NAME;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.TYPE_NAME_KEY;

public class ScriptPluginInfoStrategy extends ComponentPluginInfoStrategy {

    @Override
    public JSONObject convertPluginInfo(JSONObject clusterConfigJson, Long clusterId, Integer deployMode) {
        JSONObject confConfig = clusterConfigJson.getJSONObject(EComponentType.SCRIPT.getConfName());
        if (Objects.isNull(confConfig)) {
            throw new TaierDefineException("Component Script is not configured");
        }
        String typeName = confConfig.getString(TYPE_NAME);
        if (!StringUtils.isBlank(typeName)) {
            clusterConfigJson.put(TYPE_NAME_KEY, typeName);
        }
        clusterConfigJson.remove(EComponentType.SCRIPT.getConfName());
        // put flat all script config
        clusterConfigJson.putAll(confConfig);
        return clusterConfigJson;
    }

    @Override
    public EComponentType getComponentTypeCode() {
        return EComponentType.SCRIPT;
    }
}