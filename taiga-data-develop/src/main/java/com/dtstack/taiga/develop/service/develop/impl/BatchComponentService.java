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

package com.dtstack.taiga.develop.service.develop.impl;

import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.develop.service.develop.IComponentService;
import com.dtstack.taiga.develop.service.develop.MultiEngineServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchComponentService {

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    /**
     * 获取集群对应组件下数据源中的databases
     *
     * @param clusterId         集群ID
     * @param componentTypeCode 组件类型
     * @return
     */
    public List<String> getAllDataBases(Long clusterId, Integer componentTypeCode) {
        EComponentType eComponentType = EComponentType.getByCode(componentTypeCode);
        IComponentService componentService = multiEngineServiceFactory.getComponentService(componentTypeCode);
        return componentService.getAllDataBases(clusterId, eComponentType, "");
    }

}

