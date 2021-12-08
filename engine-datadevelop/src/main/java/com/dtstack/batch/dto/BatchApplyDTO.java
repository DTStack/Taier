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
 * @date 2018/5/24 11:27
 */
@Data
public class BatchApplyDTO {

    private Long tenantId;

    private List<Long> projectIds;

    private List<Integer> statusList;

    private Long userId;

    private Long dealUserId;

    private Integer isCancel;

    private Integer isRevoke;

    private String resourceName;

    private Timestamp startTime;

    private Timestamp endTime;

    private Integer isDeleted;

    private Integer applyResourceType;

    private Long excludeUserId;

    private Integer tableType;


    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public Integer getIsRevoke() {
        return isRevoke;
    }

    public void setIsRevoke(Integer isRevoke) {
        this.isRevoke = isRevoke;
    }
}
