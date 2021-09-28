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

package com.dtstack.engine.master.impl;

import com.dtstack.engine.domain.Component;
import com.dtstack.engine.domain.Engine;
import com.dtstack.engine.domain.EngineTenant;
import com.dtstack.engine.domain.Queue;
import com.dtstack.engine.pluginapi.pojo.ComponentTestResult;
import com.dtstack.engine.master.vo.QueueVO;
import com.dtstack.engine.master.vo.engine.EngineSupportVO;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.dao.EngineDao;
import com.dtstack.engine.dao.EngineTenantDao;
import com.dtstack.engine.dao.QueueDao;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EngineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineService.class);

    @Autowired
    private EngineDao engineDao;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private QueueDao queueDao;

    @Autowired
    private EngineTenantDao engineTenantDao;

    public List<QueueVO> getQueue(Long engineId){
        List<Queue> queueList = queueDao.listByEngineId(engineId);
        return QueueVO.toVOs(queueList);
    }

    /**
     * [
     *     {
     *         "engineType":1,
     *         "supportComponent":[1,3,4]
     *     }
     * ]
     */
    public List<EngineSupportVO> listSupportEngine(Long dtUicTenantId,Boolean needCommon){
        List<EngineSupportVO> vos = Lists.newArrayList();
        List<Engine> engineTenants = engineDao.getByDtUicTenantId(dtUicTenantId);
        if(CollectionUtils.isEmpty(engineTenants)){
            return vos;
        }
        List<Long> engineIds = engineTenants.stream().filter(engine -> needCommon || MultiEngineType.COMMON.getType()!=engine.getEngineType())
                .map(Engine::getId)
                .collect(Collectors.toList());
        List<Component> components = componentService.listComponent(engineIds);
        Map<Long, List<Component>> engineComponentMapping = components.stream()
                .collect(Collectors.groupingBy(Component::getEngineId));

        for (Engine engine : engineTenants) {
            EngineSupportVO engineSupportVO = new EngineSupportVO();
            engineSupportVO.setEngineType(engine.getEngineType());
            List<Component> componentList = engineComponentMapping.get(engine.getId());
            if (CollectionUtils.isEmpty(componentList)) {
                continue;
            }

            List<Integer> componentTypes = componentList.stream()
                    .map(Component::getComponentTypeCode)
                    .collect(Collectors.toList());
            engineSupportVO.setSupportComponent(componentTypes);
            Optional<Component> metadataComponent = componentList.stream()
                    .filter(c -> null != c.getIsMetadata() && 1 == c.getIsMetadata())
                    .findFirst();
            metadataComponent.ifPresent(component -> engineSupportVO.setMetadataComponent(component.getComponentTypeCode()));
            vos.add(engineSupportVO);
        }

        return vos;
    }


    public void updateEngineTenant(Long clusterId, Long engineId){
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        if(CollectionUtils.isEmpty(engines)){
            return;
        }

        List<Long> engineIds = engines.stream().map(Engine::getId).collect(Collectors.toList());
        engineIds.remove(engineId);
        if(CollectionUtils.isEmpty(engineIds)){
            return;
        }

        List<EngineTenant> engineTenants = engineTenantDao.listByEngineIds(engineIds);
        if(CollectionUtils.isEmpty(engineTenants)){
            return;
        }

        List<Long> tenantIds = engineTenants.stream().map(EngineTenant::getTenantId).distinct().collect(Collectors.toList());
        for (Long tenantId : tenantIds) {
            EngineTenant et = new EngineTenant();
            et.setTenantId(tenantId);
            et.setEngineId(engineId);
            engineTenantDao.insert(et);
        }
    }


    public void updateResource(Long engineId, ComponentTestResult.ClusterResourceDescription description){
        Engine engine = engineDao.getOne(engineId);
        engine.setTotalCore(description.getTotalCores());
        engine.setTotalMemory(description.getTotalMemory());
        engine.setTotalNode(description.getTotalNode());

        engineDao.update(engine);
    }


    public Engine getOne(Long engineId) {
        return engineDao.getOne(engineId);
    }

}

