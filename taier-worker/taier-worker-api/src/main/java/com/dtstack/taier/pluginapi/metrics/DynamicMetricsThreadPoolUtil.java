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

package com.dtstack.taier.pluginapi.metrics;


import com.dtstack.taier.metrics.builder.ThreadPoolBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;

/**
 * 可观测线程池创建工具类
 * @author xingyi
 * @date 2025/10/15
 */
public class DynamicMetricsThreadPoolUtil {

    /**
     * 创建可观测线程池
     * @param corePoolSize 核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime    当线程数超过核心数时，这是多余空闲线程在等待新任务前的最大等待时间。
     * @param unit 时间单位
     * @param threadPoolName 线程池名称
     * @param queueName 队列名称
     * @param capacity 队列容量
     * @param fair 是否公平队列
     * @param timeout 线程执行超时时间, 单位毫秒，默认为0时，不开启超时检测， 注意runTimeout 和queueTimeout的区别
     *                  runTimeout 表示线程执行该Runnable的超时检测时间
     *                  queueTimeout 表示线程从队列取任务的超时检测时间，
     * @param threadNamePrefix 线程名称前缀
     * @param handler 拒绝策略
     * @param eager 是否饥饿模式，true: 适用于 IO 密集型场景，在线程池没达到设置的最大值之前优先创建新线程执行任务而不是放入队列等待，比如 tomcat 线程池、dubbo 线程池都是采用这种模式, 默认false: jvm 官方线程池模式
     * @return ExecutorService
     */
    public static ExecutorService buildDynamicThreadPool(
                                                         int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                                         TimeUnit unit,
                                                         String threadPoolName, String queueName, Integer capacity,
                                                         Boolean fair, long timeout, String threadNamePrefix,
                                                         RejectedExecutionHandler handler, boolean eager) {
        return ThreadPoolBuilder.newBuilder()
                .threadPoolName(threadPoolName)
                .corePoolSize(corePoolSize)
                .maximumPoolSize(maximumPoolSize)
                .workQueue(queueName, capacity, fair)
                .queueTimeout(timeout)
                .threadFactory(threadNamePrefix)
                .keepAliveTime(keepAliveTime)
                .timeUnit(unit)
                .rejectEnhanced(true)
                .rejectedExecutionHandler(handler)
                .runTimeout(timeout)
                .eager(eager)
                .buildDynamic()
                .registry(); // register by build
    }
}
