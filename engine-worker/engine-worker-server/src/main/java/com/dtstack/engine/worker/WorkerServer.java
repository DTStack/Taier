package com.dtstack.engine.worker;

/**
 * @Auther: jiangjunjie
 * @Date: 2020/3/3
 * @Description:
 */
public interface WorkerServer<T, E> {

    /**
     * send heartbeat to master
     */
    void heartBeat(T t);

    /**
     * check the master is active
     */
    E getActiveMasterAddress();
}
