package com.dtstack.engine.remote.akka.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.SupervisorStrategy;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import com.dtstack.engine.remote.akka.config.AkkaConfig;
import com.dtstack.engine.remote.akka.ActorHandler;
import com.dtstack.engine.remote.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: dazhi
 * @Date: 2020/8/28 11:13 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ObjectActor extends AbstractLoggingActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectActor.class);
    private ActorHandler remoteRef;
    private ActorHandler localRef;
    private Cluster cluster = Cluster.get(context().system());
    private SupervisorStrategy supervisorStrategy;

    @Override
    public void preStart() throws Exception {
        cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
        initActor();
    }

    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(self());
    }

    private void initActor() {
        remoteRef = new ActorHandler(ServerActor.class,"remoteRef",getContext());
        localRef = new ActorHandler(ClientActor.class,"localRef",getContext(),"blocking-dispatcher");
    }

//    public SupervisorStrategy supervisorStrategy(){
//        if (supervisorStrategy==null) {
//            supervisorStrategy = new OneForOneStrategy(AkkaConfig.getMaxNrOfRetries(), AkkaConfig.getWithinTimeRange(), new Function<Throwable, SupervisorStrategy.Directive>() {
//                @Override
//                public SupervisorStrategy.Directive apply(Throwable param) throws Exception, Exception {
//                    return SupervisorStrategy.restart();
//                }
//            });
//        }
//        return supervisorStrategy;
//    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, msg->{
                    // 判断当前msg是 当前actor处理对象
                    if (AkkaConfig.getLocalRoles().contains(msg.getIdentifier())) {
                        msg.setStatue(Message.MessageStatue.SENDER);
                        localRef.forward(msg);
                    } else {
                        remoteRef.forward(msg);
                    }
                })
                .match(ClusterEvent.CurrentClusterState.class, state -> remoteRef.broadcast(state))
                .match(ClusterEvent.ReachableMember.class, x -> remoteRef.broadcast(x))
                .match(ClusterEvent.UnreachableMember.class, x -> remoteRef.broadcast(x))
                .match(ClusterEvent.MemberUp.class, x -> remoteRef.broadcast(x))
                .match(ClusterEvent.MemberRemoved.class, x -> remoteRef.broadcast(x))
                .matchAny(this::unhandled)
                .build();
    }

}
