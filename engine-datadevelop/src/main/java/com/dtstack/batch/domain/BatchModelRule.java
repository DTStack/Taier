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
public class BatchModelRule extends TenantProjectEntity {
    /**
     * 建表规则配置  ModelTableRule type的自定义排序
     */
    private String rule;

    /**
     * 最后修改人id
     */
    private Long modifyUserId;

    /**
     * 创建者用户id
     */
    private Long createUserId;

}
