package com.dtstack.engine.remote.akka.actor;

import akka.actor.AbstractLoggingActor;
import com.dtstack.engine.remote.akka.config.AkkaConfig;
import com.dtstack.engine.remote.akka.node.AkkaNode;
import com.dtstack.engine.remote.constant.ServerConstant;
import com.dtstack.engine.remote.exception.NoNodeException;
import com.dtstack.engine.remote.exception.RemoteException;
import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.node.AbstractNode;
import com.dtstack.engine.remote.node.RemoteNodes;
import com.dtstack.engine.remote.node.strategy.NodeInfoStrategy;
import com.dtstack.engine.remote.route.RouteStrategy;
import org.apache.commons.lang.StringUtils;
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
        ActorContext context = getContext();
        // 初始化节点
        NodeInfoStrategy nodeInfoStrategy = AkkaConfig.getNodeInfoStrategy();
        RouteStrategy routeStrategy = AkkaConfig.getRouteStrategy();

        Map<String, List<String>> nodeInfo = nodeInfoStrategy.getNodeInfo();
        for (Map.Entry<String, List<String>> entry : nodeInfo.entrySet()) {
            String identifier = entry.getKey();
            List<String> value = entry.getValue();
            RemoteNodes remoteNodes = new RemoteNodes(identifier);
            remoteNodes.setRouteStrategy(routeStrategy);
            Map<String, AbstractNode> refs = remoteNodes.getRefs();
            for (String address : value) {
                if (StringUtils.isNotBlank(address)) {
                    String[] split = address.split(":");

                    if (split.length < 2) {
                        LOGGER.warn("node info error : {}", address);
                        continue;
                    }
                    String ip = split[0];
                    int port = Integer.parseInt(split[1]);
                    AkkaNode akkaNode = new AkkaNode(identifier, ip, port, getSender(),context);
                    refs.put(address, akkaNode);
                }
            }
            remoteNodes.start();
            roles.put(identifier, remoteNodes);
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
