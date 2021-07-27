package com.dtstack.engine.remote.akka;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.dtstack.engine.remote.akka.config.AkkaConfig;
import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: dazhi
 * @Date: 2020/9/2 1:37 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AkkaClientServiceImpl  implements ClientService {

    private ActorRef actorRef;

    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaClientServiceImpl.class);

    @Override
    public Message sendMassage(Message message) throws Exception {
        Timeout askTimeout = Timeout.create(java.time.Duration.ofSeconds(AkkaConfig.getAkkaAskTimeout()));
        Duration askResultTime = Duration.create(AkkaConfig.getAkkaAskResultTimeout(), TimeUnit.SECONDS);
        Future<Object> future = Patterns.ask(actorRef, message, askTimeout);
        Object result = Await.result(future, askResultTime);
        if (result instanceof Message) {
            Message resultMsg = (Message) result;
            if (Message.MessageStatue.ERROR.equals(resultMsg.getStatue()) && resultMsg.getTransport() instanceof Exception) {
                LOGGER.error("");
                throw (Exception) resultMsg.getTransport();
            } else {
                return resultMsg;
            }
        }
        return message;
    }

    public void setActorRef(ActorRef actorRef) {
        this.actorRef = actorRef;
    }
}
