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

package com.dtstack.taier.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/12/29 2:16 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ReturnJobLogVO {

    @ApiModelProperty(value = "提交日志",example = "{\"jobid\":\"application_1634090560347_1335\",\"msg_info\":\"2021-10-15 00:00:01:submit job is success\"}")
    private String logInfo;

    @ApiModelProperty(value = "引擎日志",example = "123")
    private String engineLog;

    @ApiModelProperty(value = "任务信息",example = "select...")
    private String sqlText;

    @ApiModelProperty(value = "重试次数",example = "1")
    private Integer pageSize;

    @ApiModelProperty(value = "当前次数",example = "3")
    private Integer pageIndex;

    @ApiModelProperty(value = "数据同步信息",example = "3")
    private String syncLog;

    @ApiModelProperty(value = "日志下载链接",example = "3")
    private String downLoadUrl;

    public String getDownLoadUrl() {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }

    public String getSyncLog() {
        return syncLog;
    }

    public void setSyncLog(String syncLog) {
        this.syncLog = syncLog;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public String getEngineLog() {
        return engineLog;
    }

    public void setEngineLog(String engineLog) {
        this.engineLog = engineLog;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }
}
