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

package com.dtstack.taier.develop.service.develop.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EComputeType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.TenantComponent;
import com.dtstack.taier.dao.mapper.DevelopTenantComponentMapper;
import com.dtstack.taier.develop.service.console.ClusterTenantService;
import com.dtstack.taier.develop.service.develop.ITaskRunner;
import com.dtstack.taier.develop.service.develop.TaskConfiguration;
import com.dtstack.taier.develop.vo.develop.result.DevelopTaskGetSupportJobTypesResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTenantComponentResultVO;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class DevelopTenantComponentService {

    @Autowired
    private DevelopTenantComponentMapper developTenantComponentDao;

    @Autowired
    private DevelopTaskService developTaskService;

    @Autowired
    private ClusterTenantService clusterTenantService;

    @Autowired
    private TaskConfiguration taskConfiguration;

    /**
     * 根据 tenantId、taskType 查询组件信息
     *
     * @param tenantId
     * @param taskType
     * @return
     */
    public TenantComponent getByTenantAndTaskType(Long tenantId, Integer taskType) {
        TenantComponent tenantComponent = developTenantComponentDao.selectOne(Wrappers.lambdaQuery(TenantComponent.class)
                .eq(TenantComponent::getTenantId, tenantId)
                .eq(TenantComponent::getTaskType, taskType)
                .eq(TenantComponent::getIsDeleted, Deleted.NORMAL.getStatus()));
        if (Objects.isNull(tenantComponent)) {
            throw new RdosDefineException(ErrorCode.TASK_NOT_CONFIG_DB);
        }
        return tenantComponent;
    }

    /**
     * 获取当前租户配置的任务组件运行信息
     *
     * @param tenantId
     * @return
     */
    public List<DevelopTenantComponentResultVO> selectTenantComponentList(Long tenantId) {
        List<DevelopTaskGetSupportJobTypesResultVO> supportJobTypes = developTaskService.getSupportJobTypes(tenantId);
        if (CollectionUtils.isEmpty(supportJobTypes)) {
            return Lists.newArrayList();
        }
        Map<Integer, TenantComponent> tenantComponentMap = developTenantComponentDao.selectList(Wrappers.lambdaQuery(TenantComponent.class)
                        .eq(TenantComponent::getTenantId, tenantId)
                        .eq(TenantComponent::getIsDeleted, Deleted.NORMAL.getStatus()))
                .stream()
                .collect(Collectors.toMap(TenantComponent::getTaskType, Function.identity(), (key1, key2) -> key2));

        return supportJobTypes.stream()
                .filter(jobTypesResultVO -> EJobType.SQL.getType() == EScheduleJobType.getByTaskType(jobTypesResultVO.getKey()).getEngineJobType()
                        && EComputeType.BATCH.equals(EScheduleJobType.getByTaskType(jobTypesResultVO.getKey()).getComputeType()))
                .map(supportJobTypeInfo -> {
                    DevelopTenantComponentResultVO resultVO = new DevelopTenantComponentResultVO();
                    resultVO.setTaskType(supportJobTypeInfo.getKey());
                    resultVO.setTaskTypeName(supportJobTypeInfo.getValue());
                    resultVO.setSchema(tenantComponentMap.getOrDefault(supportJobTypeInfo.getKey(), new TenantComponent()).getComponentIdentity());
                    return resultVO;
                }).collect(Collectors.toList());
    }

    /**
     * 保存组件运行schema信息
     *
     * @param tenantId
     * @param taskType
     * @param schema
     */
    public void saveTenantComponentInfo(Long tenantId, Integer taskType, String schema) {
        TenantComponent tenantComponent = developTenantComponentDao.selectOne(Wrappers.lambdaQuery(TenantComponent.class)
                .eq(TenantComponent::getTenantId, tenantId)
                .eq(TenantComponent::getTaskType, taskType));

        if (Objects.isNull(tenantComponent)) {
            tenantComponent = new TenantComponent();
            tenantComponent.setTenantId(tenantId);
            tenantComponent.setTaskType(taskType);
            tenantComponent.setComponentIdentity(schema);
            developTenantComponentDao.insert(tenantComponent);
        } else {
            developTenantComponentDao.update(null, Wrappers.lambdaUpdate(TenantComponent.class)
                    .set(TenantComponent::getComponentIdentity, schema)
                    .set(TenantComponent::getGmtModified, LocalDateTime.now())
                    .eq(TenantComponent::getId, tenantComponent.getId()));
        }
    }

    /**
     * 获取当前任务对应组件数据源中的shema列表
     *
     * @param tenantId
     * @param taskType
     * @return
     */
    public List<String> getAllSchemaByTenantAndTaskType(Long tenantId, Integer taskType) {
        EScheduleJobType scheduleJobType = EScheduleJobType.getByTaskType(taskType);
        Long clusterId = clusterTenantService.getClusterIdByTenantId(tenantId);
        if (Objects.isNull(clusterId)) {
            throw new RdosDefineException(ErrorCode.CLUSTER_NOT_CONFIG);
        }
        ITaskRunner taskRunner = taskConfiguration.get(scheduleJobType);
        return taskRunner.getAllSchema(tenantId, taskType);
    }

}