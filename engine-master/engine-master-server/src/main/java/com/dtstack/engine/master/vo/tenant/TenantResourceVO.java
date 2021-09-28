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

package com.dtstack.engine.master.vo.tenant;

import java.util.Map;

/**
 * @Author tengzhen
 * @Description: 返回给前端的租户任务资源限制类
 * @Date: Created in 5:19 下午 2020/10/15
 */
public class TenantResourceVO {


    /**uic租户id**/
    private Integer dtUicTenantId;

    /**任务类型**/
    private Integer taskType;

    /**任务类型名称**/
    private String engineType;

    /**资源限制**/
    private Map<String,Object> resourceLimit;

    public Integer getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Integer dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public Map<String, Object> getResourceLimit() {
        return resourceLimit;
    }

    public void setResourceLimit(Map<String, Object> resourceLimit) {
        this.resourceLimit = resourceLimit;
    }
}
