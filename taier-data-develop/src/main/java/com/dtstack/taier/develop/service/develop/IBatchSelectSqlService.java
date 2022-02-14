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

package com.dtstack.taier.develop.service.develop;

import com.dtstack.taier.dao.domain.BatchTask;
import com.dtstack.taier.dao.domain.BatchSelectSql;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.scheduler.vo.action.ActionJobEntityVO;

/**
 * 执行选中的sql语句或者脚本代码
 * Date: 2019/5/21
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public interface IBatchSelectSqlService {

    String runSqlByTask(Long tenantId, ParseResult parseResult, Long userId, String database, Long taskId, Integer type, String preJobId);

    String runSqlByTask(Long tenantId, ParseResult parseResult, Long userId, String database, Boolean isCreateAs, Long taskId, Integer type, String preJobId);

    /**
     * 根据jobId 获取任务执行结果
     * @param task
     * @param selectSql
     * @param tenantId
     * @param userId
     * @param isRoot
     * @param taskType
     * @return
     * @throws Exception
     */
    ExecuteResultVO selectData(BatchTask task, BatchSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception;

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
    ExecuteResultVO selectStatus(BatchTask task, BatchSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType);

    /**
     * 根据jobId 获取任务执行日志
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
    ExecuteResultVO selectRunLog(BatchTask task, BatchSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception;;

    ActionJobEntityVO getTaskStatus(String jobId);
}
