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
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.TenantComponent;
import com.dtstack.taier.dao.mapper.DevelopTenantComponentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 项目关联引擎相关
 * Date: 2019/6/3
 * Company: www.dtstack.com
 * @author xuchao
 */

@Service
public class DevelopTenantComponentService {

    @Autowired
    private DevelopTenantComponentMapper developTenantComponentDao;

    /**
     * 根据 tenantId、taskType 查询组件信息
     *
     */
    public TenantComponent getByTenantAndEngineType(Long tenantId, Integer taskType) {
        TenantComponent tenantComponent = developTenantComponentDao.selectOne(Wrappers.lambdaQuery(TenantComponent.class)
                .eq(TenantComponent::getTenantId, tenantId)
                .eq(TenantComponent::getTaskType, taskType)
                .eq(TenantComponent::getIsDeleted, Deleted.NORMAL.getStatus()));
        if (Objects.nonNull(tenantComponent)) {
            throw new RdosDefineException(ErrorCode.TASK_NOT_CONFIG_DB);
        }
        return tenantComponent;
    }

    @Transactional(rollbackFor = Exception.class)
    public int insert(TenantComponent tenantComponent) {
        tenantComponent.setIsDeleted(Deleted.NORMAL.getStatus());
        return developTenantComponentDao.insert(tenantComponent);
    }

}