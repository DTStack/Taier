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
 * @author sanyue
 */
@Data
public class BatchModelTable extends TenantEntity {
    /**
     * 1 层级 2 主题域 3 刷新频率 4 增量定义',
     */
    private Integer type;
    /**
     * 层级编号
     */
    private Integer level;
    /**
     * '名称定义'
     */
    private String name;
    /**
     * 说明
     */
    private String modelDesc;
    /**
     * 前缀标识
     */
    private String prefix;
    /**
     * '生命周期  单位：天',
     */
    private Integer lifeDay;
    /**
     * 是否层级依赖',
     */
    private Integer depend;
    /**
     * 最近修改人id
     */
    private Long modifyUserId;

    /**
     * 创建者用户id
     */
    private Long createUserId;

}
