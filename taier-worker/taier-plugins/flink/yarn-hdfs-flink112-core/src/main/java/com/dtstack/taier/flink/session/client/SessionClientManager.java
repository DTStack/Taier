package com.dtstack.taier.flink.session.client;

import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.flink.client.AbstractClientManager;
import com.dtstack.taier.flink.config.FlinkConfig;
import com.dtstack.taier.flink.config.HadoopConfig;
import com.dtstack.taier.flink.constant.ConfigConstant;
import com.dtstack.taier.flink.constant.ErrorMessageConstant;
import com.dtstack.taier.flink.session.check.SessionHealthInfo;
import com.dtstack.taier.flink.session.check.SessionStatusMonitor;
import com.dtstack.taier.flink.util.FileUtil;
import com.dtstack.taier.flink.util.FlinkUtil;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.leader.LeaderNode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.configuration.SecurityOptions;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.util.FlinkException;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @program: engine-plugins
 * @author: xiuzhu
 * @create: 2021/07/15
 */

public class SessionClientManager extends AbstractClientManager {

    private static final Logger LOG = LoggerFactory.getLogger(SessionClientManager.class);

    private static final String FLINK_VERSION = "flink112";
    private final String sessionAppNameSuffix;
    private final LeaderNode leaderNode = LeaderNode.getInstance();
    private final String lockPath;

    private ClusterSpecification yarnSessionSpecification;

    private volatile ApplicationId clusterId;
    private volatile ClusterClient<ApplicationId> clusterClient;
    private final AtomicBoolean isLeader = new AtomicBoolean(false);
    private final SessionHealthInfo sessionHealthInfo = new SessionHealthInfo();
    private final AtomicBoolean startMonitor = new AtomicBoolean(false);
    private ExecutorService yarnMonitorService;

    public SessionClientManager(FlinkConfig flinkConfig, HadoopConfig hadoopConf, Configuration flinkGlobalConfiguration) {
        super(flinkConfig, hadoopConf);
        // add session name
        this.sessionAppNameSuffix = String.format("%s_%s_%s",
                flinkConfig.getCluster(),
                flinkConfig.getQueue(),
                FLINK_VERSION);
        flinkConfiguration.setString(YarnConfigOptions.APPLICATION_NAME,
                flinkConfig.getFlinkSessionName() + ConfigConstant.SPLIT + sessionAppNameSuffix);
        addFlinkConfiguration(flinkGlobalConfiguration);
        this.yarnSessionSpecification = FlinkUtil.createClusterSpecification(flinkConfiguration, 0, null, null);

        this.lockPath = String.format("%s/%s",
                this.sessionAppNameSuffix,
                flinkConfig.getFlinkSessionName().replace("\\s", ""));

        start();
    }

    public SessionHealthInfo getSessionHealthInfo() {
        return sessionHealthInfo;
    }

    public String getSessionAppNameSuffix() {
        return sessionAppNameSuffix;
    }

    public ApplicationId getClusterId() {
        return clusterId;
    }

    public AtomicBoolean getIsLeader() {
        return isLeader;
    }

    /**
     * check if session start, if not, build a new session
     */
    public void start() {
        try {
            // flush role at current worker node.
            flushRole();
            KerberosUtils.login(flinkConfig,
                    this::startAndGetSessionClusterClient,
                    hadoopConfig.getYarnConfiguration());
        } catch (Exception e) {
            throw new PluginDefineException("init SessionClient startAndGetSessionClusterClient error.", e);
        } finally {
            releaseLock();
        }
    }

    @Override
    public void addFlinkConfiguration(Configuration flinkGlobalConfiguration) {
        this.flinkConfiguration.addAll(flinkGlobalConfiguration);
    }

    private void startYarnSessionClientMonitor() {

        String threadName = String.format("%s-%s", sessionAppNameSuffix, "flink_yarn_monitor");
        LOG.warn("Start a yarn session client monitor [{}].", threadName);
        yarnMonitorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory(threadName));

