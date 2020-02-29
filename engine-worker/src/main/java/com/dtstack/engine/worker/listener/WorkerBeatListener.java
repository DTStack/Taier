package com.dtstack.engine.worker.listener;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.dtcenter.common.util.AddressUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.akka.message.WorkerInfo;
import com.dtstack.engine.common.util.LogCountUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkerBeatListener implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(WorkerBeatListener.class);

    private final static String MASTER_REMOTE_PATH_TEMPLATE = "akka.tcp://%s@%s/user/%s";
    private int logOutput = 0;
    private final static int MULTIPLES = 30;
    private final static int CHECK_INTERVAL = 1000;
    private static Random random = new Random();
    private volatile Set<String> availableNodes = new CopyOnWriteArraySet();
    private volatile Set<String> disableNodes = new CopyOnWriteArraySet();
    private ActorSystem system;
    private String masterAddress;
    private String masterSystemName;
    private String masterName;
    private String workerIp;
    private int workerPort;
    private String workerRemotePath;


    public WorkerBeatListener(ActorSystem system) {
        this.system = system;
        this.masterAddress = AkkaConfig.getMasterAddress();
        this.availableNodes.addAll(Arrays.asList(masterAddress.split(",")));

        this.masterSystemName = AkkaConfig.getMasterSystemName();
        this.masterName = AkkaConfig.getMasterName();

        this.workerIp = AkkaConfig.getAkkaHostname();
        this.workerPort = AkkaConfig.getAkkaPort();
        this.workerRemotePath = AkkaConfig.getWorkerRemotePath();

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(2, new CustomThreadFactory(this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
        scheduledService.scheduleWithFixedDelay(
                new WorkerBeatListener.MonitorNode(),
                CHECK_INTERVAL * 10,
                CHECK_INTERVAL * 10,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        WorkerInfo workerInfo = new WorkerInfo(workerIp, workerPort, workerRemotePath, System.currentTimeMillis());
        ActorSelection actorSelection = getOneActorSelection(masterAddress);
        actorSelection.tell(workerInfo, ActorRef.noSender());
        if (LogCountUtil.count(logOutput++, MULTIPLES)) {
            logger.info("WorkerBeatListener Running gap:[{} ms]...", CHECK_INTERVAL * MULTIPLES);
        }
    }

    private ActorSelection getOneActorSelection(String masterAddress) {
        int size = availableNodes.size();
        String ipAndPort = masterAddress.split(",")[0];
        if (availableNodes.size() != 0) {
            int index = random.nextInt(size);
            ipAndPort = Lists.newArrayList(availableNodes).get(index);
        }
        String masterRemotePath = String.format(MASTER_REMOTE_PATH_TEMPLATE, masterSystemName, ipAndPort, masterName);
        ActorSelection master = system.actorSelection(masterRemotePath);
        logger.info("Get an ActorSelection, path:{}", masterRemotePath);
        return master;
    }

    private class MonitorNode implements Runnable {
        @Override
        public void run() {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("availableNodes--->{},disableNodes---->{}", availableNodes, disableNodes);
                }
                if (disableNodes.size() > 0) {
                    Iterator<String> iterators = disableNodes.iterator();
                    while (iterators.hasNext()) {
                        String uri = iterators.next();
                        String[] up = uri.split(":");
                        if (AddressUtil.telnet(up[0], Integer.parseInt(up[1]))) {
                            availableNodes.add(uri);
                            disableNodes.remove(uri);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }
}

