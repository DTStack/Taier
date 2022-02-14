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

package com.dtstack.taier.scheduler.jobdealer.bo;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 重试的任务
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/16
 */
public class SimpleJobDelay<T> implements Delayed {
    private T job;
    private int stage;
    private long expired;

    public SimpleJobDelay(T job, int stage, long delay) {
        this.job = job;
        this.stage = stage;
        this.expired = System.currentTimeMillis() + delay;
    }

    public T getJob() {
        return job;
    }

    public int getStage() {
        return stage;
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