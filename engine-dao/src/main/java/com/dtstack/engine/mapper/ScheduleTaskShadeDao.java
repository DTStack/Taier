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

package com.dtstack.engine.mapper;

import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/4
 */
public interface ScheduleTaskShadeDao {

    ScheduleTaskShade getOne(@Param("taskId") long taskId);

    List<ScheduleTaskShade> listTaskByStatus(@Param("startId") Long startId, @Param("submitStatus") Integer submitStatus, @Param("projectScheduleStatus") Integer projectScheduleStatus, @Param("batchTaskSize") Integer batchTaskSize
    ,@Param("projectIds")Collection<Long> projectIds, @Param("appType")Integer appType);

    Integer countTaskByStatus(@Param("submitStatus") Integer submitStatus, @Param("projectScheduleStatus") Integer projectScheduleStatus,@Param("projectIds")Collection<Long> projectIds, @Param("appType")Integer appType);


    List<ScheduleTaskShade> listByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("isDeleted") Integer isDeleted, @Param("appType")Integer appType);

    List<ScheduleTaskShade> listByNameLike(@Param("projectId") Long projectId, @Param("name") String name, @Param("appType")Integer appType, @Param("ownerId") Long ownerId, @Param("projectIds") List<Long> projectIds);

    List<ScheduleTaskShade> listByNameLikeWithSearchType(@Param("projectId") Long projectId, @Param("name") String name, @Param("appType")Integer appType,
                                                   @Param("ownerId") Long ownerId, @Param("projectIds") List<Long> projectIds,@Param("searchType")Integer searchType);

    List<ScheduleTaskShade> listByName(@Param("projectId") long projectId, @Param("name") String name);

    List<ScheduleTaskShade> listByNameLikeFront(@Param("projectId") long projectId, @Param("name") String name);

    List<ScheduleTaskShade> listByNameLikeTail(@Param("projectId") long projectId, @Param("name") String name);

    ScheduleTaskShade getByName(@Param("projectId") long projectId, @Param("name") String name, @Param("appType") Integer appType,@Param("flowId") Long flowId);

    List<Map<String,Object>> listDependencyTask(@Param("projectId") long projectId, @Param("name") String name, @Param("taskIds") List<Long> taskIds);

    List<Map<String,Object>> listByTaskIdsNotIn(@Param("projectId") long projectId, @Param("taskIds") List<Long> taskIds);

    Integer updateTaskName(@Param("taskId") long taskId, @Param("name") String name,@Param("appType")Integer appType);

    Integer delete(@Param("taskId") long taskId, @Param("userId") long modifyUserId,@Param("appType")Integer appType);

    Integer insert(ScheduleTaskShade batchTaskShade);

    List<ScheduleTaskForFillDataDTO> listSimpleTaskByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("isDeleted") Integer isDeleted, @Param("appType")Integer appType);

    Integer update(ScheduleTaskShade batchTaskShade);

    List<ScheduleTaskShade> listByType(@Param("projectId") Long projectId, @Param("type") Integer type, @Param("taskName") String taskName);

    List<ScheduleTaskShade> generalQuery(PageQuery<ScheduleTaskShadeDTO> pageQuery);

    Integer generalCount(@Param("model") Object model);

    Integer batchUpdateTaskScheduleStatus(@Param("taskIds") List<Long> taskIds, @Param("scheduleStatus") Integer scheduleStatus,@Param("appType")Integer appType);

    List<ScheduleTaskShade> simpleQuery(PageQuery<ScheduleTaskShadeDTO> pageQuery);

    Integer simpleCount(@Param("model") ScheduleTaskShadeDTO pageQuery);

    Integer countPublishToProduce(@Param("projectId") Long projectId,@Param("appType")Integer appType);


    String getSqlTextById(@Param("id") Long id);

    ScheduleTaskShade getWorkFlowTopNode(@Param("workFlowId") Long workFlowId, @Param("appType") Integer appType);

    /**
     *  ps- 省略了一些大字符串 如 sql_text、task_params
     * @param taskIds
     * @param isDeleted
     * @return
     */
    List<ScheduleTaskShade> listSimpleByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("isDeleted") Integer isDeleted,@Param("appType") Integer appType);

    ScheduleTaskShade getOneWithDeleted(@Param("id") Long id, @Param("isDeleted") Integer isDeleted, @Param("appType") Integer appType);

    void updateTaskExtInfo(@Param("taskId") long taskId, @Param("appType")Integer appType, @Param("extraInfo") String extraInfo);

    String getExtInfoByTaskId(@Param("taskId") long taskId, @Param("appType")Integer appType);

    List<ScheduleTaskShade> getExtInfoByTaskIds(@Param("taskIds") List<Long> taskIds, @Param("appType")Integer appType);

    ScheduleTaskShade getById(@Param("id") Long id);

    void updateProjectScheduleStatus(@Param("projectId")Long projectId,@Param("appType")Integer appType,@Param("scheduleStatus") Integer scheduleStatus);
    
    List<ScheduleTaskShade> findFuzzyTaskNameByCondition(@Param("name") String name, @Param("appType") Integer appType, @Param("uicTenantId") Long uicTenantId, @Param("projectId") Long projectId, @Param("fuzzyProjectByProjectAliasLimit") Integer fuzzyProjectByProjectAliasLimit,@Param("projectScheduleStatus") Integer projectScheduleStatus);

    List<ScheduleTaskShade> getChildTaskByOtherPlatform(@Param("taskId") Long taskId, @Param("limit") Integer limit);

    List<ScheduleTaskShade> getTaskOtherPlatformByProjectId(@Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("limit") Integer limit);

    List<ScheduleTaskShade> listTaskRuleTask(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    /**
     * 根据appType和taskId查询任务
     * @param taskId
     * @param appType
     * @return
     */
    ScheduleTaskShade getOneByTaskIdAndAppType(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    /**
     * @param useUicTenantList 不能为空
     * @param componentVersion 不能为空
     * @return
     */
    Long hasTaskSubmit(@Param("useUicTenantList") List<Long> useUicTenantList, @Param("componentVersion") String componentVersion);

    List<ScheduleTaskShade> listByUicTenantId(@Param("tenantIds") List<Long> tenantIds, @Param("appType") Integer appType);
}
