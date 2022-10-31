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

import java.util.List;

/**
 * @author jiangbo
 * @date 2018/7/19 9:44
 */
public class ScheduleJobJobDTO {

    private String jobKey;

    private Integer relyType;

    private Integer level;

    private List<ScheduleJobJobDTO> children;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public List<ScheduleJobJobDTO> getChildren() {
        return children;
    }

    public void setChildren(List<ScheduleJobJobDTO> children) {
        this.children = children;
    }

    public Integer getRelyType() {
        return relyType;
    }

    public void setRelyType(Integer relyType) {
        this.relyType = relyType;
    }
}
