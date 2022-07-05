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

package com.dtstack.taier.scheduler.impl.pojo;

import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.scheduler.dto.schedule.ScheduleTaskShadeDTO;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2020/11/23 11:15 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ParamTaskAction {

    private ScheduleTaskShade task;

    @ApiModelProperty(notes = "任务id")
    private String jobId;

    @ApiModelProperty(notes = "是否重试: NORMAL(0), RESTARTED(1) 默认 NORMAL")
    private Integer isRestart;

    @ApiModelProperty(notes = "工作流Id")
    private String flowJobId;

    public ScheduleTaskShade getTask() {
        return task;
    }

    public void setTask(ScheduleTaskShade task) {
        this.task = task;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getIsRestart() {
        return isRestart;
    }

    public void setIsRestart(Integer isRestart) {
        this.isRestart = isRestart;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }
}
