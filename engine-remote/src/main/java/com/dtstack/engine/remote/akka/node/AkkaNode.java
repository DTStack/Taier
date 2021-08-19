package com.dtstack.engine.remote.akka.node;

import akka.actor.ActorRef;
import com.dtstack.engine.remote.exception.RemoteException;
import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.node.AbstractNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: dazhi
 * @Date: 2021/8/18 5:37 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AkkaNode extends AbstractNode {
    private final Logger LOGGER = LoggerFactory.getLogger(AkkaNode.class);
    private ActorRef nodeRef;
    private final ActorRef serverRef;

    public AkkaNode(String identifier, String ip, int port,ActorRef serverRef) {
        super(identifier, ip, port);
        this.serverRef = serverRef;
    }

    @Override
    public Message sendMessage(Message message) {
        if (NodeStatus.UNAVAILABLE.equals(getStatus()) && nodeRef == null) {
            // 节点不健康
            throw new RemoteException(String.format("node %s unavailable", getAddress()));
        }
        nodeRef.tell(message,serverRef);
        return null;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void close() {
    }

    @Override
    public Boolean heartbeat() {
        return null;
    }

    @Override
    public Boolean connect() {
        return null;
    }
}
