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

package com.dtstack.taier.dao.dto;


import com.dtstack.taier.dao.domain.Task;

import java.sql.Timestamp;
import java.util.List;

public class BatchTaskDTO extends Task {
    private Timestamp startGmtModified;
    private Timestamp endGmtModified;
    private String fuzzName;
    private List<Integer> taskTypeList;
    private List<Integer> periodTypeList;
    private Integer searchType;

    public Timestamp getStartGmtModified() {
        return startGmtModified;
    }

    public void setStartGmtModified(Timestamp startGmtModified) {
        this.startGmtModified = startGmtModified;
    }

    public Timestamp getEndGmtModified() {
        return endGmtModified;
    }

    public void setEndGmtModified(Timestamp endGmtModified) {
        this.endGmtModified = endGmtModified;
    }

    public String getFuzzName() {
        return fuzzName;
    }

    public void setFuzzName(String fuzzName) {
        this.fuzzName = fuzzName;
    }

    public List<Integer> getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(List<Integer> taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public List<Integer> getPeriodTypeList() {
        return periodTypeList;
    }

    public void setPeriodTypeList(List<Integer> periodTypeList) {
        this.periodTypeList = periodTypeList;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }
}
