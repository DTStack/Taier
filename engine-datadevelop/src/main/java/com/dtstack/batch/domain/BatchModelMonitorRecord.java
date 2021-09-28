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
public class BatchModelMonitorRecord extends TenantProjectEntity {

    /**
     * '不规范模型总数'
     */
    private Integer badTable;
    /**
     * '不规范字段总数'
     */
    private Integer badColumn;
    /**
     * '层级不规范数'
     */
    private Integer grade;
    /**
     * '主题域不规范数'
     */
    private Integer subject;
    /**
     * 刷新不规范数
     */
    private Integer refreshRate;
    /**
     * 增量方式不规范数
     */
    private Integer increType;
    /**
     * '字段名不规范数'
     */
    private Integer colName;
    /**
     * '字段数据类型不规范数'
     */
    private Integer dataType;
    /**
     * '字段描述不规范数'
     */
    private Integer colDesc;

    public BatchModelMonitorRecord(){

    }

    public BatchModelMonitorRecord(Integer badTable, Integer badColumn, Integer grade, Integer subject, Integer refreshRate, Integer increType, Integer colName, Integer dataType, Integer colDesc) {
        this.badTable = badTable;
        this.badColumn = badColumn;
        this.grade = grade;
        this.subject = subject;
        this.refreshRate = refreshRate;
        this.increType = increType;
        this.colName = colName;
        this.dataType = dataType;
        this.colDesc = colDesc;
    }
}
