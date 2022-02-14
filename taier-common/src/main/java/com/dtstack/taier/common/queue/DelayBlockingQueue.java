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

package com.dtstack.taier.common.queue;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Semaphore;

/**
 * @toutian
 */
public class DelayBlockingQueue<E extends Delayed> {

    private final DelayQueue<E> delayQ = new DelayQueue<E>();
    private final Semaphore available;
    private final int size;

    public DelayBlockingQueue(int capacity) {
        this.size = capacity;
        this.available = new Semaphore(capacity);
    }

    public void put(E e) throws InterruptedException {
        available.acquire();
        delayQ.offer(e);
    }

    public boolean tryPut(E e) {
        if (available.tryAcquire()) {
            delayQ.offer(e);
            return true;
        }
        return false;
    }

    public E take() throws InterruptedException {
        E e = delayQ.take();
        if (e != null) {
            available.release();
        }
        return e;
    }

    public E poll() {
        E e = delayQ.poll();
        if (e != null) {
            available.release();
        }
        return e;
    }

    public int size() {
        return size - available.availablePermits();
    }
}