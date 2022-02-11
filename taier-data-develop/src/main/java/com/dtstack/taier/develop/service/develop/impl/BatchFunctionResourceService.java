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

import com.dtstack.taier.dao.mapper.BatchFunctionResourceDao;
import com.dtstack.taier.dao.domain.BatchFunctionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchFunctionResourceService {

    @Autowired
    private BatchFunctionResourceDao batchFunctionResourceDao;

    /**
     * 新增记录
     *
     * @param batchFunctionResource
     */
    public void insert(BatchFunctionResource batchFunctionResource) {
        batchFunctionResourceDao.insert(batchFunctionResource);
    }

    /**
     * 根据functionId 更新记录
     *
     * @param batchFunctionResource
     */
    public void updateByFunctionId(BatchFunctionResource batchFunctionResource) {
        batchFunctionResourceDao.update(batchFunctionResource);
    }

    /**
     * 根据函数id获取函数资源关联关系
     *
     * @param functionId
     * @return
     */
    public BatchFunctionResource getResourceFunctionByFunctionId(Long functionId) {
        return batchFunctionResourceDao.getResourceFunctionByFunctionId(functionId);
    }

    /**
     * 根据functionId 删除记录
     *
     * @param functionId
     */
    public void deleteByFunctionId(Long functionId) {
        batchFunctionResourceDao.deleteByFunctionId(functionId);
    }

    /**
     * 根据资源id 获取列表
     * @param resourceId
     * @return
     */
    public  List<BatchFunctionResource> listByResourceId(Long resourceId) {
        return batchFunctionResourceDao.listByResourceId(resourceId);
    }

    /**
     * 根据resource_Id  获取列表
     * @param resource_Id
     * @return
     */
    public List<BatchFunctionResource> listByFunctionResourceId(Long resource_Id) {
        return batchFunctionResourceDao.listByFunctionResourceId(resource_Id);
    }
}
