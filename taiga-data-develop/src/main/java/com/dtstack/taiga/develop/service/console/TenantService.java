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

package com.dtstack.taiga.develop.service.console;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taiga.common.enums.Deleted;
import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.common.exception.ErrorCode;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.dao.domain.*;
import com.dtstack.taiga.dao.mapper.ClusterTenantMapper;
import com.dtstack.taiga.dao.mapper.QueueMapper;
import com.dtstack.taiga.dao.mapper.TenantMapper;
import com.dtstack.taiga.dao.pager.PageQuery;
import com.dtstack.taiga.dao.pager.PageResult;
import com.dtstack.taiga.dao.pager.Sort;
import com.dtstack.taiga.develop.dto.devlop.ComponentBindDBDTO;
import com.dtstack.taiga.develop.mapstruct.console.ClusterTransfer;
import com.dtstack.taiga.develop.mapstruct.console.TenantTransfer;
import com.dtstack.taiga.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taiga.develop.service.develop.IComponentService;
import com.dtstack.taiga.develop.service.develop.MultiEngineServiceFactory;
import com.dtstack.taiga.develop.service.develop.impl.BatchCatalogueService;
import com.dtstack.taiga.develop.service.develop.impl.TenantComponentService;
import com.dtstack.taiga.develop.utils.develop.mapping.ComponentTypeToEScheduleJobMapping;
import com.dtstack.taiga.develop.vo.console.ClusterTenantVO;
import com.dtstack.taiga.develop.vo.console.ComponentBindDBVO;
import com.dtstack.taiga.scheduler.impl.pojo.ComponentMultiTestResult;
import com.dtstack.taiga.scheduler.service.ClusterService;
import com.dtstack.taiga.scheduler.service.ComponentService;
import com.dtstack.taiga.scheduler.vo.ComponentVO;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/7/16
 */
@Service
public class TenantService {

    private static Logger LOGGER = LoggerFactory.getLogger(TenantService.class);

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private ClusterTenantMapper clusterTenantMapper;

    @Autowired
    private QueueMapper queueMapper;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private BatchCatalogueService batchCatalogueService;

    @Autowired
    private TenantComponentService tenantEngineService;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Autowired
    private ConsoleComponentService consoleComponentService;

    @Autowired
    private ClusterService clusterService;

    public PageResult<List<ClusterTenantVO>> pageQuery(Long clusterId,
                                                       String tenantName,
                                                       int pageSize,
                                                       int currentPage) {

        PageQuery query = new PageQuery(currentPage, pageSize, "gmt_modified", Sort.DESC.name());
        int count = clusterTenantMapper.generalCount(clusterId, tenantName);
        if (count == 0) {
            return PageResult.EMPTY_PAGE_RESULT;
        }
        List<ClusterTenant> clusterTenants = clusterTenantMapper.generalQuery(query, clusterId, tenantName);

        List<ClusterTenantVO> clusterTenantVOS = fillQueue(clusterTenants);
        return new PageResult(clusterTenantVOS,count,query);
    }

