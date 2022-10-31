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

import com.dtstack.taier.dao.domain.ScheduleJobExpand;

public class FlinkServerLogVO {



    private String jobId;

    /**
     * 提交过程的错误信息
     */
    private String logInfo;

    /**
     * 引擎执行的错误信息
     */
    private String engineLog;

    /**
     * 日志下载
     */
    private String downLoadLog;

    private String submitLog;


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

    public String getDownLoadLog() {
        return downLoadLog;
    }

    public void setDownLoadLog(String downLoadLog) {
        this.downLoadLog = downLoadLog;
    }

    public String getSubmitLog() {
        return submitLog;
    }

    public void setSubmitLog(String submitLog) {
        this.submitLog = submitLog;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    public FlinkServerLogVO(String jobId, String logInfo, String engineLog) {
        this.jobId = jobId;
        this.logInfo = logInfo;
        this.engineLog = engineLog;
    }

    public FlinkServerLogVO(ScheduleJobExpand scheduleJobExpand, String downLoadLog,String submitLog) {
        this.jobId = scheduleJobExpand.getJobId();
        this.logInfo = scheduleJobExpand.getLogInfo();
        this.engineLog = scheduleJobExpand.getEngineLog();
        this.downLoadLog = downLoadLog;
        this.submitLog =submitLog;
    }
}
