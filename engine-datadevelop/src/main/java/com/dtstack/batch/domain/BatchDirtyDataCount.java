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

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author jiangbo
 * @time 2018/1/11
 */
@Data
public class BatchDirtyDataCount extends TenantProjectEntity {

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 脏数据总数
     */
    private long dirtyDataNum;

    /**
     * 运行时间戳
     */
    private Timestamp runTime;

    /**
     * 各类型错误数量
     */
    private long errorNum1;

    private long errorNum2;

    private long errorNum3;

    private long errorNum4;

}
