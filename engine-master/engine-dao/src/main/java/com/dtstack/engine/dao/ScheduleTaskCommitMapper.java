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

import com.dtstack.engine.domain.ScheduleTaskCommit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/12/14 4:54 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleTaskCommitMapper {


    Boolean insertBatch(@Param("scheduleTaskCommits") List<ScheduleTaskCommit> scheduleTaskCommits);

    String getExtInfoByTaskId(@Param("taskId") Long taskId, @Param("appType") Integer appType,@Param("commitId") String commitId);

    ScheduleTaskCommit getTaskCommitByTaskId(@Param("taskId") Long taskId, @Param("appType") Integer appType,@Param("commitId") String commitId);

    Boolean updateTaskExtInfo(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("info") String info, @Param("commitId") String commitId);

    Long findMinIdOfTaskCommitByCommitId(@Param("commitId") String commitId);

    List<ScheduleTaskCommit> findTaskCommitByCommitId(@Param("minId") Long minId, @Param("commitId") String commitId, @Param("limit") Integer limit);

    Boolean updateTaskCommit(@Param("id") Long id);
}
