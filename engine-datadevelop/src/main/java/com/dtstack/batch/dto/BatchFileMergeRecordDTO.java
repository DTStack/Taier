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

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>小文件合并表记录查询对象
 *
 * @author ：wangchuan
 * date：Created in 11:32 上午 2020/12/14
 * company: www.dtstack.com
 */
@Data
public class BatchFileMergeRecordDTO extends TenantProjectEntity {

    /**
     * 规则Id
     */
    private Long ruleId;

    /**
     * hiveTableInfo的表id
     */
    private Long tableId;

    /**
     * 规则类型 一次性还是 周期
     */
    private Integer ruleType;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 合并状态
     * {@link com.dtstack.batch.common.enums.EFileMergeStatus}
     */
    private List<Integer> status;

    /**
     * 创建用户id
     */
    private Long createUserId;

    /**
     * 修改用户id
     */
    private Long modifyUserId;
}
