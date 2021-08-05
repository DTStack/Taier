package com.dtstack.engine.remote.akka.config;

import com.dtstack.engine.remote.constant.GlobalConstant;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang.StringUtils;

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

    private static Config loadCommon(Config load, String configPath, String fileNames) {
        if (StringUtils.isBlank(fileNames)) {
            return load;
        }

        String[] split = fileNames.split(",");
        for (String fileName : split) {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(configPath + "/" + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String conf = entry.getKey() + "=" + entry.getValue();
                if (load == null) {
                    load = ConfigFactory.parseString(conf);
                } else {
                    load = load.withFallback(ConfigFactory.parseString(conf));
                }
            }
        }
        return load;
    }

    public static Config load(String configPath) {
        String property = System.getProperty(GlobalConstant.REMOTE_PROPERTIES_FILE_NAME);
        Config config = null;
        if (StringUtils.isNotBlank(property)) {
            config = loadCommon(config, configPath, property);
        }

        // 加载 properties
        config = loadProperties(config, configPath + GlobalConstant.PROPERTIES_FILE_PATH);
        config = config.withFallback(ConfigFactory.load());
        return config;

    }

    private static Config loadProperties(Config config,String configPath) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (config != null) {
            config = config.withFallback(ConfigFactory.parseProperties(properties));
        } else {
            config = ConfigFactory.parseProperties(properties);
        }

        return config;
    }
}
