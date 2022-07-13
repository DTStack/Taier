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

@ApiModel("任务基本信息")
public class DevelopTaskEditVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务名称", example = "spark_test", required = true)
    private String name;

    @ApiModelProperty(value = "任务ID", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "任务所属目录", example = "101", required = true)
    private Long catalogueId;

    @ApiModelProperty(value = "任务描述", example = "测试")
    private String desc;

    @ApiModelProperty(value = "版本名称", example = "1.12")
    private String componentVersion;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getCatalogueId() {
        return catalogueId;
    }

    public void setCatalogueId(Long catalogueId) {
        this.catalogueId = catalogueId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }
}
