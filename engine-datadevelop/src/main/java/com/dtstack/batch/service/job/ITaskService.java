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

import com.dtstack.engine.domain.BatchTask;

/**
 * 任务操作相关
 * Date: 2019/5/23
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface ITaskService {

    /**
     * 上传sql到指定的位置
     * eg:
     *    hadoop平台的会将执行sql上传到hdfs上
     * @param dtuicTenantId
     * @param content
     * @param taskType
     * @param taskName
     * @param tenantId
     * @param projectId
     * @return
     */
    String uploadSqlText(Long dtuicTenantId, String content, Integer taskType, String taskName, Long tenantId, Long projectId);

    void readyForPublishTaskInfo(BatchTask task, Long dtuicTenantId, Long projectId);
}
