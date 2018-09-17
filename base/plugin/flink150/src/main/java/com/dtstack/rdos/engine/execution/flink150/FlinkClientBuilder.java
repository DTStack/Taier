package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.util.HadoopConfTool;
import com.dtstack.rdos.engine.execution.flink150.enums.Deploy;
import com.dtstack.rdos.engine.execution.flink150.enums.FlinkYarnMode;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.StandaloneClusterDescriptor;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalException;
import org.apache.flink.runtime.util.HadoopUtils;
import org.apache.flink.runtime.util.LeaderConnectionInfo;
import org.apache.flink.util.Preconditions;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.LegacyYarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterDescriptor;
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
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 根据不同的配置创建对应的client
 * Date: 2018/5/3
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class FlinkClientBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkClientBuilder.class);

    //默认使用异步提交
    private boolean isDetached = true;

    private org.apache.hadoop.conf.Configuration hadoopConf;

    private YarnConfiguration yarnConf;

    private AbstractYarnClusterDescriptor yarnClusterDescriptor;

    private Configuration flinkConfiguration;

    private static String akka_ask_timeout = "50 s";

    private static String akka_client_timeout="300 s";

    private static String akka_tcp_timeout = "60 s";

    private YarnClient yarnClient;

    private FlinkClientBuilder() {
    }

    public static FlinkClientBuilder create(org.apache.hadoop.conf.Configuration hadoopConf, YarnConfiguration yarnConf) {
        FlinkClientBuilder builder = new FlinkClientBuilder();
        builder.setHadoopConf(hadoopConf);
        builder.setYarnConf(yarnConf);
        return builder;
    }

    public ClusterClient create(FlinkConfig flinkConfig){

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

        if(clusterMode.equals( Deploy.standalone.name())) {
            return createStandalone(flinkConfig);
        } else if (clusterMode.equals(Deploy.yarn.name())) {
            initFLinkConf(flinkConfig);
            return createYarnClient(flinkConfig);
        } else {
            throw new RdosException("Unsupported clusterMode: " + clusterMode);
        }
    }

    private void initFLinkConf(FlinkConfig flinkConfig) {
        Configuration config = new Configuration();
        //FIXME 浙大环境测试修改,暂时写在这
        config.setString("akka.client.timeout",akka_client_timeout);
        config.setString("akka.ask.timeout",akka_ask_timeout);
        config.setString("akka.tcp.timeout",akka_tcp_timeout);

        if(StringUtils.isNotBlank(flinkConfig.getFlinkZkAddress())) {
            config.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.ZOOKEEPER.toString());
            config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM, flinkConfig.getFlinkZkAddress());
            config.setString(HighAvailabilityOptions.HA_STORAGE_PATH, flinkConfig.getFlinkHighAvailabilityStorageDir());
        }

        if(flinkConfig.getFlinkZkNamespace() != null){//不设置默认值"/flink"
            config.setString(HighAvailabilityOptions.HA_ZOOKEEPER_ROOT, flinkConfig.getFlinkZkNamespace());
        }

        if(flinkConfig.getFlinkClusterId() != null){//不设置默认值"/default"
            config.setString(HighAvailabilityOptions.HA_CLUSTER_ID, flinkConfig.getFlinkClusterId());
        }

        config.setBytes(HadoopUtils.HADOOP_CONF_BYTES, HadoopUtils.serializeHadoopConf(hadoopConf));
        try {
            FileSystem.initialize(config);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosException(e.getMessage());
        }
        flinkConfiguration = config;
    }

    private ClusterClient createStandalone(FlinkConfig flinkConfig) {

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

    private ClusterClient createYarnClient(FlinkConfig flinkConfig) {
        ClusterClient clusterClient = initYarnClusterClient(flinkConfig);
        return clusterClient;
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

        StandaloneClusterDescriptor descriptor = new StandaloneClusterDescriptor(config);
        RestClusterClient<StandaloneClusterId> clusterClient = null;
        try {
            clusterClient = descriptor.retrieve(null);
        } catch (ClusterRetrieveException e) {
            throw new RdosException("Couldn't retrieve standalone cluster");
        }
        clusterClient.setDetached(isDetached);

        //初始化的时候需要设置,否则提交job会出错,update config of jobMgrhost, jobMgrprt
        InetSocketAddress address = null;
        try {
            LeaderConnectionInfo connectionInfo = clusterClient.getClusterConnectionInfo();
            address = AkkaUtils.getInetSocketAddressFromAkkaURL(connectionInfo.getAddress());
        } catch (LeaderRetrievalException e) {
            throw new RdosException("Could not retrieve the leader address and leader session ID.");
        } catch (Exception e1) {
            throw new RdosException("Failed to retrieve JobManager address");
        }

        config.setString(JobManagerOptions.ADDRESS, address.getAddress().getHostName());
        config.setInteger(JobManagerOptions.PORT, address.getPort());

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
    public ClusterClient<ApplicationId> initYarnClusterClient(FlinkConfig flinkConfig) {

        AbstractYarnClusterDescriptor clusterDescriptor = getClusterDescriptor(flinkConfig.getFlinkYarnMode(),flinkConfiguration,yarnConf,".");

        ApplicationId applicationId = acquireApplicationId(clusterDescriptor, flinkConfig);

        ClusterClient<ApplicationId> clusterClient = null;
        try {
            clusterClient = clusterDescriptor.retrieve(applicationId);
        } catch (Exception e) {
            if (clusterDescriptor != null) {
                clusterDescriptor.close();
            }
            LOG.info("Couldn't retrieve Yarn cluster.", e);
            throw new RdosException("Couldn't retrieve Yarn cluster.");
        }

        clusterClient.setDetached(isDetached);
        LOG.warn("---init flink client with yarn session success----");

        return clusterClient;
    }

    public AbstractYarnClusterDescriptor createPerJobClusterDescriptor(FlinkConfig flinkConfig, String taskId) {
        Configuration newConf = new Configuration(flinkConfiguration);
        newConf.setString(HighAvailabilityOptions.HA_CLUSTER_ID, taskId);
        AbstractYarnClusterDescriptor clusterDescriptor = getClusterDescriptor(flinkConfig.getFlinkYarnMode(), newConf, yarnConf, ".");
        String flinkJarPath = null;
        File flinkLib = null;
        if (StringUtils.isNotBlank(flinkConfig.getFlinkJarPath())) {
            if (!new File(flinkConfig.getFlinkJarPath()).exists()) {
                throw new RdosException("The Flink jar path is not exist");
            }
            flinkJarPath = flinkConfig.getFlinkJarPath();
        }
        if (flinkJarPath != null) {
            clusterDescriptor.setLocalJarPath(new Path(flinkJarPath));
        } else {
            throw new RdosException("The Flink jar path is null");
        }
        clusterDescriptor.setQueue(flinkConfig.getQueue());
        yarnClusterDescriptor = clusterDescriptor;
        return clusterDescriptor;
    }

    private AbstractYarnClusterDescriptor getClusterDescriptor(
            String flinkMode,
            Configuration configuration,
            YarnConfiguration yarnConfiguration,
            String configurationDirectory) {
        if (FlinkYarnMode.NEW == FlinkYarnMode.mode(flinkMode) || FlinkYarnMode.PER_JOB == FlinkYarnMode.mode(flinkMode)) {
            return new YarnClusterDescriptor(
                    configuration,
                    yarnConfiguration,
                    configurationDirectory,
                    yarnClient,
                    false);
        } else {
            return new LegacyYarnClusterDescriptor(
                    configuration,
                    yarnConfiguration,
                    configurationDirectory,
                    yarnClient,
                    false);
        }
    }

    private ApplicationId acquireApplicationId(AbstractYarnClusterDescriptor clusterDescriptor, FlinkConfig flinkConfig) {
        try {
            Set<String> set = new HashSet<>();
            set.add("Apache Flink");
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.RUNNING);
            List<ApplicationReport> reportList = clusterDescriptor.getYarnClient().getApplications(set, enumSet);

            int maxMemory = -1;
            int maxCores = -1;
            ApplicationId applicationId = null;
            for (ApplicationReport report : reportList) {
                if (!report.getName().startsWith("Flink session")) {
                    continue;
                }

                if (!report.getYarnApplicationState().equals(YarnApplicationState.RUNNING)) {
                    continue;
                }

                if (!flinkConfig.getQueue().equals(report.getQueue())){
                    continue;
                }

                int thisMemory = report.getApplicationResourceUsageReport().getNeededResources().getMemory();
                int thisCores = report.getApplicationResourceUsageReport().getNeededResources().getVirtualCores();
                if (thisMemory > maxMemory || thisMemory == maxMemory && thisCores > maxCores) {
                    maxMemory = thisMemory;
                    maxCores = thisCores;
                    applicationId = report.getApplicationId();
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

    public AbstractYarnClusterDescriptor getYarnClusterDescriptor() {
        return yarnClusterDescriptor;
    }

    public org.apache.hadoop.conf.Configuration getHadoopConf() {
        return hadoopConf;
    }

    public void setHadoopConf(org.apache.hadoop.conf.Configuration hadoopConf) {
        this.hadoopConf = hadoopConf;
    }

    public org.apache.hadoop.conf.Configuration getYarnConf() {
        return yarnConf;
    }

    public void setYarnConf(YarnConfiguration yarnConf) {
        this.yarnConf = yarnConf;
    }

    public Configuration getFlinkConfiguration() {
        if (flinkConfiguration == null) {
            throw new RdosException("Configuration directory not set");
        }
        return flinkConfiguration;
    }

    public void setYarnClient(YarnClient yarnClient) {
        this.yarnClient = yarnClient;
    }
}
