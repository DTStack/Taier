package com.dtstack.yarn.am;

import com.dtstack.yarn.api.ApplicationContainerProtocol;
import com.dtstack.yarn.api.ApplicationContext;
import com.dtstack.yarn.common.DtContainerStatus;
import com.dtstack.yarn.common.HeartbeatRequest;
import com.dtstack.yarn.common.HeartbeatResponse;
import com.dtstack.yarn.common.LocalRemotePath;
import com.dtstack.yarn.container.ContainerEntity;
import com.dtstack.yarn.container.DtContainerId;
import com.dtstack.yarn.util.KerberosUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.service.AbstractService;
import org.apache.hadoop.yarn.api.records.NodeId;

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

    private Configuration conf;

    public ApplicationContainerListener(ApplicationContext applicationContext, Configuration conf) {
        super("slotManager");
        setConfig(conf);
        this.conf = conf;
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
        try {
            LOG.info(UserGroupInformation.getCurrentUser());
            KerberosUtils.login(conf.get("hdfsPrincipal"), conf.get("hdfsKeytabPath"), conf.get("hdfsKrb5ConfPath"), conf);
            LOG.info(UserGroupInformation.getCurrentUser());
        } catch (IOException e) {
            e.printStackTrace();
        }
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


    public void registerContainer(boolean isNew, int lane, DtContainerId containerId, NodeId nodeId) {
        if(isNew) {
            entities.add(new ContainerEntity(lane, containerId, DtContainerStatus.UNDEFINED, nodeId.getHost(), nodeId.getPort(), 1));
        } else {
            int attempt =  entities.get(lane).getAttempts();
            entities.set(lane, new ContainerEntity(lane, containerId, DtContainerStatus.UNDEFINED, nodeId.getHost(), nodeId.getPort(), attempt + 1));
        }
    }

    private ContainerEntity getLaneOf(DtContainerId containerId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getLaneOf containerId: " + containerId);
        }
        for(ContainerEntity containerEntity : entities) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getLaneOf entities.get(i).getContainerId: " + containerEntity.getContainerId().getContainerId());
            }
            if(containerId.equals(containerEntity.getContainerId())) {
                return containerEntity;
            }
        }
        return null;
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

        if (LOG.isDebugEnabled()){
            LOG.debug("Received heartbeat from container " + containerId.toString() + ", status is " + currentContainerStatus.toString());
        }

        ContainerEntity oldEntity = getLaneOf(containerId);
        if(oldEntity != null) {
            DtContainerStatus status = heartbeatRequest.getXLearningContainerStatus();
            oldEntity.setLastBeatTime(System.currentTimeMillis());
            if(oldEntity.getDtContainerStatus() != status) {
                LOG.info("Received heartbeat container status change from container " + containerId.toString() + ", status is " + currentContainerStatus.toString());
                oldEntity.setDtContainerStatus(status);
                if(status == DtContainerStatus.TIMEOUT) {
                    failed = true;
                    failedMsg = "container timeout. " + heartbeatRequest.getErrMsg();
                    LOG.error(failedMsg);
                } else if((status == DtContainerStatus.FAILED) && oldEntity.getAttempts() >= maxAttempts) {
                    failed = true;
                    failedMsg = "container max attempts exceed. \n" + heartbeatRequest.getErrMsg();
                    LOG.error(failedMsg);
                }
            }
        } else {
            LOG.warn("entities: " + entities + ", getLaneOf(containerId:"+containerId+") is not found");
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