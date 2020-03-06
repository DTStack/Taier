package com.dtstack.engine.worker;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import com.dtstack.dtcenter.common.util.AddressUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.akka.RpcService;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.akka.message.WorkerInfo;
import com.dtstack.engine.common.util.LogCountUtil;
import com.dtstack.engine.worker.service.JobService;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: jiangjunjie
 * @Date: 2020/3/3
 * @Description:
 */
public class AkkaWorkerServerImpl implements WorkerServer<WorkerInfo, ActorSelection>, RpcService<Config>, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AkkaWorkerServerImpl.class);

    private final static String MASTER_REMOTE_PATH_TEMPLATE = "akka.tcp://%s@%s/user/%s";
    private final static String IP_PORT_TEMPLATE = "%s:%s";
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
    private ActorSelection activeMasterActor;
    private FiniteDuration askResultTimeout;

    private static AkkaWorkerServerImpl akkaWorkerServer = new AkkaWorkerServerImpl();

    private AkkaWorkerServerImpl(){}

    @Override
    public Config loadConfig() {
        return null;
    }

    @Override
    public void start(Config workerConfig) {
        this.system = ActorSystem.create(AkkaConfig.getWorkerSystemName(), workerConfig);
        String workerName = AkkaConfig.getWorkerName();
        ActorRef actorRef = system.actorOf(Props.create(JobService.class), workerName);

        this.masterAddress = AkkaConfig.getMasterAddress();
        this.availableNodes.addAll(Arrays.asList(masterAddress.split(",")));
        this.masterSystemName = AkkaConfig.getMasterSystemName();
        this.masterName = AkkaConfig.getMasterName();
        this.workerIp = AkkaConfig.getAkkaHostname();
        this.workerPort = AkkaConfig.getAkkaPort();
        this.workerRemotePath = AkkaConfig.getWorkerRemotePath();
        this.askResultTimeout = Duration.create(AkkaConfig.getAkkaAskResultTimeout(), TimeUnit.SECONDS);
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(2, new CustomThreadFactory(this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
        scheduledService.scheduleWithFixedDelay(
                new MonitorNode(),
                CHECK_INTERVAL * 10,
                CHECK_INTERVAL * 10,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void heartBeat(WorkerInfo workerInfo) {
        try {
            Future<Object> future = Patterns.ask(getActiveMasterAddress(), workerInfo, AkkaConfig.getAkkaAskTimeout());
            Object result = Await.result(future, askResultTimeout);
        } catch (Throwable e){
            String ip = activeMasterActor.anchorPath().address().host().get();
            String port = activeMasterActor.anchorPath().address().port().get().toString();
            String ipAndPort = String.format(IP_PORT_TEMPLATE, ip, port);
            availableNodes.remove(ipAndPort);
            disableNodes.add(ipAndPort);
            activeMasterActor = null;
            logger.error("Can't send WorkerInfo to master, availableNodes:{} disableNodes:{}, happens error:{}", availableNodes, disableNodes, e);
        }
    }

    @Override
    public ActorSelection getActiveMasterAddress() {
        if (null == activeMasterActor){
            int size = availableNodes.size();
            String ipAndPort = masterAddress.split(",")[0];
            if (availableNodes.size() != 0) {
                int index = random.nextInt(size);
                ipAndPort = Lists.newArrayList(availableNodes).get(index);
            } else {
                logger.info("worker not connect available nodes, default ipAndPort:{} availableNodes:{} disableNodes:{}", ipAndPort, availableNodes, disableNodes);
            }
            String masterRemotePath = String.format(MASTER_REMOTE_PATH_TEMPLATE, masterSystemName, ipAndPort, masterName);
            activeMasterActor = system.actorSelection(masterRemotePath);
            logger.info("get an ActorSelection of masterRemotePath:{}", masterRemotePath);
        }
        return activeMasterActor;
    }

    @Override
    public void run() {
        WorkerInfo workerInfo = new WorkerInfo(workerIp, workerPort, workerRemotePath, System.currentTimeMillis());
        heartBeat(workerInfo);
        if (LogCountUtil.count(logOutput++, MULTIPLES)) {
            logger.info("WorkerBeatListener Running workerRemotePath:{} gap:[{} ms]...", workerRemotePath, CHECK_INTERVAL * MULTIPLES);
        }
    }

    public static AkkaWorkerServerImpl getAkkaWorkerServer() {
        return akkaWorkerServer;
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
