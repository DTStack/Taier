package com.dtstack.engine.remote.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.Broadcast;
import akka.routing.RoundRobinPool;
import com.dtstack.engine.remote.akka.config.AkkaConfig;
import com.dtstack.engine.remote.message.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: dazhi
 * @Date: 2020/9/16 9:42 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ActorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ActorHandler.class);
    /**
     * actor的class文件
     */
    private final Class<? extends AbstractActor> actorClass;
    /**
     * actorName的名字
     */
    private final String actorName;
    /**
     * 处理个数
     */
    private final Integer handlerNumber;
    private final AbstractActor.ActorContext context;
    private ActorRef roundRobinRouting;


    public ActorHandler(Class<? extends AbstractActor> actorClass, String actorName,AbstractActor.ActorContext context) {
        this.actorClass = actorClass;
        this.actorName = actorName;
        this.context = context;
        this.handlerNumber = AkkaConfig.getHandlerNumber();
        init(null);
    }

    public ActorHandler(Class<? extends AbstractActor> actorClass, String actorName,AbstractActor.ActorContext context,Integer handlerNumber) {
        this.actorClass = actorClass;
        this.actorName = actorName;
        this.context = context;
        this.handlerNumber = handlerNumber;
        init(null);
    }

    public ActorHandler(Class<? extends AbstractActor> actorClass, String actorName,AbstractActor.ActorContext context,Integer handlerNumber,String dispatcher) {
        this.actorClass = actorClass;
        this.actorName = actorName;
        this.context = context;
        this.handlerNumber = handlerNumber;
        init(dispatcher);
    }

    public ActorHandler(Class<? extends AbstractActor> actorClass, String actorName,AbstractActor.ActorContext context,String dispatcher) {
        this.actorClass = actorClass;
        this.actorName = actorName;
        this.context = context;
        this.handlerNumber = AkkaConfig.getHandlerNumber();
        init(dispatcher);
    }

    private void init(String dispatcher) {
        if (StringUtils.isNotBlank(dispatcher)) {
            roundRobinRouting = context.actorOf(Props.create(actorClass)
                    .withRouter(new RoundRobinPool(handlerNumber).withDispatcher(dispatcher)),actorName);
        } else {
            roundRobinRouting = context.actorOf(Props.create(actorClass)
                    .withRouter(new RoundRobinPool(handlerNumber)),actorName);
        }
        context.watch(roundRobinRouting);
    }

    public void forward(Message msg) {
        roundRobinRouting.tell(msg,context.getSender());
    }

    public void broadcast(Object obj){
        roundRobinRouting.tell(new Broadcast(obj),context.getSender());
    }






}
