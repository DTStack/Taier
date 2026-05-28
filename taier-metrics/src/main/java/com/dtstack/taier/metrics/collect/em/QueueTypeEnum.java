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
 
package com.dtstack.taier.metrics.collect.em;

import com.dtstack.taier.metrics.exception.TpException;
import com.dtstack.taier.metrics.queue.VariableLinkedBlockingQueue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author xingyi
 * @date 2025/9/17
 */
@Slf4j
@Getter
@AllArgsConstructor
public enum QueueTypeEnum {

    /**
     * BlockingQueue type.
     */
    ARRAY_BLOCKING_QUEUE(1, "ArrayBlockingQueue"),

    LINKED_BLOCKING_QUEUE(2, "LinkedBlockingQueue"),

    PRIORITY_BLOCKING_QUEUE(3, "PriorityBlockingQueue"),

    DELAY_QUEUE(4, "DelayQueue"),

    SYNCHRONOUS_QUEUE(5, "SynchronousQueue"),

    LINKED_TRANSFER_QUEUE(6, "LinkedTransferQueue"),

    LINKED_BLOCKING_DEQUE(7, "LinkedBlockingDeque"),

    VARIABLE_LINKED_BLOCKING_QUEUE(8, "VariableLinkedBlockingQueue");

    private final int code;

    private final String name;

    public static BlockingQueue<Runnable> buildLbq(String name, int capacity) {
        return buildLbq(name, capacity, false, 256);
    }

    @SuppressWarnings("all")
    public static BlockingQueue<Runnable> buildLbq(String name, int capacity, boolean fair, int maxFreeMemory) {
        BlockingQueue<Runnable> blockingQueue = null;
        if (Objects.equals(name, ARRAY_BLOCKING_QUEUE.getName())) {
            blockingQueue = new ArrayBlockingQueue<>(capacity);
        } else if (Objects.equals(name, LINKED_BLOCKING_QUEUE.getName())) {
            blockingQueue = new LinkedBlockingQueue<>(capacity);
        } else if (Objects.equals(name, PRIORITY_BLOCKING_QUEUE.getName())) {
            blockingQueue = new PriorityBlockingQueue<>(capacity);
        } else if (Objects.equals(name, DELAY_QUEUE.getName())) {
            blockingQueue = new DelayQueue();
        } else if (Objects.equals(name, SYNCHRONOUS_QUEUE.getName())) {
            blockingQueue = new SynchronousQueue<>(fair);
        } else if (Objects.equals(name, LINKED_TRANSFER_QUEUE.getName())) {
            blockingQueue = new LinkedTransferQueue<>();
        } else if (Objects.equals(name, LINKED_BLOCKING_DEQUE.getName())) {
            blockingQueue = new LinkedBlockingDeque<>(capacity);
        } else if (Objects.equals(name, VARIABLE_LINKED_BLOCKING_QUEUE.getName())) {
            blockingQueue = new VariableLinkedBlockingQueue<>(capacity);
        }
        if (blockingQueue != null) {
            return blockingQueue;
        }

        log.error("Cannot find specified BlockingQueue {}", name);
        throw new TpException("Cannot find specified BlockingQueue " + name);
    }
}
