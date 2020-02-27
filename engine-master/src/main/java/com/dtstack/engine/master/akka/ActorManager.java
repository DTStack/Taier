package com.dtstack.engine.master.akka;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.message.WorkerInfo;
import com.dtstack.engine.common.util.LogCountUtil;
import com.google.common.collect.Maps;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ActorManager implements InitializingBean, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ActorManager.class);

    private int logOutput = 0;
    private final static int MULTIPLES = 10;
    private final static int CHECK_INTERVAL = 2000;
    private final ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("WorkerInfoListener"));
    private ActorSystem system;
    private ActorSelection actorSelection;
    private Map<String, WorkerInfo> workerInfoMap = Maps.newHashMap();
    private String name;
    private String path;
    private long timeout = 5000L;

    @Override
    public void run() {
        try {
            updateWorkerActors();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (LogCountUtil.count(logOutput, MULTIPLES)) {
            logger.info("Update WorkerInfos...");
        }
    }

    private void updateWorkerActors() throws Exception {
        Future<Object> future = Patterns.ask(actorSelection, "getWorkerInfos", 3000);
        HashMap<String, WorkerInfo> infos = (HashMap<String, WorkerInfo>) Await.result(future, Duration.create(3, TimeUnit.SECONDS));
        workerInfoMap = infos.entrySet().stream()
                .filter(map -> System.currentTimeMillis() - map.getValue().getTimestamp() > timeout)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.system = ActorSystem.create(name, ConfigFactory.load());
        this.system.actorOf(Props.create(AkkaMasterActor.class), AkkaMasterActor.class.getSimpleName());
        this.actorSelection = system.actorSelection(path);
        scheduledService.scheduleWithFixedDelay(
                this,
                CHECK_INTERVAL,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ActorSystem getSystem() {
        return system;
    }

    public ActorSelection getActorSelection() {
        return actorSelection;
    }

    public Map<String, WorkerInfo> getWorkerInfoMap() {
        return workerInfoMap;
    }
}
