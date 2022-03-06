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

import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.dto.BatchTaskDTO;
import com.dtstack.taier.dao.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/4
 */
public interface DevelopTaskDao {

    Task getOne(@Param("id") Long id);

    Task getOneWithDeleted(@Param("id") Long id);

    List<Task> generalQuery(PageQuery<BatchTaskDTO> pageQuery);

    List<Task> generalQueryWithoutSql(PageQuery<BatchTaskDTO> pageQuery);

    Integer generalCount(@Param("model") Object model);

    List<Task> listBatchTaskByNodePid(@Param("tenantId") Long tenantId, @Param("nodePid") Long nodePid);

    List<Task> listByNameFuzzy(@Param("tenantId") Long tenantId, @Param("name") String name);

    Integer deleteById(@Param("id") Long id, @Param("gmtModified") Timestamp timestamp, @Param("tenantId") Long tenantId, @Param("modifyUserId") Long userId);

    List<Task> listByTenantId(@Param("tenantId") Long tenantId);

    List<Task> listByIds(@Param("ids") Collection<Long> taskIds);

    List<Task> listByFlowId(@Param("flowId") Long flowId);

    List<Task> listAll();

    Task getByName(@Param("name") String name, @Param("tenantId") Long tenantId);

    List<Task> getByNameList(@Param("nameList") List<String> nameList, @Param("tenantId") Long tenantId);

    Integer insert(Task task);

    Integer update(Task Task);

    Integer updateSubmitStatus(@Param("tenantId") Long tenantId, @Param("id") Long id, @Param("submitStatus") Integer submitStatus, @Param("time") Timestamp time);
    /**
     * 更新提交状态
     * @param id
     * @param submitStatus
     */
    void updateSubmitStatus(@Param("id")Long id,@Param("submitStatus") Integer submitStatus,@Param("errMsg") String errMsg);

    List<Task> listTaskByType(@Param("tenantId") Long tenantId, @Param("type") Integer type, @Param("taskName") String taskName);

    Integer batchUpdateTaskScheduleStatus(@Param("taskIds") List<Long> taskIds, @Param("scheduleStatus") Integer scheduleStatus);

    Integer countByTenantIdAndSubmit(@Param("isSubmit") Integer isSubmit, @Param("tenantId") Long tenantId);
   
    Integer countAll();

    List<Task> catalogueListBatchTaskByNodePid(@Param("tenantId") Long tenantId, @Param("nodePid") Long nodePid);

    Integer updateSqlText(Task task);

    Integer updateScheduleConf(@Param("flowId") Long flowId, @Param("periodType") Integer periodType, @Param("scheduleConf")String scheduleConf);

    void deleteByName(@Param("name") String name, @Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 根据提交状态和任务类型查询
     *
     * @param submitStatus
     * @param taskTypes
     * @return
     */
    List<Task> listAllSubmitTask(@Param("submitStatus") Integer submitStatus, @Param("taskTypes") List<Integer> taskTypes);

    /**
     * 根据任务id列表查询已经提交的任务列表
     *
     * @param taskIds
     * @return
     */
    List<Task> listSubmitTaskByIds(@Param("taskIds") List<Long> taskIds, @Param("tenantId") Long tenantId);

    Integer deleteByTenantId(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

}
