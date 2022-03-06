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

import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.domain.BatchTaskParamShade;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;

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
     * @param taskId
     * @param sql
     * @param isRoot
     * @param task
     * @param dtToken
     * @param isEnd
     * @return
     * @throws Exception
     */
    ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, String uniqueKey, Long taskId, String sql,
                                        Boolean isRoot, Task task, String dtToken, Boolean isEnd, String jobId) throws Exception;


    /**
     * 组装参数 提交调度
     *
     * eg:
     * 任务sql中参数(包括系统参数和自定义参数)的替换
     * @param actionParam
     * @param tenantId
     * @throws Exception
     */
    void readyForTaskStartTrigger(Map<String, Object> actionParam, Long tenantId, Task task, List<BatchTaskParamShade> taskParamsToReplace) throws Exception;

    /**
     * 执行数据前的准备工作
     * eg:
     * 创建分区
     * 拼接engine执行的参数
     * @param task
     * @param tenantId
     * @param isRoot
     * @return
     */
    Map<String, Object> readyForSyncImmediatelyJob(Task task, Long tenantId, Boolean isRoot);

}
