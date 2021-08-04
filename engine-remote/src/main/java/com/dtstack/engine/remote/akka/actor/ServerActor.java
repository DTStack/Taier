package com.dtstack.engine.remote.akka.actor;

import akka.actor.AbstractLoggingActor;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import com.dtstack.engine.remote.akka.config.AkkaConfig;
import com.dtstack.engine.remote.akka.node.ClientRolesNodes;
import com.dtstack.engine.remote.constant.ServerConstant;
import com.dtstack.engine.remote.exception.NoNodeException;
import com.dtstack.engine.remote.exception.RemoteException;
import com.dtstack.engine.remote.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: dazhi
 * @Date: 2020/8/28 2:04 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ServerActor extends AbstractLoggingActor {
    private Map<String, ClientRolesNodes> roles = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerActor.class);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class,msg->{
                    try {
                        String roles = msg.getIdentifier();
                        ClientRolesNodes nodes = this.roles.get(roles);

                        if (nodes==null) {
                            // 没有集群可用
                            sender().tell(msg.ask(new NoNodeException("[ " + msg.getIdentifier() + " ]: no usable node"), Message.MessageStatue.ERROR), sender());
                            return;
                        }
                        msg.setStatue(Message.MessageStatue.SENDER);
                        nodes.route(msg,sender());
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender().tell(msg.ask(new RemoteException(e), Message.MessageStatue.ERROR),sender());
                    }
                })
                .match(ClusterEvent.CurrentClusterState.class, state -> {
                    // 当前节点在刚刚加入集群时，会收到CurrentClusterState消息，从中可以解析出集群中的所有前端节点
                    roles.clear();
                    for (Member member : state.getMembers()) {
                        if (member.status().equals(MemberStatus.up())) {
                            register(member);
                        }
                    }
                })
                .match(ClusterEvent.ReachableMember.class,
                        x -> {
                            LOGGER.info("Member is Reachable: {}", x.member());
                            register(x.member());
                        }
                )
                .match(ClusterEvent.UnreachableMember.class,
                        x -> {
                            LOGGER.info("Member is Unreachable: {}", x.member());
                            //unRegister(x.member());
                        }
                )
                .match(ClusterEvent.MemberUp.class,
                        x -> {
                            LOGGER.info("Member is Up: {}", x.member());
                            register(x.member());
                        }
                )
                .match(ClusterEvent.MemberRemoved.class,
                        x -> {
                            LOGGER.info("Member is Removed: {}", x.member());
                            unRegister(x.member());
                        }
                )
                .matchAny(this::unhandled)
                .build();
    }

    private void register(Member member) {
        Set<String> rolesSet = member.getRoles();
        for (String role : rolesSet) {
            ClientRolesNodes nodes = this.roles.computeIfAbsent(role, k -> new ClientRolesNodes(role, AkkaConfig.getRouter()));
            nodes.addNodes(getContext().actorFor(member.address() + "/user/" + ServerConstant.BASE_PATH));
        }
    }

    private void unRegister(Member member) {
        Set<String> rolesSet = member.getRoles();
        for (String role : rolesSet) {
            ClientRolesNodes nodes = this.roles.computeIfAbsent(role, k -> new ClientRolesNodes(role, AkkaConfig.getRouter()));
            nodes.remove(getContext().actorFor(member.address() + "/user/" + ServerConstant.BASE_PATH));
        }
    }
}
