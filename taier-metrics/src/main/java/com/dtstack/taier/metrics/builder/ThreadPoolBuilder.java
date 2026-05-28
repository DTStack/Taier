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
 
package com.dtstack.taier.metrics.builder;

import com.dtstack.taier.metrics.collect.em.QueueTypeEnum;
import com.dtstack.taier.metrics.executor.EagerEngineExecutor;
import com.dtstack.taier.metrics.executor.EngineExecutor;
import com.dtstack.taier.metrics.queue.TaskQueue;
import com.dtstack.taier.metrics.queue.VariableLinkedBlockingQueue;
import com.dtstack.taier.metrics.rejects.RejectHandlerGetter;
import com.dtstack.taier.metrics.wrapper.TaskWrapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xingyi
 * @date 2025/9/17
 */
public class ThreadPoolBuilder {

    /**
     * Name of Dynamic ThreadPool.
     */
    private String threadPoolName = "ThreadPool";

    /**
     * CoreSize of ThreadPool.
     */
    private int corePoolSize = 1;

    /**
     * MaxSize of ThreadPool.
     */
    private int maximumPoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * When the number of threads is greater than the core,
     * this is the maximum time that excess idle threads
     * will wait for new tasks before terminating
     */
    private long keepAliveTime = 60;

    /**
     * Timeout unit.
     */
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * Blocking queue, see {@link QueueTypeEnum}
     */
    private BlockingQueue<Runnable> workQueue = new VariableLinkedBlockingQueue<>(1024);

    /**
     * Queue capacity
     */
    private int queueCapacity = 1024;

    /**
     * Max free memory for MemorySafeLBQ, unit M
     */
    private int maxFreeMemory = 16;

    /**
     * RejectedExecutionHandler
     */
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();

    /**
     * Default inner thread factory.
     */
    private ThreadFactory threadFactory = new NamedThreadFactory("dtp");

    /**
     * If allow core thread timeout.
     */
    private boolean allowCoreThreadTimeOut = false;

    /**
     * Dynamic or common.
     */
    private boolean dynamic = true;

    /**
     * Whether to wait for scheduled tasks to complete on shutdown,
     * not interrupting running tasks and executing all tasks in the queue.
     */
    private boolean waitForTasksToCompleteOnShutdown = true;

    /**
     * The maximum number of seconds that this executor is supposed to block
     * on shutdown in order to wait for remaining tasks to complete their execution
     * before the rest of the container continues to shut down.
     */
    private int awaitTerminationSeconds = 3;

    /**
     * If io intensive thread pool.
     * default false, true indicate cpu intensive thread pool.
     */
    private boolean eager = false;

    /**
     * If ordered thread pool.
     * default false, true ordered thread pool.
     */
    private boolean ordered = false;

    /**
     * If scheduled executor, default false.
     */
    private boolean scheduled = false;

    /**
     * If priority thread pool.
     * default false, true priority thread pool.
     */
    private boolean priority = false;

    /**
     * If pre start all core threads.
     */
    private boolean preStartAllCoreThreads = false;

    /**
     * If enhance reject.
     */
    private boolean rejectEnhanced = true;

    /**
     * If enable notify.
     */
    private boolean notifyEnabled = true;

    /**
     * Task execute timeout, unit (ms).
     */
    private long runTimeout = 0;

    /**
     * If try interrupt thread when task run timeout.
     */
    private boolean tryInterrupt = false;

    /**
     * Task queue wait timeout, unit (ms), just for statistics.
     */
    private long queueTimeout = 0;

    /**
     * Task wrappers.
     */
    private final List<TaskWrapper> taskWrappers = Lists.newArrayList();

    /**
     * Notify platform id
     */
    private List<String> platformIds = Lists.newArrayList();

    private ThreadPoolBuilder() {
    }

    public static ThreadPoolBuilder newBuilder() {
        return new ThreadPoolBuilder();
    }

    public ThreadPoolBuilder threadPoolName(String poolName) {
        this.threadPoolName = poolName;
        return this;
    }

    public ThreadPoolBuilder corePoolSize(int corePoolSize) {
        if (corePoolSize >= 0) {
            this.corePoolSize = corePoolSize;
        }
        return this;
    }

