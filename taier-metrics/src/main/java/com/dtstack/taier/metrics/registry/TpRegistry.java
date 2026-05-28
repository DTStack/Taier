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
 
package com.dtstack.taier.metrics.registry;

import com.dtstack.taier.metrics.ExecutorWrapper;
import com.dtstack.taier.metrics.exception.TpException;
import com.dtstack.taier.metrics.executor.EngineExecutor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * 可观测线程池注册中心,内存模式
 * @author xingyi
 * @date 2025/9/17
 */
@Slf4j
public class TpRegistry {

    /**
     * Maintain all automatically registered and manually registered Executors.
     */
    private static final Map<String, ExecutorWrapper> EXECUTOR_REGISTRY = new ConcurrentHashMap<>();

    /**
     * Get all Executor names.
     *
     * @return all executor names
     */
    public static Set<String> getAllExecutorNames() {
        return Collections.unmodifiableSet(EXECUTOR_REGISTRY.keySet());
    }

    /**
     * Get all Executors.
     *
     * @return all Executors
     */
    public static Map<String, ExecutorWrapper> getAllExecutors() {
        return EXECUTOR_REGISTRY;
    }

    /**
     * Unregister a executor.
     *
     * @param name thread pool name
     * @return the managed DtpExecutor instance
     */
    public static ExecutorWrapper unregisterExecutor(String name) {
        ExecutorWrapper executorWrapper = getExecutorWrapper(name);
        log.info("DynamicTp unregister executor: {}", executorWrapper);
        return EXECUTOR_REGISTRY.remove(name);
    }

    /**
     * Get DtpExecutor by thread pool name.
     *
     * @param name thread pool name
     * @return the managed DtpExecutor instance
     */
    public static EngineExecutor getDtpExecutor(String name) {
        val executorWrapper = getExecutorWrapper(name);
        if (!executorWrapper.isDtpExecutor()) {
            log.error("The specified executor is not a DtpExecutor, name: {}", name);
            throw new TpException("The specified executor is not a DtpExecutor, name: " + name);
        }
        return (EngineExecutor) executorWrapper.getExecutor();
    }

    /**
     * Get executor by thread pool name.
     *
     * @param name thread pool name
     * @return the managed executor instance
     */
    public static Executor getExecutor(String name) {
        val executorWrapper = EXECUTOR_REGISTRY.get(name);
        if (Objects.isNull(executorWrapper)) {
            log.error("Cannot find a specified executor, name: {}", name);
            throw new TpException("Cannot find a specified executor, name: " + name);
        }
        return executorWrapper.getExecutor();
    }

    /**
     * Get ExecutorWrapper by thread pool name.
     *
     * @param name thread pool name
     * @return the managed ExecutorWrapper instance
     */
    public static ExecutorWrapper getExecutorWrapper(String name) {
        ExecutorWrapper executorWrapper = EXECUTOR_REGISTRY.get(name);
        if (Objects.isNull(executorWrapper)) {
            log.error("Cannot find a specified executorWrapper, name: {}", name);
            throw new TpException("Cannot find a specified executorWrapper, name: " + name);
        }
        return executorWrapper;
    }

    /**
     * Register executor.
     *
     * @param wrapper the newly created ExecutorWrapper instance
     */
    public static void registerExecutor(ExecutorWrapper wrapper) {
        wrapper.initialize();
        EXECUTOR_REGISTRY.putIfAbsent(wrapper.getThreadPoolName(), wrapper);
    }

}
