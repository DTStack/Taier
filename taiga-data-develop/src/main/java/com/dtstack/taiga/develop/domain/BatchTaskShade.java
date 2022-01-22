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

package com.dtstack.taiga.develop.domain;

import com.dtstack.taiga.dao.domain.TenantEntity;
import lombok.Data;

@Data
public class BatchTaskShade extends TenantEntity {

    /**
     * '任务名称'
     */
    private String name;

    /**
     * '任务类型 0 sql，1 mr' 2 sync
     */
    private int taskType;

    /**
     * '计算类型 0实时，1 离线'
     */
    private int computeType;

    /**
     * '执行引擎类型 0 flink, 1 spark'
     */
    private int engineType;

    /**
     * 'sql 文本'
     */
    private String sqlText;

    /**
     * '任务参数'
     */
    private String taskParams;

    /**
     * 调度配置
     */
    private String scheduleConf;


    /**
     * 调度状态
     */
    private int scheduleStatus;

    private int submitStatus;

    /**
     * 最后修改task的用户
     */
    private long modifyUserId;

    /**
     * 新建task的用户
     */
    private Long createUserId;

    /**
     * 'task版本'
     */
    private int version;

    private long nodePid;

    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 入口类
     */
    private String mainClass;

    private String exeArgs;

    /**
     * 任务ID
     */
    private Long taskId;
}
