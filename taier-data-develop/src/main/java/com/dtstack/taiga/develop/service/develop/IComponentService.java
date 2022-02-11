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

package com.dtstack.taiga.develop.service.develop;

import com.dtstack.taiga.common.enums.EComponentType;

import java.util.List;


public interface IComponentService {

    /**
     * 获取数据源下所有的db
     *
     * @param clusterId      租户ID
     * @param eComponentType 组件类型
     * @param schema         schema
     * @return
     */
    List<String> getAllDataBases(Long clusterId, EComponentType eComponentType, String schema);

    /**
     * 创建对应的DB
     *
     * @param clusterId      集群ID
     * @param eComponentType 组件类型
     * @param dbName         db名称
     * @param comment        db备注
     */
    void createDatabase(Long clusterId, EComponentType eComponentType, String dbName, String comment);

}
