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

package com.dtstack.batch.dto;

import com.dtstack.engine.domain.TenantEntity;
import lombok.Data;

/**
 * <p>小文件合并规则查询对象
 *
 * @author ：wangchuan
 * date：Created in 11:31 上午 2020/12/14
 * company: www.dtstack.com
 */
@Data
public class BatchFileMergeRuleDTO extends TenantEntity {

    /**
     * table info id
     */
    private Long projectId;

    /**
     * 合并规则状态
     */
    private Integer status;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 创建人用户id
     */
    private Long createUserId;

    /**
     * 治理方式 1 周期  2 一次性
     */
    private Integer mergeType;
}
