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

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 22:10 2020/4/10
 * @Description：表大小持久化对象
 */
@Data
public class BatchTableInfoSizePO implements Serializable {
    /**
     * 表 ID
     */
    private Long id;

    /**
     * 表大小
     */
    private Long tableSize;


    /**
     * 表下文件数量
     */
    private Long tableFileCount;

    /**
     * 表大小更新时间
     */
    private Timestamp sizeUpdateTime;

    /**
     * 生命周期状态，0：未开始，1：存活，2：销毁，3：执行过程出现异常
     */
    private Integer lifeStatus;

    /**
     * 是否分区表
     */
    private Integer isPartition;

    public BatchTableInfoSizePO(Long id, Long tableSize, Long tableFileCount, Timestamp sizeUpdateTime, Integer lifeStatus) {
        this.id = id;
        this.tableSize = tableSize;
        this.tableFileCount = tableFileCount;
        this.sizeUpdateTime = sizeUpdateTime;
        this.lifeStatus = lifeStatus;
    }

    public BatchTableInfoSizePO(Long id, Long tableSize, Long tableFileCount, Timestamp sizeUpdateTime, Integer lifeStatus, Integer isPartition) {
        this.id = id;
        this.tableSize = tableSize;
        this.tableFileCount = tableFileCount;
        this.sizeUpdateTime = sizeUpdateTime;
        this.lifeStatus = lifeStatus;
        this.isPartition = isPartition;
    }
}
