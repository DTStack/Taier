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
import com.dtstack.taier.dao.domain.BatchFunctionResource;
import com.dtstack.taier.dao.mapper.DevelopFunctionResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DevelopFunctionResourceService {

    @Autowired
    private DevelopFunctionResourceMapper developFunctionResourceDao;

    /**
     * 新增记录
     *
     * @param batchFunctionResource
     */
    public void insert(BatchFunctionResource batchFunctionResource) {
        batchFunctionResource.setIsDeleted(Deleted.NORMAL.getStatus());
        developFunctionResourceDao.insert(batchFunctionResource);
    }

    /**
     * 根据functionId 更新记录
     *
     * @param batchFunctionResource
     */
    public void updateByFunctionId(BatchFunctionResource batchFunctionResource) {
        developFunctionResourceDao.update(batchFunctionResource,Wrappers.lambdaUpdate(BatchFunctionResource.class)
                                    .eq(BatchFunctionResource::getFunctionId,batchFunctionResource.getFunctionId())
                                    .eq(BatchFunctionResource::getIsDeleted,Deleted.NORMAL.getStatus()));
    }

    /**
     * 根据函数id获取函数资源关联关系
     *
     * @param functionId
     * @return
     */
    public BatchFunctionResource getResourceFunctionByFunctionId(Long functionId) {
        return developFunctionResourceDao.getResourceFunctionByFunctionId(functionId);
    }

    /**
     * 根据functionId 删除记录
     *
     * @param functionId
     */
    public void deleteByFunctionId(Long functionId) {
        developFunctionResourceDao.deleteByFunctionId(functionId);
    }

    /**
     * 根据资源id 获取列表
     * @param resourceId
     * @return
     */
    public  List<BatchFunctionResource> listByResourceId(Long resourceId) {
        return developFunctionResourceDao.listByResourceId(resourceId);
    }

}
