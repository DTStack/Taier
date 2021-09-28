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

package com.dtstack.engine.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;

/**
 * Reason:
 * Date: 2017/5/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */
@ApiModel
public class JobGraphTrigger extends BaseEntity {

    /**
     * 触发创建jobGraph的时间
     */
    @ApiModelProperty(notes = "触发创建jobGraph的时间")
    private Timestamp triggerTime;

    private Integer triggerType;
    /**
     * 当前生成的schedule_job的最小id
     */
    private Long minJobId;

    public Timestamp getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Timestamp triggerTime) {
        this.triggerTime = triggerTime;
    }

    public Integer getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(Integer triggerType) {
        this.triggerType = triggerType;
    }

    public Long getMinJobId() {
        return minJobId;
    }

    public void setMinJobId(Long minJobId) {
        this.minJobId = minJobId;
    }
}
