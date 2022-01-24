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

package com.dtstack.taiga.develop.service.impl;

import com.dtstack.taiga.dao.domain.TenantComponent;
import com.dtstack.taiga.dao.mapper.TenantComponentDao;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TenantComponentService {

    @Autowired
    private TenantComponentDao tenantComponentDao;

    public TenantComponent getByTenantAndEngineType(Long tenantId, Integer taskType) {
        return tenantComponentDao.getByTenantAndTaskType(tenantId, taskType);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insert(TenantComponent tenantComponent) {
        return tenantComponentDao.insert(tenantComponent);
    }


}