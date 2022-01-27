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
import com.dtstack.taiga.develop.utils.develop.service.IJdbcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentSparkThriftService implements IComponentService {

    @Autowired
    private IJdbcService iJdbcService;

    @Override
    public List<String> getAllDataBases(Long clusterId, EComponentType eComponentType, String schema) {
        return iJdbcService.getAllDataBases(clusterId, eComponentType, schema);
    }

    @Override
    public void createDatabase(Long clusterId, EComponentType eComponentType, String dbName, String comment) {
        iJdbcService.createDatabase(clusterId, eComponentType, dbName, comment);
    }

}
