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

package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantEntity;
import lombok.Data;

/**
 * @author : toutian
 */
@Data
public class BatchTaskVersion extends TenantEntity {

    private Long taskId;

    private String originSql;

    /**
     * 'sql 文本'
     */
    private String sqlText;

    /**
     * 'sql 文本'
     */
    private String publishDesc;

    /**
     * 新建task的用户
     */
    private Long createUserId;

    /**
     * 'task版本'
     */
    private Integer version;


    /**
     * 环境参数
     */
    private String taskParams;
    /**
     * 调度信息
     */
    private String scheduleConf;

    private Integer scheduleStatus;
    /**
     * 依赖的任务id
     */
    private String dependencyTaskIds;

}


