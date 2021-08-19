package com.dtstack.engine.remote.akka.actor;

import akka.actor.AbstractLoggingActor;
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
    private ActorHandler clientRef;

    @Override
    public void preStart() throws Exception {
        initActor();
    }

    @Override
    public void postStop() throws Exception {
    }

    private void initActor() {
        // 初始化本地处理
        clientRef = new ActorHandler(ClientActor.class,"clientRef",getContext(),"blocking-dispatcher");
        // 初始化节点
        remoteRef = new ActorHandler(ServerActor.class,"remoteRef",getContext());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, msg->{
                    // 判断当前msg是 当前actor处理对象
                    if (AkkaConfig.getLocalRoles().contains(msg.getIdentifier())) {
                        msg.setStatue(Message.MessageStatue.SENDER);
                        clientRef.forward(msg);
                    } else {
                        remoteRef.forward(msg);
                    }
                })
                .matchAny(this::unhandled)
                .build();
    }

}
