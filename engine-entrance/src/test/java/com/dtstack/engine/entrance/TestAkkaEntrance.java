package com.dtstack.engine.entrance;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.akka.message.WorkerInfo;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.master.akka.AkkaMasterActor;
import com.dtstack.engine.worker.service.JobService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import netscape.javascript.JSObject;
import org.junit.Test;

import java.util.Arrays;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/4/23
 */
public class TestAkkaEntrance {

    private ActorSystem system;

    @Test
    public void testAkkaEntrance() {
        AkkaConfig.setLocalMode(true);
        Config config = AkkaConfig.init(ConfigFactory.load());
        SystemPropertyUtil.setSystemUserDir();
        this.system = AkkaConfig.initActorSystem(config);

        ActorRef worker = system.actorOf(Props.create(JobService.class));
        ActorRef master = system.actorOf(Props.create(AkkaMasterActor.class));




        WorkerInfo workerInfo = new WorkerInfo("localhost", 0, "121212", System.currentTimeMillis());
        master.tell(workerInfo, worker);

    }
}
