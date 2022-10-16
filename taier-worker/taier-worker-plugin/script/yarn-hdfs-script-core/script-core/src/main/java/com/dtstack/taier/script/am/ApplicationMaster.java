package com.dtstack.taier.script.am;

import com.dtstack.taier.script.ScriptConfiguration;
import com.dtstack.taier.script.api.ApplicationContext;
import com.dtstack.taier.script.api.ScriptConstants;
import com.dtstack.taier.script.common.SecurityUtil;
import com.dtstack.taier.script.container.ScriptContainer;
import com.dtstack.taier.script.container.ScriptContainerId;
import com.dtstack.taier.script.util.DebugUtil;
import com.dtstack.taier.script.util.KrbUtils;
import com.dtstack.taier.script.util.Utilities;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileSystem;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class ApplicationMaster extends CompositeService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationMaster.class);

    AMRMClientAsync<AMRMClient.ContainerRequest> amrmAsync;

    NMClientAsync nmAsync;

    private ApplicationMessageService messageService;

    private RMCallbackHandler rmCallbackHandler;

    final YarnConfiguration yarnconf = new YarnConfiguration();

    final ScriptConfiguration dtconf = new ScriptConfiguration();

    final ApplicationContext applicationContext;

    ApplicationAttemptId applicationAttemptId;

    /**
     * An RPC Service listening the container status
     */
    ApplicationContainerListener containerListener;

    NMCallbackHandler nmAsyncHandler;

    String appMasterHostname;

    int workerMemory;

    int appMaxAttempts;

    Map<String, String> envs;

    long heartBeatInterval;

    final String APP_SUCCESS = "Application is success.";

    private ApplicationMaster(String name) {
        super(name);
        Path yarnConfPath = new Path(ScriptConstants.YARN_CONFIGURATION);
        yarnconf.addResource(yarnConfPath);
        Path scriptConfPath = new Path(ScriptConstants.SCRIPT_CONFIGURATION);
        dtconf.addResource(scriptConfPath);

        LOG.info("user.dir: " + System.getProperty("user.dir"));
        LOG.info("user.name: " + System.getProperty("user.name"));
        LOG.info("HADOOP_USER_NAME: " + System.getenv(ScriptConstants.Environment.HADOOP_USER_NAME.toString()));

        envs = System.getenv();
        applicationContext = new RunningAppContext(this);
        messageService = new ApplicationMessageService(this.applicationContext, yarnconf);
//        appArguments = new AppArguments(this);
        containerListener = new ApplicationContainerListener(applicationContext, yarnconf);

        heartBeatInterval = dtconf.getLong(ScriptConfiguration.SCRIPT_CONTAINER_HEARTBEAT_INTERVAL, ScriptConfiguration.DEFAULT_SCRIPT_CONTAINER_HEARTBEAT_INTERVAL);

        if (envs.containsKey(ApplicationConstants.Environment.CONTAINER_ID.toString())) {
            ContainerId containerId = ConverterUtils
                    .toContainerId(envs.get(ApplicationConstants.Environment.CONTAINER_ID.toString()));
            applicationAttemptId = containerId.getApplicationAttemptId();
        } else {
            throw new IllegalArgumentException(
                    "Application Attempt Id is not available in environment");
        }

        LOG.info("Application appId="
                + applicationAttemptId.getApplicationId().getId()
                + ", clustertimestamp="
                + applicationAttemptId.getApplicationId().getClusterTimestamp()
                + ", attemptId=" + applicationAttemptId.getAttemptId());
        workerMemory = dtconf.getInt(ScriptConfiguration.SCRIPT_WORKER_MEMORY, ScriptConfiguration.DEFAULT_SCRIPT_WORKER_MEMORY);
        appMaxAttempts = dtconf.getInt(ScriptConfiguration.APP_MAX_ATTEMPTS, ScriptConfiguration.DEFAULT_APP_MAX_ATTEMPTS);
        if (applicationAttemptId.getAttemptId() > 1 && appMaxAttempts > 1) {
            int maxMem = dtconf.getInt(ScriptConfiguration.SCRIPT_MAX_WORKER_MEMORY, ScriptConfiguration.DEFAULT_SCRIPT_MAX_WORKER_MEMORY);
            LOG.info("maxMem : " + maxMem);
            int newWorkerMemory = workerMemory + (applicationAttemptId.getAttemptId() - 1) *
                    (int) Math.ceil(workerMemory * yarnconf.getDouble(ScriptConfiguration.SCRIPT_WORKER_MEM_AUTO_SCALE, ScriptConfiguration.DEFAULT_SCRIPT_WORKER_MEM_AUTO_SCALE));
            LOG.info("Auto Scale the Worker Memory from " + workerMemory + " to " + newWorkerMemory);
            if (newWorkerMemory > maxMem) {
                newWorkerMemory = maxMem;
                LOG.info("MaxMem of Worker Memory:" + maxMem + " set workerMemory: " + newWorkerMemory);
            }
            workerMemory = newWorkerMemory;
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
        amrmAsync.init(yarnconf);

        nmAsyncHandler = new NMCallbackHandler(this);
        this.nmAsync = NMClientAsync.createNMClientAsync(nmAsyncHandler);
        this.nmAsync.init(yarnconf);

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
        } finally {
            Utilities.cleanStagingRemotePath(this.yarnconf, this.dtconf, this.applicationAttemptId.getApplicationId());
            LOG.info("cleanStagingRemotePath ApplicationId:" + this.applicationAttemptId.getApplicationId());
        }
    }

    private AMRMClient.ContainerRequest buildContainerRequest() {
        Priority priority = Records.newRecord(Priority.class);
        priority.setPriority(dtconf.getInt(ScriptConfiguration.APP_PRIORITY, ScriptConfiguration.DEFAULT_SCRIPT_APP_PRIORITY));
        Resource workerCapability = Records.newRecord(Resource.class);
        workerCapability.setMemory(workerMemory);
        workerCapability.setVirtualCores(dtconf.getInt(ScriptConfiguration.SCRIPT_WORKER_CORES, ScriptConfiguration.DEFAULT_SCRIPT_WORKER_CORES));
//        if (appArguments.workerGCores > 0) {
//            workerCapability.setResourceValue(DtYarnConstants.GPU, appArguments.workerGCores);
//        }
        String[] nodes = dtconf.getStrings(ScriptConfiguration.SCRIPT_WORKER_NODES, (String[]) null);
        String[] racks = dtconf.getStrings(ScriptConfiguration.SCRIPT_WORKER_RACKS, (String[]) null);
        boolean isRelaxLocality = nodes == null && racks == null;
        List nodeList = nodes == null ? null : Arrays.asList(nodes);
        List racksList = racks == null ? null : Arrays.asList(racks);
        LOG.info("ContainerRequest nodes: " + nodeList + ", racks: " + racksList);
        LOG.info("workermemory: " + workerMemory);
        return new AMRMClient.ContainerRequest(workerCapability, nodes, racks, priority, isRelaxLocality);

    }

    private List<String> buildContainerLaunchCommand(int containerMemory, boolean enableKerberos) {
        List<String> containerLaunchcommands = new ArrayList<>();
        LOG.info("Setting up container command");
        Vector<CharSequence> vargs = new Vector<>(11);
        vargs.add(dtconf.get(ScriptConfiguration.JAVA_PATH, "${JAVA_HOME}" + "/bin/java"));
        vargs.add("-server -XX:+UseConcMarkSweepGC -XX:-UseCompressedClassPointers -XX:+DisableExplicitGC -XX:-OmitStackTraceInFastThrow");
        vargs.add("-Xmx" + containerMemory + "m");
        vargs.add("-Xms" + containerMemory + "m");

        if (enableKerberos) {
            vargs.add("-Djava.security.krb5.conf=" + ScriptConstants.LOCALIZED_KR5B_PATH);
        }

        String javaOpts = dtconf.get(ScriptConfiguration.SCRIPT_JAVA_OPTS, ScriptConfiguration.DEFAULT_SCRIPT_CONTAINER_EXTRA_JAVA_OPTS);
        String logging = "-Dlog.file=" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/worker.log";
        logging += " -Dlog4j.configuration=file:" + ScriptConfiguration.SCRIPT_LOG4J_FILENAME;
        vargs.add(logging);
        if (!StringUtils.isBlank(javaOpts)) {
            vargs.add(javaOpts);
        }
        vargs.add(ScriptContainer.class.getName());
        vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/" + "worker.out");
        vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/" + "worker.error");

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
        LOG.info("---ugi:" + UserGroupInformation.getCurrentUser());

        register();

        if (dtconf.getBoolean(ScriptConfiguration.SCRIPT_WORKER_EXCLUSIVE, ScriptConfiguration.DEFAULT_SCRIPT_WORKER_EXCLUSIVE)) {
            rmCallbackHandler.startWorkerContainersExclusive();
        }

        AMRMClient.ContainerRequest workerContainerRequest = buildContainerRequest();
        LOG.info("ContainerRequest:" + workerContainerRequest.toString() + " nodes:" + workerContainerRequest.getNodes() + " racks:" + workerContainerRequest.getRacks());

        int interval = dtconf.getInt(ScriptConfiguration.SCRIPT_ALLOCATE_INTERVAL, ScriptConfiguration.DEFAULT_SCRIPT_ALLOCATE_INTERVAL);
        amrmAsync.setHeartbeatInterval(interval);

        List<String> workerContainerLaunchCommands = buildContainerLaunchCommand(workerMemory, KrbUtils.hasKrb(envs));
        Map<String, LocalResource> containerLocalResource = buildContainerLocalResource();
        Map<String, String> workerContainerEnv = new ContainerEnvBuilder(ScriptConstants.WORKER, this).build(dtconf);

        // Kerberos
        if (KrbUtils.hasKrb(envs)) {
            workerContainerEnv.put(ScriptConstants.ENV_PRINCIPAL, envs.get(ScriptConstants.ENV_PRINCIPAL));
            setKrbLocalResource(containerLocalResource);
        }
        if (envs.containsKey(ScriptConstants.Environment.PROJECT_TYPE.toString())) {
            workerContainerEnv.put(ScriptConstants.Environment.PROJECT_TYPE.toString(), "science");
            setSingleLocalResource(ScriptConstants.LOCALIZED_GATEWAY_PATH, containerLocalResource);
        }


        if (StringUtils.isNotBlank(dtconf.get(ScriptConfiguration.SCRIPT_SHIP_FILES))) {
            if (dtconf.get(ScriptConfiguration.SCRIPT_APP_TYPE).contains("python")) {
                String[] shipFiles = dtconf.get(ScriptConfiguration.SCRIPT_SHIP_FILES).split(",");
                for (int i = 0; i < shipFiles.length; i++) {
                    shipFiles[i] = new Path(shipFiles[i]).getName();
                }
                String join = StringUtils.join(shipFiles, ":");
                workerContainerEnv.put("PYTHONPATH", "$PYTHONPATH" + ":" + join);
            }
        }

        List<Container> acquiredWorkerContainers = handleRmCallbackOfContainerRequest(dtconf.getInt(ScriptConfiguration.SCRIPT_WORKER_NUM, ScriptConfiguration.DEFAULT_SCRIPT_WORKER_NUM), workerContainerRequest, interval);

        int i = 0;
        for (Container container : acquiredWorkerContainers) {
            LOG.info("Launching worker container " + container.getId()
                    + " on " + container.getNodeId().getHost() + ":" + container.getNodeId().getPort());
            launchContainer(containerLocalResource, workerContainerEnv,
                    workerContainerLaunchCommands, container, i);
            containerListener.registerContainer(new ScriptContainerId(container.getId()), container.getNodeId());
        }

        while (!containerListener.isTrainCompleted()) {
            Utilities.sleep(heartBeatInterval);
        }

        LOG.info("Worker container completed");
        containerListener.setFinished();

        boolean finalSuccess = containerListener.isAllWorkerContainersSucceeded();

        if (!finalSuccess && applicationAttemptId.getAttemptId() < appMaxAttempts) {
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
            if ((System.currentTimeMillis() - startAllocatedTimeStamp) > yarnconf.getInt(YarnConfiguration.RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS, YarnConfiguration.DEFAULT_RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS)) {
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
        URI defaultUri = new Path(yarnconf.get("fs.defaultFS")).toUri();
        Map<String, LocalResource> containerLocalResource = new HashMap<>();
        try (FileSystem fileSystem = FileSystem.get(yarnconf)) {
            containerLocalResource.put(ScriptConfiguration.SCRIPT_APPMASTERJAR_PATH,
                    Utilities.createApplicationResource(fileSystem,
                            new Path(envs.get(ScriptConstants.Environment.APP_JAR_LOCATION.toString())),
                            LocalResourceType.FILE));

            containerLocalResource.put(ScriptConstants.YARN_CONFIGURATION,
                    Utilities.createApplicationResource(fileSystem,
                            new Path(envs.get(ScriptConstants.Environment.YARN_JOB_CONF_LOCATION.toString())),
                            LocalResourceType.FILE));

            containerLocalResource.put(ScriptConstants.SCRIPT_CONFIGURATION,
                    Utilities.createApplicationResource(fileSystem,
                            new Path(envs.get(ScriptConstants.Environment.SCRIPT_JOB_CONF_LOCATION.toString())),
                            LocalResourceType.FILE));

            containerLocalResource.put(ScriptConfiguration.SCRIPT_LOG4J_FILENAME,
                    Utilities.createApplicationResource(fileSystem,
                            new Path(envs.get(ScriptConstants.Environment.LOG4J_JOB_CONF_LOCATION.toString())),
                            LocalResourceType.FILE));

            Path execFilePath = new Path(envs.get(ScriptConstants.Environment.SCRIPT_FILES.toString()));
            containerLocalResource.put(execFilePath.getName(),
                    Utilities.createApplicationResource(fileSystem,
                            execFilePath,
                            LocalResourceType.FILE));

            if (StringUtils.isNotBlank(envs.get(ScriptConstants.Environment.SCRIPT_SHIP_FILES.toString()))) {
                String[] shipFiles = StringUtils.split(envs.get(ScriptConstants.Environment.SCRIPT_SHIP_FILES.toString()), ",");
                for (String file : shipFiles) {
                    Path path = new Path(file);
                    containerLocalResource.put(path.getName(),
                            Utilities.createApplicationResource(fileSystem,
                                    path,
                                    LocalResourceType.FILE));
                }
            }

            if (StringUtils.isNotBlank(envs.get(ScriptConstants.Environment.CACHE_FILE_LOCATION.toString()))) {
                String[] cacheFiles = StringUtils.split(envs.get(ScriptConstants.Environment.CACHE_FILE_LOCATION.toString()), ",");
                for (String path : cacheFiles) {
                    Path pathRemote;
                    String aliasName;
                    if (path.contains("#")) {
                        String[] paths = StringUtils.split(path, "#");
                        if (paths.length != 2) {
                            throw new RuntimeException("Error cacheFile path format " + envs.get(ScriptConstants.Environment.CACHE_FILE_LOCATION.toString()));
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
                            Utilities.createApplicationResource(pathRemote.getFileSystem(yarnconf),
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
     * 设置Worker节点所需本地化的kerberos相关文件资源
     *
     * @param containerLocalResource
     * @throws IOException
     */
    private void setKrbLocalResource(Map<String, LocalResource> containerLocalResource) throws IOException {
        setSingleLocalResource(ScriptConstants.LOCALIZED_KEYTAB_PATH, containerLocalResource);
        setSingleLocalResource(ScriptConstants.LOCALIZED_KR5B_PATH, containerLocalResource);

        String appType = envs.get(ScriptConstants.Environment.APP_TYPE.toString());
    }

    private void setSingleLocalResource(String fileName, Map<String, LocalResource> containerLocalResource) throws IOException {
        FileSystem fs = FileSystem.get(yarnconf);
        Path remotePath = Utilities.getRemotePath(
                (YarnConfiguration) this.yarnconf, this.dtconf,
                this.applicationAttemptId.getApplicationId(),
                fileName);
        containerLocalResource.put(
                fileName,
                Utilities.createApplicationResource(fs, remotePath, LocalResourceType.FILE));
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
        LOG.info("container nodeId: " + container.getNodeId().toString());
        LOG.info("Setting up launch context for containerID="
                + container.getId());

        containerEnv.put(ScriptConstants.Environment.SCRIPT_TF_INDEX.toString(), String.valueOf(index));

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
        try (
                ApplicationMaster appMaster = new ApplicationMaster();
        ) {
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
            LOG.info("Error running ApplicationMaster", e);
            System.exit(1);
        }
    }
}