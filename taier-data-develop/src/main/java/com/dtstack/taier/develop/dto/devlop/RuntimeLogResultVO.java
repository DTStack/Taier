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

package com.dtstack.taier.develop.dto.devlop;

import io.swagger.annotations.ApiModelProperty;

public class RuntimeLogResultVO {

    @ApiModelProperty(value = "提交日志")
    private String submitLog;

    @ApiModelProperty(value = "engine 日志")
    private String engineLog;

    @ApiModelProperty(value = "总大小")
    private Integer totalBytes;

    @ApiModelProperty(value = "总页码")
    private Integer totalPage;

    @ApiModelProperty(value = "place 位置")
    private Integer place;

    public String getSubmitLog() {
        return submitLog;
    }

    public void setSubmitLog(String submitLog) {
        this.submitLog = submitLog;
    }

    public String getEngineLog() {
        return engineLog;
    }

    public void setEngineLog(String engineLog) {
        this.engineLog = engineLog;
    }

    public Integer getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(Integer totalBytes) {
        this.totalBytes = totalBytes;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPlace() {
        return place;
    }

    public void setPlace(Integer place) {
        this.place = place;
    }

}