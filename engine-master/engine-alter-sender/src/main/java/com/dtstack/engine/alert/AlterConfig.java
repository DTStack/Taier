package com.dtstack.engine.alert;

/**
 * @Auther: dazhi
 * @Date: 2021/1/14 3:48 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterConfig {

    /**
     * 队列大小，默认大小: 200
     */
    private Integer queueSize = 200;

    /**
     * 核心线程数，默认大小：5
     */
    private Integer jobExecutorPoolCorePoolSize = 5;

    /**
     * 最大线程数，默认大小：10
     */
    private Integer jobExecutorPoolMaximumPoolSize = 10;

    /**
     * 线程存活时间: 默认 1000ms
     */
    private Integer jobExecutorPoolKeepAliveTime = 1000;
    /**
     *
     */
    private Integer acquireQueueJobInterval;


    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    public Integer getAcquireQueueJobInterval() {
        return acquireQueueJobInterval;
    }

    public void setAcquireQueueJobInterval(Integer acquireQueueJobInterval) {
        this.acquireQueueJobInterval = acquireQueueJobInterval;
    }

    public Integer getJobExecutorPoolCorePoolSize() {
        return jobExecutorPoolCorePoolSize;
    }

    public void setJobExecutorPoolCorePoolSize(Integer jobExecutorPoolCorePoolSize) {
        this.jobExecutorPoolCorePoolSize = jobExecutorPoolCorePoolSize;
    }

    public Integer getJobExecutorPoolMaximumPoolSize() {
        return jobExecutorPoolMaximumPoolSize;
    }

    public void setJobExecutorPoolMaximumPoolSize(Integer jobExecutorPoolMaximumPoolSize) {
        this.jobExecutorPoolMaximumPoolSize = jobExecutorPoolMaximumPoolSize;
    }

    public Integer getJobExecutorPoolKeepAliveTime() {
        return jobExecutorPoolKeepAliveTime;
    }

    public void setJobExecutorPoolKeepAliveTime(Integer jobExecutorPoolKeepAliveTime) {
        this.jobExecutorPoolKeepAliveTime = jobExecutorPoolKeepAliveTime;
    }
}
