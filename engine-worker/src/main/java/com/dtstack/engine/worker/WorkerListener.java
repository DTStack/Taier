package com.dtstack.engine.worker;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import com.dtstack.engine.common.WorkerInfo;

public class WorkerListener implements  Runnable{

    ActorSelection actorSelection;
    String ip;
    int port;
    String path;

    public WorkerListener(ActorSelection actorSelection, String ip, int port, String path){
        this.actorSelection = actorSelection;
        this.ip = ip;
        this.port = port;
        this.path = path;
    }
    @Override
    public void run() {
        while (true){
            WorkerInfo workerInfo = new WorkerInfo(ip, port, path, System.currentTimeMillis());
            actorSelection.tell(workerInfo,  ActorRef.noSender());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

