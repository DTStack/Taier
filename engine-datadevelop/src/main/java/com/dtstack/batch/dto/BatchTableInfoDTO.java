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

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author jiangbo
 * @time 2018/1/8
 */
@Data
public class BatchTableInfoDTO {

    private Long tenantId;

    private Long projectId;

    private List<Long> tableIds;

    private String tableName;

    private Integer tableType;

    private List<String> tableNames;

    private Integer isDeleted = 0;

    private Integer isDirtyDataTable;

    private String grade;
    private String subject;
    private String refreshRate;
    private String increType;
    private Integer ignore;
    private List<Integer> triggerType;
    private Integer lifeStatus;

    /**
     * 时间段过滤
     */
    private Timestamp mdfBeginTime;

    private Timestamp mdfEndTime;

    private Long modifyUserId;

    /**
     * 表责任人
     */
    private Long chargeUserId;

}
