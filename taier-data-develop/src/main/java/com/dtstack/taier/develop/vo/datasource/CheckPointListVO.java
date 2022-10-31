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

package com.dtstack.taier.develop.vo.datasource;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class CheckPointListVO {


    @NotNull(message = "application can be null")

    @ApiModelProperty(value = "applicationId")
    private String applicationId;

    @NotNull(message = "jobId can be null")
    private String jobId;

    private String engineJobId;

    private boolean getSavePointPath;

    public boolean isGetSavePointPath() {
        return getSavePointPath;
    }

    public void setGetSavePointPath(boolean getSavePointPath) {
        this.getSavePointPath = getSavePointPath;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
    }
}
