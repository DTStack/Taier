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

package com.dtstack.batch.service.job;

import com.dtstack.batch.domain.BatchTaskParamShade;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.engine.domain.BatchTask;

import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2019/5/17
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface IBatchJobExeService {

    /**
     * 直接调用sql执行
     * @param userId
     * @param tenantId
     * @param uniqueKey
     * @param projectId
     * @param taskId
     * @param sql
     * @param isRoot
     * @param dtuicTenantId
     * @param task
     * @param dtToken
     * @param isEnd
     * @return
     * @throws Exception
     */
    ExecuteResultVO startSqlImmediately(Long userId, Long tenantId,
                                        String uniqueKey, long projectId,
                                        long taskId, String sql,
                                        Boolean isRoot, Long dtuicTenantId,
                                        BatchTask task, String dtToken, Boolean isEnd, String jobId) throws Exception;


    /**
     * 解析sql，返回sql对应的uuid以及将sql封装通过引擎执行
     * @param userId
     * @param tenantId
     * @param uniqueKey
     * @param projectId
     * @param taskId
     * @param sqlList
     * @param isRoot
     * @param dtuicTenantId
     * @param task
     * @param dtToken
     * @param database
     * @return
     */
    ExecuteSqlParseVO startSqlSophisticated(Long userId, Long tenantId,
                                            String uniqueKey, long projectId,
                                            long taskId, List<String> sqlList,
                                            Boolean isRoot, Long dtuicTenantId,
                                            BatchTask task, String dtToken, String database)throws Exception;

    /**
     * 组装参数 提交调度
     *
     * eg:
     * 任务sql中参数(包括系统参数和自定义参数)的替换
     * @param actionParam
     * @param dtuicTenantId
     * @throws Exception
     */
    void readyForTaskStartTrigger(Map<String, Object> actionParam, Long dtuicTenantId, BatchTask batchTask, List<BatchTaskParamShade> taskParamsToReplace) throws Exception;

    /**
     * 执行数据前的准备工作
     * eg:
     * 创建分区
     * 拼接engine执行的参数
     * @param batchTask
     * @param jobId
     * @param dtuicTenantId
     * @param isRoot
     * @return
     */
    Map<String, Object> readyForSyncImmediatelyJob(BatchTask batchTask, Long dtuicTenantId, Boolean isRoot);

}
