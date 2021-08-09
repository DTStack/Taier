package com.dtstack.engine.common.akka.config;

import com.dtstack.engine.common.constrant.ConfigConstant;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @Auther: dazhi
 * @Date: 2021/2/8 11:06 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AkkaLoad {

    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaLoad.class);

    private final static String COMMON_FILE_PATH = "application-common.properties";
    private final static String PROPERTIES_FILE_PATH = "application.properties";

    private static Config loadCommon(String configPath) {
        Properties properties = new Properties();
        try (FileInputStream fs = new FileInputStream(configPath)){
            properties.load(fs);
        } catch (IOException e) {
            LOGGER.error("properties load error:",e);
        }

        // 加载 application-common.properties
        Config load = ConfigFactory.load();

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String conf = entry.getKey() + "=" + entry.getValue();
            load = load.withFallback(ConfigFactory.parseString(conf));
        }

        return load;
    }

    public static Config load(String configPath) {
        // 加载 common
        Config config = loadCommon(configPath + COMMON_FILE_PATH);

        // 加载 properties
        return loadProperties(config,configPath + PROPERTIES_FILE_PATH);

    }

    private static Config loadProperties(Config config,String configPath) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configPath));
        } catch (IOException e) {
            LOGGER.error("loadProperties error:",e);
        }
        putLogStoreConfigIfNotExist(properties);
        if (config != null) {
            config = config.withFallback(ConfigFactory.parseProperties(properties));
        }

        return config;
    }


    /**
     * logstore 默认和 dagschedulex 数据库一致
     *
     * @param properties
     */
    private static void putLogStoreConfigIfNotExist(Properties properties) {
        if (properties.containsKey(ConfigConstant.DAGSCHEULEX_JDBC_URL)) {
            properties.putIfAbsent(ConfigConstant.AKKA_WORKER_LOGSTORE_JDBCURL, properties.getProperty(ConfigConstant.DAGSCHEULEX_JDBC_URL));
        }
        if (properties.containsKey(ConfigConstant.DAGSCHEULEX_JDBC_USERNAME)) {
            properties.putIfAbsent(ConfigConstant.AKKA_WORKER_LOGSTORE_USERNAME, properties.getProperty(ConfigConstant.DAGSCHEULEX_JDBC_USERNAME));
        }
        if (properties.containsKey(ConfigConstant.DAGSCHEULEX_JDBC_PASSWORD)) {
            properties.putIfAbsent(ConfigConstant.AKKA_WORKER_LOGSTORE_PASSWORD, properties.getProperty(ConfigConstant.DAGSCHEULEX_JDBC_PASSWORD));
        }
    }
}
