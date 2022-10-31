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

package com.dtstack.taier.scheduler.dto.scheduler;

import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2022/2/16 3:28 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class SimpleScheduleJobDTO {

    /**
     * 记录id
     */
    private Long id;

    /**
     * 实例id
     */
    private String jobId;

    /**
     * 任务类型
     */
    private Integer type;

    /**
     * 队列状态
     */
    private Integer phaseStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPhaseStatus() {
        return phaseStatus;
    }

    public void setPhaseStatus(Integer phaseStatus) {
        this.phaseStatus = phaseStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleScheduleJobDTO that = (SimpleScheduleJobDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(jobId, that.jobId) && Objects.equals(type, that.type) && Objects.equals(phaseStatus, that.phaseStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jobId, type, phaseStatus);
    }

    @Override
    public String toString() {
        return "SimpleScheduleJobDTO{" +
                "id=" + id +
                ", jobId='" + jobId + '\'' +
                ", type=" + type +
                ", phaseStatus=" + phaseStatus +
                '}';
    }
}
