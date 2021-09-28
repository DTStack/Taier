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

package com.dtstack.engine.alert;

/**
 * @Auther: dazhi
 * @Date: 2021/1/14 3:48 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterConfig {

    /**
     * 队列大小，默认大小: 200
     */
    private Integer queueSize = 200;

    /**
     * 核心线程数，默认大小：5
     */
    private Integer jobExecutorPoolCorePoolSize = 5;

    /**
     * 最大线程数，默认大小：10
     */
    private Integer jobExecutorPoolMaximumPoolSize = 10;

    /**
     * 线程存活时间: 默认 1000ms
     */
    private Integer jobExecutorPoolKeepAliveTime = 1000;


    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    public Integer getJobExecutorPoolCorePoolSize() {
        return jobExecutorPoolCorePoolSize;
    }

    public void setJobExecutorPoolCorePoolSize(Integer jobExecutorPoolCorePoolSize) {
        this.jobExecutorPoolCorePoolSize = jobExecutorPoolCorePoolSize;
    }

    public Integer getJobExecutorPoolMaximumPoolSize() {
        return jobExecutorPoolMaximumPoolSize;
    }

    public void setJobExecutorPoolMaximumPoolSize(Integer jobExecutorPoolMaximumPoolSize) {
        this.jobExecutorPoolMaximumPoolSize = jobExecutorPoolMaximumPoolSize;
    }

    public Integer getJobExecutorPoolKeepAliveTime() {
        return jobExecutorPoolKeepAliveTime;
    }

    public void setJobExecutorPoolKeepAliveTime(Integer jobExecutorPoolKeepAliveTime) {
        this.jobExecutorPoolKeepAliveTime = jobExecutorPoolKeepAliveTime;
    }
}
