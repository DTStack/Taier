package com.dtstack.engine.flink.storage;

import com.dtstack.engine.base.util.HadoopConfTool;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.factory.PerJobClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.ResourceManagerOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.runtime.util.HadoopUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

public class HdfsStorage extends AbstractStorage {

    private static final Logger LOG = LoggerFactory.getLogger(PerJobClientFactory.class);

    private Configuration configuration;

    private Map<String, Object> hadoopConfMap;

    @Override
    public void init(Properties pluginInfo) {
        hadoopConfMap = (Map<String, Object>) pluginInfo.get("hadoopConf");
        initHadoopConf(hadoopConfMap);
    }

    public void initHadoopConf(Map<String, Object> conf){
        if (conf == null || conf.size() == 0) {
            throw new RdosDefineException("No set hdfs config!");
        }
        configuration = new Configuration();
        HadoopConfTool.setFsHdfsImplDisableCache(configuration);
        conf.keySet().forEach(key ->{
            Object value = conf.get(key);
            if (value instanceof String){
                configuration.set(key, (String) value);
            } else if (value instanceof Boolean){
                configuration.setBoolean(key, (boolean) value);
            }
        });
    }

    @Override
    public void fillStorageConfig(org.apache.flink.configuration.Configuration config, FlinkConfig flinkConfig) {
        // hadoop
        config.setBytes(HadoopUtils.HADOOP_CONF_BYTES, HadoopUtils.serializeHadoopConf(configuration));

        // set hadoop conf dir
        String hadoopConfDir = config.getString(KubernetesConfigOptions.HADOOP_CONF_DIR);
        config.setString(ResourceManagerOptions.CONTAINERIZED_MASTER_ENV_PREFIX + ConfigConstrant.HADOOP_CONF_DIR, hadoopConfDir);
        config.setString(ResourceManagerOptions.CONTAINERIZED_TASK_MANAGER_ENV_PREFIX + ConfigConstrant.HADOOP_CONF_DIR, hadoopConfDir);

        // set hadoop name
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

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String hadoopConfString = objectMapper.writeValueAsString(hadoopConfMap);
            config.setString(HadoopUtils.HADOOP_CONF_STRING, hadoopConfString);
            FileSystem.initialize(config);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public Configuration getStorageConfig() {
        return configuration;
    }

}
