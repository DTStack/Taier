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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhiChen
 * @date 2021/9/16 15:59
 */
public class TaskDirtyDataManageVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "任务id")
    private Long taskId;

    @ApiModelProperty(value = "输出类型1.log2.jdbc")
    private String outputType;

    @ApiModelProperty(value = "日志打印频率")
    private Integer logPrintInterval;

    @ApiModelProperty(value = "连接信息json")
    private JSONObject linkInfo;

    @ApiModelProperty(value = "脏数据最大值")
    private Integer maxRows;

    @ApiModelProperty(value = "失败条数")
    private Integer maxCollectFailedRows;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public Integer getLogPrintInterval() {
        return logPrintInterval;
    }

    public void setLogPrintInterval(Integer logPrintInterval) {
        this.logPrintInterval = logPrintInterval;
    }

    public JSONObject getLinkInfo() {
        return linkInfo;
    }

    public void setLinkInfo(JSONObject linkInfo) {
        this.linkInfo = linkInfo;
    }

    public Integer getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
    }

    public Integer getMaxCollectFailedRows() {
        return maxCollectFailedRows;
    }

    public void setMaxCollectFailedRows(Integer maxCollectFailedRows) {
        this.maxCollectFailedRows = maxCollectFailedRows;
    }
}
