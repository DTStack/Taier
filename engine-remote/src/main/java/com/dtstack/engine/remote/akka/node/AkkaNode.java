package com.dtstack.engine.remote.akka.node;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.dtstack.engine.remote.akka.config.AkkaConfig;
import com.dtstack.engine.remote.constant.ServerConstant;
import com.dtstack.engine.remote.exception.RemoteException;
import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.node.AbstractNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: dazhi
 * @Date: 2021/8/18 5:37 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AkkaNode extends AbstractNode {
    private final Logger LOGGER = LoggerFactory.getLogger(AkkaNode.class);
    private ActorRef nodeRef;
    private final AbstractActor.ActorContext context;
    private final ActorRef serverRef;
    private final String path;
    private final String PREFIX_PATH="akka.tcp://dagschedulex@";

    public AkkaNode(String identifier, String ip, int port, ActorRef serverRef, AbstractActor.ActorContext context) {
        super(identifier, ip, port);
        this.serverRef = serverRef;
        this.path = PREFIX_PATH+getAddress();
        this.context = context;
        if (connect()){
            this.setStatus(NodeStatus.USABLE);
        } else {
            this.setStatus(NodeStatus.UNAVAILABLE);
        }
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
        try {
            return tryConn();
        } catch (Exception e) {
            LOGGER.error("",e);
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean connect() {
        try {
            nodeRef = context.actorFor(path + "/user/" + ServerConstant.BASE_PATH);
            return tryConn();
        } catch (Exception e) {
            setStatus(NodeStatus.UNAVAILABLE);
            LOGGER.error("",e);
        }
        return Boolean.FALSE;
    }

    private Boolean tryConn() throws Exception {
        Timeout askTimeout = Timeout.create(java.time.Duration.ofSeconds(AkkaConfig.getAkkaAskTimeout()));
        Duration askResultTime = Duration.create(AkkaConfig.getAkkaAskResultTimeout(), TimeUnit.SECONDS);
        Future<Object> future = Patterns.ask(nodeRef, CommandType.PING, askTimeout);
        Object result = Await.result(future, askResultTime);
        if (result instanceof CommandType && CommandType.PONG.equals(result)) {
            setStatus(NodeStatus.USABLE);
            return Boolean.TRUE;
        } else {
            nodeRef = null;
            setStatus(NodeStatus.UNAVAILABLE);
            return Boolean.FALSE;
        }
    }

    enum  CommandType {
        /**
         * ping
         */
        PING,

        /**
         *  pong
         */
        PONG;
    }
}
