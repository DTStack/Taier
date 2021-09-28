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

import dt.insight.plat.lang.web.R;

/**
 * @Auther: dazhi
 * @Date: 2021/1/14 3:41 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface EventMonitor {

    /**
     * 开始告警事件
     * @param alterContext
     */
    Boolean startEvent(AlterContext alterContext);

    /**
     * 拒绝事件
     *
     * @param alterContext 上下文对象
     */
    void refuseEvent(AlterContext alterContext);

    /**
     * 入队事件
     *
     * @param alterContext 上下文对象
     */
    void joiningQueueEvent(AlterContext alterContext);

    /**
     * 出队事件
     *
     * @param alterContext 上下文对象
     */
    void leaveQueueAndSenderBeforeEvent(AlterContext alterContext);

    /**
     * 告警失败事件
     *
     * @param alterContext 上下文对象
     * @param r 结果
     * @param e 失败原因异常
     */
    void alterFailure(AlterContext alterContext, R r,Exception e);

    /**
     * 告警成功事件
     *
     * @param alterContext 上下文对象
     * @param r 结果
     */
    void alterSuccess(AlterContext alterContext, R r);
}
