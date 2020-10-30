package com.dtstack.engine.dtscript.am;


import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.common.DtContainerStatus;
import com.dtstack.engine.dtscript.common.HeartbeatRequest;
import com.dtstack.engine.dtscript.container.ContainerEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ContainerLostDetector implements Runnable {

    private static final Log LOG = LogFactory.getLog(ContainerLostDetector.class);

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private ApplicationContainerListener parent;

    private long heartBeatTimeout;
    private long heartBeatInterval;
    private long localResourceTimeOut;

    public ContainerLostDetector(ApplicationContainerListener applicationContainerListener) {
        parent = applicationContainerListener;
        Configuration configuration = parent.getConfig();
        this.heartBeatInterval = configuration.getLong(DtYarnConfiguration.DTSCRIPT_CONTAINER_HEARTBEAT_INTERVAL, DtYarnConfiguration.DEFAULT_DTSCRIPT_CONTAINER_HEARTBEAT_INTERVAL);
        this.heartBeatTimeout = configuration.getLong(DtYarnConfiguration.DTSCRIPT_CONTAINER_HEARTBEAT_TIMEOUT, DtYarnConfiguration.DEFAULT_DTSCRIPT_CONTAINER_HEARTBEAT_TIMEOUT);
        this.localResourceTimeOut = configuration.getInt(DtYarnConfiguration.DTSCRIPT_LOCALRESOURCE_TIMEOUT, DtYarnConfiguration.DEFAULT_DTSCRIPT_LOCALRESOURCE_TIMEOUT);
        LOG.info("HeartBeatParams, heartBeatTimeout: " + heartBeatTimeout + ", heartBeatInterval:" + heartBeatInterval + ", localResourceTimeOut:" + localResourceTimeOut);
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        for (ContainerEntity entity : parent.getAllContainers()) {
            if (entity.getDtContainerStatus().equals(DtContainerStatus.UNDEFINED)) {
                if (now - entity.getLastBeatTime() > localResourceTimeOut) {
                    String errorMsg = "Container " + entity.getContainerId().toString() + " local resource timed out after "
                            + localResourceTimeOut / 1000 + " seconds";
                    LOG.info(errorMsg);
                    HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
                    heartbeatRequest.setXlearningContainerStatus(DtContainerStatus.FAILED);
                    heartbeatRequest.setErrMsg(errorMsg);
                    parent.heartbeat(entity.getContainerId(), heartbeatRequest);
                }
            } else {
                if (now - entity.getLastBeatTime() > heartBeatTimeout) {
                    String errorMsg = "Container " + entity.getContainerId().toString() + " timed out after "
                            + heartBeatTimeout / 1000 + " seconds";
                    LOG.info(errorMsg);
                    HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
                    heartbeatRequest.setXlearningContainerStatus(DtContainerStatus.FAILED);
                    heartbeatRequest.setErrMsg(errorMsg);
                    parent.heartbeat(entity.getContainerId(), heartbeatRequest);
                }
            }
        }
    }

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this, heartBeatInterval, heartBeatInterval, TimeUnit.MILLISECONDS);
    }

}
