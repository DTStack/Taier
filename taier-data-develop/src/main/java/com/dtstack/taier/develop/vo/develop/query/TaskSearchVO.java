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

import java.util.List;

public class TaskSearchVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务名称", example = "taskName")
    private String taskName = "";

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage = 1;

    @ApiModelProperty(value = "页面数量", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "任务状态列表")
    private List<Integer> statusList;

    @ApiModelProperty(value = "任务类型")
    private List<Integer> type;

    @ApiModelProperty(value = "组件版本")
    private List<String> componentVersion;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public List<Integer> getType() {
        return type;
    }

    public void setType(List<Integer> type) {
        this.type = type;
    }

    public List<String> getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(List<String> componentVersion) {
        this.componentVersion = componentVersion;
    }
}
