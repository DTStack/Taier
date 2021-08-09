package com.dtstack.engine.remote.config;

import com.dtstack.engine.remote.akka.constant.AkkaConfigConstant;
import com.dtstack.engine.remote.constant.GlobalConstant;
import com.dtstack.engine.remote.exception.RemoteException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Auther: dazhi
 * @Date: 2021/8/4 5:29 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RemoteConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteConfig.class);
    private static Environment environment;
    private static ApplicationContext applicationContext;
    private static final AtomicBoolean load = new AtomicBoolean(Boolean.FALSE);
    private static Properties config = new Properties();

    public static void init(Environment environment, ApplicationContext applicationContext) {
        RemoteConfig.applicationContext = applicationContext;
        RemoteConfig.environment = environment;
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

    public static String getValueWithDefault(String configKey, String defaultValue) {
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

    public static String getValueByKey(String configKey) {
        return getValueWithDefault(configKey,null);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static boolean hasLoad() {
        return load.get();
    }


    public static Set<String> getLocalRoles() {
        String value = getValueByKey("remote.local.identifier");
        if (StringUtils.isBlank(value)) {
            throw new RemoteException("config : remote.local.identifier is not null");
        }
        return new HashSet<>(Arrays.asList(value.split(",")));
    }

}
