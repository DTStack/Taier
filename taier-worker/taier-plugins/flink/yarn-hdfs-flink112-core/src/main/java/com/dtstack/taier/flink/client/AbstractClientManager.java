package com.dtstack.taier.flink.client;


import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.flink.base.enums.ClusterMode;
import com.dtstack.taier.flink.config.FlinkConfig;
import com.dtstack.taier.flink.config.HadoopConfig;
import com.dtstack.taier.flink.constant.ConfigConstant;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.util.RetryUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: engine-plugins
 * @author: xiuzhu
 * @create: 2021/07/15
 */

public class AbstractClientManager implements IClientManager {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractClientManager.class);

    /**
     * original properties from console
     */
    private Properties flinkExtProp;

    /**
     * dtstack-defined configuration
     */
    protected FlinkConfig flinkConfig;

    /**
     * flink-defined configuration
     */
    protected Configuration flinkConfiguration;

    protected HadoopConfig hadoopConfig;

    private volatile YarnClient yarnClient;

    private final ThreadPoolExecutor threadPoolExecutor;

    public AbstractClientManager(FlinkConfig flinkConfig, HadoopConfig hadoopConf) {
        this.hadoopConfig = hadoopConf;
        this.flinkConfig = flinkConfig;
        flinkConfiguration = new Configuration();
        if (!ClusterMode.STANDALONE.name().equalsIgnoreCase(flinkConfig.getClusterMode())) {
            this.yarnClient = buildYarnClient();
        }
        this.threadPoolExecutor = new ThreadPoolExecutor(this.flinkConfig.getAsyncCheckYarnClientThreadNum(), this.flinkConfig.getAsyncCheckYarnClientThreadNum(),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("flink_yarnclient"));
    }

    @Override
    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {
        throw new PluginDefineException("subClass must override getClusterClient method");
    }

    /**
     * SecurityUtils.login 的地方才可以直接调用此方法，否则使用 buildYarnClient
     */
    @Override
    public YarnClient getYarnClient(){
        long startTime = System.currentTimeMillis();
        try {
            if (yarnClient == null) {
                synchronized (this) {
                    if (yarnClient == null) {
                        LOG.info("build Yarn Client!");
                        YarnClient tempYarnClient = YarnClient.createYarnClient();
                        tempYarnClient.init(hadoopConfig.getYarnConfiguration());
                        tempYarnClient.start();
                        yarnClient = tempYarnClient;
                    }
                }
            } else {
                //异步超时判断下是否可用，kerberos 开启下会出现hang死情况
                RetryUtil.asyncExecuteWithRetry(() -> yarnClient.getAllQueues(),
                        1,
                        0,
                        false,
                        30000L,
                        threadPoolExecutor);
            }
        } catch (Throwable e) {
            LOG.error("build YarnClient failed![backup]", e);
            YarnClient tempYarnClient = YarnClient.createYarnClient();
            tempYarnClient.init(hadoopConfig.getYarnConfiguration());
            tempYarnClient.start();
            yarnClient = tempYarnClient;
        } finally {
            long endTime= System.currentTimeMillis();
            LOG.info("cost getYarnClient start-time:{} end-time:{}, cost:{}.", startTime, endTime, endTime - startTime);
        }
        return yarnClient;
    }

    /**
     * 创建YarnClient 增加KerberosUtils 逻辑
     */
    private YarnClient buildYarnClient() {
        try {
            return KerberosUtils.login(flinkConfig, () -> {
                LOG.info("buildYarnClient, init YarnClient!");
                YarnClient tempYarnClient = YarnClient.createYarnClient();
                tempYarnClient.init(hadoopConfig.getYarnConfiguration());
                tempYarnClient.start();
                return tempYarnClient;
            }, hadoopConfig.getYarnConfiguration());
        } catch (Exception e) {
            LOG.error("buildYarnClient initSecurity happens error", e);
            throw new PluginDefineException(e);
        }
    }

    public FlinkConfig getFlinkConfig() {
        return flinkConfig;
    }

    public void setFlinkConfig(FlinkConfig flinkConfig) {
        this.flinkConfig = flinkConfig;
    }

    public Configuration getFlinkConfiguration() {
        return flinkConfiguration;
    }

    public void addFlinkConfiguration(Configuration flinkGlobalConfiguration) {
        this.flinkConfiguration.addAll(flinkGlobalConfiguration);
    }

    public HadoopConfig getHadoopConfig() {
        return hadoopConfig;
    }

    public void setHadoopConfig(HadoopConfig hadoopConfig) {
        this.hadoopConfig = hadoopConfig;
    }

    /**
     * 从flink lib中找到dist jar
     */
    public List<URL> getFlinkJarFile(String flinkJarPath, YarnClusterDescriptor clusterDescriptor) throws MalformedURLException {
        List<URL> classPaths = new ArrayList<>();
        if (flinkJarPath != null) {
            File flinkDir = new File(flinkJarPath);
            if(flinkDir.exists() && flinkDir.isDirectory()){
                File[] jars = flinkDir.listFiles();
                if(null == jars){
                    throw new PluginDefineException("could not find any jar file from lib dir");
                }
                for (File file : jars) {
                    if (file.toURI().toURL().toString().contains("flink-dist")) {
                        clusterDescriptor.setLocalJarPath(new Path(file.toURI().toURL().toString()));
                    } else {
                        classPaths.add(file.toURI().toURL());
                    }
                }
            }

        } else {
            throw new PluginDefineException("The Flink jar path is null");
        }
        return classPaths;
    }

    public YarnClusterDescriptor getClusterDescriptor(Configuration configuration, YarnConfiguration yarnConfiguration) {
        // add yarn queue configure
        configuration.setString(YarnConfigOptions.APPLICATION_QUEUE.key(), flinkConfig.getQueue());

        YarnClusterClientFactory clusterClientFactory = new YarnClusterClientFactory();

        YarnConfiguration newYarnConfig = new YarnConfiguration();
        for (Map.Entry<String, String> next : yarnConfiguration) {
            newYarnConfig.set(next.getKey(), next.getValue());
        }
        return clusterClientFactory.createClusterDescriptor(configuration, newYarnConfig);
    }

    /**
     * 插件包及Lib包提前上传至HDFS，设置远程HDFS路径参数
     * {@link com.dtstack.taier.flink.config.FlinkConfig}
     */
    public Configuration setHdfsFlinkJarPath(FlinkConfig flinkConfig, Configuration flinkConfiguration){
        //检查HDFS上是否已经上传插件包及Lib包
        String remoteFlinkLibDir = flinkConfig.getRemoteFlinkLibDir();
        //remotePluginRootDir默认不为空
        String remoteChunjunDistDir = flinkConfig.getRemoteChunjunDistDir();
        //不考虑二者只有其一上传到了hdfs上的情况
        if(StringUtils.startsWith(remoteFlinkLibDir, ConfigConstant.PREFIX_HDFS) && StringUtils.startsWith(remoteChunjunDistDir, ConfigConstant.PREFIX_HDFS)){
            flinkConfiguration.setString(ConfigConstant.REMOTE_FLINK_LIB_DIR, remoteFlinkLibDir);
            flinkConfiguration.setString(ConfigConstant.REMOTE_CHUNJUN_DIST_DIR, remoteChunjunDistDir);
            flinkConfiguration.setString(ConfigConstant.FLINK_LIB_DIR, flinkConfig.getFlinkLibDir());
            flinkConfiguration.setString(ConfigConstant.CHUNJUN_DIST_DIR, flinkConfig.getChunjunDistDir());
        }
        return flinkConfiguration;
    }
}
