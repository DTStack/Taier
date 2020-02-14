package com.dtstack.engine.worker;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.dtstack.engine.common.JobClient;

public class Worker extends AbstractActor{
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public Receive createReceive() {
        return receiveBuilder()
                .match(JobClient.class, msg->{ log.info("submitjob");})
                .build();
    }
}

