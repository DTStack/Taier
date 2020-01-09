package com.dtstack.engine.dtscript.am;

import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.api.ApplicationContext;
import com.dtstack.engine.dtscript.common.SecurityUtil;
import com.dtstack.engine.dtscript.container.ContainerEntity;
import com.dtstack.engine.dtscript.container.DtContainer;
import com.dtstack.engine.dtscript.container.DtContainerId;
import com.dtstack.engine.dtscript.util.DebugUtil;
import com.dtstack.engine.dtscript.util.Utilities;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.service.CompositeService;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.yarn.util.Records;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;


public class ApplicationMaster extends CompositeService {

    private static final Log LOG = LogFactory.getLog(ApplicationMaster.class);

    AMRMClientAsync<AMRMClient.ContainerRequest> amrmAsync;

    NMClientAsync nmAsync;

    private ApplicationMessageService messageService;

    private RMCallbackHandler rmCallbackHandler;

    final Configuration conf = new DtYarnConfiguration();

    final ApplicationContext applicationContext;

    ApplicationAttemptId applicationAttemptID;

    AppArguments appArguments;

    /**
     * An RPC Service listening the container status
     */
    ApplicationContainerListener containerListener;

    NMCallbackHandler nmAsyncHandler;

    String appMasterHostname;


    private ApplicationMaster(String name) {
        super(name);
        Path jobConfPath = new Path(DtYarnConstants.LEARNING_JOB_CONFIGURATION);
        LOG.info("user.dir: " + System.getProperty("user.dir"));
        LOG.info("user.name: " + System.getProperty("user.name"));
        conf.addResource(jobConfPath);
        applicationContext = new RunningAppContext(this);
        messageService = new ApplicationMessageService(this.applicationContext, conf);
        appArguments = new AppArguments(this);
        containerListener = new ApplicationContainerListener(applicationContext, conf);
    }

    private ApplicationMaster() {
        this(ApplicationMaster.class.getName());
    }

