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


import com.dtstack.engine.common.annotation.Unique;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ScheduleTaskShade extends ScheduleTask {

    /**
     * 是否发布到了生产环境
     */
    @ApiModelProperty(notes = "是否发布到了生产环境")
    private Long isPublishToProduce;

    @ApiModelProperty(notes = "扩展信息")
    private String extraInfo;

    @Unique
    private Long taskId;

    /**
     * batchJob执行的时候的vesion版本
     */
    @ApiModelProperty(notes = "batchJob执行的时候的vesion版本")
    private Integer versionId;
    /**
     * 选择的运行组件版本
     * e.g Flink 110
     */
    private String componentVersion;

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    private Integer taskRule;

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getIsPublishToProduce() {
        return isPublishToProduce;
    }

    public void setIsPublishToProduce(Long isPublishToProduce) {
        this.isPublishToProduce = isPublishToProduce;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Integer getTaskRule() {
        return taskRule;
    }

    public void setTaskRule(Integer taskRule) {
        this.taskRule = taskRule;
    }
}
