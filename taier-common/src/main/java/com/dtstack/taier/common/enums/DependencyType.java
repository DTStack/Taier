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
 * 任务依赖类型
 * Date: 2018/6/12
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public enum DependencyType {

    NO_SELF_DEPENDENCY(0),
    //自依赖上一个周期的完成状态
    SELF_DEPENDENCY_SUCCESS(1),
    //依赖下游任务的上一个周期完成状态
    PRE_PERIOD_CHILD_DEPENDENCY_SUCCESS(2),
    //自依赖上一个周期的结束:只要运行结束就可以
    SELF_DEPENDENCY_END(3),
    //依赖下游任务的上一个周期的结束:只要运行结束就可以
    PRE_PERIOD_CHILD_DEPENDENCY_END(4);

    Integer type;

    DependencyType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
