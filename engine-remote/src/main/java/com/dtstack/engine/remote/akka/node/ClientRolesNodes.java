package com.dtstack.engine.remote.akka.node;

import akka.actor.ActorRef;
import akka.routing.ActorRefRoutee;
import akka.routing.Router;
import com.dtstack.engine.remote.exception.NoNodeException;
import com.dtstack.engine.remote.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Auther: dazhi
 * @Date: 2020/9/1 3:07 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ClientRolesNodes {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRolesNodes.class);
    private final String roleName;
    private List<ClientRolesNode> refs;
    private Router router;
    private final Lock lock = new ReentrantLock();

    public ClientRolesNodes(String roleName,Router router) {
        this.roleName = roleName;
        this.router = router;
    }

    public String getRoleName() {
        return roleName;
    }

    public Router getRouter() {
        return router;
    }

    public Boolean addNodes(ActorRef actorFor) {
        if (actorFor != null) {
            if (CollectionUtils.isEmpty(refs)) {
                refs = new ArrayList<>();
            }
            try {
                lock.lock();
                ClientRolesNode node = new ClientRolesNode(actorFor.path().toString(), actorFor);
                if (!refs.contains(node)) {
                    refs.add(node);
                    router = router.addRoutee(new ActorRefRoutee(actorFor));
                    LOGGER.info("roles:{} add node :{},refs:{}",roleName,node.toString(),refs.toString());
                }
            } finally {
                lock.unlock();
            }

            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean remove(ActorRef actorFor) {
        if (actorFor != null) {
            if (CollectionUtils.isEmpty(refs)) {
                refs = new ArrayList<>();
            }
            ClientRolesNode node;
            try {
                lock.lock();
                node = new ClientRolesNode(actorFor.path().toString(), actorFor);
                refs.remove(node);
                router = router.removeRoutee(actorFor);
            } finally {
                lock.unlock();
            }
            LOGGER.info("roles:{} remove node :{},refs:{}",roleName,node.toString(),refs.toString());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void send(Message msg, ActorRef sender) {
        ClientRolesNode clientRolesNode = refs.get(0);

        ActorRef actorRef = clientRolesNode.getActorRef();

        actorRef.tell(msg,sender);
    }

    public void route(Message msg, ActorRef sender) {
        LOGGER.info("roles:{} ,refs:{}",msg.getRoles(),msg.getTransport());
        if (router.routees().size() == 0) {
            // 无节点不可以发送
            sender.tell(msg.ask(new NoNodeException("[ "+msg.getRoles()+" ]: no usable node"), Message.MessageStatue.ERROR),sender);
            return;
        }

        router.route(msg,sender);
    }
}
