package com.dtstack.engine.dtscript.am;

import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.common.*;
import com.dtstack.engine.dtscript.container.ContainerEntity;
import com.dtstack.engine.dtscript.container.DtContainerId;
import com.dtstack.engine.dtscript.api.ApplicationContainerProtocol;
import com.dtstack.engine.dtscript.api.ApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.security.authorize.ServiceAuthorizationManager;
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

    private int maxAttempts = 3;

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
        if (conf.get(DtYarnConfiguration.CONTAINER_MAX_ATTEMPTS) != null){
            maxAttempts = Integer.parseInt(conf.get(DtYarnConfiguration.CONTAINER_MAX_ATTEMPTS));
        }
        this.applicationContext = applicationContext;
        this.entities = Collections.synchronizedList(new ArrayList<>());
        this.containerLostDetector = new ContainerLostDetector(this);
    }

    public List<ContainerEntity> getEntities() {
        return entities;
    }

    @Override
    public void start() {

        try{
            final Configuration newConf = new Configuration(conf);
            Configuration conf = SecurityUtil.disableSecureRpc(getConfig());

            RPC.Builder builder = new RPC.Builder(conf)
                    .setProtocol(ApplicationContainerProtocol.class)
                    .setInstance(this)
                    .setBindAddress("0.0.0.0")
                    .setPort(0);

            server = builder.build();
            server.start();
            containerLostDetector.start();

            ServiceAuthorizationManager serviceAuthorizationManager = server.getServiceAuthorizationManager();
            serviceAuthorizationManager.refreshWithLoadedConfiguration(newConf, new DTPolicyProvider());
            LOG.info(serviceAuthorizationManager);

            LOG.info("----start rpc success----");
        } catch (Exception e) {
            LOG.error("Error starting application containers handler server!", e);
            e.printStackTrace();
            return;
        }finally {
            //SecurityUtil.setSecurityInfoProviders(new SecurityInfo[0]);
        }

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

        try{
            DtContainerStatus currentContainerStatus = heartbeatRequest.getXlearningContainerStatus();

            LOG.warn("Received heartbeat from container " + containerId.toString() + ", status is " + currentContainerStatus.toString());

            ContainerEntity oldEntity = getLaneOf(containerId);
            if(oldEntity != null) {
                DtContainerStatus status = heartbeatRequest.getXlearningContainerStatus();
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
        }catch (Exception e){
            LOG.error("-----", e);
            throw e;
        }

    }

    @Override
    public LocalRemotePath[] getOutputLocation(String str) {
        LOG.debug("-----call getOutputLocation-----");
        return new LocalRemotePath[0];
    }

    @Override
    public LocalRemotePath[] getInputSplit(DtContainerId containerId) {
        LOG.debug("-----call getInputSplit-----");
        return new LocalRemotePath[0];
    }


    @Override
    public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
        return 0;
    }

    @Override
    public ProtocolSignature getProtocolSignature(String protocol, long clientVersion, int clientMethodsHash) throws IOException {
        return null;
    }
}