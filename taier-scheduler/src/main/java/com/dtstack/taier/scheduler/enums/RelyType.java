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
 * @Date: 2022/1/4 3:48 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum RelyType {
    SELF_RELIANCE(1,"自依赖"),UPSTREAM(2,"上游实例"),UPSTREAM_NEXT_JOB(3,"上游任务的下一个周期key");

    private final Integer type;

    private final String msg;

    RelyType(Integer type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}