    private void init() throws IOException {
        LOG.info("appmaster init start...");

        LOG.info("-------------");

        rmCallbackHandler = new RMCallbackHandler();
        amrmAsync = AMRMClientAsync.createAMRMClientAsync(1000, rmCallbackHandler);
        amrmAsync.init(conf);
        amrmAsync.start();

        nmAsyncHandler = new NMCallbackHandler(this);
        this.nmAsync = NMClientAsync.createNMClientAsync(nmAsyncHandler);
        this.nmAsync.init(conf);
        this.amrmAsync.start();

        addService(amrmAsync);
        addService(nmAsync);
        addService(messageService);
        addService(containerListener);

        try {
            serviceStart();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        LOG.info("appmaster init end...");
    }

    private void register() {
        LOG.info("appmaster register start...");
        try {
            LOG.info("amrmAsync: " + amrmAsync);
            appMasterHostname = NetUtils.getHostname();
            RegisterApplicationMasterResponse response = amrmAsync.registerApplicationMaster(appMasterHostname,
                    this.messageService.getServerAddress().getPort(), null);

            int maxMem = response.getMaximumResourceCapability().getMemory();
            LOG.info("Max mem capabililty of resources in this cluster " + maxMem);

            int maxVCores = response.getMaximumResourceCapability().getVirtualCores();
            LOG.info("Max vcores capabililty of resources in this cluster " + maxVCores);
        } catch (Exception e) {
            LOG.info("app master register failed: " + DebugUtil.stackTrace(e));
            throw new RuntimeException("Registering application master failed,", e);
        }
        LOG.info("appmaster register end...");
    }

    private void unregister(FinalApplicationStatus finalStatus, String diagnostics) {
        try {
            amrmAsync.unregisterApplicationMaster(finalStatus, diagnostics,
                    null);
            amrmAsync.stop();
        } catch (Exception e) {
            LOG.error("Error while unregister Application", e);
        }
    }

    private AMRMClient.ContainerRequest buildContainerRequest() {
        Priority priority = Records.newRecord(Priority.class);
        priority.setPriority(appArguments.appPriority);
        Resource workerCapability = Records.newRecord(Resource.class);
        workerCapability.setMemory(appArguments.workerMemory+appArguments.containerMemory);
        workerCapability.setVirtualCores(appArguments.workerVCores);
        return new AMRMClient.ContainerRequest(workerCapability, null, null, priority);
    }

    private List<String> buildContainerLaunchCommand(int containerMemory) {
        List<String> containerLaunchcommands = new ArrayList<>();
        LOG.info("Setting up container command");
        Vector<CharSequence> vargs = new Vector<>(10);
        vargs.add("${JAVA_HOME}" + "/bin/java");
        vargs.add("-server -XX:+UseConcMarkSweepGC -XX:-UseCompressedClassPointers -XX:+DisableExplicitGC -XX:-OmitStackTraceInFastThrow");
        vargs.add("-Xmx" + containerMemory + "m");
        vargs.add("-Xms" + containerMemory + "m");
        String javaOpts = conf.get(DtYarnConfiguration.XLEARNING_CONTAINER_EXTRA_JAVA_OPTS, DtYarnConfiguration.DEFAULT_XLEARNING_CONTAINER_JAVA_OPTS_EXCEPT_MEMORY);
        if (!StringUtils.isBlank(javaOpts)) {
            vargs.add(javaOpts);
        }
        vargs.add(DtContainer.class.getName());
        vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/" + ApplicationConstants.STDOUT);
        vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/" + ApplicationConstants.STDERR);

        StringBuilder containerCmd = new StringBuilder();
        for (CharSequence str : vargs) {
            containerCmd.append(str).append(" ");
        }
        containerLaunchcommands.add(containerCmd.toString());
        LOG.info("Container launch command: " + containerLaunchcommands.toString());
        return containerLaunchcommands;
    }

    private boolean run() throws IOException, NoSuchAlgorithmException, InterruptedException {
        LOG.info("ApplicationMaster Starting ...");
        LOG.info("---ugi:" + UserGroupInformation.getCurrentUser() );


        register();

        if (appArguments.exclusive) {
            rmCallbackHandler.startWorkerContainersExclusive();
        }

        AMRMClient.ContainerRequest workerContainerRequest = buildContainerRequest();
        LOG.info("ContainerRequest:" + workerContainerRequest.toString() + " nodes:" + workerContainerRequest.getNodes() + " racks:" + workerContainerRequest.getRacks());

        List<String> workerContainerLaunchCommands = buildContainerLaunchCommand(appArguments.containerMemory);
        Map<String, LocalResource> containerLocalResource = buildContainerLocalResource();
        Map<String, String> workerContainerEnv = new ContainerEnvBuilder(DtYarnConstants.WORKER, this).build();


        List<Container> acquiredWorkerContainers = handleRMCallbackOfContainerRequest(appArguments.workerNum, workerContainerRequest);

        int i = 0;
        for (Container container : acquiredWorkerContainers) {
            LOG.info("Launching worker container " + container.getId()
                    + " on " + container.getNodeId().getHost() + ":" + container.getNodeId().getPort());
            launchContainer(containerLocalResource, workerContainerEnv,
                    workerContainerLaunchCommands, container, i);
            containerListener.registerContainer(true, i++, new DtContainerId(container.getId()), container.getNodeId());
        }

        while (!containerListener.isFinished()) {
            Utilities.sleep(1000);
            List<ContainerEntity> failedEntities = containerListener.getFailedContainerEntities();

            if (failedEntities.isEmpty()) {
                continue;
            }

            for (ContainerEntity containerEntity : failedEntities) {
                ContainerId containerId = containerEntity.getContainerId().getContainerId();
                LOG.info("Canceling container: " + containerId.toString() + " nodeHost: " + containerEntity.getNodeHost());
                amrmAsync.releaseAssignedContainer(containerId);
                rmCallbackHandler.removeLaunchFailed(containerEntity.getNodeHost());
                clearContainerInfo(containerId);
            }

            //失败后重试
            acquiredWorkerContainers = handleRMCallbackOfContainerRequest(failedEntities.size(), workerContainerRequest);

            for (ContainerEntity containerEntity : failedEntities) {
                Container container = acquiredWorkerContainers.remove(0);
                LOG.warn("Retry Launching worker container " + container.getId()
                        + " on " + container.getNodeId().getHost() + ":" + container.getNodeId().getPort());
                launchContainer(containerLocalResource, workerContainerEnv,
                        workerContainerLaunchCommands, container, containerEntity.getLane());
                containerListener.registerContainer(false, containerEntity.getLane(), new DtContainerId(container.getId()), container.getNodeId());
            }
        }

        if (containerListener.isFailed()) {
            unregister(FinalApplicationStatus.FAILED, containerListener.getFailedMsg());
            return false;
        } else {
            unregister(FinalApplicationStatus.SUCCEEDED, "Task is success.");
            return true;
        }

    }

    public List<Container> handleRMCallbackOfContainerRequest(int workerNum, AMRMClient.ContainerRequest request) {
        rmCallbackHandler.setNeededWorkerContainersCount(workerNum);
        rmCallbackHandler.resetAllocatedWorkerContainerNumber();
        rmCallbackHandler.resetAcquiredWorkerContainers();

        clearAMRMRequests(request);

        for (int i = 0; i < workerNum; ++i) {
            amrmAsync.addContainerRequest(request);
        }

        LOG.info("Try to allocate " + workerNum + " worker containers");

        //对独占的nm，向rm进行updateBlacklist操作
        long startAllocatedTimeStamp = System.currentTimeMillis();
        while (rmCallbackHandler.getAllocatedWorkerContainerNumber() < workerNum) {
            List<Container> releaseContainers = rmCallbackHandler.getReleaseContainers();
            List<String> blackHosts = rmCallbackHandler.getBlackHosts();
            try {
                Method updateBlacklist = amrmAsync.getClass().getMethod("updateBlacklist", List.class, List.class);
                updateBlacklist.invoke(amrmAsync, blackHosts, null);
            } catch (NoSuchMethodException e) {
                LOG.debug("current hadoop version don't have the method updateBlacklist of Class " + amrmAsync.getClass().toString() + ". For More Detail:" + e);
            } catch (InvocationTargetException e) {
                LOG.error("InvocationTargetException : " + e);
            } catch (IllegalAccessException e) {
                LOG.error("IllegalAccessException : " + e);
            }
            if (releaseContainers.size() != 0) {
                for (Container container : releaseContainers) {
                    LOG.info("Releaseing container: " + container.getId().toString());
                    amrmAsync.releaseAssignedContainer(container.getId());
                    amrmAsync.addContainerRequest(request);
                }
                rmCallbackHandler.removeReleaseContainers(releaseContainers);
            }
            if ((System.currentTimeMillis() - startAllocatedTimeStamp) > conf.getInt(YarnConfiguration.RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS, YarnConfiguration.DEFAULT_RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS)) {
                String failMessage = "Container waiting except the allocated expiry time. Maybe the Cluster available resources are not satisfied the user need. Please resubmit !";
                LOG.error(failMessage);
                throw new RuntimeException("Container waiting except the allocated expiry time.");
            }
            Utilities.sleep(1000);
        }

        List<Container> acquiredWorkerContainers = rmCallbackHandler.getAcquiredWorkerContainer();
        //释放可能的多余资源
        int totalNumAllocatedWorkers = rmCallbackHandler.getAllocatedWorkerContainerNumber();
        if (totalNumAllocatedWorkers > workerNum) {
            while (acquiredWorkerContainers.size() > workerNum) {
                Container releaseContainer = acquiredWorkerContainers.remove(0);
                amrmAsync.releaseAssignedContainer(releaseContainer.getId());
                LOG.info("Release container " + releaseContainer.getId().toString());
            }
        }

        LOG.info("Total " + acquiredWorkerContainers.size() + " worker containers has allocated.");
        return acquiredWorkerContainers;
    }

    private void clearAMRMRequests(AMRMClient.ContainerRequest request) {
        try {
            Field amrmField = amrmAsync.getClass().getSuperclass().getDeclaredField("client");
            amrmField.setAccessible(true);
            Object amrm = amrmField.get(amrmAsync);

            Field remoteRequestsTableField = amrm.getClass().getDeclaredField("remoteRequestsTable");
            remoteRequestsTableField.setAccessible(true);
            Map<Priority, Map<String, TreeMap<Resource, Object>>> remoteRequestsTable = (Map) remoteRequestsTableField.get(amrm);

            if (remoteRequestsTable != null) {
                Map<String, TreeMap<Resource, Object>> remoteRequests = remoteRequestsTable.get(request.getPriority());
                if (remoteRequests != null) {
                    TreeMap<Resource, Object> reqMap = remoteRequests.get("*");
                    if (reqMap != null) {
                        Object resourceRequestInfo = reqMap.get(request.getCapability());
                        if (resourceRequestInfo != null) {
                            Field remoteRequestField = resourceRequestInfo.getClass().getDeclaredField("remoteRequest");
                            remoteRequestField.setAccessible(true);
                            ResourceRequest resourceRequest = (ResourceRequest) remoteRequestField.get(resourceRequestInfo);
                            if (resourceRequest != null) {
                                LOG.info("clearAMRMRequests reset resourceRequest numContainers:" + resourceRequest.getNumContainers() + " to 0");
                                resourceRequest.setNumContainers(0);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    private Map<String, LocalResource> buildContainerLocalResource() {
        URI defaultUri = new Path(conf.get("fs.defaultFS")).toUri();
        Map<String, LocalResource> containerLocalResource = new HashMap<>();
        try {
            containerLocalResource.put(DtYarnConstants.APP_MASTER_JAR,
                    Utilities.createApplicationResource(appArguments.appJarRemoteLocation.getFileSystem(conf),
                            appArguments.appJarRemoteLocation,
                            LocalResourceType.FILE));
            containerLocalResource.put(DtYarnConstants.LEARNING_JOB_CONFIGURATION,
                    Utilities.createApplicationResource(appArguments.appConfRemoteLocation.getFileSystem(conf),
                            appArguments.appConfRemoteLocation,
                            LocalResourceType.FILE));

            if (appArguments.appFilesRemoteLocation != null) {
                String[] xlearningFiles = StringUtils.split(appArguments.appFilesRemoteLocation, ",");
                for (String file : xlearningFiles) {
                    Path path = new Path(file);
                    containerLocalResource.put(path.getName(),
                            Utilities.createApplicationResource(path.getFileSystem(conf),
                                    path,
                                    LocalResourceType.FILE));
                }
            }

            if (appArguments.appCacheFilesRemoteLocation != null) {
                String[] cacheFiles = StringUtils.split(appArguments.appCacheFilesRemoteLocation, ",");
                for (String path : cacheFiles) {
                    Path pathRemote;
                    String aliasName;
                    if (path.contains("#")) {
                        String[] paths = StringUtils.split(path, "#");
                        if (paths.length != 2) {
                            throw new RuntimeException("Error cacheFile path format " + appArguments.appCacheFilesRemoteLocation);
                        }
                        pathRemote = new Path(paths[0]);
                        aliasName = paths[1];
                    } else {
                        pathRemote = new Path(path);
                        aliasName = pathRemote.getName();
                    }
                    URI pathRemoteUri = pathRemote.toUri();
                    if (pathRemoteUri.getScheme() == null || pathRemoteUri.getHost() == null) {
                        pathRemote = new Path(defaultUri.toString(), pathRemote.toString());
                    }
                    LOG.info("Cache file remote path is " + pathRemote + " and alias name is " + aliasName);
                    containerLocalResource.put(aliasName,
                            Utilities.createApplicationResource(pathRemote.getFileSystem(conf),
                                    pathRemote,
                                    LocalResourceType.FILE));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while build container local resource", e);
        }
        return containerLocalResource;
    }

    /**
     * Async Method telling NMClientAsync to launch specific container
     *
     * @param container the container which should be launched
     * @return is launched success
     */
    private void launchContainer(Map<String, LocalResource> containerLocalResource,
                                 Map<String, String> containerEnv,
                                 List<String> containerLaunchcommands,
                                 Container container, int index) throws IOException {
        System.out.println("container nodeId: " + container.getNodeId().toString());
        LOG.info("Setting up launch context for containerID="
                + container.getId());

        containerEnv.put(DtYarnConstants.Environment.XLEARNING_TF_INDEX.toString(), String.valueOf(index));

        ContainerLaunchContext ctx = ContainerLaunchContext.newInstance(
                containerLocalResource, containerEnv, containerLaunchcommands, null, SecurityUtil.copyUserToken(), null);

        try {
            LOG.info("nmAsync.class: " + nmAsync.getClass().getName());
            LOG.info("nmAsync.client: " + nmAsync.getClient());

            nmAsyncHandler.addContainer(container.getId(), container);
            nmAsync.startContainerAsync(container, ctx);
        } catch (Exception e) {
            LOG.info("exception: " + DebugUtil.stackTrace(e));
            DebugUtil.pause();
        }

    }


    private void clearContainerInfo(ContainerId containerId) {
        Path cIdPath = Utilities.getRemotePath((YarnConfiguration) conf, containerId.getApplicationAttemptId().getApplicationId(), "containers/" + containerId.toString());
        try {
            FileSystem dfs = cIdPath.getFileSystem(conf);
            if (dfs.exists(cIdPath)) {
                dfs.delete(cIdPath);
            }
        } catch (Exception e) {
            LOG.info(DebugUtil.stackTrace(e));
        }
    }

    public static void main(String[] args) {
        ApplicationMaster appMaster;
        try {
            appMaster = new ApplicationMaster();
            appMaster.init();
            boolean tag;
            try {
                tag = appMaster.run();
            } catch (Throwable t) {
                tag = false;
                String stackTrace = DebugUtil.stackTrace(t);
                appMaster.unregister(FinalApplicationStatus.FAILED, stackTrace);
                LOG.error(stackTrace);
            }

            if (tag) {
                LOG.info("Application completed successfully.");
                System.exit(0);
            } else {
                LOG.info("Application failed.");
                System.exit(1);
            }
        } catch (Exception e) {
            LOG.fatal("Error running ApplicationMaster", e);
            System.exit(1);
        }
    }
}