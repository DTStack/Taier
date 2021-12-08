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

package com.dtstack.batch.enums;

/**
 * @author mading
 * @create 2019-03-18 11:40 AM
 */
public enum AlarmType {

    /**
     * 任务失败
     */
    TASK_FAIL("任务失败"),

    /**
     * 任务停止
     */
    TASK_STOP("任务停止"),

    /**
     * 定时未完成
     */
    TIMING_UNCOMPLETED("定时%s未完成"),

    /**
     * 超时未完成
     */
    TIMING_EXEC_OVER("超时,%s分钟未完成");

    String type;

    AlarmType(String type) {
        this.type = type;
    }

    public String getType() {
         return this.type;
    }
}
