package com.dtstack.task.server.zk.listener;

/**
 * company: www.dtstack.com
 * @author: toutian
 * create: 2019/10/22
 */
public interface Listener extends Runnable {

    void close() throws Exception;
}
