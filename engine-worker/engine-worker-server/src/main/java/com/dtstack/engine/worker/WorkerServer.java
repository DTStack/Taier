package com.dtstack.engine.worker;

/**
 * @Auther: jiangjunjie
 * @Date: 2020/3/3
 * @Description:
 */
public interface WorkerServer<T, E> {

    /**
     * send worker base info to master, e.g: ip,host,systemResource
     */
    void reportWorkerInfo(T t);

    /**
     * check the master is active
     */
    E getActiveMasterAddress();
}
