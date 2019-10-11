package com.dtstack.rdos.engine.execution.flink180;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.JarFileInfo;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.util.HadoopConfTool;
import com.dtstack.rdos.engine.execution.flink180.enums.Deploy;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.client.deployment.StandaloneClusterDescriptor;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.MiniClusterClient;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalException;
import org.apache.flink.runtime.minicluster.MiniCluster;
import org.apache.flink.runtime.minicluster.MiniClusterConfiguration;
import org.apache.flink.runtime.util.HadoopUtils;
import org.apache.flink.runtime.util.LeaderConnectionInfo;
import org.apache.flink.util.Preconditions;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * 根据不同的配置创建对应的client
 * Date: 2018/5/3
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class FlinkClientBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkClientBuilder.class);

    private final static String AKKA_ASK_TIMEOUT = "50 s";

    private final static String AKKA_CLIENT_TIMEOUT = "300 s";

    private final static String AKKA_TCP_TIMEOUT = "60 s";

    //默认使用异步提交
    private boolean isDetached = true;

    private FlinkConfig flinkConfig;

    private org.apache.hadoop.conf.Configuration hadoopConf;

    private YarnConfiguration yarnConf;

    private YarnClient yarnClient;

    private Configuration flinkConfiguration;

    private FlinkPrometheusGatewayConfig gatewayConfig;

    private FlinkClientBuilder() {
    }

    public static FlinkClientBuilder create(FlinkConfig flinkConfig, org.apache.hadoop.conf.Configuration hadoopConf, YarnConfiguration yarnConf, YarnClient yarnClient) {
        FlinkClientBuilder builder = new FlinkClientBuilder();
        builder.flinkConfig = flinkConfig;
        builder.gatewayConfig = flinkConfig.getPrometheusGatewayConfig();
        builder.hadoopConf = hadoopConf;
        builder.yarnConf = yarnConf;
        builder.yarnClient = yarnClient;
        return builder;
    }

    public void initFLinkConfiguration(Properties extProp) {
        String clusterMode = flinkConfig.getClusterMode();
        if(StringUtils.isEmpty(clusterMode)) {
            clusterMode = Deploy.standalone.name();
        }

        String defaultFS = hadoopConf.get(HadoopConfTool.FS_DEFAULTFS);
        if(Strings.isNullOrEmpty(flinkConfig.getFlinkHighAvailabilityStorageDir())){
            //设置默认值
            flinkConfig.setDefaultFlinkHighAvailabilityStorageDir(defaultFS);
        }

        flinkConfig.updateFlinkHighAvailabilityStorageDir(defaultFS);

        if (!clusterMode.equals(Deploy.yarn.name())){
            return;
        }

        Configuration config = new Configuration();
        //FIXME 浙大环境测试修改,暂时写在这
        config.setString("akka.client.timeout", AKKA_CLIENT_TIMEOUT);
        config.setString("akka.ask.timeout", AKKA_ASK_TIMEOUT);
        config.setString("akka.tcp.timeout", AKKA_TCP_TIMEOUT);

        if(StringUtils.isNotBlank(flinkConfig.getFlinkZkAddress())) {
            config.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.ZOOKEEPER.toString());
            config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM, flinkConfig.getFlinkZkAddress());
            config.setString(HighAvailabilityOptions.HA_STORAGE_PATH, flinkConfig.getFlinkHighAvailabilityStorageDir());
        }

        if(flinkConfig.getFlinkZkNamespace() != null){//不设置默认值"/flink"
            config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_ROOT, flinkConfig.getFlinkZkNamespace());
        }

        if(flinkConfig.getFlinkClusterId() != null){//standalone必须设置
            config.setString(HighAvailabilityOptions.HA_CLUSTER_ID, flinkConfig.getFlinkClusterId());
        }

        config.setBytes(HadoopUtils.HADOOP_CONF_BYTES, HadoopUtils.serializeHadoopConf(hadoopConf));

        //FIXME 临时处理填写的flink配置,在console上区分开之后区分出配置信息是engine-flink-plugin,还是flink本身
        if(extProp != null){
            extProp.forEach((key, value) -> {
                if (key.toString().contains(".")) {
                    config.setString(key.toString(), value.toString());
                }
            });
        }

        try {
            FileSystem.initialize(config);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosException(e.getMessage());
        }

        flinkConfiguration = config;
    }

    public ClusterClient createStandalone() {
        Preconditions.checkState(flinkConfig.getFlinkJobMgrUrl() != null || flinkConfig.getFlinkZkNamespace() != null,
                "flink client can not init for host and zkNamespace is null at the same time.");

        if (flinkConfig.getFlinkZkNamespace() != null) {//优先使用zk
            Preconditions.checkNotNull(flinkConfig.getFlinkHighAvailabilityStorageDir(), "you need to set high availability storage dir...");
            return initClusterClientByZK(flinkConfig.getFlinkZkNamespace(), flinkConfig.getFlinkZkAddress(), flinkConfig.getFlinkClusterId(),
                    flinkConfig.getFlinkHighAvailabilityStorageDir());
        } else {
            return initClusterClientByURL(flinkConfig.getFlinkJobMgrUrl());
        }
    }

    /**
     * 根据zk获取clusterclient
     *
     * @param zkNamespace
     */
    private ClusterClient initClusterClientByZK(String zkNamespace, String zkAddress, String clusterId, String flinkHighAvailabilityStorageDir) {

        Configuration config = new Configuration();
        config.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.ZOOKEEPER.toString());
        config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM, zkAddress);
        config.setString(HighAvailabilityOptions.HA_STORAGE_PATH, flinkHighAvailabilityStorageDir);
        if (zkNamespace != null) {//不设置默认值"/flink"
            config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_ROOT, zkNamespace);
        }

        if (clusterId != null) {//不设置默认值"/default"
            config.setString(HighAvailabilityOptions.HA_CLUSTER_ID, clusterId);
        }
        MiniClusterConfiguration.Builder configBuilder = new MiniClusterConfiguration.Builder();
        configBuilder.setConfiguration(config);
        //初始化的时候需要设置,否则提交job会出错,update config of jobMgrhost, jobMgrprt
        MiniCluster cluster = null;
        MiniClusterClient clusterClient = null;
        try {
            cluster = new MiniCluster(configBuilder.build());
            clusterClient = new MiniClusterClient(config, cluster);
            LeaderConnectionInfo connectionInfo = clusterClient.getClusterConnectionInfo();
            InetSocketAddress address = AkkaUtils.getInetSocketAddressFromAkkaURL(connectionInfo.getAddress());
            config.setString(JobManagerOptions.ADDRESS, address.getAddress().getHostName());
            config.setInteger(JobManagerOptions.PORT, address.getPort());
        } catch (LeaderRetrievalException e) {
            throw new RdosException("Could not retrieve the leader address and leader session ID.");
        } catch (Exception e1) {
            throw new RdosException("Failed to retrieve JobManager address");
        }
        return clusterClient;
    }

    /**
     * 直接指定jobmanager host:port方式
     *
     * @return
     * @throws Exception
     */
    private ClusterClient initClusterClientByURL(String jobMgrURL) {

        String[] splitInfo = jobMgrURL.split(":");
        if (splitInfo.length < 2) {
            throw new RdosException("the config of engineUrl is wrong. " +
                    "setting value is :" + jobMgrURL + ", please check it!");
        }

        String jobMgrHost = splitInfo[0].trim();
        Integer jobMgrPort = Integer.parseInt(splitInfo[1].trim());

        Configuration config = new Configuration();
        config.setString(JobManagerOptions.ADDRESS, jobMgrHost);
        config.setInteger(JobManagerOptions.PORT, jobMgrPort);

        StandaloneClusterDescriptor descriptor = new StandaloneClusterDescriptor(config);
        RestClusterClient<StandaloneClusterId> clusterClient = null;
        try {
            clusterClient = descriptor.retrieve(null);
        } catch (ClusterRetrieveException e) {
            throw new RdosException("Couldn't retrieve standalone cluster");
        }
        clusterClient.setDetached(isDetached);
        return clusterClient;
    }

    /**
     * 根据yarn方式获取ClusterClient
     */
    @Deprecated
    public ClusterClient<ApplicationId> initYarnClusterClient(Configuration configuration) {

        Configuration newConf = new Configuration(configuration);

        ApplicationId applicationId = acquireApplicationId(newConf);

        if (!flinkConfig.getFlinkHighAvailabilityForBatch()) {
            newConf.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.NONE.toString());
        }

        AbstractYarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, yarnConf, ".");

        ClusterClient<ApplicationId> clusterClient = null;
        try {
            clusterClient = clusterDescriptor.retrieve(applicationId);
        } catch (Exception e) {
            LOG.info("No flink session, Couldn't retrieve Yarn cluster.", e);
            throw new RdosException("No flink session, Couldn't retrieve Yarn cluster.");
        }

        clusterClient.setDetached(isDetached);
        LOG.warn("---init flink client with yarn session success----");

        return clusterClient;
    }

    public AbstractYarnClusterDescriptor createClusterDescriptorByMode(Configuration configuration, JobClient jobClient, boolean isPerjob) throws MalformedURLException {
        if (configuration == null){
            configuration = flinkConfiguration;
        }
        Configuration newConf = new Configuration(configuration);
        if (isPerjob && jobClient != null){
            newConf.setString(HighAvailabilityOptions.HA_CLUSTER_ID, jobClient.getTaskId());
            newConf.setInteger(YarnConfigOptions.APPLICATION_ATTEMPTS.key(), 0);
            perJobMetricConfigConfig(newConf);

        } else if (!isPerjob) {
           //由engine管控的yarnsession clusterId不进行设置，默认使用appId作为clusterId
            newConf.setString(HighAvailabilityOptions.HA_CLUSTER_ID, null);
            if (!flinkConfig.getFlinkHighAvailabilityForBatch()) {
                newConf.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.NONE.toString());
            }
        }

        AbstractYarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, yarnConf, ".");
        String flinkJarPath = null;

        if (StringUtils.isNotBlank(flinkConfig.getFlinkJarPath())) {

            if (!new File(flinkConfig.getFlinkJarPath()).exists()) {
                throw new RdosException("The Flink jar path is not exist");
            }

            flinkJarPath = flinkConfig.getFlinkJarPath();
        }

        if(StringUtils.isNotBlank(flinkConfig.getJobmanagerArchiveFsDir())){
            newConf.setString(JobManagerOptions.ARCHIVE_DIR, flinkConfig.getJobmanagerArchiveFsDir());
        }

        List<URL> classpaths = new ArrayList<>();
        if (flinkJarPath != null) {
            File[] jars = new File(flinkJarPath).listFiles();

            for (File file : jars){
                if (file.toURI().toURL().toString().contains("flink-dist")){
                    clusterDescriptor.setLocalJarPath(new Path(file.toURI().toURL().toString()));
                } else {
                    classpaths.add(file.toURI().toURL());
                }
            }

        } else {
            throw new RdosException("The Flink jar path is null");
        }

        if (isPerjob && jobClient != null && CollectionUtils.isNotEmpty(jobClient.getAttachJarInfos())) {
            for (JarFileInfo jarFileInfo : jobClient.getAttachJarInfos()) {
                classpaths.add(new File(jarFileInfo.getJarPath()).toURI().toURL());
            }
        }

        clusterDescriptor.setProvidedUserJarFiles(classpaths);
        clusterDescriptor.setQueue(flinkConfig.getQueue());
        return clusterDescriptor;
    }

    private AbstractYarnClusterDescriptor getClusterDescriptor(
            Configuration configuration,
            YarnConfiguration yarnConfiguration,
            String configurationDirectory) {
            return new YarnClusterDescriptor(
                    configuration,
                    yarnConfiguration,
                    configurationDirectory,
                    yarnClient,
                    true);
    }

    private ApplicationId acquireApplicationId(Configuration configuration) {
        try {
            Set<String> set = new HashSet<>();
            set.add("Apache Flink");
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.RUNNING);
            enumSet.add(YarnApplicationState.ACCEPTED);
            List<ApplicationReport> reportList = yarnClient.getApplications(set, enumSet);

            int maxMemory = -1;
            int maxCores = -1;
            ApplicationId applicationId = null;


            for (ApplicationReport report : reportList) {
                if (!report.getName().startsWith(flinkConfig.getFlinkSessionName())) {
                    continue;
                }

                if (!report.getYarnApplicationState().equals(YarnApplicationState.RUNNING)) {
                    continue;
                }

                if (!report.getQueue().endsWith(flinkConfig.getQueue())){
                    continue;
                }

                int thisMemory = report.getApplicationResourceUsageReport().getNeededResources().getMemory();
                int thisCores = report.getApplicationResourceUsageReport().getNeededResources().getVirtualCores();
                if (thisMemory > maxMemory || thisMemory == maxMemory && thisCores > maxCores) {
                    maxMemory = thisMemory;
                    maxCores = thisCores;
                    applicationId = report.getApplicationId();
                    //clusterId不为空 且 yarnsession不是由engine来管控时，需要设置clusterId（兼容手动启动yarnsession的情况）
                    if (StringUtils.isNotBlank(flinkConfig.getFlinkClusterId()) && !report.getName().endsWith(flinkConfig.getCluster() + "_" + flinkConfig.getQueue())){
                        configuration.setString(HighAvailabilityOptions.HA_CLUSTER_ID, flinkConfig.getFlinkClusterId());
                    } else {
                        configuration.setString(HighAvailabilityOptions.HA_CLUSTER_ID, applicationId.toString());
                    }
                }

            }

            if (applicationId == null) {
                throw new RdosException("No flink session found on yarn cluster.");
            }
            return applicationId;
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosException(e.getMessage());
        }
    }

    private void perJobMetricConfigConfig(Configuration configuration){
        if(StringUtils.isBlank(gatewayConfig.getReporterClass())){
            return;
        }

        configuration.setString(FlinkPrometheusGatewayConfig.PROMGATEWAY_CLASS_KEY, gatewayConfig.getReporterClass());
        configuration.setString(FlinkPrometheusGatewayConfig.PROMGATEWAY_HOST_KEY, gatewayConfig.getGatewayHost());
        configuration.setString(FlinkPrometheusGatewayConfig.PROMGATEWAY_PORT_KEY, gatewayConfig.getGatewayPort());
        configuration.setString(FlinkPrometheusGatewayConfig.PROMGATEWAY_JOBNAME_KEY, gatewayConfig.getGatewayJobName());
        configuration.setString(FlinkPrometheusGatewayConfig.PROMGATEWAY_RANDOMJOBNAMESUFFIX_KEY, gatewayConfig.getRandomJobNameSuffix());
        configuration.setString(FlinkPrometheusGatewayConfig.PROMGATEWAY_DELETEONSHUTDOWN_KEY, gatewayConfig.getDeleteOnShutdown());
    }

    public FlinkConfig getFlinkConfig() {
        return flinkConfig;
    }

    public org.apache.hadoop.conf.Configuration getHadoopConf() {
        return hadoopConf;
    }

    public YarnConfiguration getYarnConf() {
        return yarnConf;
    }

    public YarnClient getYarnClient(){
        return this.yarnClient;
    }

    public Configuration getFlinkConfiguration() {
        if (flinkConfiguration == null) {
            throw new RdosException("Configuration directory not set");
        }
        return flinkConfiguration;
    }
}
