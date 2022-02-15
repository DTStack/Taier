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

package com.dtstack.taier.common.enums;

/**
 * 0:可以执行
 * 1:时间未到
 * 2:依赖父任务未完成
 * 3:父任务运行失败
 * 4:超过等待时间还未执行
 * 5:当前任务处于暂停状态
 * 6:找不到该任务
 * 7:非未未提交状态
 * Date: 2017/5/28
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public enum JobCheckStatus {
    TASK_STATUS_STOP(1,"任务:%s,调度状态:%s,不能执行实例"),
    TASK_DELETE(2, "该任务已经被删除"),
    NOT_UNSUBMIT(3, "并非未提交状态"),
    TIME_NOT_REACH(4, "时间未到"),
    FATHER_JOB_EXCEPTION(5, "父实例运行失败,父实例名称:%s,jobId:%s"),
    FATHER_JOB_FROZEN(6, "父实例(上游实例或者上游下一个周期的实例，不包含自依赖实例)冻结,父实例名称:%s,jobId:%s"),
    FATHER_NO_CREATED(7, "当前实例所依赖的父实例没有生成，当前实例:%s，jobId:%s，父实例：%s"),
    FATHER_JOB_NOT_FINISHED(8, "依赖父任务未完成"),
    DEPENDENCY_JOB_CANCELED(9, "当前实例所依赖的父实例运行取消，当前实例:%s，jobId:%s，父实例：%s,jobId:%s"),
    NO_TASK(10, "找不到该任务"),
    ;

    private Integer status;

    private String msg;

    JobCheckStatus(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
