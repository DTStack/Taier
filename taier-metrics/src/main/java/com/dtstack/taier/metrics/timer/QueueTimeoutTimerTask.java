/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.metrics.timer;

import com.dtstack.taier.metrics.ExecutorWrapper;
import com.dtstack.taier.metrics.ThreadPoolStatProvider;
import com.dtstack.taier.metrics.adapter.ExecutorAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A timer task used to handle queued timeout.
 **/
@Slf4j
public class QueueTimeoutTimerTask extends AbstractTimeoutTimerTask {

    public QueueTimeoutTimerTask(ExecutorWrapper executorWrapper, Runnable runnable) {
        super(executorWrapper, runnable);
    }

    @Override
    protected void doRun() {
        ThreadPoolStatProvider statProvider = executorWrapper.getThreadPoolStatProvider();
        ExecutorAdapter<?> executor = statProvider.getExecutorWrapper().getExecutor();
        Pair<String, String> pair = getTaskNameAndTraceId();
        statProvider.incQueueTimeoutCount(1);
        String logMsg = String.format("DynamicTp execute, queue timeout, " +
                "tpName: %s, taskName: %s, traceId: %s, queueTimeout: %sms, " +
                "poolSize: %s (active: %s, core: %s, max: %s, largest: %s), " +
                "queueCapacity: %s (currSize: %s, remaining: %s)",
                statProvider.getExecutorWrapper().getThreadPoolName(), pair.getLeft(), pair.getRight(),
                statProvider.getQueueTimeout(), executor.getPoolSize(), executor.getActiveCount(),
                executor.getCorePoolSize(), executor.getMaximumPoolSize(),
                executor.getLargestPoolSize(), statProvider.getExecutorWrapper().getExecutor().getQueueCapacity(),
                executor.getQueue().size(), executor.getQueue().remainingCapacity());
        // log.warn(logMsg);
    }
}
