package com.dtstack.engine.worker;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.akka.RpcService;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.akka.message.WorkerInfo;
import com.dtstack.engine.common.util.AddressUtil;
import com.dtstack.engine.common.util.LogCountUtil;
import com.dtstack.engine.worker.metric.SystemResourcesMetricsAnalyzer;
import com.dtstack.engine.worker.service.JobService;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import org.apache.commons.collections.MapUtils;
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

    private int logOutput = 0;
    private final static int MULTIPLES = 30;
    private final static int CHECK_INTERVAL = 1000;
    private static Random random = new Random();

    private ActorSystem actorSystem;
    private ActorSelection masterActorSelection;

    private String hostname;
    private Integer port;
    private String workerRemotePath;
    private FiniteDuration askResultTimeout;
    private Timeout askTimeout;
    private MonitorNode monitorNode;

    private SystemResourcesMetricsAnalyzer systemResourcesMetricsAnalyzer = new SystemResourcesMetricsAnalyzer();

    private static AkkaWorkerServerImpl akkaWorkerServer = new AkkaWorkerServerImpl();


    private AkkaWorkerServerImpl() {
    }

    @Override
    public Config loadConfig() {
        return null;
    }

    @Override
    public void start(Config config) {
        this.actorSystem = AkkaConfig.initActorSystem(config);
        this.actorSystem.actorOf(Props.create(JobService.class), AkkaConfig.getWorkerName());
        for (int i = 0; i < 20; i++) {
            this.actorSystem.actorOf(Props.create(JobService.class), AkkaConfig.getWorkerName() + "SubmitJob" + i);
            this.actorSystem.actorOf(Props.create(JobService.class), AkkaConfig.getWorkerName() + "DealJob" + i);
        }

        this.hostname = AkkaConfig.getAkkaHostname();
        this.port = AkkaConfig.getAkkaPort();
        this.workerRemotePath = AkkaConfig.getWorkerPath();
        this.askResultTimeout = Duration.create(AkkaConfig.getAkkaAskResultTimeout(), TimeUnit.SECONDS);
        this.askTimeout = Timeout.create(java.time.Duration.ofSeconds(AkkaConfig.getAkkaAskTimeout()));

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(3, new CustomThreadFactory(this.getClass().getSimpleName()));

        if (!AkkaConfig.isLocalMode()) {
            monitorNode = new MonitorNode();
            scheduledService.scheduleWithFixedDelay(
                    monitorNode,
                    CHECK_INTERVAL * 10,
                    CHECK_INTERVAL * 10,
                    TimeUnit.MILLISECONDS);
        }

        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);

        long systemResourceProbeInterval = AkkaConfig.getSystemResourceProbeInterval();
        systemResourcesMetricsAnalyzer.instantiateSystemMetrics(systemResourceProbeInterval);
        scheduledService.scheduleWithFixedDelay(
                systemResourcesMetricsAnalyzer,
                systemResourceProbeInterval,
                systemResourceProbeInterval,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void reportWorkerInfo(WorkerInfo workerInfo) {
        ActorSelection actorSelection = getActiveMasterAddress();
        if (null == actorSelection) {
            if (null != monitorNode) {
                monitorNode.printNodes();
            }
            return;
        }
        try {
            if (MapUtils.isNotEmpty(systemResourcesMetricsAnalyzer.getMetrics())) {
                String systemResource = JSONObject.toJSONString(systemResourcesMetricsAnalyzer.getMetrics());
                workerInfo.setSystemResource(systemResource);
            }
            Future<Object> future = Patterns.ask(actorSelection, workerInfo, askTimeout);
            Object result = Await.result(future, askResultTimeout);
            if (LogCountUtil.count(logOutput++, MULTIPLES)) {
                logger.info("WorkerBeatListener Running result:{},  workerRemotePath:{} gap:[{} ms]...", result, workerRemotePath, CHECK_INTERVAL * MULTIPLES);
            }
        } catch (Throwable e) {
            if (monitorNode != null) {
                monitorNode.add(actorSelection);
            }
            logger.error("Can't send WorkerInfo to master actorSelection-path:{}, happens error:", actorSelection.anchorPath(), e);
            masterActorSelection = null;
        }
    }


    @Override
    public ActorSelection getActiveMasterAddress() {
        if (masterActorSelection != null) {
            return masterActorSelection;
        }
        if (monitorNode != null) {
            int size = monitorNode.getAvailableNodes().size();
            if (size != 0) {
                int index = random.nextInt(size);
                String ipAndPort = Lists.newArrayList(monitorNode.getAvailableNodes()).get(index);
                String[] hostInfo = ipAndPort.split(":");
                if (hostInfo.length == 2) {
                    String masterRemotePath = AkkaConfig.getMasterPath(hostInfo[0], hostInfo[1]);
                    masterActorSelection = this.actorSystem.actorSelection(masterRemotePath);
                    logger.info("get an ActorSelection of masterRemotePath:{}", masterActorSelection.anchorPath());
                }
            }
        } else {
            //monitor == null, 是localMode
            masterActorSelection = this.actorSystem.actorSelection(AkkaConfig.getMasterPath(null, null));
        }
        return masterActorSelection;
    }

    @Override
    public void run() {
        WorkerInfo workerInfo = new WorkerInfo(hostname, port, workerRemotePath, System.currentTimeMillis());
        reportWorkerInfo(workerInfo);
    }

    public static AkkaWorkerServerImpl getAkkaWorkerServer() {
        return akkaWorkerServer;
    }

    private class MonitorNode implements Runnable {

        private Set<String> availableNodes = new CopyOnWriteArraySet();
        private Set<String> disableNodes = new CopyOnWriteArraySet();

        public MonitorNode() {
            this.availableNodes.addAll(Arrays.asList(AkkaConfig.getMasterAddress().split(",")));
        }

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

        public void add(ActorSelection actorSelection) {
            String ip = actorSelection.anchorPath().address().host().get();
            String port = actorSelection.anchorPath().address().port().get().toString();
            String ipAndPort = String.format("%s:%s", ip, port);
            availableNodes.remove(ipAndPort);
            disableNodes.add(ipAndPort);
        }

        public void printNodes() {
            logger.info("worker nodes detail, availableNodes:{} disableNodes:{}", availableNodes, disableNodes);
        }

        public Set<String> getAvailableNodes() {
            return availableNodes;
        }
    }
}
