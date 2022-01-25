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

package com.dtstack.taiga.dao.mapper;

import com.dtstack.taiga.dao.domain.BatchTaskTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Reason:
 * Date: 2017/5/5
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public interface BatchTaskTaskDao {

    /**
     * 根据 任务Id 查询锁列表
     * @param taskId
     * @return
     */
    List<BatchTaskTask> listByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据 父任务Id 查询锁列表
     * @param parentTaskId
     * @return
     */
    List<BatchTaskTask> listByParentTaskId(@Param("parentTaskId") Long parentTaskId);

    /**
     * 根据 任务id  删除锁
     * @param taskId
     * @return
     */
    Integer deleteByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据 Id 删除锁
     * @param id
     * @return
     */
    Integer delete(@Param("id") Long id);

    /**
     * 插入锁
     * @param batchTaskTask
     * @return
     */
    Integer insert(BatchTaskTask batchTaskTask);

    /**
     * 更新锁
     * @param batchTaskTask
     * @return
     */
    Integer update(BatchTaskTask batchTaskTask);

    /**
     * 根据父任务id删除项目
     * @param parentId
     * @return
     */
    Integer deleteByParentId(@Param("parentId") Long parentId);


    /**
     * 根据任务Id，获取所有父任务的id
     * @param taskId
     * @return
     */
    List<Long> listParentTaskIdByTaskId(@Param("taskId") Long taskId);
}
