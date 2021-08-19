package com.dtstack.engine.remote.akka.actor;

import akka.actor.AbstractLoggingActor;
import com.dtstack.engine.remote.akka.config.AkkaConfig;
import com.dtstack.engine.remote.exception.NoNodeException;
import com.dtstack.engine.remote.exception.RemoteException;
import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.node.AbstractNode;
import com.dtstack.engine.remote.node.RemoteNodes;
import com.dtstack.engine.remote.node.strategy.NodeInfoStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: dazhi
 * @Date: 2020/8/28 2:04 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ServerActor extends AbstractLoggingActor {
    private Map<String, RemoteNodes> roles = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerActor.class);



    @Override
    public void preStart() throws Exception {
        // 初始化节点
        NodeInfoStrategy nodeInfoStrategy = AkkaConfig.getNodeInfoStrategy();
        List<String> identifiers = AkkaConfig.getIdentifiers();

        for (String identifier : identifiers) {
            List<String> nodes = AkkaConfig.getNodes(identifier);


        }
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class,msg->{
                    try {
                        String roles = msg.getIdentifier();
                        RemoteNodes nodes = this.roles.get(roles);

                        if (nodes==null) {
                            // 没有集群可用
                            sender().tell(msg.ask(new NoNodeException("[ " + msg.getIdentifier() + " ]: no usable node"), Message.MessageStatue.ERROR), sender());
                            return;
                        }
                        msg.setStatue(Message.MessageStatue.SENDER);
                        AbstractNode route = nodes.route();
                        route.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender().tell(msg.ask(new RemoteException(e), Message.MessageStatue.ERROR),sender());
                    }
                })
                .matchAny(this::unhandled)
                .build();
    }

}
