package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.Engine;
import com.dtstack.engine.api.domain.EngineTenant;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.EngineVO;
import com.dtstack.engine.api.vo.QueueVO;
import com.dtstack.engine.api.vo.engine.EngineSupportVO;
import com.dtstack.engine.dao.EngineDao;
import com.dtstack.engine.dao.EngineTenantDao;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.enums.EComponentType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Autowired
    private TenantDao tenantDao;

    private static final long DEFAULT_KUBERNETES_PARENT_NODE = -2L;

    public List<QueueVO> getQueue( Long engineId){
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
    public List<EngineSupportVO> listSupportEngine( Long dtUicTenantId){
        List<EngineSupportVO> vos = Lists.newArrayList();
        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        if (tenantId == null){
            return vos;
        }

        List<Long> engineIds = engineTenantDao.listEngineIdByTenantId(tenantId);
        List<Engine> engineList = engineDao.listByEngineIds(engineIds);
        if(CollectionUtils.isEmpty(engineList)){
            return vos;
        }

        for (Engine engine : engineList) {
            EngineSupportVO engineSupportVO = new EngineSupportVO();
            engineSupportVO.setEngineType(engine.getEngineType());
            List<Component> componentList = componentService.listComponent(engine.getId());
            if (CollectionUtils.isEmpty(componentList)){
                continue;
            }

            List<Integer> componentTypes = componentList.stream()
                    .map(Component::getComponentTypeCode)
                    .collect(Collectors.toList());
            engineSupportVO.setSupportComponent(componentTypes);
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

    public List<EngineVO> listClusterEngines(Long clusterId, boolean queryQueue) {
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        List<EngineVO> result = EngineVO.toVOs(engines);

        if (queryQueue) {
            for (EngineVO engineVO : result) {
                List<Queue> queues = queueDao.listByEngineIdWithLeaf(engineVO.getId());
                engineVO.setResourceType(EComponentType.YARN.getName());
                if (CollectionUtils.isNotEmpty(queues)) {
                    if (queues.get(0).getParentQueueId() == DEFAULT_KUBERNETES_PARENT_NODE) {
                        engineVO.setResourceType(EComponentType.KUBERNETES.getName());
                    }
                }
                engineVO.setQueues(QueueVO.toVOs(queues));
            }
        }

        return result;
    }
}

