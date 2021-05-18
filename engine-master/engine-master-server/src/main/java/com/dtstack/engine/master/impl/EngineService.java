package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.Engine;
import com.dtstack.engine.api.domain.EngineTenant;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.QueueVO;
import com.dtstack.engine.api.vo.engine.EngineSupportVO;
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
import sun.nio.ch.ThreadPool;

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
    public List<EngineSupportVO> listSupportEngine(Long dtUicTenantId){
        List<EngineSupportVO> vos = Lists.newArrayList();
        List<Engine> engineTenants = engineDao.getByDtUicTenantId(dtUicTenantId);
        if(CollectionUtils.isEmpty(engineTenants)){
            return vos;
        }
        List<Long> engineIds = engineTenants.stream().filter(engine -> MultiEngineType.EMPTY.getType()!=engine.getEngineType())
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

