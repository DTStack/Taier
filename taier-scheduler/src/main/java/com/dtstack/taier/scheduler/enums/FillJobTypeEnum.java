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

package com.dtstack.taier.scheduler.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/9/16 3:02 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum FillJobTypeEnum {
    // 补数据类型 0 默认值 周期实例，立即运行等非补数据实例的默认值 1 可执行补数据实例 2 不可执行的补数据实例（例如黑名单，中间实例等）
    DEFAULT(0,"默认值"),
    RUN_JOB(1,"可执行补数据实例"),
    MIDDLE_JOB(2,"中间实例"),
    ;

    private final Integer type;

    private final String name;

    FillJobTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
