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
 * @Date: 2021/12/26 11:26 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryJobDisplayVO {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id",required  = true)
    private String jobId;

    /**
     * 查询层级
     */
    @ApiModelProperty(value = "查询层级")
    private Integer level;

    /**
     * 方向
     */
    @ApiModelProperty(value = "查询方向:\n" +
            "FATHER(1):向上查询 \n" +
            "CHILD(2):向下查询",required = true)
    private Integer directType;


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getDirectType() {
        return directType;
    }

    public void setDirectType(Integer directType) {
        this.directType = directType;
    }
}