        //启动守护线程---用于获取当前application状态和更新flink对应的application
        yarnMonitorService.submit(new SessionStatusMonitor(this));
    }

    public ClusterClient<ApplicationId> startAndGetSessionClusterClient() {
        boolean startRs = startFlinkYarnSession();
        LOG.info("FlinkYarnSession launched {}.", startRs ? "succeeded" : "failed");
        if (startRs) {
            sessionHealthInfo.healthy();
        } else {
            sessionHealthInfo.unHealthy();
        }
        if (startMonitor.compareAndSet(false, true)) {
            startYarnSessionClientMonitor();
        }
        return clusterClient;
    }

    private boolean startFlinkYarnSession() {
        try {
            ClusterClient<ApplicationId> retrieveClusterClient = null;
            try {
                retrieveClusterClient = initYarnClusterClient();
            } catch (Exception e) {
                LOG.error("", e);
            }

            if (retrieveClusterClient != null) {
                clusterClient = retrieveClusterClient;
                clusterId = clusterClient.getClusterId();
                LOG.info("retrieve flink client with yarn session success");
                return true;
            }

            LOG.info("Current role is [{}] and session start auto is {} and Lock path is {}", isLeader.get() ? "Leader" : "Follower", flinkConfig.getSessionStartAuto(), lockPath);
            if (isLeader.get() && flinkConfig.getSessionStartAuto()) {
                try {
                    try (
                            YarnClusterDescriptor yarnSessionDescriptor = createYarnSessionClusterDescriptor();
                    ) {
                        clusterClient = yarnSessionDescriptor.deploySessionCluster(yarnSessionSpecification).getClusterClient();
                        clusterId = clusterClient.getClusterId();
                    }
                    return true;
                } catch (FlinkException e) {
                    LOG.error("Couldn't deploy Yarn session cluster, ", e);
                    throw e;
                }
            }
        } catch (Exception e) {
            // 这个方法不抛出异常，只有返回值
            LOG.error("Couldn't deploy Yarn session cluster:", e);
        }
        return false;
    }

    /**
     * get cluster client by session status montior
     * need'nt judge session state.
     * because it's watch session state. when session is unhealthy, it will restart session by this cluster client.
     *
     * @return
     */
    public ClusterClient<ApplicationId> getClusterClient() {
        return clusterClient;
    }

    /**
     * get cluster client by flink client.
     * should judge session state is healthy.
     * because this method provides external services.
     *
     * @param jobIdentifier
     * @return
     */
    @Override
    public ClusterClient<ApplicationId> getClusterClient(JobIdentifier jobIdentifier) {
        if (!sessionHealthInfo.getSessionState()) {
            LOG.warn(ErrorMessageConstant.WAIT_SESSION_RECOVER);
            // TODO 抛出一个FlinkSessionUnhealthyException类型？
            throw new PluginDefineException(ErrorMessageConstant.WAIT_SESSION_RECOVER);
        }
        return clusterClient;
    }

    @Override
    public void dealWithClientError() {
        sessionHealthInfo.incrSubmitError();
    }

    public ClusterClient<ApplicationId> initYarnClusterClient() {
        Configuration newConf = new Configuration(flinkConfiguration);
        ApplicationId applicationId = acquireAppIdAndSetClusterId(newConf);
        if (applicationId == null) {
            throw new PluginDefineException("No flink session found on yarn cluster.");
        }

        if (!flinkConfig.getFlinkHighAvailability()) {
            setNoneHaModeConfig(newConf);
        }

        YarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, hadoopConfig.getYarnConfiguration());

        ClusterClient<ApplicationId> clusterClient;
        try {
            ClusterClientProvider<ApplicationId> clusterClientProvider = clusterDescriptor.retrieve(applicationId);
            clusterClient = clusterClientProvider.getClusterClient();
        } catch (Exception e) {
            throw new PluginDefineException("No flink session, Couldn't retrieve Yarn cluster.");
        }

        LOG.warn("---init flink client with yarn session success----");

        return clusterClient;
    }

    public ApplicationId acquireAppIdAndSetClusterId(Configuration configuration) {
        try {
            Set<String> set = new HashSet<>();
            set.add("Apache Flink");
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.RUNNING);
            enumSet.add(YarnApplicationState.ACCEPTED);

            YarnClient yarnClient = getYarnClient();
            if (null == yarnClient) {
                throw new PluginDefineException("getYarnClient error, Yarn Client is null!");
            }

            List<ApplicationReport> reportList = yarnClient.getApplications(set, enumSet);

            int maxMemory = -1;
            int maxCores = -1;
            ApplicationId applicationId = null;

            for (ApplicationReport report : reportList) {
                LOG.info("filter flink session application current reportName:{} queue:{} status:{}",
                        report.getName(), report.getQueue(), report.getYarnApplicationState());
                boolean checkState = report.getYarnApplicationState().equals(YarnApplicationState.RUNNING);
                boolean checkQueue = report.getQueue().endsWith(flinkConfig.getQueue());
                boolean checkPrefixName = report.getName().startsWith(flinkConfig.getFlinkSessionName());
                boolean checkSuffixName = report.getName().endsWith(sessionAppNameSuffix);
                if (!checkState || !checkQueue || !checkPrefixName || !checkSuffixName) {
                    continue;
                }

                int thisMemory = report.getApplicationResourceUsageReport().getNeededResources().getMemory();
                int thisCores = report.getApplicationResourceUsageReport().getNeededResources().getVirtualCores();
                LOG.info("current flink session memory {},Cores{}", thisMemory, thisCores);
                // todo 这个表达式很模糊
                if (thisMemory > maxMemory
                        || (thisMemory == maxMemory && thisCores > maxCores)) {
                    maxMemory = thisMemory;
                    maxCores = thisCores;
                    applicationId = report.getApplicationId();
                    //flinkClusterId不为空 且 yarn session不是由engine来管控时，需要设置clusterId（兼容手动启动yarn session的情况）
                    if (StringUtils.isBlank(configuration.getValue(HighAvailabilityOptions.HA_CLUSTER_ID))
                            || report.getName().endsWith(sessionAppNameSuffix)) {
                        configuration.setString(HighAvailabilityOptions.HA_CLUSTER_ID, applicationId.toString());
                    }
                }

            }
            return applicationId;
        } catch (Exception e) {
            throw new PluginDefineException(e);
        }
    }

    public YarnClusterDescriptor createYarnSessionClusterDescriptor() throws MalformedURLException {
        Configuration newConf = new Configuration(flinkConfiguration);

        String flinkJarPath = flinkConfig.getFlinkLibDir();
        String pluginLoadMode = flinkConfig.getPluginLoadMode();
        YarnConfiguration yarnConf = hadoopConfig.getYarnConfiguration();

        FileUtil.checkFileExist(flinkJarPath);

        if (!flinkConfig.getFlinkHighAvailability()) {
            setNoneHaModeConfig(newConf);
        } else {
            //由engine管控的yarnsession clusterId不进行设置，默认使用appId作为clusterId
            newConf.removeConfig(HighAvailabilityOptions.HA_CLUSTER_ID);
        }

        List<File> keytabFiles = null;
        if (flinkConfig.isOpenKerberos()) {
            keytabFiles = getKeytabFilesAndSetSecurityConfig(newConf);
        }

        newConf = setHdfsFlinkJarPath(flinkConfig, newConf);

        YarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, yarnConf);

        if (StringUtils.isNotBlank(pluginLoadMode) && ConfigConstant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(pluginLoadMode)) {
            newConf.setString(ConfigConstant.FLINKX_PLUGIN_LOAD_MODE, flinkConfig.getPluginLoadMode());

            String flinkPluginRoot = flinkConfig.getChunjunDistDir();
            if (StringUtils.isNotBlank(flinkPluginRoot)) {
                String syncPluginDir = flinkPluginRoot;
                File syncFile = new File(syncPluginDir);
                if (!syncFile.exists()) {
                    throw new PluginDefineException("syncPlugin path is null");
                }
                List<File> pluginPaths = Arrays.stream(syncFile.listFiles())
                        .filter(file -> !file.getName().endsWith("zip"))
                        .collect(Collectors.toList());
                clusterDescriptor.addShipFiles(pluginPaths);
            }
        }
        if (CollectionUtils.isNotEmpty(keytabFiles)) {
            clusterDescriptor.addShipFiles(keytabFiles);
        }
        List<URL> classpaths = getFlinkJarFile(flinkJarPath, clusterDescriptor);
        clusterDescriptor.setProvidedUserJarFiles(classpaths);

        return clusterDescriptor;
    }

    /**
     * set the copy of configuration
     */
    public void setNoneHaModeConfig(Configuration configuration) {
        configuration.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.NONE.toString());
        configuration.removeConfig(HighAvailabilityOptions.HA_CLUSTER_ID);
        configuration.removeConfig(HighAvailabilityOptions.HA_ZOOKEEPER_ROOT);
        configuration.removeConfig(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM);
    }

    private List<File> getKeytabFilesAndSetSecurityConfig(Configuration config) {
        Map<String, File> keytabs = new HashMap<>();
        String remoteDir = flinkConfig.getRemoteDir();

        // 任务提交keytab
        String clusterKeytabDirPath = com.dtstack.taier.pluginapi.constrant.ConfigConstant.LOCAL_KEYTAB_DIR_PARENT + remoteDir;
        File clusterKeytabDir = new File(clusterKeytabDirPath);
        File[] clusterKeytabFiles = clusterKeytabDir.listFiles();

        if (clusterKeytabFiles == null || clusterKeytabFiles.length == 0) {
            throw new PluginDefineException("not find keytab file from " + clusterKeytabDirPath);
        }
        for (File file : clusterKeytabFiles) {
            String fileName = file.getName();
            String keytabPath = file.getAbsolutePath();
            String keytabFileName = flinkConfig.getPrincipalFile();

            if (StringUtils.equals(fileName, keytabFileName)) {
                String principal = flinkConfig.getPrincipal();
                if (StringUtils.isEmpty(principal)) {
                    principal = KerberosUtils.getPrincipal(keytabPath);
                }
                config.setString(SecurityOptions.KERBEROS_LOGIN_KEYTAB, keytabPath);
                config.setString(SecurityOptions.KERBEROS_LOGIN_PRINCIPAL, principal);
                continue;
            }
            if (StringUtils.endsWith(fileName, ".conf") || StringUtils.endsWith(fileName, ".keytab")) {
                keytabs.put(file.getName(), file);
            }
        }

        return new ArrayList<>(keytabs.values());
    }

    /**
     * flush current node role.
     */
    public void flushRole() {
        boolean isLock = tryLock(lockPath);
        if (LOG.isDebugEnabled()) {
            LOG.debug("LockPath {}, lock result {} ", lockPath, isLock);
        }
        isLeader.set(isLock);
    }

    /**
     * get distribute lock
     *
     * @return
     */
    private boolean tryLock(String lockPath) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("==> SessionClientFactory.tryLock()");
        }
        boolean result = false;
        try {
            result = leaderNode.tryLock(lockPath, 1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOG.error(" == SessionClientFactory.tryLock failed. ", e);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("<== SessionClientFactory.tryLock()");
        }
        return result;
    }

    /**
     * release distribute lock
     */
    public void releaseLock() {
        LOG.debug("==> SessionClientFactory.releaseLock()");
        if (!isLeader.get()) {
            return;
        }

        try {
            leaderNode.release(lockPath);
        } catch (Exception e) {
            LOG.error(" == SessionClientFactory.releaseLock failed.", e);

        }

        isLeader.set(false);
        LOG.debug("<== SessionClientFactory.releaseLock()");
    }
}
