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

import com.dtstack.engine.domain.BaseEntity;
import lombok.Data;

/**
 * @author jiangbo
 * @time 2017/12/7
 */
@Data
public class BatchTableTable  extends BaseEntity {

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 数据源id，hive类型的id为-1
     */
    private Long dataSourceId;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 字段名称
     */
    private String col;

    /**
     * 数据来源表所属项目id
     */
    private Long inputProjectId;

    /**
     * 数据来源表的数据源id，hive为-1
     */
    private Long inputDataSourceId;

    /**
     * 数据来源表的数据类型
     */
    private String inputTableName;

    /**
     * 数据来源表的数据类型
     */
    private String inputCol;

    /**
     * 血缘唯一标识
     */
    private String uniqueKey;

    /**
     * 所属任务
     */
    private Long taskId;


    public BatchTableTable() {
    }

    public BatchTableTable(String tableName, String col, String inputTableName, String inputCol) {
        this.tableName = tableName;
        this.col = col;
        this.inputTableName = inputTableName;
        this.inputCol = inputCol;
    }

}
