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

/**
 * @Auther: dazhi
 * @Date: 2022/1/27 11:42 AM
 * @Email: dazhi@dtstack.com
 * @Description: 返回可依赖的任务
 */
@ApiModel("返回可依赖的任务")
public class DevelopAllProductGlobalReturnVO {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id", example = "1")
    private Long taskId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称", example = "123")
    private String taskName;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id", example = "1")
    private Long tenantId;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称", example = "1")
    private String tenantName;


    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
