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

package com.dtstack.taier.dao.domain;

import lombok.Data;

/**
 * date: 2022/1/24 3:14 下午
 * author: zhaiyue
 */
@Data
public class BatchReadWriteLock  extends BaseEntity{

    /**
     * 锁名称
     */
    private String lockName;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 修改的用户
     */
    private Long modifyUserId;

    /**
     * 锁版本号
     */
    private Integer version;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 任务Id
     */
    private Long relationId;

    /**
     * 任务类型
     */
    private String type;


}
