package com.dtstack.engine.worker.listener;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
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

public class HeartBeatListener implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatListener.class);

    private final String masterTemp = "akka.tcp://AkkaRemoteMaster@%s/user/AkkaMasterActor";
    private int logOutput = 0;
    private final static int MULTIPLES = 10;
    private final static int CHECK_INTERVAL = 2000;
    private static Random random = new Random();
    private volatile Set<String> availableNodes = new CopyOnWriteArraySet();
    private volatile Set<String> disableNodes = new CopyOnWriteArraySet();
    private ActorSystem system;
    private String masterAddress;
    private String ip;
    private int port;
    private String path;


    public HeartBeatListener(ActorSystem system, String masterAddress, String ip, int port, String path) {
        this.system = system;
        this.masterAddress = masterAddress;
        availableNodes.addAll(Arrays.asList(masterAddress.split(",")));
        this.ip = ip;
        this.port = port;
        this.path = path;

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(2, new CustomThreadFactory("HeartBeatListener"));
        scheduledService.submit(new HeartBeatListener.MonitorNode());
        scheduledService.scheduleWithFixedDelay(
                this,
                CHECK_INTERVAL,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        WorkerInfo workerInfo = new WorkerInfo(ip, port, path, System.currentTimeMillis());
        ActorSelection actorSelection = getOneActorSelection(masterAddress);
        actorSelection.tell(workerInfo, ActorRef.noSender());
        if (LogCountUtil.count(logOutput, MULTIPLES)) {
            logger.info("HeartBeatListener Running...");
        }
    }

    private ActorSelection getOneActorSelection(String masterAddress){
        int size = availableNodes.size();
        String ipAndPort = masterAddress.split(",")[0];
        if (availableNodes.size() != 0) {
            int index = random.nextInt(size);
            ipAndPort = Lists.newArrayList(availableNodes).get(index);
        }
        String masterRemotePath = String.format(masterTemp, ipAndPort);
        ActorSelection master = system.actorSelection(masterRemotePath);
        return master;
    }

    private class MonitorNode implements Runnable {
        @Override
        public void run() {
            while (true) {
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
                    Thread.sleep(5000);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }
}

