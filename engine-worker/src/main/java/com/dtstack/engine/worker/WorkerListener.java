package com.dtstack.engine.worker;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import com.dtstack.engine.common.WorkerInfo;

public class WorkerListener implements  Runnable{

    ActorSelection actorSelection;
    WorkerInfo workerInfo;

    public WorkerListener(ActorSelection actorSelection, WorkerInfo workerInfo){
        this.actorSelection = actorSelection;
        this.workerInfo = workerInfo;
    }
    @Override
    public void run() {
        while (true){
            actorSelection.tell(workerInfo,  ActorRef.noSender());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

