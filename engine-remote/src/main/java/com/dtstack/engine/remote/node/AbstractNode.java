package com.dtstack.engine.remote.node;

import com.dtstack.engine.remote.message.Message;

/**
 * @Auther: dazhi
 * @Date: 2021/8/3 5:01 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractNode {

    /**
     * 节点名称
     */
    private String identifier;

    /**
     * address
     */
    private String address;

    /**
     * ip
     */
    private String ip;

    /**
     * port
     */
    private int port;


    public AbstractNode(String identifier, String ip, int port) {
        this.identifier = identifier;
        this.ip = ip;
        this.port = port;
        this.address = ip + ":" + port;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getAddress() {
        return address;
    }

    public String getIp() {
        return ip;
    }


    public int getPort() {
        return port;
    }

    /**
     * 负载均衡，发送消息
     *
     * @param message 消息
     * @return
     */
    protected abstract Message sendMessage(Message message);

    /**
     * 初始化方法
     */
    protected abstract void init();

    /**
     * 销毁方法
     *
     */
    protected abstract void close();

}
