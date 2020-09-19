package com.dtstack.engine.dtscript.container;

import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.api.ApplicationContainerProtocol;
import com.dtstack.engine.dtscript.common.DtContainerStatus;
import com.dtstack.engine.dtscript.common.HeartbeatRequest;
import com.dtstack.engine.dtscript.common.HeartbeatResponse;
import com.dtstack.engine.dtscript.util.Utilities;
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

    private volatile Boolean isCompleted = false;

    private long interResultTimeStamp;

    public ContainerStatusNotifier(ApplicationContainerProtocol protocol, Configuration conf, DtContainerId xlearningContainerId) {
        this.protocol = protocol;
        this.conf = conf;
        this.containerId = xlearningContainerId;
        this.heartbeatRequest.setContainerUserDir(System.getProperty("user.dir"));
        // 自定义 CustomThreadFactory 对线程设置为守护线程
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(containerId.toString() + "heartbeat"));
        this.heartbeatRequest = new HeartbeatRequest();
        this.heartbeatResponse = new HeartbeatResponse();
        this.isCompleted = false;
        this.heartbeatInterval = this.conf.getInt(DtYarnConfiguration.DTSCRIPT_CONTAINER_HEARTBEAT_INTERVAL, DtYarnConfiguration.DEFAULT_DTSCRIPT_CONTAINER_HEARTBEAT_INTERVAL);
        this.heartbeatRetryMax = this.conf.getInt(DtYarnConfiguration.DTSCRIPT_CONTAINER_HEARTBEAT_RETRY, DtYarnConfiguration.DEFAULT_DTSCRIPT_CONTAINER_HEARTBEAT_RETRY);
    }

    public void setContainerErrorMessage(String msg) {
        heartbeatRequest.setErrMsg(msg);
    }

    public void setContainerStatus(DtContainerStatus containerStatus) {
        this.heartbeatRequest.setXlearningContainerStatus(containerStatus);
    }

    public void setContainersStartTime(String startTime) {
        this.heartbeatRequest.setContainersStartTime(startTime);
    }

    public void setContainersFinishTime(String finishTime) {
        this.heartbeatRequest.setContainersFinishTime(finishTime);
    }

    public void reportContainerStatusNow(DtContainerStatus containerStatus) {
        heartbeatRequest.setXlearningContainerStatus(containerStatus);
        heartbeatWithRetry();
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public HeartbeatResponse heartbeatWithRetry() {
        int retry = 0;
        while (true) {
            try {
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
        LOG.debug("Received the heartbeat response from the AM. CurrentJob finished " + heartbeatResponse.getIsCompleted()
                + " , currentInnerModelSavedTimeStamp is " + heartbeatResponse.getInterResultTimeStamp());
        if (!heartbeatResponse.getIsCompleted()) {
            if (!heartbeatResponse.getInterResultTimeStamp().equals(interResultTimeStamp)) {
                this.interResultTimeStamp = heartbeatResponse.getInterResultTimeStamp();
            }
            LOG.debug("container " + containerId + " currentStatus:" + heartbeatRequest.getXlearningContainerStatus());
        }

        this.isCompleted = heartbeatResponse.getIsCompleted();
    }

    @Override
    public void run() {
        heartbeatResponse = heartbeatWithRetry();
        heartbeatResponseHandle(heartbeatResponse);
    }

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this, 0L, heartbeatInterval, TimeUnit.MILLISECONDS);
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