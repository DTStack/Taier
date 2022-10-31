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

package com.dtstack.taier.develop.dto.devlop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.sql.Timestamp;

public class StreamTaskCheckpoint {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id = 0L;

    private String taskId;

    private String taskEngineId;

    private String checkpointID;

    private Timestamp checkpointTrigger;

    private String checkpointSavepath;

    private String checkpointCounts;

    private Long checkpointSize;

    /**
     * 持续时间
     */
    private Long checkpointDuration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskEngineId() {
        return taskEngineId;
    }

    public void setTaskEngineId(String taskEngineId) {
        this.taskEngineId = taskEngineId;
    }

    public String getCheckpointID() {
        return checkpointID;
    }

    public void setCheckpointID(String checkpointID) {
        this.checkpointID = checkpointID;
    }

    public Timestamp getCheckpointTrigger() {
        return checkpointTrigger;
    }

    public void setCheckpointTrigger(Timestamp checkpointTrigger) {
        this.checkpointTrigger = checkpointTrigger;
    }

    public String getCheckpointSavepath() {
        return checkpointSavepath;
    }

    public void setCheckpointSavepath(String checkpointSavepath) {
        this.checkpointSavepath = checkpointSavepath;
    }

    public String getCheckpointCounts() {
        return checkpointCounts;
    }

    public void setCheckpointCounts(String checkpointCounts) {
        this.checkpointCounts = checkpointCounts;
    }

    public Long getCheckpointSize() {
        return checkpointSize;
    }

    public void setCheckpointSize(Long checkpointSize) {
        this.checkpointSize = checkpointSize;
    }

    public Long getCheckpointDuration() {
        return checkpointDuration;
    }

    public void setCheckpointDuration(Long checkpointDuration) {
        this.checkpointDuration = checkpointDuration;
    }

}
