package com.dtstack.engine.common.akka;

import akka.actor.ActorSelection;
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
    private ActorSelection actorSelection;
    private Map<String, WorkerInfo> workerInfoMap = Maps.newHashMap();
    private static volatile ActorManager actorManager;
    private Long timeout = Long.valueOf(5000);

    private ActorManager(String name, String path){
        this.system = ActorSystem.create(name, ConfigFactory.load());
        system.actorOf(Props.create(Master.class), "Master");
        this.actorSelection = system.actorSelection(path);
    }

    public static ActorManager createMasterActorManager(String name, String path){
        if(actorManager == null){
            synchronized(ActorManager.class){
                if(actorManager == null){
                    actorManager = new ActorManager(name, path);
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

    public ActorSelection getActorSelection(){
        return actorSelection;
    }

    public Map getWorkerInfoMap(){
        return workerInfoMap;
    }

    @Override
    public void run() {
        while (true){
            try {
                updateWorkerActors();
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateWorkerActors() throws Exception {
        Future<Object> future = Patterns.ask(actorSelection, "getWorkerInfos", 3000);
        HashMap<String, WorkerInfo> infos = (HashMap<String, WorkerInfo>) Await.result(future, Duration.create(3, TimeUnit.SECONDS));
        workerInfoMap = infos.entrySet().stream()
                .filter(map ->System.currentTimeMillis() - map.getValue().getTimestamp() > timeout)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }
}
