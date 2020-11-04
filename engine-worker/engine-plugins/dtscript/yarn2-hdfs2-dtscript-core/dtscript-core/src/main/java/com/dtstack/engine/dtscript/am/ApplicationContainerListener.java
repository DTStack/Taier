package com.dtstack.engine.dtscript.am;

import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.api.ApplicationContainerProtocol;
import com.dtstack.engine.dtscript.api.ApplicationContext;
import com.dtstack.engine.dtscript.common.DtContainerStatus;
import com.dtstack.engine.dtscript.common.HeartbeatRequest;
import com.dtstack.engine.dtscript.common.HeartbeatResponse;
import com.dtstack.engine.dtscript.common.LocalRemotePath;
import com.dtstack.engine.dtscript.common.SecurityUtil;
import com.dtstack.engine.dtscript.container.ContainerEntity;
import com.dtstack.engine.dtscript.container.DtContainerId;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class ApplicationContainerListener
        extends AbstractService
        implements ApplicationContainerProtocol {

    private static final Log LOG = LogFactory.getLog(ApplicationContainerListener.class);

    private int maxAttempts = 3;

    private Server server;

    private ConcurrentHashMap<DtContainerId, ContainerEntity> allContainers;

    private volatile boolean isFinished = false;

    private volatile boolean emergencyFailed = false;

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
        if (conf.get(DtYarnConfiguration.APP_MAX_ATTEMPTS) != null) {
            maxAttempts = Integer.parseInt(conf.get(DtYarnConfiguration.APP_MAX_ATTEMPTS));
            LOG.info("MaxAttempts: " + maxAttempts);
        }
        this.applicationContext = applicationContext;
        this.allContainers = new ConcurrentHashMap<DtContainerId, ContainerEntity>();
        this.containerLostDetector = new ContainerLostDetector(this);
    }

    public Collection<ContainerEntity> getAllContainers() {
        return allContainers.values();
    }

    @Override
    public void start() {

        try {
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
        }

    }

    public boolean isTrainCompleted() {
        if (emergencyFailed) {
            return true;
        }

        if (allContainers.isEmpty()) {
            return false;
        }

        boolean isCompleted = true;
        int failedNum = 0;
        for (Map.Entry<DtContainerId, ContainerEntity> e : allContainers.entrySet()) {
            if (e.getValue().getDtContainerStatus().equals(DtContainerStatus.FAILED)) {
                failedNum += 1;
            } else {
                if (e.getValue().getDtContainerStatus().equals(DtContainerStatus.UNDEFINED)
                        || e.getValue().getDtContainerStatus().equals(DtContainerStatus.INITIALIZING)
                        || e.getValue().getDtContainerStatus().equals(DtContainerStatus.RUNNING)) {
                    isCompleted = false;
                }
            }
        }

        Double jobFailedNum = allContainers.size() * this.getConfig().getDouble(DtYarnConfiguration.DTSCRIPT_CONTAINER_MAX_FAILURES_RATE, DtYarnConfiguration.DEFAULT_DTSCRIPT_CONTAINER_MAX_FAILURES_RATE);
        if (failedNum >= jobFailedNum) {
            return true;
        }

        return isCompleted;
    }

    public boolean isAllWorkerContainersSucceeded() {
        if (emergencyFailed) {
            return false;
        }

        if (allContainers.isEmpty()) {
            return false;
        }

        int failedNum = 0;
        for (Map.Entry<DtContainerId, ContainerEntity> e : allContainers.entrySet()) {
            if (!e.getValue().getDtContainerStatus().equals(DtContainerStatus.SUCCEEDED)) {
                failedNum += 1;
            }
        }
        LOG.warn("isAllWorkerContainersSucceeded containerFailedNum:" + failedNum);

        Double jobFailedNum = allContainers.size() * this.getConfig().getDouble(DtYarnConfiguration.DTSCRIPT_CONTAINER_MAX_FAILURES_RATE, DtYarnConfiguration.DEFAULT_DTSCRIPT_CONTAINER_MAX_FAILURES_RATE);
        if (failedNum >= jobFailedNum) {
            return false;
        }

        return true;
    }

    public int getServerPort() {
        return server.getPort();
    }

    public void setFinished() {
        isFinished = true;
    }

    public void registerContainer(DtContainerId containerId, NodeId nodeId) {
        ContainerEntity containerEntity = new ContainerEntity(containerId, DtContainerStatus.UNDEFINED, nodeId.getHost(), nodeId.getPort());
        allContainers.put(containerId, containerEntity);
    }

    public List<String> getNodeAddress() {
        return allContainers.values().stream().map(e -> e.getNodeHost()).collect(Collectors.toList());
    }

    @Override
    public HeartbeatResponse heartbeat(DtContainerId containerId, HeartbeatRequest heartbeatRequest) {

        DtContainerStatus currentContainerStatus = heartbeatRequest.getXlearningContainerStatus();
        LOG.debug("Received heartbeat from container " + containerId.toString() + ", status is " + currentContainerStatus.toString());

        ContainerEntity containerEntity = allContainers.get(containerId);
        if (containerEntity == null) {
            emergencyFailed = true;
            failedMsg = "Emergency Failed!!! containerId:" + containerId + " is not found, allContainers: " + allContainers + "; " + heartbeatRequest.getErrMsg();
            LOG.error(failedMsg);
            return new HeartbeatResponse(true, System.currentTimeMillis());
        }

        if (containerEntity.getLastBeatTime() != null) {
            containerEntity.setLastBeatTime(System.currentTimeMillis());
        }

        if (containerEntity.getDtContainerStatus() != currentContainerStatus) {
            LOG.info("Received heartbeat from container " + containerId.toString() + ", Update status " + containerEntity.getDtContainerStatus().toString() + " to " + currentContainerStatus.toString());
            containerEntity.setDtContainerStatus(currentContainerStatus);
            if (currentContainerStatus.equals(DtContainerStatus.SUCCEEDED) || currentContainerStatus.equals(DtContainerStatus.FAILED)) {
                LOG.info("container " + containerId.toString() + " is " + currentContainerStatus);
            }
        }

        return new HeartbeatResponse(isFinished, System.currentTimeMillis());
    }

    @Override
    public LocalRemotePath[] getOutputLocation() {
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