    public ThreadPoolBuilder maximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize > 0) {
            this.maximumPoolSize = maximumPoolSize;
        }
        return this;
    }

    public ThreadPoolBuilder keepAliveTime(long keepAliveTime) {
        if (keepAliveTime > 0) {
            this.keepAliveTime = keepAliveTime;
        }
        return this;
    }

    public ThreadPoolBuilder timeUnit(TimeUnit timeUnit) {
        if (timeUnit != null) {
            this.timeUnit = timeUnit;
        }
        return this;
    }

    /**
     * Create work queue
     *
     * @param queueName     queue name
     * @param capacity      queue capacity
     * @param fair          for SynchronousQueue
     * @param maxFreeMemory for MemorySafeLBQ
     * @return the ThreadPoolBuilder instance
     */
    public ThreadPoolBuilder workQueue(String queueName, Integer capacity, Boolean fair, Integer maxFreeMemory) {
        if (StringUtils.isNotBlank(queueName)) {
            workQueue = QueueTypeEnum.buildLbq(queueName, capacity != null ? capacity : this.queueCapacity,
                    fair != null && fair, maxFreeMemory != null ? maxFreeMemory : this.maxFreeMemory);
        }
        return this;
    }

    /**
     * Create work queue
     *
     * @param queueName queue name
     * @param capacity  queue capacity
     * @param fair      for SynchronousQueue
     * @return the ThreadPoolBuilder instance
     */
    public ThreadPoolBuilder workQueue(String queueName, Integer capacity, Boolean fair) {
        if (StringUtils.isNotBlank(queueName)) {
            workQueue = QueueTypeEnum.buildLbq(queueName, capacity != null ? capacity : this.queueCapacity,
                    fair != null && fair, maxFreeMemory);
        }
        return this;
    }

    public ThreadPoolBuilder workQueue(String queueName, Integer capacity) {
        if (StringUtils.isNotBlank(queueName)) {
            workQueue = QueueTypeEnum.buildLbq(queueName, capacity != null ? capacity : this.queueCapacity,
                    false, maxFreeMemory);
        }
        return this;
    }

    public ThreadPoolBuilder queueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
        return this;
    }

    public ThreadPoolBuilder maxFreeMemory(int maxFreeMemory) {
        this.maxFreeMemory = maxFreeMemory;
        return this;
    }

    public ThreadPoolBuilder rejectedExecutionHandler(String rejectedName) {
        if (StringUtils.isNotBlank(rejectedName)) {
            rejectedExecutionHandler = RejectHandlerGetter.buildRejectedHandler(rejectedName);
        }
        return this;
    }

    public ThreadPoolBuilder rejectedExecutionHandler(RejectedExecutionHandler handler) {
        if (Objects.nonNull(handler)) {
            rejectedExecutionHandler = handler;
        }
        return this;
    }

    public ThreadPoolBuilder threadFactory(String prefix) {
        if (StringUtils.isNotBlank(prefix)) {
            threadFactory = new NamedThreadFactory(prefix);
        }
        return this;
    }

    public ThreadPoolBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    public ThreadPoolBuilder dynamic(boolean dynamic) {
        this.dynamic = dynamic;
        return this;
    }

    public ThreadPoolBuilder awaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
        return this;
    }

    public ThreadPoolBuilder waitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
        return this;
    }

    /**
     * @param eager true or false
     * @return the ThreadPoolBuilder instance
     */
    public ThreadPoolBuilder eager(boolean eager) {
        checkExecutorType();
        this.eager = eager;
        return this;
    }

    /**
     * @param ordered true or false
     * @return the ThreadPoolBuilder instance
     * @deprecated use {@link #ordered()} instead
     */
    @Deprecated
    public ThreadPoolBuilder ordered(boolean ordered) {
        checkExecutorType();
        this.ordered = ordered;
        return this;
    }

    /**
     * @param scheduled true or false
     * @return the ThreadPoolBuilder instance
     * @deprecated use {@link #scheduled()} instead
     */
    @Deprecated
    public ThreadPoolBuilder scheduled(boolean scheduled) {
        checkExecutorType();
        this.scheduled = scheduled;
        return this;
    }

    /**
     * @param priority true or false
     * @return the ThreadPoolBuilder instance
     * @deprecated use {@link #priority()} instead
     */
    @Deprecated
    public ThreadPoolBuilder priority(boolean priority) {
        checkExecutorType();
        this.priority = priority;
        return this;
    }

    /**
     * set eager type
     *
     * @return the ThreadPoolBuilder instance
     */
    public ThreadPoolBuilder eager() {
        checkExecutorType();
        this.eager = true;
        return this;
    }

    /**
     * set ordered type
     *
     * @return the ThreadPoolBuilder instance
     */
    public ThreadPoolBuilder ordered() {
        checkExecutorType();
        this.ordered = true;
        return this;
    }

    /**
     * set scheduled type
     *
     * @return the ThreadPoolBuilder instance
     */
    public ThreadPoolBuilder scheduled() {
        checkExecutorType();
        this.scheduled = true;
        return this;
    }

    /**
     * set priority type
     *
     * @return the ThreadPoolBuilder instance
     */
    public ThreadPoolBuilder priority() {
        checkExecutorType();
        this.priority = true;
        return this;
    }

    public ThreadPoolBuilder preStartAllCoreThreads(boolean preStartAllCoreThreads) {
        this.preStartAllCoreThreads = preStartAllCoreThreads;
        return this;
    }

    public ThreadPoolBuilder rejectEnhanced(boolean rejectEnhanced) {
        this.rejectEnhanced = rejectEnhanced;
        return this;
    }

    public ThreadPoolBuilder notifyEnabled(boolean notifyEnabled) {
        this.notifyEnabled = notifyEnabled;
        return this;
    }

    public ThreadPoolBuilder runTimeout(long runTimeout) {
        this.runTimeout = runTimeout;
        return this;
    }

    public ThreadPoolBuilder tryInterrupt(boolean tryInterrupt) {
        this.tryInterrupt = tryInterrupt;
        return this;
    }

    public ThreadPoolBuilder queueTimeout(long queueTimeout) {
        this.queueTimeout = queueTimeout;
        return this;
    }

    public ThreadPoolBuilder taskWrappers(List<TaskWrapper> taskWrappers) {
        this.taskWrappers.addAll(taskWrappers);
        return this;
    }

    public ThreadPoolBuilder taskWrapper(TaskWrapper taskWrapper) {
        this.taskWrappers.add(taskWrapper);
        return this;
    }

    public ThreadPoolBuilder platformIds(List<String> platformIds) {
        if (CollectionUtils.isNotEmpty(platformIds)) {
            this.platformIds = platformIds;
        }
        return this;
    }

    /**
     * Build according to dynamic field.
     *
     * @return the newly created ThreadPoolExecutor instance
     */
    public ThreadPoolExecutor build() {
        if (dynamic) {
            return buildDtpExecutor(this);
        } else {
            return buildCommonExecutor(this);
        }
    }

    /**
     * Build a dynamic ThreadPoolExecutor.
     *
     * @return the newly created DtpExecutor instance
     */
    public EngineExecutor buildDynamic() {
        return buildDtpExecutor(this);
    }

    /**
     * Build common ThreadPoolExecutor.
     *
     * @return the newly created ThreadPoolExecutor instance
     */
    public ThreadPoolExecutor buildCommon() {
        return buildCommonExecutor(this);
    }

    /**
     * Build dynamic threadPoolExecutor.
     *
     * @param builder the targeted builder
     * @return the newly created DtpExecutor instance
     */
    private EngineExecutor buildDtpExecutor(ThreadPoolBuilder builder) {
        Preconditions.checkNotNull(builder.threadPoolName, "The thread pool name must not be null.");
        EngineExecutor dtpExecutor = createInternal(builder);
        dtpExecutor.setThreadPoolName(builder.threadPoolName);
        dtpExecutor.allowCoreThreadTimeOut(builder.allowCoreThreadTimeOut);
        dtpExecutor.setWaitForTasksToCompleteOnShutdown(builder.waitForTasksToCompleteOnShutdown);
        dtpExecutor.setAwaitTerminationSeconds(builder.awaitTerminationSeconds);
        dtpExecutor.setPreStartAllCoreThreads(builder.preStartAllCoreThreads);
        dtpExecutor.setRejectEnhanced(builder.rejectEnhanced);
        dtpExecutor.setRunTimeout(builder.runTimeout);
        dtpExecutor.setTryInterrupt(builder.tryInterrupt);
        dtpExecutor.setQueueTimeout(builder.queueTimeout);
        dtpExecutor.setTaskWrappers(builder.taskWrappers);
        dtpExecutor.setPlatformIds(builder.platformIds);
        dtpExecutor.setNotifyEnabled(builder.notifyEnabled);
        dtpExecutor.setRejectHandler(builder.rejectedExecutionHandler);
        return dtpExecutor;
    }

    private EngineExecutor createInternal(ThreadPoolBuilder builder) {
        EngineExecutor dtpExecutor;
        if (eager) {
            TaskQueue taskQueue = new TaskQueue(builder.queueCapacity);
            dtpExecutor = new EagerEngineExecutor(
                    builder.corePoolSize,
                    builder.maximumPoolSize,
                    builder.keepAliveTime,
                    builder.timeUnit,
                    taskQueue,
                    builder.threadFactory,
                    builder.rejectedExecutionHandler);
            taskQueue.setExecutor((EagerEngineExecutor) dtpExecutor);
        } else {
            dtpExecutor = new EngineExecutor(
                    builder.corePoolSize,
                    builder.maximumPoolSize,
                    builder.keepAliveTime,
                    builder.timeUnit,
                    builder.workQueue,
                    builder.threadFactory,
                    builder.rejectedExecutionHandler);
        }
        return dtpExecutor;
    }

    /**
     * Build common threadPoolExecutor, does not manage by DynamicTp framework.
     *
     * @param builder the targeted builder
     * @return the newly created ThreadPoolExecutor instance
     */
    private ThreadPoolExecutor buildCommonExecutor(ThreadPoolBuilder builder) {
        if (scheduled) {
            return new ScheduledThreadPoolExecutor(
                    builder.corePoolSize,
                    builder.threadFactory,
                    builder.rejectedExecutionHandler);
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                builder.corePoolSize,
                builder.maximumPoolSize,
                builder.keepAliveTime,
                builder.timeUnit,
                builder.workQueue,
                builder.threadFactory,
                builder.rejectedExecutionHandler);
        executor.allowCoreThreadTimeOut(builder.allowCoreThreadTimeOut);
        return executor;
    }

    /**
     * Check executor type.
     */
    private void checkExecutorType() {
        if (eager || ordered || scheduled || priority) {
            // 抛异常
            throw new IllegalArgumentException("More than one executor type is defined");
        }
    }
}
