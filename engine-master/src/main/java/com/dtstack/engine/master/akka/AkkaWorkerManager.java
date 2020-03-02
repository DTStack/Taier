package com.dtstack.engine.master.akka;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.akka.message.WorkerInfo;
import com.dtstack.engine.common.util.LogCountUtil;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AkkaWorkerManager implements InitializingBean, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AkkaWorkerManager.class);

    private static ObjectMapper objectMapper = new ObjectMapper();
    private final static String TIMESTAMP = "timestamp";
    private final static String PATH = "path";
    private int logOutput = 0;
    private final static int MULTIPLES = 10;
    private final static int CHECK_INTERVAL = 10000;
    private final ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
    private ActorSystem system;
    private ActorSelection actorSelection;
    private Set<String> availableWorkers = new HashSet<>();
    private long workNodeTimeout;

    private Duration duration;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ZkService zkService;

    @Override
    public void run() {
        try {
            updateWorkerActors();
        } catch (Exception e) {
            logger.error("updateWorkerActors happens error:", e);
        }
    }

    private void updateWorkerActors() throws Exception {
        Future<Object> future = Patterns.ask(actorSelection, AkkaMasterActor.GET_WORKER_INFOS, environmentContext.getAkkaAskTimeout());
        Object askResult = Await.result(future, duration);
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
        Config config = AkkaConfig.checkIpAndPort(ConfigFactory.load());
        this.duration = Duration.create(environmentContext.getAkkaAskResultTimeout(), TimeUnit.SECONDS);
        this.workNodeTimeout = environmentContext.getWorkerNodeTimeout();
        this.system = ActorSystem.create(AkkaConfig.getMasterSystemName(), config);
        this.system.actorOf(Props.create(AkkaMasterActor.class), AkkaConfig.getMasterName());
        this.actorSelection = system.actorSelection(AkkaConfig.getMasterRemotePath());
        logger.info("get an ActorSelection of masterRemotePath:{}", AkkaConfig.getMasterRemotePath());
        scheduledService.scheduleWithFixedDelay(
                this,
                CHECK_INTERVAL,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    public ActorSystem getSystem() {
        return system;
    }

    public ActorSelection getActorSelection() {
        return actorSelection;
    }

    public Set<String> getAvailableWorkers() {
        return availableWorkers;
    }

    public EnvironmentContext getEnvironmentContext() {
        return environmentContext;
    }

    public void setEnvironmentContext(EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
    }
}
