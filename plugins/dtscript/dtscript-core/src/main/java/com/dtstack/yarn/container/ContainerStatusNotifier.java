package com.dtstack.yarn.container;

import com.dtstack.yarn.DtYarnConfiguration;
import com.dtstack.yarn.api.ApplicationContainerProtocol;
import com.dtstack.yarn.common.DtContainerStatus;
import com.dtstack.yarn.common.HeartbeatRequest;
import com.dtstack.yarn.common.HeartbeatResponse;
import com.dtstack.yarn.util.Utilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ContainerStatusNotifier implements Runnable {

    private static final Log LOG = LogFactory.getLog(ContainerStatusNotifier.class);

    private ApplicationContainerProtocol protocol;

    private Configuration conf;

    private DtContainerId containerId;

    private HeartbeatRequest heartbeatRequest;

    private HeartbeatResponse heartbeatResponse;

    private int heartbeatInterval;

    private int heartbeatRetryMax;

    private ScheduledExecutorService scheduledExecutorService;

    public ContainerStatusNotifier(ApplicationContainerProtocol protocol, Configuration conf, DtContainerId xlearningContainerId) {
        this.protocol = protocol;
        this.conf = conf;
        this.containerId = xlearningContainerId;
        // 自定义 CustomThreadFactory 对线程设置为守护线程
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(containerId.toString() + "heartbeat"));
        this.heartbeatRequest = new HeartbeatRequest();
        this.heartbeatRequest.setContainerUserDir(System.getProperty("user.dir"));
        this.heartbeatResponse = new HeartbeatResponse();
        this.heartbeatInterval = this.conf.getInt(DtYarnConfiguration.XLEARNING_CONTAINER_HEARTBEAT_INTERVAL, DtYarnConfiguration.DEFAULT_XLEARNING_CONTAINER_HEARTBEAT_INTERVAL);
        this.heartbeatRetryMax = this.conf.getInt(DtYarnConfiguration.XLEARNING_CONTAINER_HEARTBEAT_RETRY, DtYarnConfiguration.DEFAULT_XLEARNING_CONTAINER_HEARTBEAT_RETRY);
    }

    public void setContainerErrorMessage(String msg) {
        heartbeatRequest.setErrMsg(msg);
    }

    public void setContainerStatus(DtContainerStatus containerStatus) {
        this.heartbeatRequest.setXLearningContainerStatus(containerStatus);
    }

    public void setContainersStartTime(String startTime) {
        this.heartbeatRequest.setContainersStartTime(startTime);
    }

    public void setContainersFinishTime(String finishTime) {
        this.heartbeatRequest.setContainersFinishTime(finishTime);
    }

    public void reportContainerStatusNow(DtContainerStatus containerStatus) {
        heartbeatRequest.setXLearningContainerStatus(containerStatus);
        heartbeatWithRetry();
    }

    public HeartbeatResponse heartbeatWithRetry() {
        int retry = 0;
        while (true) {
            try {
                LOG.debug("Begin Send HeartBeat to ApplicationMaster");
                heartbeatResponse = protocol.heartbeat(containerId, heartbeatRequest);
                LOG.debug("Send HeartBeat to ApplicationMaster");
                return heartbeatResponse;
            } catch (Exception e) {
                retry++;
                if (retry <= heartbeatRetryMax) {
                    LOG.warn("Send heartbeat to ApplicationMaster failed in retry " + retry);
                    Utilities.sleep(heartbeatInterval);
                } else {
                    LOG.warn("Send heartbeat to ApplicationMaster failed in retry " + retry
                            + ", container will suicide!", e);
                    System.exit(1);
                }
            }
        }
    }

    public void heartbeatResponseHandle(HeartbeatResponse heartbeatResponse) {
        LOG.info("handle response: " + heartbeatResponse.toString());
    }

    @Override
    public void run() {
        heartbeatResponse = heartbeatWithRetry();
        heartbeatResponseHandle(heartbeatResponse);
    }

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this, 0L, 3L, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }


    private class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public CustomThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + name + "-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}