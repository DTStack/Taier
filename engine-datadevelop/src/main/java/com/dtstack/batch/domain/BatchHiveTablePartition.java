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
 * hive 分区 对应的实体类
 */
@Data
public class BatchHiveTablePartition extends TenantProjectEntity {

    /**
     * hiveTableInfo表的id
     */
    private Long tableId;

    /**
     * 分区名称
     */
    private String partitionName;

    /**
     * 分区location
     */
    private String partition;

    /**
     * 分区大小
     */
    private Long storeSize;

    /**
     * 文件数量
     */
    private Long fileCount;

    /**
     * 最后更新时间
     */
    private Timestamp lastDDLTime;

}
