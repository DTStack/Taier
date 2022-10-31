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

package com.dtstack.taier.datasource.api.enums;

/**
 * job相关状态统一维护，有的状态重复但是写法不同的，统一用一份来维护
 *
 * @author luming
 * date 2022/3/9
 */
public enum JobStatus {
    /**
     * 新建
     */
    NEW,
    /**
     * 等待
     */
    PENDING,
    /**
     * 运行中
     */
    RUNNING,
    /**
     * 停止
     */
    STOPPED,
    /**
     * 运行结束/成功
     */
    FINISHED,
    /**
     * 运行失败
     */
    ERROR,
    /**
     * 丢弃
     */
    DISCARDED,
    /**
     * 被挂起
     */
    SUSPENDED,
    /**
     * 被取消
     */
    CANCELLED;

    JobStatus() {
    }

    public static String getStatus(String statusStr) {
        //重复状态适配
        switch (statusStr) {
            case "WAITING":
                return JobStatus.PENDING.name();
            case "SUCCESS":
                return JobStatus.FINISHED.name();
            case "FAILED":
                return JobStatus.ERROR.name();
            default:
        }
        return JobStatus.valueOf(statusStr).name();
    }
}
