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
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qianyi
 * @version 1.0
 * @date 2021/1/3 6:55 下午
 */
public class TaskStatusSearchVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务名称")
    private String taskName = "";

    @ApiModelProperty(value = "任务类型")
    private List<Integer> type = new ArrayList<>();

    @ApiModelProperty(value = "任务状态")
    private List<Integer> statusList;

    @ApiModelProperty(value = "组件版本")
    private List<String> componentVersion;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public List<Integer> getType() {
        return type;
    }

    public void setType(List<Integer> type) {
        this.type = type;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public List<String> getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(List<String> componentVersion) {
        this.componentVersion = componentVersion;
    }
}
