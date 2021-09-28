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

import com.dtstack.batch.domain.BatchTaskTask;
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

    List<BatchTaskTask> listByTaskId(@Param("taskId") long taskId);

    List<BatchTaskTask> listByParentTaskId(@Param("parentTaskId") long parentTaskId);

    Integer deleteByTaskId(@Param("taskId") long taskId);

    Integer delete(@Param("id") long id);

    Integer insert(BatchTaskTask batchTaskTask);

    Integer update(BatchTaskTask batchTaskTask);

    Integer deleteByParentId(@Param("parentId") long parentId, @Param("parentAppType") Integer parentAppType);

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("parentAppType") Integer parentAppType);

    List<BatchTaskTask> listTaskTaskByTaskIds(@Param("taskIds") List<Long> taskIds);
}
