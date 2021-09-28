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

package com.dtstack.engine.dao;

import com.dtstack.engine.domain.ScheduleTaskTaskShade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleTaskTaskShadeDao {

    ScheduleTaskTaskShade getOne(@Param("id") long id);

    ScheduleTaskTaskShade getOneByTaskId(@Param("taskId") Long taskId, @Param("parentTaskId") Long parentTaskId,@Param("appType")Integer appType);

    List<ScheduleTaskTaskShade> listChildTask(@Param("parentTaskId") long parentTaskId,@Param("appType")Integer appType);

    List<ScheduleTaskTaskShade> listChildTaskLimit(@Param("parentTaskId") Long taskId, @Param("appType") Integer appType, @Param("limit") Integer limit);

    List<ScheduleTaskTaskShade> listParentTask(@Param("childTaskId") long childTaskId,@Param("appType")Integer appType);

    Integer deleteByTaskId(@Param("taskId") long taskId,@Param("appType")Integer appType);

    Integer insert(ScheduleTaskTaskShade scheduleTaskTaskShade);

    Integer update(ScheduleTaskTaskShade scheduleTaskTaskShade);

    List<ScheduleTaskTaskShade> listParentTaskKeys(@Param("taskKeys") List<String> taskKeys);

    List<ScheduleTaskTaskShade> listTaskKeys(@Param("taskKeys") List<String> taskKeys);

    List<ScheduleTaskTaskShade> getTaskOtherPlatformByProjectId(@Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("listChildTaskLimit") Integer listChildTaskLimit);
}
