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

package com.dtstack.taier.flink.session.check;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @description:
 * @program: engine-plugins
 * @author: lany
 * @create: 2021/07/11 21:26
 */
public class SessionCheckInfo {

    /**
     * interval of check.
     * unit: second
     */
    public int checkSubmitJobGraphInterval;


    public AtomicLong checkSubmitJobGraph = new AtomicLong(0);

    /**
     * health info of session.
     */
    public SessionHealthInfo sessionHealthInfo;

    public SessionCheckInfo(int checkSubmitJobGraphInterval, SessionHealthInfo sessionHealthInfo) {
        this.checkSubmitJobGraphInterval = checkSubmitJobGraphInterval;
        this.sessionHealthInfo = sessionHealthInfo;
    }

    /**
     * 1: 是否开启了check
     * 2: 是否满足interval条件
     * 3: submit error在interval时间内超过了指定次数
     *
     * @return
     */
    public boolean doCheck() {
        boolean checkRs = checkSubmitJobGraphInterval > 0 && (!sessionHealthInfo.getSessionState()
                || checkSubmitJobGraph.getAndIncrement() % checkSubmitJobGraphInterval == 0
                || sessionHealthInfo.getSubmitErrorCount() >= 3);
        return checkRs;
    }

}
