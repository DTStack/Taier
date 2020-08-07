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

package com.dtstack.engine.common.akka.message;

import com.dtstack.engine.common.JobIdentifier;

import java.io.Serializable;

/**
 *
 *  获取任务运行时的日志基本信息
 *
 * Date: 2020/7/7
 * Company: www.dtstack.com
 * @author maqi
 */
public class MessageGetAppLogDir implements Serializable {
    private static final long serialVersionUID = 1L;

    private String engineType;
    private String pluginInfo;
    private JobIdentifier jobIdentifier;

    public MessageGetAppLogDir(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        this.engineType = engineType;
        this.pluginInfo = pluginInfo;
        this.jobIdentifier = jobIdentifier;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(String pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public JobIdentifier getJobIdentifier() {
        return jobIdentifier;
    }

    public void setJobIdentifier(JobIdentifier jobIdentifier) {
        this.jobIdentifier = jobIdentifier;
    }
}
