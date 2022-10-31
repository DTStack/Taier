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

package com.dtstack.taier.develop.model.datasource;

import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.ComponentConfig;
import com.dtstack.taier.develop.model.ComponentFacade;
import com.dtstack.taier.develop.model.DataSource;

import java.util.Collections;
import java.util.List;

public class ImmediatelyLoadDataSource implements DataSource {

    private final Long clusterId;
    private final ComponentFacade facade;

    public ImmediatelyLoadDataSource(Long clusterId, ComponentFacade facade) {
        this.clusterId = clusterId;
        this.facade = facade;
    }

    public List<Component> listAllByClusterId() {
        return this.facade.listAllByClusterId(clusterId);
    }

    @Override
    public List<ComponentConfig> listComponentConfig(List<Long> componentIds, boolean excludeCustom) {
        if (componentIds.isEmpty()) {
            return Collections.emptyList();
        }
        return this.facade.listByComponentIds(componentIds, excludeCustom);
    }

    @Override
    public List<Component> getComponents(EComponentType type, String versionName) {
        return this.facade.listAllByClusterIdAndComponentTypeAndVersionName(this.clusterId, type, versionName);
    }

    @Override
    public List<Component> getComponents(EComponentType type) {
        return this.facade.listAllByClusterIdAndComponentType(this.clusterId, type);
    }

    @Override
    public List<Component> getComponents(List<EComponentType> types) {
        return this.facade.listAllByClusterIdAndComponentTypes(this.clusterId, types);
    }

}
