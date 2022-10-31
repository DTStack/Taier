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

package com.dtstack.taier.common.param;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-11 16:28
 * @Description:
 */
public class MetricPO {

    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("subtask_index")
    private Integer subtaskIndex;

    @JsonProperty("task_id")
    private String taskId;

    @JsonProperty("operator_id")
    private String operatorId;

    @JsonProperty("operator_subtask_index")
    private String operatorSubtaskIndex;

    @JsonProperty("quantile")
    private String quantile;

    @JsonProperty("source_id")
    private String sourceId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getSubtaskIndex() {
        return subtaskIndex;
    }

    public void setSubtaskIndex(Integer subtaskIndex) {
        this.subtaskIndex = subtaskIndex;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorSubtaskIndex() {
        return operatorSubtaskIndex;
    }

    public void setOperatorSubtaskIndex(String operatorSubtaskIndex) {
        this.operatorSubtaskIndex = operatorSubtaskIndex;
    }

    public String getQuantile() {
        return quantile;
    }

    public void setQuantile(String quantile) {
        this.quantile = quantile;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}
