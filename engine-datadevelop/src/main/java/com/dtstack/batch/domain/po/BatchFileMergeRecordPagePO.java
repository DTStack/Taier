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

package com.dtstack.batch.domain.po;

import com.dtstack.batch.common.enums.EFileMergeStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BatchFileMergeRecordPagePO {

    /**
     * recordId
     */
    private Long id;

    /**
     * 治理方式
     */
    private Integer mergeType;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * table info id
     */
    private Long tableId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 合并规则id
     */
    private Long ruleId;

    /**
     * 合并状态
     * {@link EFileMergeStatus}
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * hive表在HDFS上的路径
     */
    private String location;

    /**
     * 合并开始时间
     */
    private Timestamp startTime;

    /**
     * 合并结束时间
     */
    private Timestamp endTime;

    /**
     * 是否是分区表，1为true，0为false
     */
    private Integer isPartition;

    /**
     * 创建人用户id
     */
    private Long createUserId;

    /**
     * 修改人用户id
     */
    private Long modifyUserId;

    /**
     * 合并前文件数量
     */
    private Long countBefore;

    /**
     * 合并后文件数量
     */
    private Long countAfter;

    /**
     * 计划时间
     */
    private Timestamp planTime;
}
