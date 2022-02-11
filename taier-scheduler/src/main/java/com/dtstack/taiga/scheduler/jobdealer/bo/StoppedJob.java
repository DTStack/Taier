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

package com.dtstack.taiga.scheduler.jobdealer.bo;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 停止的任务
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/16
 */
public class StoppedJob<T> implements Delayed {
    private T job;
    private int count;
    private int retry;
    private long now;
    private long expired;

    public StoppedJob(T job, int retry, long delay) {
        this.job = job;
        this.retry = retry;
        this.now = System.currentTimeMillis();
        this.expired = now + delay;
    }

    public void incrCount() {
        count += 1;
    }

    public int getIncrCount() {
        return count;
    }

    public boolean isRetry() {
        return retry == 0 || count <= retry;
    }

    public void resetDelay(long delay) {
        this.now = System.currentTimeMillis();
        this.expired = now + delay;
    }

    public T getJob() {
        return job;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expired - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}