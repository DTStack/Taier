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

package com.dtstack.taier.dao.mapper;

import com.dtstack.taier.dao.domain.BatchTaskResourceShade;
import org.apache.ibatis.annotations.Param;

/**
 * Created by jiangbo on 2017/5/3 0003.
 */
public interface DevelopTaskResourceShadeDao {

    /**
     * 根据 Id 查询
     * @param id
     * @return
     */
    BatchTaskResourceShade getOne(@Param("id") Long id);

    /**
     * 根据 任务Id 查询
     * @param taskId
     * @return
     */
    Integer deleteByTaskId(@Param("taskId") long taskId);

    /**
     * 插入任务资源
     * @param batchTaskResourceShade
     * @return
     */
    Integer insert(BatchTaskResourceShade batchTaskResourceShade);

    /**
     * 更新任务资源
     * @param batchTaskResourceShade
     * @return
     */
    Integer update(BatchTaskResourceShade batchTaskResourceShade);

}
