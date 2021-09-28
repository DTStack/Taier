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

import com.dtstack.engine.common.annotation.Unique;
import io.swagger.annotations.ApiModel;

import java.sql.Timestamp;

/**
 * stream checkpoint 对象
 * Date: 2017/12/21
 * Company: www.dtstack.com
 * @author xuchao
 */

@ApiModel
public class EngineJobCheckpoint extends DataObject {

    @Unique
    private String taskId;

    private String taskEngineId;

    private String checkpointId;

    private Timestamp checkpointTrigger;

    private String checkpointSavepath;

    private String checkpointCounts;

    private Timestamp triggerStart;

    private Timestamp triggerEnd;

    /**
     * page size
     */
    private Integer size;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
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

    public Timestamp getTriggerStart() {
        return triggerStart;
    }

    public void setTriggerStart(Timestamp triggerStart) {
        this.triggerStart = triggerStart;
    }

    public Timestamp getTriggerEnd() {
        return triggerEnd;
    }

    public void setTriggerEnd(Timestamp triggerEnd) {
        this.triggerEnd = triggerEnd;
    }

    public String getCheckpointId() {
        return checkpointId;
    }

    public void setCheckpointId(String checkpointId) {
        this.checkpointId = checkpointId;
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
}
