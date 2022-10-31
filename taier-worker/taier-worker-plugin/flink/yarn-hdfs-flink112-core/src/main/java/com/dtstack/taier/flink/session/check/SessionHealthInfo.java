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


import com.dtstack.taier.flink.base.enums.SessionState;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @program: engine-plugins
 * @author: lany
 * @create: 2021/07/11 21:26
 */
public class SessionHealthInfo {

    /**
     * session 是否健康运行
     */
    private volatile SessionState sessionState = SessionState.UNHEALTHY;

    private AtomicInteger submitErrorCount = new AtomicInteger(0);

    private volatile long lastResetTIme;

    public boolean getSessionState() {
        return sessionState.getState();
    }

    public int getSubmitErrorCount() {
        return submitErrorCount.get();
    }

    public long getLastResetTIme() {
        return lastResetTIme;
    }

    public int incrSubmitError() {
        return submitErrorCount.incrementAndGet();
    }

    public void unHealthy() {
        this.sessionState = SessionState.UNHEALTHY;
    }

    public void healthy() {
        this.sessionState = SessionState.HEALTHY;
        this.lastResetTIme = System.currentTimeMillis();
        this.submitErrorCount.set(0);
    }

}

