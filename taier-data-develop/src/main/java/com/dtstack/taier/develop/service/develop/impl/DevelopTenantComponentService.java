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
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.dao.domain.TenantComponent;
import com.dtstack.taier.dao.mapper.DevelopTenantComponentDao;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 项目关联引擎相关
 * Date: 2019/6/3
 * Company: www.dtstack.com
 * @author xuchao
 */

@Service
public class DevelopTenantComponentService {

    @Autowired
    private DevelopTenantComponentDao developTenantComponentDao;

    @Autowired
    private DatasourceService datasourceService;

    /**
     * 根据 tenantId、taskType 查询组件信息
     *
     */
    public TenantComponent getByTenantAndEngineType(Long tenantId, Integer taskType) {

        // SparkSql、HiveSql 都使用的同一个db，所以需要特殊处理
        if (EScheduleJobType.SPARK_SQL.getType().equals(taskType)
            || EScheduleJobType.HIVE_SQL.getType().equals(taskType)) {
            taskType = datasourceService.getHadoopDefaultJobTypeByTenantId(tenantId).getType();
        }

        return developTenantComponentDao.selectOne(Wrappers.lambdaQuery(TenantComponent.class)
                        .eq(TenantComponent::getTenantId,tenantId)
                        .eq(TenantComponent::getTaskType,taskType)
                        .eq(TenantComponent::getIsDeleted, Deleted.NORMAL.getStatus())
                        .last("limit 1"));
    }

    @Transactional(rollbackFor = Exception.class)
    public int insert(TenantComponent tenantComponent) {
        tenantComponent.setIsDeleted(Deleted.NORMAL.getStatus());
        return developTenantComponentDao.insert(tenantComponent);
    }

}