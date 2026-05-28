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
 
package com.dtstack.taier.metrics.collect.entity;

/**
 * collector for ThreadPool ThreadPoolStats
 * @author xingyi
 * @date 2025/9/16
 */
public class ThreadPoolStats extends Metrics {

    /**
     * 线程池名字
     */
    private String poolName;

    /**
     * 线程池别名
     */
    private String poolAliasName;

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程数
     */
    private int maximumPoolSize;

    /**
     * 空闲时间 (ms)
     */
    private long keepAliveTime;

    /**
     * 队列类型
     */
    private String queueType;

    /**
     * 队列容量
     */
    private int queueCapacity;

    /**
     * 队列任务数量
     */
    private int queueSize;

    /**
     * SynchronousQueue队列模式
     */
    private boolean fair;

    /**
     * 队列剩余容量
     */
    private int queueRemainingCapacity;

    /**
     * 正在执行任务的活跃线程大致总数
     */
    private int activeCount;

    /**
     * 大致任务总数
     */
    private long taskCount;

    /**
     * 已执行完成的大致任务总数
     */
    private long completedTaskCount;

    /**
     * 池中曾经同时存在的最大线程数量
     */
    private int largestPoolSize;

    /**
     * 当前池中存在的线程总数
     */
    private int poolSize;

    /**
     * 等待执行的任务数量
     */
    private int waitTaskCount;

    /**
     * 拒绝的任务数量
     */
    private long rejectCount;

    /**
     * 拒绝策略名称
     */
    private String rejectHandlerName;

    /**
     * 是否DtpExecutor线程池
     */
    private boolean dynamic;

    /**
     * 执行超时任务数量
     */
    private long runTimeoutCount;

    /**
     * 在队列等待超时任务数量
     */
    private long queueTimeoutCount;

    /**
     * tps
     */
    private double tps;

    /**
     * 最大任务耗时
     */
    private long maxRt;

    /**
     * 最小任务耗时
     */
    private long minRt;

    /**
     * 任务平均耗时(单位:ms)
     */
    private double avg;

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getPoolAliasName() {
        return poolAliasName;
    }

    public void setPoolAliasName(String poolAliasName) {
        this.poolAliasName = poolAliasName;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public boolean isFair() {
        return fair;
    }

    public void setFair(boolean fair) {
        this.fair = fair;
    }

    public int getQueueRemainingCapacity() {
        return queueRemainingCapacity;
    }

    public void setQueueRemainingCapacity(int queueRemainingCapacity) {
        this.queueRemainingCapacity = queueRemainingCapacity;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(long taskCount) {
        this.taskCount = taskCount;
    }

    public long getCompletedTaskCount() {
        return completedTaskCount;
    }

    public void setCompletedTaskCount(long completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
    }

    public int getLargestPoolSize() {
        return largestPoolSize;
    }

    public void setLargestPoolSize(int largestPoolSize) {
        this.largestPoolSize = largestPoolSize;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getWaitTaskCount() {
        return waitTaskCount;
    }

    public void setWaitTaskCount(int waitTaskCount) {
        this.waitTaskCount = waitTaskCount;
    }

    public long getRejectCount() {
        return rejectCount;
    }

    public void setRejectCount(long rejectCount) {
        this.rejectCount = rejectCount;
    }

    public String getRejectHandlerName() {
        return rejectHandlerName;
    }

    public void setRejectHandlerName(String rejectHandlerName) {
        this.rejectHandlerName = rejectHandlerName;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public long getRunTimeoutCount() {
        return runTimeoutCount;
    }

    public void setRunTimeoutCount(long runTimeoutCount) {
        this.runTimeoutCount = runTimeoutCount;
    }

    public long getQueueTimeoutCount() {
        return queueTimeoutCount;
    }

    public void setQueueTimeoutCount(long queueTimeoutCount) {
        this.queueTimeoutCount = queueTimeoutCount;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }

    public long getMaxRt() {
        return maxRt;
    }

    public void setMaxRt(long maxRt) {
        this.maxRt = maxRt;
    }

    public long getMinRt() {
        return minRt;
    }

    public void setMinRt(long minRt) {
        this.minRt = minRt;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    @Override
    public String toString() {
        return "ThreadPoolStats{" +
                "poolName='" + poolName + '\'' +
                ", poolAliasName='" + poolAliasName + '\'' +
                ", corePoolSize=" + corePoolSize +
                ", maximumPoolSize=" + maximumPoolSize +
                ", keepAliveTime=" + keepAliveTime +
                ", queueType='" + queueType + '\'' +
                ", queueCapacity=" + queueCapacity +
                ", queueSize=" + queueSize +
                ", fair=" + fair +
                ", queueRemainingCapacity=" + queueRemainingCapacity +
                ", activeCount=" + activeCount +
                ", taskCount=" + taskCount +
                ", completedTaskCount=" + completedTaskCount +
                ", largestPoolSize=" + largestPoolSize +
                ", poolSize=" + poolSize +
                ", waitTaskCount=" + waitTaskCount +
                ", rejectCount=" + rejectCount +
                ", rejectHandlerName='" + rejectHandlerName + '\'' +
                ", dynamic=" + dynamic +
                ", runTimeoutCount=" + runTimeoutCount +
                ", queueTimeoutCount=" + queueTimeoutCount +
                ", tps=" + tps +
                ", maxRt=" + maxRt +
                ", minRt=" + minRt +
                ", avg=" + avg +
                '}';
    }
}
