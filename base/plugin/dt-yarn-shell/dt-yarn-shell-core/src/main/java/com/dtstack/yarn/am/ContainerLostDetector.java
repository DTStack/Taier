package com.dtstack.yarn.am;


import com.dtstack.yarn.common.DtContainerStatus;
import com.dtstack.yarn.common.HeartbeatRequest;
import com.dtstack.yarn.container.ContainerEntity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ContainerLostDetector implements Runnable {

    //超时3分钟 失败
    private static final long MAX_HEART_BEAT_WAIT_TIME = 1 * 60 * 1000L;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private ApplicationContainerListener parent;

    public ContainerLostDetector(ApplicationContainerListener applicationContainerListener) {
        parent = applicationContainerListener;
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        for(ContainerEntity entity : parent.getEntities()) {
            if(now - entity.getLastBeatTime() > MAX_HEART_BEAT_WAIT_TIME) {
                HeartbeatRequest request = new HeartbeatRequest();
                request.setErrMsg("CONTAINER TIME OUT");
                request.setXLearningContainerStatus(DtContainerStatus.TIMEOUT);
                parent.heartbeat(entity.getContainerId(), request);
            }
        }
    }

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this, 0L, 15L, TimeUnit.SECONDS);
    }

}
