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

package com.dtstack.engine.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * @author sishu.yss
 */
@TableName("schedule_task_task_shade")
public class ScheduleTaskTaskShade extends BaseEntity {

    private Long taskId;

    private Long parentTaskId;

    private Integer parentAppType;

    private String taskKey;

    private String parentTaskKey;

    private Integer appType;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public Integer getParentAppType() {
        return parentAppType;
    }

    public void setParentAppType(Integer parentAppType) {
        this.parentAppType = parentAppType;
    }

    public String getTaskKey() {
        if (StringUtils.isBlank(taskKey)) {
            taskKey = taskId + "-" + getAppType();
        }
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getParentTaskKey() {
        if (StringUtils.isBlank(parentTaskKey) && parentTaskId != null) {
            Integer parentAppType = getParentAppType();
            if (parentAppType == null) {
                parentTaskKey = parentTaskId + "-" + getAppType();
            } else {
                parentTaskKey = parentTaskId + "-" + parentAppType;
            }
        }
        return parentTaskKey;
    }

    public void setParentTaskKey(String parentTaskKey) {
        this.parentTaskKey = parentTaskKey;
    }
}
