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

package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("任务血缘关系信息")
public class DevelopTaskTaskAddOrUpdateVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "父任务列表", required = true)
    private List<DevelopTaskTaskAddOrUpdateDependencyVO> dependencyVOS;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public List<DevelopTaskTaskAddOrUpdateDependencyVO> getDependencyVOS() {
        return dependencyVOS;
    }

    public void setDependencyVOS(List<DevelopTaskTaskAddOrUpdateDependencyVO> dependencyVOS) {
        this.dependencyVOS = dependencyVOS;
    }
}
