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

import javax.validation.constraints.NotNull;

/**
 * @Auther: dazhi
 * @Date: 2021/12/29 2:13 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryJobLogVO {

    /**
     * 任务实例ID
     */
    @ApiModelProperty(value = "任务实例ID",example = "1", required = true)
    @NotNull
    private String jobId;

    /**
     * 页数
     */
    @ApiModelProperty(value = "页数 默认 1",example = "1")
    private Integer pageInfo;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(Integer pageInfo) {
        this.pageInfo = pageInfo;
    }
}
