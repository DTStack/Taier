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

package com.dtstack.batch.service.console;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.batch.domain.TenantComponent;
import com.dtstack.batch.mapstruct.console.TenantTransfer;
import com.dtstack.batch.service.datasource.impl.DatasourceService;
import com.dtstack.batch.service.impl.BatchCatalogueService;
import com.dtstack.batch.service.impl.TenantComponentService;
import com.dtstack.batch.vo.console.ClusterTenantVO;
import com.dtstack.engine.common.enums.Deleted;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.Sort;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.domain.ClusterTenant;
import com.dtstack.engine.domain.Component;
import com.dtstack.engine.domain.Queue;
import com.dtstack.engine.domain.Tenant;
import com.dtstack.engine.mapper.ClusterTenantMapper;
import com.dtstack.engine.mapper.QueueMapper;
import com.dtstack.engine.mapper.TenantMapper;
import com.dtstack.engine.master.impl.pojo.ComponentMultiTestResult;
import com.dtstack.engine.master.service.ComponentService;
import com.dtstack.engine.master.vo.ComponentVO;
import com.dtstack.engine.pager.PageQuery;
import com.dtstack.engine.pager.PageResult;
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
                              Long queueId, String clusterName) throws Exception {
        Tenant tenant = getTenant(tenantId);
        checkTenantBindStatus(tenantId);
        checkClusterCanUse(clusterName);
        addClusterTenant(tenant.getId(), clusterId);
        if (queueId != null) {
            //hadoop
            updateTenantQueue(tenantId, clusterId, queueId);
        }
        initBatch(tenantId, tenant.getCreateUserId(), tenant.getTenantName(), tenant.getTenantDesc());
    }

    private void checkTenantBindStatus(Long tenantId) {
        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        if (null != clusterId) {
            throw new RdosDefineException("The tenant has been bound");
        }
    }


    public void checkClusterCanUse(String clusterName) throws Exception {
        List<ComponentMultiTestResult> testConnectionVO = componentService.testConnects(clusterName);
        boolean canUse = true;
        StringBuilder msg = new StringBuilder();
        msg.append("此集群不可用,测试连通性为通过：\n");
        for (ComponentMultiTestResult testResult : testConnectionVO) {
            EComponentType componentType = EComponentType.getByCode(testResult.getComponentTypeCode());
            if (!noNeedCheck(componentType) && !testResult.getResult()) {
                canUse = false;
                msg.append("组件:").append(componentType.getName()).append(" ").append(JSON.toJSONString(testResult.getErrorMsg())).append("\n");
            }
        }

        if (!canUse) {
            throw new RdosDefineException(msg.toString());
        }
    }

    private Boolean noNeedCheck(EComponentType componentType) {
        switch (componentType) {
            case LIBRA_SQL:
            case IMPALA_SQL:
            case TIDB_SQL:
            case SPARK_THRIFT:
            case CARBON_DATA:
            case SFTP:
                return true;
            default:
                return false;
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

    public Tenant getByDtUicTenantId(Long tenantId) {
        return getTenantById(tenantId);
    }

    public Long getDtuicTenantId(Long id) {
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
    public void bindingQueue(Long queueId,
                             Long tenantId) {
        Queue queue = queueMapper.selectById(queueId);
        if (queue == null) {
            throw new RdosDefineException("Queue does not exist", ErrorCode.DATA_NOT_FIND);
        }

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
    public void initBatch(Long tenantId, Long userId, String tenantName, String tenantDesc) throws Exception {
        //初始化目录
        List<Component> components = componentService.listAllComponents(tenantId);
        List<ComponentVO> componentVOS = ComponentVO.toVOS(components);
        batchCatalogueService.initCatalogue(tenantId, userId, componentVOS);

        //初始化数据源
        datasourceService.initDefaultSource(tenantId, tenantName, tenantDesc, userId);

        //初始化租户引擎
        TenantComponent tenantEngine = new TenantComponent();
        tenantEngine.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        tenantEngine.setTenantId(tenantId);
        tenantEngine.setComponentIdentity(tenantName);
        tenantEngine.setCreateUserId(userId);
        tenantEngine.setStatus(0);
        tenantEngineService.insert(tenantEngine);
    }
}
