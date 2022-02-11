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

package com.dtstack.taier.flink.entity;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/9/11
 */
public class SessionHealthCheckedInfo {

    /**
     * session 是否健康运行
     */
    private volatile boolean running = false;

    private AtomicInteger submitErrorCount = new AtomicInteger(0);

    private volatile long lastResetTIme;

    public boolean isRunning() {
        return running;
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

    public void unHealth() {
        this.running = false;
    }

    public void reset() {
        this.running = true;
        this.lastResetTIme = System.currentTimeMillis();
        this.submitErrorCount.set(0);
    }

}
