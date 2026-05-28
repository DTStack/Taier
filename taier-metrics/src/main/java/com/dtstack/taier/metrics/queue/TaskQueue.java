/*
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  *
  *     
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
package com.dtstack.taier.metrics.queue;

import com.dtstack.taier.metrics.executor.EagerEngineExecutor;
import lombok.NonNull;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @see EagerEngineExecutor io 密集型,优先创建核心线程池,如果pooSize = maximunPoolSzie, 才会将Runnable 加入到对列中
 * @author xingyi
 * @date 2025/9/17
 */
public class TaskQueue extends VariableLinkedBlockingQueue<Runnable> {

    private static final long serialVersionUID = -1L;

    private transient EagerEngineExecutor executor;

    public TaskQueue(int queueCapacity) {
        super(queueCapacity);
    }

    public void setExecutor(EagerEngineExecutor exec) {
        executor = exec;
    }

    @Override
    public boolean offer(@NonNull Runnable runnable) {
        if (executor == null) {
            throw new RejectedExecutionException("The task queue does not have executor.");
        }
        if (executor.getPoolSize() == executor.getMaximumPoolSize()) {
            return super.offer(runnable);
        }
        // have free worker. put task into queue to let the worker deal with task.
        if (executor.getSubmittedTaskCount() <= executor.getPoolSize()) {
            return super.offer(runnable);
        }
        // return false to let executor create new worker.
        if (executor.getPoolSize() < executor.getMaximumPoolSize()) {
            return false;
        }
        // currentPoolThreadSize >= max
        return super.offer(runnable);
    }

    /**
     * Force offer task
     *
     * @param o task
     * @param timeout timeout
     * @param unit unit
     * @return offer success or not
     * @throws RejectedExecutionException if executor is terminated.
     * @throws InterruptedException if interrupted while waiting.
     */
    public boolean force(Runnable o, long timeout, TimeUnit unit) throws InterruptedException {
        if (executor.isShutdown()) {
            throw new RejectedExecutionException("Executor is shutdown.");
        }
        return super.offer(o, timeout, unit);
    }
}
