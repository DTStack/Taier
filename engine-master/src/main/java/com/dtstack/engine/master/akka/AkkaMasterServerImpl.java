package com.dtstack.engine.master.akka;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.akka.RpcService;
import com.dtstack.engine.common.akka.config.AkkaConfig;
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
    private ActorSystem system;
    private ActorSelection actorSelection;
    private Set<String> availableWorkers = new HashSet<>();
    private long workNodeTimeout;
    private Duration duration;
    private EnvironmentContext environmentContext;

    @Autowired
    private ZkService zkService;

    public AkkaMasterServerImpl(EnvironmentContext environmentContext){
        this.environmentContext = environmentContext;
    }

    @Override
    public Config loadConfig() {
        return AkkaConfig.checkIpAndPort(ConfigFactory.load());
    }

    @Override
    public void start(Config config) {
        this.system = ActorSystem.create(AkkaConfig.getMasterSystemName(), config);
        this.system.actorOf(Props.create(AkkaMasterActor.class), AkkaConfig.getMasterName());
    }

    @Override
    public String strategyForGetWorker(Collection<String> workers) {
        return RandomUtils.getRandomValueFromMap(workers);
    }

    @Override
    public Object sendMessage(Object message) throws Exception  {
        String path = strategyForGetWorker(availableWorkers);
        if (null == path) {
            Thread.sleep(10000);
            throw new WorkerAccessException("sleep 10000 ms.");
        }
        ActorSelection actorRef = system.actorSelection(path);
        Future<Object> future = Patterns.ask(actorRef, message, environmentContext.getAkkaAskTimeout());
        Object result = Await.result(future, Duration.create(environmentContext.getAkkaAskResultTimeout(), TimeUnit.SECONDS));
        return result;
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
        Future<Object> future = Patterns.ask(actorSelection, AkkaMasterActor.GET_WORKER_INFOS, environmentContext.getAkkaAskTimeout());
        Object askResult = null;
        try {
            askResult = Await.result(future, duration);
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
        this.duration = Duration.create(environmentContext.getAkkaAskResultTimeout(), TimeUnit.SECONDS);
        this.workNodeTimeout = environmentContext.getWorkerNodeTimeout();
        this.start(loadConfig());
        this.actorSelection = system.actorSelection(AkkaConfig.getMasterPath());
        logger.info("get an ActorSelection of masterRemotePath:{}", AkkaConfig.getMasterPath());
        scheduledService.scheduleWithFixedDelay(
                this,
                CHECK_INTERVAL,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }
}
