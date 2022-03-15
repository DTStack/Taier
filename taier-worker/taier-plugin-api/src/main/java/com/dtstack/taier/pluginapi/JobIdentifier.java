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

package com.dtstack.taier.pluginapi;

import com.dtstack.taier.pluginapi.enums.EDeployMode;

import java.io.Serializable;

/**
 * Company: www.dtstack.com
 *
 * @author yuebai
 */

public class JobIdentifier implements Serializable {

    private String engineJobId;

    private String applicationId;

    private String jobId;

    private Long tenantId;

    private Integer taskType;

    /**
     * 默认 perjob
     */
    private Integer deployMode = EDeployMode.PERJOB.getType();

    private Long userId;

    private String pluginInfo;

    private Long timeout;

    private Boolean forceCancel = Boolean.FALSE;

    private String componentVersion;

    private String archiveFsDir;


    private JobIdentifier() {
    }

    public JobIdentifier(String engineJobId, String applicationId, String jobId, Long tenantId, Integer taskType, Integer deployMode, Long userId, String pluginInfo, String componentVersion) {
        this.engineJobId = engineJobId;
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.tenantId = tenantId;
        this.taskType = taskType;
        this.deployMode = deployMode;
        this.userId = userId;
        this.pluginInfo = pluginInfo;
        this.componentVersion = componentVersion;
    }

    public JobIdentifier(String engineJobId, String applicationId, String jobId, Boolean forceCancel) {
        this.engineJobId = engineJobId;
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.forceCancel = forceCancel;
    }

    public JobIdentifier(String engineJobId, String applicationId, String jobId) {
        this.engineJobId = engineJobId;
        this.applicationId = applicationId;
        this.jobId = jobId;
    }

    public static JobIdentifier createInstance(String engineJobId, String applicationId, String jobId, Boolean forceCancel) {
        return new JobIdentifier(engineJobId, applicationId, jobId, forceCancel);
    }

    public static JobIdentifier createInstance(String engineJobId, String applicationId, String jobId) {
        return new JobIdentifier(engineJobId, applicationId, jobId);
    }

    public Boolean isForceCancel() {
        return forceCancel;
    }

    public void setForceCancel(Boolean forceCancel) {
        this.forceCancel = forceCancel;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getJobId() {
        return jobId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public Integer getDeployMode() {
        return deployMode;
    }

    public Long getUserId() {
        return userId;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public String getArchiveFsDir() {
        return archiveFsDir;
    }

    public void setArchiveFsDir(String archiveFsDir) {
        this.archiveFsDir = archiveFsDir;
    }

    @Override
    public String toString() {
        return "JobIdentifier{" +
                "engineJobId='" + engineJobId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", jobId='" + jobId + '\'' +
                ", tenantId=" + tenantId +
                ", taskType=" + taskType +
                ", deployMode=" + deployMode +
                ", userId=" + userId +
                ", timeout=" + timeout +
                '}';
    }
}
