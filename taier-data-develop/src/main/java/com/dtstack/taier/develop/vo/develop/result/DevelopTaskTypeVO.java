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

package com.dtstack.taier.develop.vo.develop.result;

import com.dtstack.taier.develop.vo.develop.result.job.TaskProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel("支持的任务类型")
public class DevelopTaskTypeVO {

    @ApiModelProperty(value = "任务类型", example = "0")
    private Integer key;

    @ApiModelProperty(value = "任务描述", example = "SparkSQL")
    private String value;

    @ApiModelProperty(value = "任务计算类型", example = "0：stream 1:batch")
    private Integer computeType;

    private TaskProperties taskProperties;

    @ApiModelProperty(value = "任务类型", example = "0: sql 1:mr 2: sync 3: python shell")
    private Integer jobType;

    public Integer getJobType() {
        return jobType;
    }

    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }

    public TaskProperties getTaskProperties() {
        return taskProperties;
    }

    public void setTaskProperties(TaskProperties taskProperties) {
        this.taskProperties = taskProperties;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DevelopTaskTypeVO(Integer key, String value, Integer computeType) {
        this.key = key;
        this.value = value;
        this.computeType = computeType;
    }

    public DevelopTaskTypeVO() {
    }
}