    private List<ClusterTenantVO> fillQueue(List<ClusterTenant> clusterTenants) {
        List<Long> queueIds = clusterTenants.stream()
                .map(ClusterTenant::getQueueId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, Queue> queueMap = queueMapper.listByIds(queueIds)
                .stream()
                .collect(Collectors.toMap(Queue::getId, q -> q));

        return clusterTenants.stream().map(clusterTenant -> {
            Queue queue = queueMap.getOrDefault(clusterTenant.getQueueId(), new Queue());
            return TenantTransfer.INSTANCE.toClusterTenantVO(clusterTenant, queue);
        }).collect(Collectors.toList());
    }


    @Transactional(rollbackFor = Exception.class)
    public void bindingTenant(Long tenantId, Long clusterId,
                              Long queueId, String clusterName,
                              List<ComponentBindDBVO> bindDBDTOList) throws Exception {
        Tenant tenant = getTenant(tenantId);
        checkTenantBindStatus(tenantId);
        checkClusterCanUse(clusterName);
        addClusterTenant(tenant.getId(), clusterId);
        if (queueId != null) {
            //hadoop
            updateTenantQueue(tenantId, clusterId, queueId);
        }
        List<ComponentBindDBDTO> bindDTOList = ClusterTransfer.INSTANCE.bindDBtoDTOList(bindDBDTOList);
        initDataDevelop(clusterId, tenantId, tenant.getCreateUserId(), tenant.getTenantName(), tenant.getTenantDesc(), bindDTOList);
    }

    private void checkTenantBindStatus(Long tenantId) {
        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        if (null != clusterId) {
            throw new RdosDefineException("The tenant has been bound");
        }
    }


    public void checkClusterCanUse(String clusterName) throws Exception {
        List<ComponentMultiTestResult> testConnectionVO = consoleComponentService.testConnects(clusterName);
        boolean canUse = true;
        StringBuilder msg = new StringBuilder();
        msg.append("此集群不可用,测试连通性为通过：\n");
        for (ComponentMultiTestResult testResult : testConnectionVO) {
            EComponentType componentType = EComponentType.getByCode(testResult.getComponentTypeCode());
            if (!EComponentType.notCheckComponent.contains(componentType) && !testResult.getResult()) {
                canUse = false;
                msg.append("组件:").append(componentType.getName()).append(" ").append(JSON.toJSONString(testResult.getErrorMsg())).append("\n");
            }
        }

        if (!canUse) {
            throw new RdosDefineException(msg.toString());
        }
    }


    private void addClusterTenant(Long tenantId, Long clusterId) {
        ClusterTenant et = new ClusterTenant();
        et.setTenantId(tenantId);
        et.setClusterId(clusterId);
        clusterTenantMapper.insert(et);
    }

    private Tenant getTenant(Long tenantId) {
        Tenant tenant = tenantMapper.selectById(tenantId);
        if (tenant != null) {
            return tenant;
        }
        throw new RdosDefineException(ErrorCode.TENANT_IS_NULL);
    }

    public Tenant getByDtTenantId(Long tenantId) {
        return getTenantById(tenantId);
    }

    public Long getDtTenantId(Long id) {
        return id;
    }

    public Tenant getTenantById(Long id) {
        return getTenant(id);
    }

    public void updateTenantQueue(Long tenantId, Long clusterId, Long queueId) {
        Integer childCount = queueMapper.countByParentQueueId(queueId);
        if (childCount != 0) {
            throw new RdosDefineException("The selected queue has sub-queues, and the correct sub-queues are selected", ErrorCode.DATA_NOT_FIND);
        }

        LOGGER.info("switch queue, tenantId:{} queueId:{} clusterId:{}", tenantId, queueId, clusterId);
        int result = clusterTenantMapper.updateQueueId(tenantId, clusterId, queueId);
        if (result == 0) {
            throw new RdosDefineException("The update engine queue failed");
        }
    }

    /**
     * 绑定/切换队列
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindingQueue(String queueName,Long clusterId,
                             Long tenantId) {
        List<Queue> queues = queueMapper.listByClusterId(clusterId);
        Optional<Queue> queueOptional = queues.stream().filter(queue -> queue.getQueueName().equals(queueName)).findFirst();
        if (!queueOptional.isPresent()) {
            throw new RdosDefineException("Queue does not exist", ErrorCode.DATA_NOT_FIND);
        }
        Queue queue = queueOptional.get();
        Long queueId = queue.getId();
        try {
            LOGGER.info("switch queue, tenantId:{} queueId:{} queueName:{} clusterId:{}", tenantId, queueId, queue.getQueueName(), queue.getClusterId());
            updateTenantQueue(tenantId, queue.getClusterId(), queueId);
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new RdosDefineException("Failed to switch queue");
        }
    }

    public List<Tenant> listAllTenant() {
        return tenantMapper.selectList(Wrappers.lambdaQuery(Tenant.class).eq(Tenant::getIsDeleted, Deleted.NORMAL.getStatus()));
    }

    public Tenant findByName(String tenantName) {
        return tenantMapper.selectOne(Wrappers.lambdaQuery(Tenant.class).eq(Tenant::getTenantName, tenantName));
    }

    public void addTenant(String tenantName, Long createUserId) {
        Tenant tenant = new Tenant();
        tenant.setTenantName(tenantName);
        tenant.setCreateUserId(createUserId);
        tenant.setGmtCreate(Timestamp.from(Instant.now()));
        tenantMapper.insert(tenant);
    }

    @Transactional(rollbackFor = Exception.class)
    public void initDataDevelop(Long clusterId, Long tenantId, Long userId, String tenantName, String tenantDesc, List<ComponentBindDBDTO> bindDBDTOList) throws Exception {
        //初始化目录
        List<Component> components = componentService.listAllComponents(clusterId);

        List<ComponentVO> componentVOS = ComponentVO.toVOS(components);
        batchCatalogueService.initCatalogue(tenantId, userId, componentVOS);

        Cluster cluster = clusterService.getCluster(clusterId);

        // 初始化数据源相关的信息
        IComponentService componentService = null;
        for (ComponentBindDBDTO componentBindDBDTO : bindDBDTOList) {
            EComponentType eComponentType = EComponentType.getByCode(componentBindDBDTO.getComponentCode());
            String componentIdentity = cluster.getClusterName();

            // db相关的操作
            if (BooleanUtils.isTrue(componentBindDBDTO.getCreateFlag())) {
                componentService = multiEngineServiceFactory.getComponentService(eComponentType.getTypeCode());
                componentService.createDatabase(clusterId, eComponentType, componentIdentity, tenantDesc);
            } else {
                componentIdentity = componentBindDBDTO.getDbName();
            }

            // 初始化数据源
            datasourceService.initDefaultSource(clusterId, eComponentType, tenantId, componentIdentity, tenantDesc, userId);

            // 初始化租户引擎关系
            TenantComponent tenantEngine = new TenantComponent();
            tenantEngine.setTaskType(ComponentTypeToEScheduleJobMapping.getEScheduleTypeByComponentCode(eComponentType.getTypeCode()).getType());
            tenantEngine.setTenantId(tenantId);
            tenantEngine.setComponentIdentity(componentIdentity);
            tenantEngine.setCreateUserId(userId);
            tenantEngine.setStatus(0);
            tenantEngineService.insert(tenantEngine);
        }
    }
}
