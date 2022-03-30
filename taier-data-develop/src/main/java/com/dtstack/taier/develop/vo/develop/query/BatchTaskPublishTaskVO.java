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
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("任务信息")
public class BatchTaskPublishTaskVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long id;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "发布备注", example = "test", required = true)
    private String publishDesc;

    @ApiModelProperty(value = "是否root用户", hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "忽略检查", example = "true", required = true)
    private Boolean ignoreCheck;

    @ApiModelProperty(value = "flink版本", example = "1.12", required = true)
    private String componentVersion;

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPublishDesc() {
        return publishDesc;
    }

    public void setPublishDesc(String publishDesc) {
        this.publishDesc = publishDesc;
    }

    public Boolean getIsRoot() {
        return isRoot;
    }

    public void setIsRoot(Boolean root) {
        isRoot = root;
    }

    public Boolean getIgnoreCheck() {
        return ignoreCheck;
    }

    public void setIgnoreCheck(Boolean ignoreCheck) {
        this.ignoreCheck = ignoreCheck;
    }
}
