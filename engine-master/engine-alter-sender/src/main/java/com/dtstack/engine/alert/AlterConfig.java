package com.dtstack.engine.alert;

/**
 * @Auther: dazhi
 * @Date: 2021/1/14 3:48 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterConfig {

    /**
     * 默认大小: 200
     */
    private Integer queueSize = 200;

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
}
