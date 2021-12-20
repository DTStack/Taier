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

import com.dtstack.batch.dao.po.TaskOwnerAndProjectPO;
import com.dtstack.engine.domain.BatchTask;
import com.dtstack.batch.dto.BatchTaskDTO;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/4
 */
public interface BatchTaskDao {

    BatchTask getOne(@Param("id") Long id);

    BatchTask getOneWithDeleted(@Param("id") Long id);

    List<BatchTask> generalQuery(PageQuery<BatchTaskDTO> pageQuery);

    List<BatchTask> generalQueryWithoutSql(PageQuery<BatchTaskDTO> pageQuery);


    Integer generalCount(@Param("model") Object model);

    List<BatchTask> listBatchTaskByNodePid(@Param("nodePid") long nodePid, @Param("projectId") long projectId);

    List<BatchTask> listByNameFuzzy(@Param("projectId") long projectId, @Param("name") String name);

    Integer deleteById(@Param("id") Long id, @Param("gmtModified") Timestamp timestamp, @Param("projectId") Long projectId, @Param("modifyUserId") Long userId);

    List<BatchTask> listByProjectId(@Param("projectId") long projectId);

    List<BatchTask> listByIds(@Param("ids") Collection<Long> taskIds);

    List<BatchTask> listByFlowId(@Param("flowId") Long flowId);

    List<BatchTask> listAll();

    BatchTask getByName(@Param("name") String name, @Param("projectId") Long projectId);

    List<BatchTask> getByNameList(@Param("nameList") List<String> nameList, @Param("projectId") Long projectId);

    Integer insert(BatchTask batchTask);

    Integer update(BatchTask batchTask);

    Integer updateSubmitStatus(@Param("projectId") Long projectId, @Param("id") Long id, @Param("submitStatus") Integer submitStatus, @Param("time") Timestamp time);

    List<BatchTask> listTaskByType(@Param("projectId") Long projectId, @Param("type") Integer type, @Param("taskName") String taskName);

    Integer batchUpdateTaskScheduleStatus(@Param("taskIds") List<Long> taskIds, @Param("scheduleStatus") Integer scheduleStatus);

    Integer countByProjectIdAndSubmit(@Param("isSubmit") Integer isSubmit, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId);
   
    Map<String, Object> countScienceJobStatus(@Param("projectIds") List<Long> projectIds, @Param("taskType")Integer taskType, @Param("tenantId") Long tenantId);

    Integer countAll();

    List<BatchTask> catalogueListBatchTaskByNodePid(@Param("nodePid") Long nodePid, @Param("tenantId") Long tenantId);

    Integer updateSqlText(BatchTask batchTask);

    Integer updateScheduleConf(@Param("flowId") Long flowId, @Param("periodType") Integer periodType, @Param("scheduleConf")String scheduleConf);

    void deleteByName(@Param("name") String name, @Param("projectId") Long projectId, @Param("userId") Long userId);

    /**
     * 根据提交状态和任务类型查询
     *
     * @param submitStatus
     * @param taskTypes
     * @return
     */
    List<BatchTask> listAllSubmitTask(@Param("submitStatus") Integer submitStatus, @Param("taskTypes") List<Integer> taskTypes);

    /**
     * 根据任务id列表查询已经提交的任务列表
     *
     * @param taskIds
     * @return
     */
    List<BatchTask> listSubmitTaskByIds(@Param("taskIds") List<Long> taskIds, @Param("projectId") Long projectId);

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    List<TaskOwnerAndProjectPO> getTaskOwnerAndProjectId();

    /**
     * 更新此项目下的任务负责人
     *
     * @param oldOwnerUserId
     * @param newOwnerUserId
     * @param projectId
     * @return
     */
    Integer updateTaskOwnerUser(@Param("oldOwnerUserId") Long oldOwnerUserId, @Param("newOwnerUserId") Long newOwnerUserId, @Param("projectId") Long projectId);
}
