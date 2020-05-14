/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.bo;

import com.dtstack.engine.common.JobIdentifier;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Date: 2020/4/28
 * Company: www.dtstack.com
 * @author maqi
 */
public class TaskCheckpointInfo implements Delayed {

    private Integer computeType;

    private String taskId;

    private JobIdentifier jobIdentifier;

    private String engineTypeName;

    private String pluginInfo;

    private long expired;

    private long checkpointInterval;

    public TaskCheckpointInfo(JobIdentifier jobIdentifier, String engineTypeName, String pluginInfo) {
        this(null, null, jobIdentifier, engineTypeName, pluginInfo, 0);
    }

    public TaskCheckpointInfo(Integer computeType, String taskId, JobIdentifier jobIdentifier, String engineTypeName, String pluginInfo, long checkpointInterval) {
        this.computeType = computeType;
        this.taskId = taskId;
        this.jobIdentifier = jobIdentifier;
        this.engineTypeName = engineTypeName;
        this.pluginInfo = pluginInfo;
        this.checkpointInterval = checkpointInterval;

        refreshExpired();
    }

    public void refreshExpired() {
        this.expired = System.currentTimeMillis() + checkpointInterval;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public String getTaskId() {
        return taskId;
    }

    public JobIdentifier getJobIdentifier() {
        return jobIdentifier;
    }

    public String getEngineTypeName() {
        return engineTypeName;
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expired - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

    @Override
    public String toString() {
        return "TaskCheckpointInfo{" +
                "computeType=" + computeType +
                ", taskId='" + taskId + '\'' +
                ", jobIdentifier=" + jobIdentifier +
                ", engineTypeName='" + engineTypeName + '\'' +
                ", pluginInfo='" + pluginInfo + '\'' +
                ", expired=" + expired +
                '}';
    }
}
