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

/**
 * @author sanyue
 */
@Data
public class BatchModelColumn extends TenantProjectEntity {
    /**
     * 类型
     */
    private Integer type;
    /**
     * 指标类型
     */
    private Integer columnType;
    /**
     * 指标命名
     */
    private String columnName;
    /**
     * '指标名称'
     */
    private String columnNameZh;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 指标口径
     */
    private String modelDesc;
    /**
     * '最近修改人id'
     */
    private Long modifyUserId;

    /**
     * 创建者用户id
     */
    private Long createUserId;

}
