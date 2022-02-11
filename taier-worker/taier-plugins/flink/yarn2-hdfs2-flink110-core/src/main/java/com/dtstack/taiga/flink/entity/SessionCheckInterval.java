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

package com.dtstack.taiga.flink.entity;

import java.util.concurrent.atomic.AtomicLong;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/9/11
 */
public class SessionCheckInterval {

    public int checkSubmitJobGraphInterval;
    public AtomicLong checkSubmitJobGraph = new AtomicLong(0);

    public SessionHealthCheckedInfo sessionHealthCheckedInfo;

    public SessionCheckInterval(int checkSubmitJobGraphInterval, SessionHealthCheckedInfo sessionHealthCheckedInfo) {
        this.checkSubmitJobGraphInterval = checkSubmitJobGraphInterval;
        this.sessionHealthCheckedInfo = sessionHealthCheckedInfo;
    }

    /**
     * 1: 是否开启了check
     * 2: 是否满足interval条件
     * 3: submit error在interval时间内超过了指定次数
     *
     * @return
     */
    public boolean doCheck() {
        boolean checkRs = checkSubmitJobGraphInterval > 0 && (!sessionHealthCheckedInfo.isRunning()
                || checkSubmitJobGraph.getAndIncrement() % checkSubmitJobGraphInterval == 0
                || sessionHealthCheckedInfo.getSubmitErrorCount() >= 3);
        return checkRs;
    }
}