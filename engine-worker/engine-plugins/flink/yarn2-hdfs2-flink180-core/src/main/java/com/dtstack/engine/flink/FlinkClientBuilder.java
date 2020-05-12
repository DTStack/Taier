package com.dtstack.engine.flink;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.enums.Deploy;
import com.dtstack.engine.flink.util.KerberosUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.runtime.util.HadoopUtils;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  客户端需要的参数及组件
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

    private static String jvm_options = "-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing";

    private static final String DIR = "/keytab/";

    private static final String USER_DIR = System.getProperty("user.dir");

    private FlinkConfig flinkConfig;

    private org.apache.hadoop.conf.Configuration hadoopConf;

    private YarnConfiguration yarnConf;

    private volatile YarnClient yarnClient;

    private Configuration flinkConfiguration;

    public static FlinkClientBuilder create(FlinkConfig flinkConfig, org.apache.hadoop.conf.Configuration hadoopConf, YarnConfiguration yarnConf) throws IOException {
        FlinkClientBuilder builder = new FlinkClientBuilder();
        builder.flinkConfig = flinkConfig;
        builder.hadoopConf = hadoopConf;
        builder.yarnConf = yarnConf;

        if (flinkConfig.isOpenKerberos()) {
            initSecurity(flinkConfig);
        }
        if (Deploy.yarn.name().equalsIgnoreCase(flinkConfig.getClusterMode())) {
            builder.yarnClient = initYarnClient(yarnConf);
        }
        return builder;
    }

    public void initFlinkConfiguration(Properties extProp) {
        Configuration config = new Configuration();
        config.setString("akka.client.timeout", AKKA_CLIENT_TIMEOUT);
        config.setString("akka.ask.timeout", AKKA_ASK_TIMEOUT);
        config.setString("akka.tcp.timeout", AKKA_TCP_TIMEOUT);
        // JVM Param
        config.setString(CoreOptions.FLINK_JVM_OPTIONS, jvm_options);
        config.setBytes(HadoopUtils.HADOOP_CONF_BYTES, HadoopUtils.serializeHadoopConf(hadoopConf));
        config.setLong("submitTimeout", 5);

        if (extProp != null) {
            extProp.forEach((key, value) -> {
                if (!FlinkConfig.getEngineFlinkConfigs().contains(key.toString())) {
                    config.setString(key.toString(), value.toString());
                }
            });
        }

        try {
            FileSystem.initialize(config);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }

        flinkConfiguration = config;
    }

    public AbstractYarnClusterDescriptor createYarnSessionClusterDescriptor() throws MalformedURLException {
        Configuration newConf = new Configuration(flinkConfiguration);
        String flinkJarPath = flinkConfig.getFlinkJarPath();
        String pluginLoadMode = flinkConfig.getPluginLoadMode();

        checkFileExist(flinkJarPath);

        if (!flinkConfig.getFlinkHighAvailability()) {
            setNoneHaModeConfig(newConf);
        } else {
            //由engine管控的yarnsession clusterId不进行设置，默认使用appId作为clusterId
            newConf.removeConfig(HighAvailabilityOptions.HA_CLUSTER_ID);
        }

        AbstractYarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, yarnConf, ".");

        if (StringUtils.isNotBlank(pluginLoadMode) && ConfigConstrant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(pluginLoadMode)) {
            newConf.setString(ConfigConstrant.FLINK_PLUGIN_LOAD_MODE, flinkConfig.getPluginLoadMode());
            newConf.setString("classloader.resolve-order", "parent-first");

            String flinkPluginRoot = flinkConfig.getFlinkPluginRoot();
            if (StringUtils.isNotBlank(flinkPluginRoot)) {
                String syncPluginDir = flinkPluginRoot + SyncPluginInfo.FILE_SP + SyncPluginInfo.SYNC_PLUGIN_DIR_NAME;
                List<File> pluginPaths = Arrays.stream(new File(syncPluginDir).listFiles()).collect(Collectors.toList());
                clusterDescriptor.addShipFiles(pluginPaths);
            }
        }

        List<URL> classpaths = getFlinkJarFile(flinkJarPath, clusterDescriptor);
        clusterDescriptor.setProvidedUserJarFiles(classpaths);
        clusterDescriptor.setQueue(flinkConfig.getQueue());
        return clusterDescriptor;
    }

    public AbstractYarnClusterDescriptor createPerJobClusterDescriptor(JobClient jobClient) throws MalformedURLException {
        String flinkJarPath = flinkConfig.getFlinkJarPath();
        checkFileExist(flinkJarPath);

        Configuration newConf = new Configuration(flinkConfiguration);
        newConf = appendConfigAndInitFs(jobClient, newConf);

        AbstractYarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, yarnConf, ".");

        List<URL> classpaths = getFlinkJarFile(flinkJarPath, clusterDescriptor);

        if (CollectionUtils.isNotEmpty(jobClient.getAttachJarInfos())) {
            for (JarFileInfo jarFileInfo : jobClient.getAttachJarInfos()) {
                classpaths.add(new File(jarFileInfo.getJarPath()).toURI().toURL());
            }
        }
        List<File> keytabFilePath = getKeytabFilePath(jobClient);

        clusterDescriptor.addShipFiles(keytabFilePath);
        clusterDescriptor.setProvidedUserJarFiles(classpaths);
        clusterDescriptor.setQueue(flinkConfig.getQueue());
        return clusterDescriptor;
    }

    public AbstractYarnClusterDescriptor getClusterDescriptor(
            Configuration configuration,
            YarnConfiguration yarnConfiguration,
            String configurationDirectory) {
        return new YarnClusterDescriptor(
                configuration,
                yarnConfiguration,
                configurationDirectory,
                getYarnClient(),
                true);
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

    private void checkFileExist(String flinkJarPath) {
        if (StringUtils.isNotBlank(flinkJarPath)) {
            if (!new File(flinkJarPath).exists()) {
                throw new RdosDefineException(String.format("The Flink jar %s  path is not exist ", flinkConfig.getFlinkJarPath()));
            }
        }
    }

    private List<URL> getFlinkJarFile(String flinkJarPath, AbstractYarnClusterDescriptor clusterDescriptor) throws MalformedURLException {
        List<URL> classpaths = new ArrayList<>();
        if (flinkJarPath != null) {
            File[] jars = new File(flinkJarPath).listFiles();
            for (File file : jars) {
                if (file.toURI().toURL().toString().contains("flink-dist")) {
                    clusterDescriptor.setLocalJarPath(new Path(file.toURI().toURL().toString()));
                } else {
                    classpaths.add(file.toURI().toURL());
                }
            }
        } else {
            throw new RdosDefineException("The Flink jar path is null");
        }
        return classpaths;
    }

    private List<File> getKeytabFilePath(JobClient jobClient) {
        List<File> keytabs = Lists.newLinkedList();
        String keytabDir = USER_DIR + DIR + jobClient.getTaskId();
        File keytabDirName = new File(keytabDir);
        File[] files = keytabDirName.listFiles();

        if (flinkConfig.isOpenKerberos() && keytabDirName.isDirectory() && files.length > 0) {
            for (File file : files) {
                keytabs.add(file);
            }
            return keytabs;
        }
        return keytabs;
    }

    private Configuration appendConfigAndInitFs(JobClient jobClient, Configuration configuration) {
        Properties properties = jobClient.getConfProperties();
        if (properties != null) {
            properties.stringPropertyNames()
                    .stream()
                    .filter(key -> key.toString().contains("."))
                    .forEach(key -> configuration.setString(key.toString(), properties.getProperty(key)));
        }

        if (!flinkConfig.getFlinkHighAvailability() && ComputeType.BATCH == jobClient.getComputeType()) {
            setNoneHaModeConfig(configuration);
        } else {
            configuration.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.ZOOKEEPER.toString());
            configuration.setString(HighAvailabilityOptions.HA_CLUSTER_ID, jobClient.getTaskId());
        }

        configuration.setInteger(YarnConfigOptions.APPLICATION_ATTEMPTS.key(), 0);

        if (StringUtils.isNotBlank(flinkConfig.getPluginLoadMode()) && ConfigConstrant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(flinkConfig.getPluginLoadMode())) {
            configuration.setString(ConfigConstrant.FLINK_PLUGIN_LOAD_MODE, flinkConfig.getPluginLoadMode());
            configuration.setString("classloader.resolve-order", "parent-first");
        }

        try {
            FileSystem.initialize(configuration);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }
        return configuration;
    }

    private static void initSecurity(FlinkConfig flinkConfig) throws IOException {
        try {
            LOG.info("start init security!");
            KerberosUtils.login(flinkConfig);
        } catch (IOException e) {
            LOG.error("initSecurity happens error", e);
            throw new IOException("InitSecurity happens error", e);
        }
        LOG.info("UGI info: " + UserGroupInformation.getCurrentUser());
    }

    private static YarnClient initYarnClient(YarnConfiguration yarnConf) throws IOException {
        YarnClient yarnClient = YarnClient.createYarnClient();
        yarnClient.init(yarnConf);
        yarnClient.start();
        return yarnClient;
    }

    public YarnClient getYarnClient() {
        try {
            if (yarnClient == null) {
                synchronized (this) {
                    if (yarnClient == null) {
                        YarnClient yarnClient1 = YarnClient.createYarnClient();
                        yarnClient1.init(yarnConf);
                        yarnClient1.start();
                        yarnClient = yarnClient1;
                    }
                }
            } else {
                //判断下是否可用
                yarnClient.getAllQueues();
            }
        } catch (Throwable e) {
            LOG.error("getYarnClient error:{}", e);
            synchronized (this) {
                if (yarnClient != null) {
                    boolean flag = true;
                    try {
                        //判断下是否可用
                        yarnClient.getAllQueues();
                    } catch (Throwable e1) {
                        LOG.error("getYarnClient error:{}", e1);
                        flag = false;
                    }
                    if (!flag) {
                        try {
                            yarnClient.stop();
                        } finally {
                            yarnClient = null;
                        }
                    }
                }
                if (yarnClient == null) {
                    YarnClient yarnClient1 = YarnClient.createYarnClient();
                    yarnClient1.init(yarnConf);
                    yarnClient1.start();
                    yarnClient = yarnClient1;
                }
            }
        }
        return yarnClient;
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

    public Configuration getFlinkConfiguration() {
        if (flinkConfiguration == null) {
            throw new RdosDefineException("Configuration directory not set");
        }
        return flinkConfiguration;
    }
}
