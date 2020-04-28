package com.dtstack.engine.master.listener;

/**
 * company: www.dtstack.com
 * @author: toutian
 * create: 2019/10/22
 */
public interface Listener extends Runnable {

    void close() throws Exception;
}
