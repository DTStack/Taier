package com.dtstack.yarn.am;

import com.dtstack.yarn.api.ApplicationContainerProtocol;
import com.dtstack.yarn.api.ApplicationContext;
import com.dtstack.yarn.common.DtContainerStatus;
import com.dtstack.yarn.common.HeartbeatRequest;
import com.dtstack.yarn.common.HeartbeatResponse;
import com.dtstack.yarn.common.LocalRemotePath;
import com.dtstack.yarn.container.ContainerEntity;
import com.dtstack.yarn.container.DtContainerId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.service.AbstractService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class ApplicationContainerListener
        extends AbstractService
        implements ApplicationContainerProtocol {

    private static final Log LOG = LogFactory.getLog(ApplicationContainerListener.class);

    private final int maxAttempts = 3;

    private Server server;

    private List<ContainerEntity> entities;

    private AtomicInteger lanes = new AtomicInteger();

    private volatile boolean failed = false;

    public String getFailedMsg() {
        return failedMsg;
    }

    private volatile String failedMsg = "";

    private final ApplicationContext applicationContext;

    private ContainerLostDetector containerLostDetector;

    public ApplicationContainerListener(ApplicationContext applicationContext, Configuration conf) {
        super("slotManager");
        setConfig(conf);
        this.applicationContext = applicationContext;
        this.entities = Collections.synchronizedList(new ArrayList<>());
        this.containerLostDetector = new ContainerLostDetector(this);
    }

    public List<ContainerEntity> getEntities() {
        return entities;
    }

    @Override
    public void start() {
        LOG.info("Starting application containers handler server");
        RPC.Builder builder = new RPC.Builder(getConfig());
        builder.setProtocol(ApplicationContainerProtocol.class);
        builder.setInstance(this);
        builder.setBindAddress("0.0.0.0");
        builder.setPort(0);
        try {
            server = builder.build();
        } catch (Exception e) {
            LOG.error("Error starting application containers handler server!", e);
            e.printStackTrace();
            return;
        }
        server.start();
        containerLostDetector.start();
    }


    public boolean isFinished() {
        if (failed) {
            return true;
        }

        for(ContainerEntity entity : entities) {
            if(entity.getDtContainerStatus() != DtContainerStatus.SUCCEEDED) {
                return false;
            }
        }

        return true;
    }

    public boolean isFailed() {
        return failed;
    }


    public int getServerPort() {
        return server.getPort();
    }


    public void registerContainer(boolean isNew, int lane, DtContainerId containerId, String nodeHost) {
        if(isNew) {
            entities.add(new ContainerEntity(lane, containerId, DtContainerStatus.UNDEFINED, nodeHost, 1));
        } else {
            int attempt =  entities.get(lane).getAttempts();
            entities.set(lane, new ContainerEntity(lane, containerId, DtContainerStatus.UNDEFINED, nodeHost, attempt + 1));
        }
    }

    private int getLaneOf(DtContainerId containerId) {
        LOG.info("getLaneOf containerId: " + containerId);
        for(int i = 0; i < entities.size(); ++i) {
            ContainerEntity containerEntity = entities.get(i);
            LOG.info("getLaneOf entities.get(i): " + containerEntity);
            LOG.info("getLaneOf entities.get(i).getContainerId: " + containerEntity.getContainerId().getContainerId());
            if(containerId.equals(containerEntity.getContainerId())) {
                return containerEntity.getLane();
            }
        }
        return -1;
    }

    public List<ContainerEntity> getFailedContainerEntities() {
        // container entities
        List<ContainerEntity> failedEntities = new ArrayList<>();
        for(ContainerEntity entity : entities) {
            if(entity.getDtContainerStatus() == DtContainerStatus.FAILED) {
                if(entity.getAttempts() < maxAttempts) {
                    failedEntities.add(entity);
                }
            }
        }
        return failedEntities;
    }

    public List<String> getNodeAddress() {
        return entities.stream().map(e->e.getNodeHost()).collect(Collectors.toList());
    }

    @Override
    public HeartbeatResponse heartbeat(DtContainerId containerId, HeartbeatRequest heartbeatRequest) {
        DtContainerStatus currentContainerStatus = heartbeatRequest.getXLearningContainerStatus();

        LOG.info("Received heartbeat from container " + containerId.toString() + ", status is " + currentContainerStatus.toString());

        int lane = getLaneOf(containerId);
        if(lane != -1) {
            ContainerEntity oldEntity = entities.get(lane);
            DtContainerStatus status = heartbeatRequest.getXLearningContainerStatus();
            oldEntity.setLastBeatTime(System.currentTimeMillis());
            if(oldEntity.getDtContainerStatus() != status) {
                oldEntity.setDtContainerStatus(status);
                if(status == DtContainerStatus.TIMEOUT) {
                    failed = true;
                    failedMsg = "container timeout. " + heartbeatRequest.getErrMsg();
                } else if((status == DtContainerStatus.FAILED) && oldEntity.getAttempts() >= maxAttempts) {
                    failed = true;
                    failedMsg = "container max attempts exceed. \n" + heartbeatRequest.getErrMsg();
                }
            }
        }

        return new HeartbeatResponse(100L);
    }

    @Override
    public LocalRemotePath[] getOutputLocation() {
        return applicationContext.getOutputs().toArray(new LocalRemotePath[0]);
    }

    @Override
    public LocalRemotePath[] getInputSplit(DtContainerId containerId) {
        return applicationContext.getInputs(containerId).toArray(new LocalRemotePath[0]);
    }


    @Override
    public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
        return ApplicationContainerProtocol.versionID;
    }

    @Override
    public ProtocolSignature getProtocolSignature(
            String protocol, long clientVersion, int clientMethodsHash) throws IOException {
        return ProtocolSignature.getProtocolSignature(this, protocol,
                clientVersion, clientMethodsHash);
    }

}
