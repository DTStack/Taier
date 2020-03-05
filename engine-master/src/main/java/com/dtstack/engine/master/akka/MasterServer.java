package com.dtstack.engine.master.akka;

import java.util.Collection;

/**
 * @Auther: jiangjunjie
 * @Date: 2020/3/3
 * @Description:
 */
public interface MasterServer<T, E> {

    /**
     * strategy for get one worker
     */
    E strategyForGetWorker(Collection<E> workers);

    /**
     * send message to worker
     */
    Object sendMessage(T message) throws Exception ;

    /**
     * update worker info
     */
    void updateWorkerInfo();
}
