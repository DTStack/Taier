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

package com.dtstack.taiga.scheduler.server.scheduler.exec;


import com.dtstack.taiga.common.enums.JobCheckStatus;

/**
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/30
 */
public class JobCheckRunInfo {

    /**
     * 是否通过
     */
    private Boolean isPass;

    /**
     * 校验状态
     */
    private JobCheckStatus status;

    /**
     * 日志信息
     */
    private String logInfo;

    public Boolean getPass() {
        return isPass;
    }

    public void setPass(Boolean pass) {
        isPass = pass;
    }

    public JobCheckStatus getStatus() {
        return status;
    }

    public void setStatus(JobCheckStatus status) {
        this.status = status;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }
}
