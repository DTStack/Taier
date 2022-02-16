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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("sql或者脚本执行日志信息")
public class BatchExecuteRunLogResultVO<T> {

    @ApiModelProperty(value = "任务 ID", example = "3")
    private String jobId;

    @ApiModelProperty(value = "sql", example = "select * from test")
    private String sqlText;

    @ApiModelProperty(value = "消息", example = "test")
    private String msg;

    @ApiModelProperty(value = "下载路径")
    private String download;

    @ApiModelProperty(value = "引擎类别", example = "1")
    private Integer taskType;

    @ApiModelProperty(value = "是否需要重新获取日志", example = "false")
    private Boolean retryLog;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Boolean getRetryLog() {
        return retryLog;
    }

    public void setRetryLog(Boolean retryLog) {
        this.retryLog = retryLog;
    }
}
