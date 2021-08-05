package com.dtstack.engine.remote.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Auther: dazhi
 * @Date: 2021/8/3 5:01 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RemoteNodes {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteNodes.class);
    /**
     * 节点信息
     */
    private final String identifier;

    /**
     * key : address
     * value : node
     */
    private final Map<String , AbstractNode> refs = new ConcurrentHashMap<>(128);

    public RemoteNodes(String identifier) {
        this.identifier = identifier;
    }

    public Map<String, AbstractNode> getRefs() {
        return refs;
    }

    public String getIdentifier() {
        return identifier;
    }


    public void close() {
        for (Map.Entry<String, AbstractNode> entry : refs.entrySet()) {
            AbstractNode value = entry.getValue();
            value.close();
        }
    }

    public void close(String address) {
        AbstractNode abstractNode = refs.get(address);
        if (abstractNode != null) {
            abstractNode.close();
        }
    }

    public AbstractNode route() {
        return null;
    }





}
