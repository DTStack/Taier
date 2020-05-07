package com.dtstack.engine.master.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.akka.RpcService;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.akka.message.MessageSubmitJob;
import com.dtstack.engine.common.akka.message.WorkerInfo;
import com.dtstack.engine.common.exception.WorkerAccessException;
import com.dtstack.engine.common.util.LogCountUtil;
import com.dtstack.engine.common.util.RandomUtils;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Auther: jiangjunjie
 * @Date: 2020/3/3
 * @Description:
 */
public class AkkaMasterServerImpl implements InitializingBean, Runnable, MasterServer<Object, String>, RpcService<Config> {

    private static final Logger logger = LoggerFactory.getLogger(AkkaMasterServerImpl.class);

    private static ObjectMapper objectMapper = new ObjectMapper();
    private final static String TIMESTAMP = "timestamp";
    private final static String PATH = "path";
    private int logOutput = 0;
    private final static int MULTIPLES = 10;
    private final static int CHECK_INTERVAL = 10000;
    private final ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
    private ActorSystem actorSystem;
    private ActorRef masterActorRef;
    private Set<String> availableWorkers = new HashSet<>();
    private long workNodeTimeout;
    private Duration askResultTime;
    private Duration askSubmitTime;
    private Timeout askTimeout;
    private EnvironmentContext environmentContext;
    private int akkaConcurrent;
    private AtomicLong submitJob = new AtomicLong(0);
    private AtomicLong dealJob = new AtomicLong(0);

    @Autowired
    private ZkService zkService;

    public AkkaMasterServerImpl(EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
    }

    @Override
    public Config loadConfig() {
        Config config = AkkaConfig.init(ConfigFactory.load());
        this.askResultTime = Duration.create(AkkaConfig.getAkkaAskResultTimeout(), TimeUnit.SECONDS);
        this.askSubmitTime = Duration.create(AkkaConfig.getAkkaAskSubmitTimeout(), TimeUnit.SECONDS);
        this.askTimeout = Timeout.create(java.time.Duration.ofSeconds(AkkaConfig.getAkkaAskTimeout()));
        this.akkaConcurrent = AkkaConfig.getAkkaAskConcurrent();
        return config;
    }

    @Override
    public void start(Config config) {
        this.actorSystem = AkkaConfig.initActorSystem(config);
        this.masterActorRef = this.actorSystem.actorOf(Props.create(AkkaMasterActor.class), AkkaConfig.getMasterName());
        logger.info("get an masterActorRef of masterRemotePath:{}", masterActorRef.path());
    }

    @Override
    public String strategyForGetWorker(Collection<String> workers) {
        return RandomUtils.getRandomValueFromMap(workers);
    }

    @Override
    public Object sendMessage(Object message) throws Exception {
        String path = strategyForGetWorker(availableWorkers);
        if (null == path) {
            Thread.sleep(10000);
            throw new WorkerAccessException("sleep 10000 ms.");
        }
        ActorSelection actorSelection;
        if (message instanceof MessageSubmitJob) {
            actorSelection = actorSystem.actorSelection(path + "SubmitJob" + (submitJob.getAndIncrement() % akkaConcurrent));
        } else {
            actorSelection = actorSystem.actorSelection(path + "DealJob" + (dealJob.getAndIncrement() % akkaConcurrent));
        }
        Future<Object> future = Patterns.ask(actorSelection, message, askTimeout);
        if (message instanceof MessageSubmitJob) {
            return Await.result(future, askSubmitTime);
        } else {
            return Await.result(future, askResultTime);
        }
    }

    @Override
    public void run() {
        try {
            updateWorkerInfo();
        } catch (Exception e) {
            logger.error("updateWorkerActors happens error:", e);
        }
    }

    @Override
    public void updateWorkerInfo() {
        Future<Object> future = Patterns.ask(masterActorRef, AkkaMasterActor.GET_WORKER_INFOS, askTimeout);
        Object askResult = null;
        try {
            askResult = Await.result(future, askResultTime);
        } catch (Exception e) {
            logger.error("updateWorkerActors happens error:", e);
        }
        if (askResult == null) {
            return;
        }
        Set<WorkerInfo> workerInfos = (Set<WorkerInfo>) askResult;
        if (workerInfos.size() > 0) {
            zkService.updateBrokerWorkersNode(workerInfos);
        }
        List<Map<String, Object>> allWorkers = zkService.getAllBrokerWorkersNode();
        Set<String> newWorkers = new HashSet<>(allWorkers.size());
        if (!allWorkers.isEmpty()) {
            for (Map<String, Object> workNode : allWorkers) {
                if (!workNode.isEmpty()) {
                    long timestamp = MapUtils.getLong(workNode, TIMESTAMP, 0L);
                    String path = MapUtils.getString(workNode, PATH, "");
                    if (timestamp == 0 || StringUtils.isBlank(path)) {
                        continue;
                    }
                    if ((System.currentTimeMillis() - timestamp) < workNodeTimeout) {
                        newWorkers.add(path);
                    }
                }
            }
        }
        availableWorkers = newWorkers;
        if (LogCountUtil.count(logOutput++, MULTIPLES)) {
            logger.info("availableWorkers:{} gap:[{} ms]", availableWorkers, CHECK_INTERVAL * MULTIPLES);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        objectMapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        this.start(loadConfig());
        this.workNodeTimeout = environmentContext.getWorkerNodeTimeout();
        scheduledService.scheduleWithFixedDelay(
                this,
                CHECK_INTERVAL,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }
}
