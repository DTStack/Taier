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

package com.dtstack.taier.develop.service.console;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.dao.domain.ClusterTenant;
import com.dtstack.taier.dao.domain.Tenant;
import com.dtstack.taier.dao.mapper.ClusterTenantMapper;
import com.dtstack.taier.dao.mapper.TenantMapper;
import com.dtstack.taier.dao.pager.PageQuery;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.dao.pager.Sort;
import com.dtstack.taier.develop.mapstruct.console.TenantTransfer;
import com.dtstack.taier.develop.service.develop.impl.DevelopCatalogueService;
import com.dtstack.taier.develop.vo.console.ClusterTenantVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/7/16
 */
@Service
public class TenantService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantService.class);

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private ClusterTenantMapper clusterTenantMapper;

    @Autowired
    private DevelopCatalogueService developCatalogueService;

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

        List<ClusterTenantVO> clusterTenantVOS = clusterTenants.stream().map(TenantTransfer.INSTANCE::toClusterTenantVO).collect(Collectors.toList());
        return new PageResult(clusterTenantVOS, count, query);
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindingTenant(Long tenantId, Long clusterId,
                              String queueName) throws Exception {
        Tenant tenant = getTenant(tenantId);
        checkTenantBindStatus(tenantId);
        addClusterTenant(tenant.getId(), clusterId);
        if (StringUtils.isNotBlank(queueName)) {
            //hadoop
            updateTenantQueue(tenantId, clusterId, queueName);
        }
        initDataDevelop(tenantId, tenant.getCreateUserId());
    }

    private void checkTenantBindStatus(Long tenantId) {
        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        if (null != clusterId) {
            throw new TaierDefineException("The tenant has been bound");
        }
    }


    private void addClusterTenant(Long tenantId, Long clusterId) {
        ClusterTenant et = new ClusterTenant();
        et.setTenantId(tenantId);
        et.setClusterId(clusterId);
        et.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        et.setGmtModified(new Timestamp(System.currentTimeMillis()));
        clusterTenantMapper.insert(et);
    }

    private Tenant getTenant(Long tenantId) {
        Tenant tenant = tenantMapper.selectById(tenantId);
        if (tenant != null) {
            return tenant;
        }
        throw new TaierDefineException(ErrorCode.TENANT_IS_NULL);
    }

    public List<Tenant> getTenants(List<Long> tenantId) {
        return tenantMapper.selectBatchIds(tenantId);
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

    public void updateTenantQueue(Long tenantId, Long clusterId, String queueName) {
        LOGGER.info("switch queue, tenantId:{} queueName:{} clusterId:{}", tenantId, queueName, clusterId);
        int result = clusterTenantMapper.updateQueueName(tenantId, clusterId, queueName);
        if (result == 0) {
            throw new TaierDefineException("The update engine queue failed");
        }
    }

    public List<Tenant> listAllTenant() {
        return tenantMapper.selectList(Wrappers.lambdaQuery(Tenant.class).eq(Tenant::getIsDeleted, Deleted.NORMAL.getStatus()));
    }

    public Tenant findByName(String tenantName) {
        return tenantMapper.selectOne(Wrappers.lambdaQuery(Tenant.class).eq(Tenant::getTenantName, tenantName));
    }


    public void addTenant(String tenantName, Long createUserId, String tenantIdentity) {
        Tenant tenant = new Tenant();
        tenant.setTenantName(tenantName);
        tenant.setCreateUserId(createUserId);
        tenant.setTenantIdentity(tenantIdentity);
        tenant.setGmtCreate(Timestamp.from(Instant.now()));
        tenant.setGmtModified(Timestamp.from(Instant.now()));
        tenantMapper.insert(tenant);
    }


    @Transactional(rollbackFor = Exception.class)
    public void initDataDevelop(Long tenantId, Long userId) {
        //初始化目录
        developCatalogueService.initCatalogue(tenantId, userId);
    }
}
