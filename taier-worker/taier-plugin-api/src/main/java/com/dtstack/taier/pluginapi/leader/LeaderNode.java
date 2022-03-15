package com.dtstack.taier.pluginapi.leader;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class LeaderNode {

    private static final LeaderNode INSTANCE = new LeaderNode();

    public static LeaderNode getInstance() {
        return INSTANCE;
    }

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private LockService lockService;

    private LeaderNode() {
    }

    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }

    public void finishInit() {
        this.initialized.compareAndSet(false, true);
    }

    /**
     *
     * @param lockName lockName
     * @param time timeout
     * @param timeUnit the unit of timeout
     * @return true for success, otherwise false
     * @throws LockServiceException Lock service error
     */
    public boolean tryLock(String lockName, int time, TimeUnit timeUnit) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("Schedule node not initialize.");
        }

        return lockService.tryLock(lockName, time, timeUnit);
    }

    /**
     *
     * @param lockName lockName
     */
    public void release(String lockName) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("Schedule node not initialize.");
        }

        lockService.release(lockName);
    }

    /**
     *
     * @param lockName lockName
     * @param runnable run when locked
     * @throws LockServiceException Lock service error
     * @throws LockTimeoutException Failed to get lock
     */
    public void execWithLock(String lockName, Runnable runnable) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("Schedule node not initialize.");
        }

        lockService.execWithLock(lockName, runnable);
    }

}
