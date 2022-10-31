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

public interface LockService {
    /**
     *
     * @param lockName lockName
     * @param runnable run when locked
     * @throws LockServiceException Lock service error
     * @throws com.dtstack.schedule.common.LockTimeoutException Failed to get lock
     */
    void execWithLock(String lockName, Runnable runnable);

    /**
     *
     * @param lockName lockName
     * @param time timeout
     * @param timeUnit the unit of timeout
     * @return true for success, otherwise false
     * @throws LockServiceException Lock service error
     */
    boolean tryLock(String lockName, int time, TimeUnit timeUnit);

    /**
     *
     * @param lockName lockName
     */
    void release(String lockName);
}
