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

package com.dtstack.taier.scheduler.dto.schedule;

/**
 * @Auther: dazhi
 * @Date: 2021/12/26 11:30 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryTaskDisplayDTO {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 查询层级
     */
    private Integer level;

    /**
     * 方向
     */
    private Integer directType;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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
