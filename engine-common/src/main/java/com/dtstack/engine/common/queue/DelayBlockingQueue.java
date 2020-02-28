package com.dtstack.engine.common.queue;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Semaphore;

/**
 * @toutian
 */
public class DelayBlockingQueue<E extends Delayed> {

    private final DelayQueue<E> delayQ = new DelayQueue<E>();
    private final Semaphore available;

    public DelayBlockingQueue(int capacity) {
        available = new Semaphore(capacity, true);
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
        return available.drainPermits();
    }
}