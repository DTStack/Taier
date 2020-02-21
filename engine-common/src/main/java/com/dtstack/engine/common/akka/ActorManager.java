package com.dtstack.engine.common.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import com.dtstack.engine.common.WorkerInfo;
import com.google.common.collect.Maps;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ActorManager implements Runnable {

    private ActorSystem system;
    private ActorRef actorRef;
    private Map<String, WorkerInfo> workerInfoMap = Maps.newHashMap();
    private static volatile ActorManager actorManager;
    private Long timeout = Long.valueOf(4000);

    private ActorManager(String name){
        this.system = ActorSystem.create(name, ConfigFactory.load());
        this.actorRef = system.actorOf(Props.create(Master.class), "Master");
    }

    public static ActorManager createMasterActorManager(String name){
        if(actorManager == null){
            synchronized(ActorManager.class){
                if(actorManager == null){
                    actorManager = new ActorManager(name);
                }
            }
        }
        return actorManager;

    }

    public static ActorManager getInstance(){
        return actorManager;
    }

    public ActorSystem getSystem(){
        return system;
    }

    public ActorRef getActorRef(){
        return actorRef;
    }

    public Map getWorkerInfoMap(){
        return workerInfoMap;
    }

    @Override
    public void run() {
        try {
            updateWorkerActors();
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateWorkerActors() throws Exception {
        Future<Object> future = Patterns.ask(actorRef, "getWorkerInfos", 3000);
        HashMap<String, WorkerInfo> infos = (HashMap<String, WorkerInfo>) Await.result(future, Duration.create(3, TimeUnit.SECONDS));
        workerInfoMap = infos.entrySet().stream()
                .filter(map ->System.currentTimeMillis() - map.getValue().getTimestamp() > timeout)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }
}
