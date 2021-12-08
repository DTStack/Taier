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
import com.dtstack.batch.domain.BatchTaskResourceShade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by jiangbo on 2017/5/3 0003.
 */
public interface BatchTaskResourceShadeDao {

    BatchTaskResourceShade getOne(@Param("id") Long id);

    List<BatchTaskResourceShade> listByTaskId(@Param("taskId") long taskId, @Param("resourceType") Integer resourceType, @Param("projectId") long projectId);

    Integer deleteByTaskId(@Param("taskId") long taskId);

    Integer insert(BatchTaskResourceShade batchTaskResourceShade);

    Integer update(BatchTaskResourceShade batchTaskResourceShade);

    List<BatchResource> listResourceByTaskId(@Param("taskId") long taskId, @Param("resourceType") Integer resourceType, @Param("projectId") long projectId);

    Integer deleteByProjectId(@Param("projectId") Long projectId);
}
