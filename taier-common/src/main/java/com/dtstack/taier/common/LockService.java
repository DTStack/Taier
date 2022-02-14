package com.dtstack.taier.common;

import com.dtstack.taier.common.exception.LockServiceException;
import com.dtstack.taier.common.exception.LockTimeoutException;

import java.util.concurrent.TimeUnit;

public interface LockService {
    /**
     *
     * @param lockName lockName
     * @param runnable run when locked
     * @throws LockServiceException Lock service error
     * @throws LockTimeoutException Failed to get lock
     */
    void execWithLock(String lockName, Runnable runnable);

    /**
     *
     * @param lockName lockName
     * @param time timeout
     * @param timeUnit the unit of timeout
     * @return true for success, otherwise false
     * @throws LockServiceException Lock service error
     */
    boolean tryLock(String lockName, int time, TimeUnit timeUnit);

    /**
     * @param lockName
     * @throws LockServiceException Lock service error
     */
    void lock(String lockName);

    /**
     *
     * @param lockName lockName
     */
    void release(String lockName);
}
