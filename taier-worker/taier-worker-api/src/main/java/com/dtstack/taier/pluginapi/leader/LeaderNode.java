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

package com.dtstack.taier.pluginapi.leader;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class LeaderNode {

    private static final LeaderNode INSTANCE = new LeaderNode();

    public static LeaderNode getInstance() {
        return INSTANCE;
    }

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private LockService lockService;

    private LeaderNode() {
    }

    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }

    public void finishInit() {
        this.initialized.compareAndSet(false, true);
    }

    /**
     *
     * @param lockName lockName
     * @param time timeout
     * @param timeUnit the unit of timeout
     * @return true for success, otherwise false
     * @throws LockServiceException Lock service error
     */
    public boolean tryLock(String lockName, int time, TimeUnit timeUnit) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("Schedule node not initialize.");
        }

        return lockService.tryLock(lockName, time, timeUnit);
    }

    /**
     *
     * @param lockName lockName
     */
    public void release(String lockName) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("Schedule node not initialize.");
        }

        lockService.release(lockName);
    }

    /**
     *
     * @param lockName lockName
     * @param runnable run when locked
     * @throws LockServiceException Lock service error
     * @throws LockTimeoutException Failed to get lock
     */
    public void execWithLock(String lockName, Runnable runnable) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("Schedule node not initialize.");
        }

        lockService.execWithLock(lockName, runnable);
    }

}
