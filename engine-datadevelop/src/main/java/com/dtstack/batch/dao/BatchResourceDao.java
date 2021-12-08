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

package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchResource;
import com.dtstack.batch.dto.BatchResourceDTO;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author sishu.yss
 */
public interface BatchResourceDao {

    BatchResource getOne(@Param("id") Long id);

    BatchResource getByIdIgnoreDeleted(@Param("id") Long id);

    /**
     * 根据函数找出资源文件地址
     *
     * @param functionId
     * @return
     */
    String getResourceURLByFunctionId(@Param("functionId") Long functionId);


    List<BatchResource> listByIds(@Param("ids") List<Long> resourceIdList, @Param("isDeleted") Integer isDeleted);

    List<BatchResource> listByPidAndProjectId(@Param("projectId") Long projectId, @Param("nodePid") Long nodePid);

    List<BatchResource> listByProjectId(@Param("projectId") long projectId);

    Integer deleteById(@Param("id") Long resourceId, @Param("projectId") Long projectId);

    Integer deleteByIds(@Param("ids") List<Long> resourceIds, @Param("projectId") Long projectId);

    String getUrlById(@Param("id") long id);

    List<BatchResource> listByNameAndProjectId(@Param("projectId") Long projectId, @Param("resourceName") String resourceName, @Param("isDeleted") int isDeleted);

    Integer insert(BatchResource batchResource);

    Integer update(BatchResource batchResource);

    Integer batchInsert(@Param("list") List<BatchResource> list);

    Integer countByProjectId(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId);

    List<BatchResource> generalQuery(PageQuery<BatchResourceDTO> query);

    Integer generalCount(@Param("model") BatchResourceDTO model);

    BatchResource getByName(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("resourceName") String resourceName);

    List<BatchResource> listByUrls(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("list") List<String> list);

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
