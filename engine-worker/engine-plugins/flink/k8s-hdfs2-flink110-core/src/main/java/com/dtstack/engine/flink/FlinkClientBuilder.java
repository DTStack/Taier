package com.dtstack.engine.flink;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.enums.FlinkMode;
import com.dtstack.engine.flink.util.FlinkUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.ClusterDescriptor;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.configuration.ResourceManagerOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.kubernetes.KubernetesClusterClientFactory;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.kubeclient.Fabric8FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.KubeClientFactory;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.runtime.util.HadoopUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/04/03
 */
public class FlinkClientBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkClientBuilder.class);

    private final static String AKKA_ASK_TIMEOUT = "50 s";

    private final static String AKKA_CLIENT_TIMEOUT = "300 s";

    private final static String AKKA_TCP_TIMEOUT = "60 s";

    private final static String JVM_OPTIONS = "-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing";

    private FlinkConfig flinkConfig;

    private org.apache.hadoop.conf.Configuration hadoopConf;

    private FlinkKubeClient flinkKubeClient;

    private Configuration flinkConfiguration;

    public FlinkClientBuilder(FlinkConfig flinkConfig, org.apache.hadoop.conf.Configuration hadoopConf, Properties extProp) {
        this.flinkConfig = flinkConfig;
        this.hadoopConf = hadoopConf;
        this.flinkConfiguration = initFLinkConfiguration(extProp);

        String defaultClusterId = flinkConfig.getFlinkSessionName() + ConfigConstrant.CLUSTER_ID_SPLIT + flinkConfig.getCluster() + ConfigConstrant.CLUSTER_ID_SPLIT + flinkConfig.getQueue();
        if (!flinkConfiguration.contains(KubernetesConfigOptions.CLUSTER_ID)) {
            flinkConfiguration.setString(KubernetesConfigOptions.CLUSTER_ID, defaultClusterId.toLowerCase());
        }
        this.flinkKubeClient = KubeClientFactory.fromConfiguration(flinkConfiguration);
    }

    private Configuration initFLinkConfiguration(Properties extProp) {
        Configuration config = new Configuration();
        config.setString("akka.client.timeout", AKKA_CLIENT_TIMEOUT);
        config.setString("akka.ask.timeout", AKKA_ASK_TIMEOUT);
        config.setString("akka.tcp.timeout", AKKA_TCP_TIMEOUT);
        // JVM Param
        config.setString(CoreOptions.FLINK_JVM_OPTIONS, JVM_OPTIONS);


        // hadoop
        config.setBytes(HadoopUtils.HADOOP_CONF_BYTES, HadoopUtils.serializeHadoopConf(hadoopConf));

        String hadoopConfDir = config.getString(KubernetesConfigOptions.HADOOP_CONF_DIR);
        config.setString(ResourceManagerOptions.CONTAINERIZED_MASTER_ENV_PREFIX + ConfigConstrant.HADOOP_CONF_DIR, hadoopConfDir);
        config.setString(ResourceManagerOptions.CONTAINERIZED_TASK_MANAGER_ENV_PREFIX + ConfigConstrant.HADOOP_CONF_DIR, hadoopConfDir);

        String hadoopUserName = config.getString(ConfigConstrant.HADOOP_USER_NAME, "");
        if (StringUtils.isBlank(hadoopUserName)) {
            hadoopUserName = System.getenv(ConfigConstrant.HADOOP_USER_NAME);
        }
        config.setString(ResourceManagerOptions.CONTAINERIZED_MASTER_ENV_PREFIX + ConfigConstrant.HADOOP_USER_NAME, hadoopUserName);
        config.setString(ResourceManagerOptions.CONTAINERIZED_TASK_MANAGER_ENV_PREFIX + ConfigConstrant.HADOOP_USER_NAME, hadoopUserName);

        LOG.info("hadoop env info, {}:{} {}:{}", ConfigConstrant.HADOOP_CONF_DIR, hadoopConfDir, ConfigConstrant.HADOOP_USER_NAME, hadoopUserName);

        if (extProp != null) {
            extProp.forEach((key, value) -> {
                if (!FlinkConfig.getEngineFlinkConfigs().contains(key.toString())) {
                    config.setString(key.toString(), value.toString());
                }
            });
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String hadoopConfString = objectMapper.writeValueAsString(extProp.get("hadoopConf"));
            config.setString(HadoopUtils.HADOOP_CONF_STRING, hadoopConfString);
            FileSystem.initialize(config);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }

        return config;
    }

    /**
     * 获取ClusterClient
     */
    public ClusterClient<String> initClusterClient() {

        Configuration newConf = new Configuration(flinkConfiguration);

        if (!flinkConfig.getFlinkHighAvailability()) {
            setNoneHaModeConfig(newConf);
        }

        KubernetesClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf);

        String clusterId = acquireAppIdAndSetClusterId(newConf);

        ClusterClient<String> clusterClient = null;
        if (clusterId != null && flinkKubeClient.getInternalService(clusterId) != null) {
            ClusterClientProvider<String> clusterClientProvider = clusterDescriptor.retrieve(clusterId);
            clusterClient = clusterClientProvider.getClusterClient();

            LOG.warn("---init flink client with session on Kubernetes success----");

            return clusterClient;
        }

        LOG.info("No flink session, Couldn't retrieve Kubernetes cluster.");
        return null;
    }

    public ClusterDescriptor<String> createClusterDescriptorByMode(JobClient jobClient, Configuration configuration, boolean isPerjob) throws MalformedURLException {
        if (configuration == null) {
            configuration = flinkConfiguration;
        }
        Configuration newConf = new Configuration(configuration);
        if (isPerjob && jobClient != null) {
            newConf = addConfiguration(jobClient.getConfProperties(), newConf);
            if (!flinkConfig.getFlinkHighAvailability() && ComputeType.BATCH == jobClient.getComputeType()) {
                setNoneHaModeConfig(newConf);
            } else {
                String projobClusterId = String.format("%s-%s", "flinkperjob", jobClient.getTaskId());
                newConf.setString(KubernetesConfigOptions.CLUSTER_ID, projobClusterId);
            }
        } else if (!isPerjob) {
            if (!flinkConfig.getFlinkHighAvailability()) {
                setNoneHaModeConfig(newConf);
            } else {
                //由engine管控的session clusterId不进行设置，默认使用appId作为clusterId
                newConf.removeConfig(HighAvailabilityOptions.HA_CLUSTER_ID);
            }
        }

        KubernetesClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf);

        // plugin dependent on shipfile
        if (StringUtils.isNotBlank(flinkConfig.getPluginLoadMode()) && ConfigConstrant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(flinkConfig.getPluginLoadMode())) {
            newConf.setString(ConfigConstrant.FLINK_PLUGIN_LOAD_MODE, flinkConfig.getPluginLoadMode());
            newConf.setString("classloader.resolve-order", "parent-first");
        }
        return clusterDescriptor;
    }


    private KubernetesClusterDescriptor getClusterDescriptor(Configuration configuration) {

        KubernetesClusterClientFactory clusterClientFactory = new KubernetesClusterClientFactory();
        return clusterClientFactory.createClusterDescriptor(configuration);
    }

    private String acquireAppIdAndSetClusterId(Configuration configuration) {
        try {
            String clusterId = configuration.get(KubernetesConfigOptions.CLUSTER_ID);
            return clusterId;
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }
    }

    /**
     * set the copy of configuration
     */
    private void setNoneHaModeConfig(Configuration configuration) {
        configuration.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.NONE.toString());
        configuration.removeConfig(HighAvailabilityOptions.HA_CLUSTER_ID);
        configuration.removeConfig(HighAvailabilityOptions.HA_ZOOKEEPER_ROOT);
        configuration.removeConfig(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM);
    }

    public FlinkConfig getFlinkConfig() {
        return flinkConfig;
    }

    public org.apache.hadoop.conf.Configuration getHadoopConf() {
        return hadoopConf;
    }

    public Configuration getFlinkConfiguration() {
        if (flinkConfiguration == null) {
            throw new RdosDefineException("Configuration directory not set");
        }
        return flinkConfiguration;
    }

    private Configuration addConfiguration(Properties properties, Configuration configuration) {
        if (properties != null) {
            properties.forEach((key, value) -> {
                if (key.toString().contains(".")) {
                    configuration.setString(key.toString(), value.toString());
                }
            });
        }
        try {
            FileSystem.initialize(configuration);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }

        return configuration;
    }

    public KubernetesClient getKubernetesClient() {
        return ((Fabric8FlinkKubeClient) flinkKubeClient).getInternalClient();
    }

    public FlinkKubeClient getFlinkKubeClient() {
        return flinkKubeClient;
    }

}
