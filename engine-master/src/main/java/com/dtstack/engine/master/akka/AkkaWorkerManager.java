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
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.util.Strings;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AkkaWorkerManager implements InitializingBean, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AkkaWorkerManager.class);

    private static ObjectMapper objectMapper = new ObjectMapper();
    private final static String GET_WORKER_INFOS = "getWorkerInfos";
    private final static String NODE_SUFFIX = "/workers";
    private final static String TIMESTAMP = "timestamp";
    private final static String PATH = "path";
    private int logOutput = 0;
    private final static int MULTIPLES = 10;
    private final static int CHECK_INTERVAL = 2000;
    private final ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("WorkerInfoListener"));
    private ActorSystem system;
    private ActorSelection actorSelection;
    private Map<String, String> workerInfoMap = Maps.newHashMap();
    private long timeout = 5000L;

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
        if (LogCountUtil.count(logOutput, MULTIPLES)) {
            logger.info("Update WorkerInfos...");
        }
    }

    private void updateWorkerActors() throws Exception {
        Future<Object> future = Patterns.ask(actorSelection, GET_WORKER_INFOS, environmentContext.getAkkaAskTimeout());
        Object askResult = Await.result(future, duration);
        if (askResult == null){
            return;
        }
        HashMap<String, WorkerInfo> infos = (HashMap<String, WorkerInfo>) askResult;
        if (infos.size() > 0){
            updateToZk(infos);
        }
        HashMap<String, HashMap> allWorkers = getWorkersFromZk();
        for (Map.Entry<String, HashMap> entry : allWorkers.entrySet()){
            if ((System.currentTimeMillis() - (Long) entry.getValue().get(TIMESTAMP)) < timeout){
                workerInfoMap.put(entry.getKey(), (String) entry.getValue().get(PATH));
            }
        }
    }

    private void updateToZk(HashMap<String, WorkerInfo> infos) throws Exception {
        String node = zkService.getLocalNode() + NODE_SUFFIX;
        if (zkService.nodeIfExists(node)){
            zkService.setDataOnPath(node, infos);
        } else {
            zkService.createNodeIfNotExists(node, infos);
        }
    }

    private HashMap<String, HashMap> getWorkersFromZk(){
        HashMap<String, HashMap> workers = Maps.newHashMap();
        List<String> children = zkService.getBrokersChildren();
        for (String address : children){
            String node = String.format("%s/%s%s",zkService.getBrokersNode(), address,NODE_SUFFIX);
            HashMap<String, HashMap> nodeMap = Maps.newHashMap();
            try {
                if (zkService.nodeIfExists(node)){
                    nodeMap = objectMapper.readValue(zkService.getDataFromPath(node), HashMap.class);
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            if (nodeMap != null && nodeMap.size() > 0){
                workers.putAll(nodeMap);
            }
        }
        return workers;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        objectMapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true) ;
        Config config = AkkaConfig.checkIpAndPort(ConfigFactory.load());
        this.duration = Duration.create(environmentContext.getAkkaAskResultTimeout(), TimeUnit.SECONDS);
        this.system = ActorSystem.create(AkkaConfig.getMasterSystemName(), config);
        this.system.actorOf(Props.create(AkkaMasterActor.class), AkkaConfig.getMasterName());
        this.actorSelection = system.actorSelection(AkkaConfig.getMasterRemotePath());
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

    public Map<String, String> getWorkerInfoMap() {
        return workerInfoMap;
    }

    public EnvironmentContext getEnvironmentContext() {
        return environmentContext;
    }

    public void setEnvironmentContext(EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
    }
}
