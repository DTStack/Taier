package com.dtstack.engine.remote.akka.actor;

import akka.actor.AbstractLoggingActor;
import akka.pattern.Patterns;
import com.dtstack.engine.remote.akka.config.AkkaConfig;
import com.dtstack.engine.remote.exception.RemoteException;
import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.message.TargetInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @Auther: dazhi
 * @Date: 2020/9/2 2:14 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class LocalActor extends AbstractLoggingActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalActor.class);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, msg->{
                    try {
                        msg.setStatue(Message.MessageStatue.RECEIVE);
                        TargetInfo targetInfo = msg.getTargetInfo();

                        if (targetInfo == null || targetInfo.getClazz() == null || targetInfo.getMethod() == null) {
                            getSender().tell(msg.ask(new RemoteException("无目标信息，无法调用"), Message.MessageStatue.ERROR), self());
                            return;
                        }
                        Object transport = msg.getTransport();

                        String methodName = targetInfo.getMethod();

                        Object bean = AkkaConfig.getApplicationContext().getBean(Class.forName(targetInfo.getClazz()));

                        Method targetMethod;
                        if (transport.getClass().isArray()) {
                            Object[] objects = (Object[]) transport;
                            Class<?>[] clazzs = new Class<?>[objects.length];

                            for (int i = 0; i < objects.length; i++) {
                                clazzs[i] = objects[i].getClass();
                            }
                            targetMethod = bean.getClass().getMethod(methodName, clazzs);

                            Executor ex = context().system().dispatchers().lookup("blocking-io-dispatcher");
                            CompletableFuture<Object> future = CompletableFuture.supplyAsync(
                                    () -> {
                                        try {
                                            return msg.ask(targetMethod.invoke(bean, objects), Message.MessageStatue.RESULT);
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                            return msg.ask(new RemoteException(e), Message.MessageStatue.ERROR);
                                        }
                                    }, ex);

                            Patterns.pipe(future,context().system().dispatcher()).to(sender());
                        } else {
                            getSender().tell(msg.ask(new RemoteException("Parameter error :" + transport), Message.MessageStatue.ERROR), self());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender().tell(msg.ask(new RemoteException(e), Message.MessageStatue.ERROR),sender());
                    }
                })
                .matchAny(this::unhandled)
                .build();
    }
}
