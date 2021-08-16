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
    private final String identifier;

    /**
     * address
     */
    private final String address;

    /**
     * ip
     */
    private final String ip;

    /**
     * port
     */
    private final int port;

    /**
     * status 节点的状态
     */
    private NodeStatus status;


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

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    /**
     * 负载均衡，发送消息
     *
     * @param message 消息
     * @return
     */
    public abstract Message sendMessage(Message message);

    /**
     * 初始化方法
     */
    protected abstract void init();

    /**
     * 销毁方法
     *
     */
    protected abstract void close();

    /**
     *
     *
     * @return true 活的 false 死的
     */
    public abstract Boolean heartbeat();

    /**
     * 重新发起连接
     *
     * @return true 连接成功 false 连接失败
     */
    public abstract Boolean connect();

    public enum NodeStatus {
        USABLE,UNAVAILABLE
    }

}
