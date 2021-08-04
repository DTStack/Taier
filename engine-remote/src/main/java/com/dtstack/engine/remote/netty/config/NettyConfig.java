package com.dtstack.engine.remote.netty.config;

import com.dtstack.engine.remote.akka.config.AkkaConfig;
import com.dtstack.engine.remote.akka.config.AkkaLoad;
import com.dtstack.engine.remote.akka.constant.AkkaConfigConstant;
import com.dtstack.engine.remote.akka.constant.GlobalConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Auther: dazhi
 * @Date: 2021/7/30 4:41 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NettyConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyConfig.class);
    private static Environment environment;
    private static ApplicationContext applicationContext;
    private static final AtomicBoolean load = new AtomicBoolean(Boolean.FALSE);
    private static Properties config = new Properties();

    public static void init(Environment environment, ApplicationContext applicationContext) {
        NettyConfig.applicationContext = applicationContext;
        NettyConfig.environment = environment;
        init();
        load.set(Boolean.TRUE);
    }

    public static void init() {
        String property = environment.getProperty(AkkaConfigConstant.CONFIG_PATH);
        if (StringUtils.isBlank(property)) {
            // 不配置配置文件，默认{user.dir}/conf下文件 application-common.properties 和 application.properties
            loadProperties(environment.getProperty(GlobalConstant.BASE_PATH));
            return;
        }

        loadProperties(property);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private static void loadProperties(String basePath) {
        if (StringUtils.isBlank(basePath)) {
            return;
        }

        String property = System.getProperty(GlobalConstant.REMOTE_PROPERTIES_FILE_NAME);

        String[] split = property.split(",");
        for (String fileName : split) {
            try {
                config.load(new FileInputStream(basePath + "/" + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getValueWithDefault(String configKey, String defaultValue) {
        String configValue = null;
        configValue = config.getProperty(configKey);
        if (StringUtils.isBlank(configValue)) {
            configValue = environment.getProperty(configKey);
        }

        if (StringUtils.isBlank(configValue)) {
            return defaultValue;
        } else {
            return configValue;
        }
    }

    public static int getWorkerThread() {
        return 0;
    }

    public static int getWorkerThreads() {
        return 0;
    }

    public static String getEpollEnableSwitch() {
        return "";
    }

    public static Boolean isSoKeepalive() {
        return Boolean.FALSE;
    }

    public static Boolean isTcpNoDelay() {
        return Boolean.FALSE;
    }

    public static Integer getSendBufferSize() {
        return null;
    }


    public static Integer getReceiveBufferSize() {
        return null;
    }

    public static Integer getConnectTimeoutMillis() {
        return null;
    }

    public static Integer getSoBacklog() {
        return null;
    }

    public static Integer getListenPort() {
        return 0;
    }

    public static boolean hasLoad() {
        return load.get();
    }
}
