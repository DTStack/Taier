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
 * 周期实例分配策略
 * @author xingyi
 */
public enum EScheduleJobDistributeType {

    /**
     * 历史默认策略，通过构建schedule_engine_job_cache 进行初始化分配，依据数据总量+节点负载 来分配
     */
    DEFAULT(0),

    /**
     * 依据ScheduleJob -> taskType_cycTime 策略进行平均分配，不关注schedule_engine_job_cache
     */
    TASK_TYPE_CYCTIME(1);

    private int value;

    EScheduleJobDistributeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EScheduleJobDistributeType getDistributeType(int value) {
        for (EScheduleJobDistributeType type : EScheduleJobDistributeType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return EScheduleJobDistributeType.DEFAULT;
    }
}
