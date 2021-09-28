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

import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageColumnColumnAppRef
 * @Description 字段级血缘应用关联表
 * @Date 2020/10/28 19:46
 * @Created chener@dtstack.com
 */
public class LineageColumnColumnUniqueKeyRef extends BaseEntity{

    @ApiModelProperty(notes = "app类型")
    private Integer appType;

    @ApiModelProperty(notes = "血缘批次码，离线中通常为taskId")
    private String uniqueKey;

    @ApiModelProperty(notes = "字段血缘关联id")
    private Long lineageColumnColumnId;

    /**任务提交版本号**/
    private Integer versionId;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getLineageColumnColumnId() {
        return lineageColumnColumnId;
    }

    public void setLineageColumnColumnId(Long lineageColumnColumnId) {
        this.lineageColumnColumnId = lineageColumnColumnId;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }
}
