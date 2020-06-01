package com.dtstack.engine.api.service;

public interface NodeRecoverService {
    /**
     * 接收 master 节点容灾后的消息
     */
    public void masterTriggerNode();

    public void recoverJobCaches();
}
