package com.dtstack.engine.flink;

import com.dtstack.engine.base.util.HadoopConfTool;
import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.enums.ClusterMode;
import com.dtstack.engine.flink.util.HadoopConf;
import com.sun.istack.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.util.HadoopUtils;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  客户端需要的配置信息及YarnClient
 * Date: 2018/5/3
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class FlinkClientBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkClientBuilder.class);

    private FlinkConfig flinkConfig;

    private org.apache.hadoop.conf.Configuration hadoopConf;

    private YarnConfiguration yarnConf;

    private volatile YarnClient yarnClient;

    private Configuration flinkConfiguration;

    private ThreadPoolExecutor threadPoolExecutor;

    public FlinkClientBuilder(FlinkConfig flinkConfig, org.apache.hadoop.conf.Configuration hadoopConf, YarnConfiguration yarnConf) {
        this.hadoopConf = hadoopConf;
        this.yarnConf = yarnConf;
        this.flinkConfig = flinkConfig;
        if (!ClusterMode.STANDALONE.name().equalsIgnoreCase(flinkConfig.getClusterMode())) {
            this.yarnClient = buildYarnClient();
        }
        this.threadPoolExecutor = new ThreadPoolExecutor(this.flinkConfig.getAsyncCheckYarnClientThreadNum(), this.flinkConfig.getAsyncCheckYarnClientThreadNum(),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory("flink_yarnclient"));
    }

    public void initFlinkGlobalConfiguration(Properties extProp) {
        Configuration config = new Configuration();
        config.setString("akka.client.timeout", ConfigConstrant.AKKA_CLIENT_TIMEOUT);
        config.setString("akka.ask.timeout", ConfigConstrant.AKKA_ASK_TIMEOUT);
        config.setString("akka.tcp.timeout", ConfigConstrant.AKKA_TCP_TIMEOUT);

        config.setBytes(ConfigConstrant.HADOOP_CONF_BYTES_KEY, HadoopConfTool.serializeHadoopConf(hadoopConf));
        config.setBytes(ConfigConstrant.YARN_CONF_BYTES_KEY, HadoopConfTool.serializeHadoopConf(yarnConf));

        config.setLong("submitTimeout", 5);

        if (extProp != null) {
            for (Object key : extProp.keySet()) {
                String newKey = (String) key;
                String value = extProp.getProperty(newKey);
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                if (!FlinkConfig.getEngineFlinkConfigs().contains(key.toString())) {
                    config.setString(newKey, value);
                }
            }
        }

        config.setBoolean(ConfigConstrant.OPEN_KERBEROS_KEY, flinkConfig.isOpenKerberos());

        try {
            FileSystem.initialize(config);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }
        flinkConfiguration = config;
    }

    public static HadoopConf initHadoopConf(FlinkConfig flinkConfig) {
        HadoopConf customerConf = new HadoopConf();
        customerConf.initHadoopConf(flinkConfig.getHadoopConf());
        customerConf.initYarnConf(flinkConfig.getYarnConf());
        return customerConf;
    }

    /**
     * KerberosUtils.login 的地方才可以直接调用此方法，否则使用 buildYarnClient
     * @return
     */
    public YarnClient getYarnClient(){
        long startTime = System.currentTimeMillis();
        try {
            if (yarnClient == null) {
                synchronized (this) {
                    if (yarnClient == null) {
                        LOG.info("buildYarnClient!");
                        YarnClient yarnClient1 = YarnClient.createYarnClient();
                        yarnClient1.init(yarnConf);
                        yarnClient1.start();
                        yarnClient = yarnClient1;
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
            LOG.error("buildYarnClient![backup]", e);
            YarnClient yarnClient1 = YarnClient.createYarnClient();
            yarnClient1.init(yarnConf);
            yarnClient1.start();
            yarnClient = yarnClient1;
        } finally {
            long endTime= System.currentTimeMillis();
            LOG.info("cost getYarnClient start-time:{} end-time:{}, cost:{}.", startTime, endTime, endTime - startTime);
        }
        return yarnClient;
    }

    /**
     * 创建YarnClient 增加KerberosUtils 逻辑
     * @return
     */
    private YarnClient buildYarnClient() {
        try {
            return KerberosUtils.login(flinkConfig, () -> {
                LOG.info("buildYarnClient, init YarnClient!");
                YarnClient yarnClient1 = YarnClient.createYarnClient();
                yarnClient1.init(yarnConf);
                yarnClient1.start();
                return yarnClient1;
            }, yarnConf);
        } catch (Exception e) {
            LOG.error("buildYarnClient initSecurity happens error", e);
            throw new RdosDefineException(e);
        }
    }

    @NotNull
    public FlinkConfig getFlinkConfig() {
        return flinkConfig;
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
