package com.dtstack.engine.flink;

import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.enums.ClusterMode;
import com.dtstack.engine.flink.util.HadoopConf;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.SecurityOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.util.HadoopUtils;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    private static String jvm_options = "-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing";

    private FlinkConfig flinkConfig;

    private org.apache.hadoop.conf.Configuration hadoopConf;

    private YarnConfiguration yarnConf;

    private YarnClient yarnClient;

    private Configuration flinkConfiguration;

    public static FlinkClientBuilder create(FlinkConfig flinkConfig, org.apache.hadoop.conf.Configuration hadoopConf, YarnConfiguration yarnConf) throws Exception {
        FlinkClientBuilder builder = new FlinkClientBuilder();
        builder.hadoopConf = hadoopConf;
        builder.yarnConf = yarnConf;

        KerberosUtils.login(flinkConfig, () -> {
            if (!ClusterMode.STANDALONE.name().equalsIgnoreCase(flinkConfig.getClusterMode())) {
                try {
                    builder.yarnClient = initYarnClient(yarnConf);
                } catch (Exception e) {
                    LOG.error("init  yarn client error", e);
                    throw new RdosDefineException(e);
                }
            }
            return null;
        }, yarnConf);
        builder.flinkConfig = flinkConfig;

        return builder;
    }

    public void initFlinkGlobalConfiguration(Properties extProp) {
        Configuration config = new Configuration();
        config.setString("akka.client.timeout", AKKA_CLIENT_TIMEOUT);
        config.setString("akka.ask.timeout", AKKA_ASK_TIMEOUT);
        config.setString("akka.tcp.timeout", AKKA_TCP_TIMEOUT);
        // JVM Param
        config.setString(CoreOptions.FLINK_JVM_OPTIONS, jvm_options);
        config.setBytes(HadoopUtils.HADOOP_CONF_BYTES, HadoopUtils.serializeHadoopConf(hadoopConf));
        // yarn queue
        config.setString(YarnConfigOptions.APPLICATION_QUEUE, flinkConfig.getQueue());

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

    public void setSecurityConfig() {
        String keytabPath = flinkConfig.getPrincipalPath();
        String principal = flinkConfig.getPrincipalName();
        flinkConfiguration.setString(SecurityOptions.KERBEROS_LOGIN_KEYTAB, keytabPath);
        flinkConfiguration.setString(SecurityOptions.KERBEROS_LOGIN_PRINCIPAL, principal);
    }

    public static HadoopConf initHadoopConf(FlinkConfig flinkConfig) {
        HadoopConf customerConf = new HadoopConf();
        customerConf.initHadoopConf(flinkConfig.getHadoopConf());
        customerConf.initYarnConf(flinkConfig.getYarnConf());
        return customerConf;
    }

    private static YarnClient initYarnClient(YarnConfiguration yarnConf) throws IOException {
        YarnClient yarnClient = YarnClient.createYarnClient();
        yarnClient.init(yarnConf);
        yarnClient.start();
        return yarnClient;
    }

    public FlinkConfig getFlinkConfig() {
        return flinkConfig;
    }

    public YarnConfiguration getYarnConf() {
        return yarnConf;
    }

    public YarnClient getYarnClient() {
        try {
            if (yarnClient == null) {
                synchronized (this) {
                    if (yarnClient == null) {
                        return buildYarnClient();
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
                    return buildYarnClient();
                }
            }
        }
        return yarnClient;
    }

    public YarnClient buildYarnClient() {
        try {
            return KerberosUtils.login(flinkConfig, () -> {
                YarnClient yarnClient1 = YarnClient.createYarnClient();
                yarnClient1.init(yarnConf);
                yarnClient1.start();
                yarnClient = yarnClient1;
                return yarnClient;
            },yarnConf);
        } catch (Exception e) {
            throw new RdosDefineException("build yarn client error", e);
        }
    }

    public Configuration getFlinkConfiguration() {
        if (flinkConfiguration == null) {
            throw new RdosDefineException("Configuration directory not set");
        }
        return flinkConfiguration;
    }

}
