/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.service.develop;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.develop.dto.devlop.BuildSqlVO;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public interface ITaskRunner {

    Logger LOGGER = LoggerFactory.getLogger(ITaskRunner.class);

    List<EScheduleJobType> support();

    /**
     * 运行任务
     *
     * @param userId
     * @param tenantId
     * @param sql
     * @param task
     * @param taskVariables
     * @return
     * @throws Exception
     */
    ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, String sql, Task task, List<Map<String, Object>> taskVariables) throws Exception;


    /**
     * 根据jobId 获取任务执行结果
     *
     * @param task
     * @param selectSql
     * @param tenantId
     * @param userId
     * @param isRoot
     * @param taskType
     * @return
     * @throws Exception
     */
    ExecuteResultVO selectData(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception;

    /**
     * 根据jobId 获取任务执行状态
     *
     * @param task
     * @param selectSql
     * @param tenantId
     * @param userId
     * @param isRoot
     * @param taskType
     * @return
     */
    ExecuteResultVO selectStatus(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType);


    /**
     * 运行日志查看
     *
     * @param jobId
     * @param taskType
     * @param tenantId
     * @param limitNum
     * @return
     */
    ExecuteResultVO runLog(String jobId, Integer taskType, Long tenantId, Integer limitNum);


    /**
     * 完整日志下载
     *
     * @param tenantId
     * @param jobId
     * @param limitNum
     * @return
     */
    IDownload logDownLoad(Long tenantId, String jobId, Integer limitNum);

    /**
     * 获取schema
     */
    List<String> getAllSchema(Long tenantId, Integer taskType);


    /**
     * 获取datasourcex对象信息
     *
     * @param tenantId
     * @param userId
     * @param taskType
     * @return
     */
    ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType, boolean useSchema, Long datasourceId);


    /**
     * 构建sqlVO （支持：sql运行直接走调度任务的方式）
     *
     * @param parseResult
     * @param userId
     * @param task
     * @return
     */
    BuildSqlVO buildSql(ParseResult parseResult, Long userId, Task task);


    /**
     * 执行数据前的准备工作
     * 拼接调度执行的参数
     *
     * @param task
     * @param tenantId
     * @param isRoot
     * @return
     */
    Map<String, Object> readyForSyncImmediatelyJob(Task task, Long tenantId, Boolean isRoot);
}
