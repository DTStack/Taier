package com.dtstack.engine.flink;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.ResourceManagerOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.kubeclient.Fabric8FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.KubeClientFactory;
import org.apache.flink.runtime.util.HadoopUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/04/03
 */
public class FlinkClientBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkClientBuilder.class);

    private FlinkConfig flinkConfig;

    private org.apache.hadoop.conf.Configuration hadoopConf;

    private FlinkKubeClient flinkKubeClient;

    private Configuration flinkConfiguration;

    public FlinkClientBuilder(FlinkConfig flinkConfig, org.apache.hadoop.conf.Configuration hadoopConf, Properties extProp) {
        this.flinkConfig = flinkConfig;
        this.hadoopConf = hadoopConf;
        this.flinkConfiguration = initFlinkGlobalConfiguration(extProp);

        this.flinkConfiguration.set(KubernetesConfigOptions.NAMESPACE, flinkConfig.getNamespace());
        this.flinkKubeClient = KubeClientFactory.fromConfiguration(flinkConfiguration);

    }

    private Configuration initFlinkGlobalConfiguration(Properties extProp) {
        Configuration config = new Configuration();
        config.setString("akka.client.timeout", ConfigConstrant.AKKA_CLIENT_TIMEOUT);
        config.setString("akka.ask.timeout", ConfigConstrant.AKKA_ASK_TIMEOUT);
        config.setString("akka.tcp.timeout", ConfigConstrant.AKKA_TCP_TIMEOUT);
        // JVM Param
        config.setString(CoreOptions.FLINK_JVM_OPTIONS, ConfigConstrant.JVM_OPTIONS);

        // hadoop
        config.setBytes(HadoopUtils.HADOOP_CONF_BYTES, HadoopUtils.serializeHadoopConf(hadoopConf));

        String hadoopConfDir = config.getString(KubernetesConfigOptions.HADOOP_CONF_DIR);
        config.setString(ResourceManagerOptions.CONTAINERIZED_MASTER_ENV_PREFIX + ConfigConstrant.HADOOP_CONF_DIR, hadoopConfDir);
        config.setString(ResourceManagerOptions.CONTAINERIZED_TASK_MANAGER_ENV_PREFIX + ConfigConstrant.HADOOP_CONF_DIR, hadoopConfDir);

        String hadoopUserName = config.getString(ConfigConstrant.HADOOP_USER_NAME, "");
        if (StringUtils.isBlank(hadoopUserName)) {
            hadoopUserName = System.getenv(ConfigConstrant.HADOOP_USER_NAME);
            if (StringUtils.isBlank(hadoopUserName)) {
                hadoopUserName = System.getProperty(ConfigConstrant.HADOOP_USER_NAME);
            }
        }
        config.setString(ResourceManagerOptions.CONTAINERIZED_MASTER_ENV_PREFIX + ConfigConstrant.HADOOP_USER_NAME, hadoopUserName);
        config.setString(ResourceManagerOptions.CONTAINERIZED_TASK_MANAGER_ENV_PREFIX + ConfigConstrant.HADOOP_USER_NAME, hadoopUserName);

        LOG.info("hadoop env info, {}:{} {}:{}", ConfigConstrant.HADOOP_CONF_DIR, hadoopConfDir, ConfigConstrant.HADOOP_USER_NAME, hadoopUserName);

        if (extProp != null) {
            extProp.forEach((key, value) -> {
                String v = value == null? "": value.toString();
                if (Objects.nonNull(key) && StringUtils.isNotEmpty(v)) {
                    config.setString(key.toString(), value.toString());
                }
            });
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String hadoopConfString = "";
            if(extProp!=null) {
                hadoopConfString = objectMapper.writeValueAsString(extProp.get("hadoopConf"));
            }
            config.setString(HadoopUtils.HADOOP_CONF_STRING, hadoopConfString);
            FileSystem.initialize(config);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }

        return config;
    }

    public FlinkConfig getFlinkConfig() {
        return flinkConfig;
    }

    public Configuration getFlinkConfiguration() {
        if (flinkConfiguration == null) {
            throw new RdosDefineException("Configuration directory not set");
        }
        return flinkConfiguration;
    }


    public KubernetesClient getKubernetesClient() {
        if (null == flinkKubeClient) {
            this.flinkKubeClient = KubeClientFactory.fromConfiguration(flinkConfiguration);
        }
        return ((Fabric8FlinkKubeClient) flinkKubeClient).getInternalClient();
    }

    public FlinkKubeClient getFlinkKubeClient() {
        if (null == flinkKubeClient) {
            this.flinkKubeClient = KubeClientFactory.fromConfiguration(flinkConfiguration);
        }
        return flinkKubeClient;
    }

}
