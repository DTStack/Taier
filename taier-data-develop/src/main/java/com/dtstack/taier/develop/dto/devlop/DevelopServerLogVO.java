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


import java.sql.Timestamp;
import java.util.Map;

/**
 * @author jiangbo
 */
public class DevelopServerLogVO {

    private String name;
    private String logInfo;
    private Timestamp execStartTime;
    private Timestamp execEndTime;
    private Integer taskType = 0;
    private Integer computeType = 0;
    private SyncJobInfo syncJobInfo;
    private String downloadLog;
    private Map<String, String> subNodeDownloadLog;
    //经过几次任务重试
    private Integer pageSize;
    //当前页
    private Integer pageIndex;

    public static class SyncJobInfo{

        private Integer readNum = 0;

        private Integer writeNum = 0;

        private Float dirtyPercent = 0.0F;

        private Long execTime = 0L;

        public Integer getReadNum() {
            return readNum;
        }

        public void setReadNum(Integer readNum) {
            this.readNum = readNum;
        }

        public Integer getWriteNum() {
            return writeNum;
        }

        public void setWriteNum(Integer writeNum) {
            this.writeNum = writeNum;
        }

        public Float getDirtyPercent() {
            return dirtyPercent;
        }

        public void setDirtyPercent(Float dirtyPercent) {
            this.dirtyPercent = dirtyPercent;
        }

        public Long getExecTime() {
            return execTime;
        }

        public void setExecTime(Long execTime) {
            this.execTime = execTime;
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public Timestamp getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Timestamp execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Timestamp getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(Timestamp execEndTime) {
        this.execEndTime = execEndTime;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public SyncJobInfo getSyncJobInfo() {
        return syncJobInfo;
    }

    public void setSyncJobInfo(SyncJobInfo syncJobInfo) {
        this.syncJobInfo = syncJobInfo;
    }

    public String getDownloadLog() {
        return downloadLog;
    }

    public void setDownloadLog(String downloadLog) {
        this.downloadLog = downloadLog;
    }

    public Map<String, String> getSubNodeDownloadLog() {
        return subNodeDownloadLog;
    }

    public void setSubNodeDownloadLog(Map<String, String> subNodeDownloadLog) {
        this.subNodeDownloadLog = subNodeDownloadLog;
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
