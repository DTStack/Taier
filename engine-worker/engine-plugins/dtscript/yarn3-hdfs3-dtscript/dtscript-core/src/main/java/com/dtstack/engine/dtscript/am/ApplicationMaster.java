package com.dtstack.engine.dtscript.am;

import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.api.ApplicationContext;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.common.SecurityUtil;
import com.dtstack.engine.dtscript.container.DtContainer;
import com.dtstack.engine.dtscript.container.DtContainerId;
import com.dtstack.engine.dtscript.util.DebugUtil;
import com.dtstack.engine.dtscript.util.Utilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
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
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class ApplicationMaster extends CompositeService {

    private static final Log LOG = LogFactory.getLog(ApplicationMaster.class);

    AMRMClientAsync<AMRMClient.ContainerRequest> amrmAsync;

    NMClientAsync nmAsync;

    private ApplicationMessageService messageService;

    private RMCallbackHandler rmCallbackHandler;

    final Configuration conf = new DtYarnConfiguration();

    final ApplicationContext applicationContext;

    ApplicationAttemptId applicationAttemptId;

    AppArguments appArguments;

    /**
     * An RPC Service listening the container status
     */
    ApplicationContainerListener containerListener;

    NMCallbackHandler nmAsyncHandler;

    String appMasterHostname;

    Map<String, String> envs;

    ApplicationAttemptId applicationAttemptID;

    long heartBeatInterval;

    final String APP_SUCCESS = "Application is success.";

    private ApplicationMaster(String name) {
        super(name);
        Path jobConfPath = new Path(DtYarnConstants.LEARNING_JOB_CONFIGURATION);
        LOG.info("hadoop.job.ugi: " + conf.get("hadoop.job.ugi"));
        LOG.info("user.dir: " + System.getProperty("user.dir"));
//        System.setProperty(DtYarnConstants.Environment.HADOOP_USER_NAME.toString(), conf.get("hadoop.job.ugi").split(",")[0]);
        LOG.info("user.name: " + System.getProperty("user.name"));
        LOG.info("HADOOP_USER_NAME: " + System.getProperty(DtYarnConstants.Environment.HADOOP_USER_NAME.toString()));

        conf.addResource(jobConfPath);
        envs = System.getenv();
        applicationContext = new RunningAppContext(this);
        messageService = new ApplicationMessageService(this.applicationContext, conf);
        appArguments = new AppArguments(this);
        containerListener = new ApplicationContainerListener(applicationContext, conf);

        heartBeatInterval = conf.getLong(DtYarnConfiguration.DTSCRIPT_CONTAINER_HEARTBEAT_INTERVAL, DtYarnConfiguration.DEFAULT_DTSCRIPT_CONTAINER_HEARTBEAT_INTERVAL);


        if (envs.containsKey(ApplicationConstants.Environment.CONTAINER_ID.toString())) {
            ContainerId containerId = ConverterUtils
                    .toContainerId(envs.get(ApplicationConstants.Environment.CONTAINER_ID.toString()));
            applicationAttemptID = containerId.getApplicationAttemptId();
        } else {
            throw new IllegalArgumentException(
                    "Application Attempt Id is not available in environment");
        }

        LOG.info("Application appId="
                + applicationAttemptID.getApplicationId().getId()
                + ", clustertimestamp="
                + applicationAttemptID.getApplicationId().getClusterTimestamp()
                + ", attemptId=" + applicationAttemptID.getAttemptId());

        if (applicationAttemptID.getAttemptId() > 1 && appArguments.appMaxAttempts > 1) {
            int maxMem = conf.getInt(DtYarnConfiguration.DTSCRIPT_MAX_WORKER_MEMORY, DtYarnConfiguration.DEFAULT_DTSCRIPT_MAX_WORKER_MEMORY);
            LOG.info("maxMem : " + maxMem);
            int newWorkerMemory = appArguments.workerMemory + (applicationAttemptID.getAttemptId() - 1) * (int) Math.ceil(appArguments.workerMemory * conf.getDouble(DtYarnConfiguration.DTSCRIPT_WORKER_MEM_AUTO_SCALE, DtYarnConfiguration.DEFAULT_DTSCRIPT_WORKER_MEM_AUTO_SCALE));
            LOG.info("Auto Scale the Worker Memory from " + appArguments.workerMemory + " to " + newWorkerMemory);
            if (newWorkerMemory > maxMem) {
                newWorkerMemory = maxMem;
                LOG.info("MaxMem of Worker Memory:" + maxMem + " set workerMemory: " + newWorkerMemory);
            }
            appArguments.workerMemory = newWorkerMemory;
        }
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

            int maxVcores = response.getMaximumResourceCapability().getVirtualCores();
            LOG.info("Max vcores capabililty of resources in this cluster " + maxVcores);
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
        workerCapability.setMemory(appArguments.workerMemory);
        workerCapability.setVirtualCores(appArguments.workerVcores);
        if (appArguments.workerGCores > 0) {
            workerCapability.setResourceValue(DtYarnConstants.GPU, appArguments.workerGCores);
        }
        if (appArguments.nodes == null){
            return new AMRMClient.ContainerRequest(workerCapability, null, null, priority, true);
        } else {
            return new AMRMClient.ContainerRequest(workerCapability, appArguments.nodes, null, priority, false);
        }
    }

    private List<String> buildContainerLaunchCommand(int containerMemory) {
        List<String> containerLaunchcommands = new ArrayList<>();
        LOG.info("Setting up container command");
        Vector<CharSequence> vargs = new Vector<>(10);
        vargs.add(conf.get(DtYarnConfiguration.JAVA_PATH,"${JAVA_HOME}" + "/bin/java"));
        vargs.add("-server -XX:+UseConcMarkSweepGC -XX:-UseCompressedClassPointers -XX:+DisableExplicitGC -XX:-OmitStackTraceInFastThrow");
        vargs.add("-Xmx" + containerMemory + "m");
        vargs.add("-Xms" + containerMemory + "m");
        String javaOpts = conf.get(DtYarnConfiguration.DTSCRIPT_CONTAINER_EXTRA_JAVA_OPTS, DtYarnConfiguration.DEFAULT_DTSCRIPT_CONTAINER_EXTRA_JAVA_OPTS);
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

        int interval = conf.getInt(DtYarnConfiguration.DTSCRIPT_ALLOCATE_INTERVAL, DtYarnConfiguration.DEFAULT_DTSCRIPT_ALLOCATE_INTERVAL);
        amrmAsync.setHeartbeatInterval(interval);

        List<String> workerContainerLaunchCommands = buildContainerLaunchCommand(appArguments.workerMemory);
        Map<String, LocalResource> containerLocalResource = buildContainerLocalResource();
        Map<String, String> workerContainerEnv = new ContainerEnvBuilder(DtYarnConstants.WORKER, this).build();


        List<Container> acquiredWorkerContainers = handleRmCallbackOfContainerRequest(appArguments.workerNum, workerContainerRequest, interval);

        int i = 0;
        for (Container container : acquiredWorkerContainers) {
            LOG.info("Launching worker container " + container.getId()
                    + " on " + container.getNodeId().getHost() + ":" + container.getNodeId().getPort());
            launchContainer(containerLocalResource, workerContainerEnv,
                    workerContainerLaunchCommands, container, i);
            containerListener.registerContainer(new DtContainerId(container.getId()), container.getNodeId());
        }

        while (!containerListener.isTrainCompleted()) {
            Utilities.sleep(heartBeatInterval);
        }

        LOG.info("Worker container completed");
        containerListener.setFinished();

        boolean finalSuccess = containerListener.isAllWorkerContainersSucceeded();

        if (!finalSuccess && applicationAttemptID.getAttemptId() < appArguments.appMaxAttempts) {
            throw new RuntimeException("Application Failed, retry starting. Note that container memory will auto scale if user config the setting.");
        }

        unregister(finalSuccess ? FinalApplicationStatus.SUCCEEDED : FinalApplicationStatus.FAILED,
                finalSuccess ? APP_SUCCESS : containerListener.getFailedMsg());

        return finalSuccess;
    }

    private List<Container> handleRmCallbackOfContainerRequest(int workerNum, AMRMClient.ContainerRequest request, int allocateInterval) {
        rmCallbackHandler.setNeededWorkerContainersCount(workerNum);

        for (int i = 0; i < workerNum; ++i) {
            amrmAsync.addContainerRequest(request);
        }

        LOG.info("Try to allocate " + workerNum + " worker containers");

        //对独占的nm，向rm进行updateBlacklist操作
        long startAllocatedTimeStamp = System.currentTimeMillis();
        int workerReleaseCount = 0;
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
            synchronized (releaseContainers) {
                if (releaseContainers.size() != 0) {
                    for (Container container : releaseContainers) {
                        LOG.info("Releaseing Black-Host container: " + container.getId().toString());
                        amrmAsync.releaseAssignedContainer(container.getId());
                        amrmAsync.addContainerRequest(request);
                        workerReleaseCount++;
                    }
                    releaseContainers.clear();
                }
            }
            if ((System.currentTimeMillis() - startAllocatedTimeStamp) > conf.getInt(YarnConfiguration.RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS, YarnConfiguration.DEFAULT_RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS)) {
                String failMessage = "Container waiting except the allocated expiry time. Maybe the Cluster available resources are not satisfied the user need. Please resubmit !";
                LOG.error(failMessage);
                throw new RuntimeException("Container waiting except the allocated expiry time.");
            }
            Utilities.sleep(allocateInterval);
        }

        List<Container> acquiredWorkerContainers = rmCallbackHandler.getAcquiredWorkerContainer();
        //释放可能的多余资源
        int totalNumAllocatedWorkers = rmCallbackHandler.getAllocatedWorkerContainerNumber();
        if (totalNumAllocatedWorkers > workerNum) {
            while (acquiredWorkerContainers.size() > workerNum) {
                Container releaseContainer = acquiredWorkerContainers.remove(0);
                amrmAsync.releaseAssignedContainer(releaseContainer.getId());
                LOG.info("Release Needless container " + releaseContainer.getId().toString());
            }
        }

        LOG.info("Total " + acquiredWorkerContainers.size() + " worker containers has allocated.");
        for (int i = 0; i < workerNum + workerReleaseCount; i++) {
            amrmAsync.removeContainerRequest(request);
        }

        // 再次check
        List<Container> releaseContainersTotal = rmCallbackHandler.getReleaseContainers();
        synchronized (releaseContainersTotal) {
            if (releaseContainersTotal.size() != 0) {
                for (Container container : releaseContainersTotal) {
                    LOG.info("Check And Release container: " + container.getId().toString());
                    amrmAsync.releaseAssignedContainer(container.getId());
                }
                releaseContainersTotal.clear();
            }
        }
        return acquiredWorkerContainers;
    }

    private Map<String, LocalResource> buildContainerLocalResource() {
        URI defaultUri = new Path(conf.get("fs.defaultFS")).toUri();
        Map<String, LocalResource> containerLocalResource = new HashMap<>();
        try {
            containerLocalResource.put(DtYarnConfiguration.DTSCRIPT_APPMASTERJAR_PATH,
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

    public static void main(String[] args) {
        ApplicationMaster appMaster;
        try {
            appMaster = new ApplicationMaster();
            appMaster.init();
            boolean tag = appMaster.run();
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