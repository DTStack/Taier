package com.dtstack.taier.pluginapi.leader;

import java.util.concurrent.TimeUnit;

public interface LockService {
    /**
     * @param lockName lockName
     * @param runnable run when locked
     * @throws LockServiceException                             Lock service error
     * @throws com.dtstack.schedule.common.LockTimeoutException Failed to get lock
     */
    void execWithLock(String lockName, Runnable runnable);

    /**
     * @param lockName lockName
     * @param time     timeout
     * @param timeUnit the unit of timeout
     * @return true for success, otherwise false
     * @throws LockServiceException Lock service error
     */
    boolean tryLock(String lockName, int time, TimeUnit timeUnit);

    /**
     * @param lockName lockName
     */
    void release(String lockName);
}
