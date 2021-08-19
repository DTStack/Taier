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

    public static Config loadProperties(Properties properties) {
        Config config = null;

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String conf = entry.getKey() + "=" + entry.getValue();
            if (config == null) {
                config = ConfigFactory.parseString(conf);
            } else {
                config = config.withFallback(ConfigFactory.parseString(conf));
            }
        }
        if (config != null) {
            config = config.withFallback(ConfigFactory.load());
        } else {
            config = ConfigFactory.load();
        }
        return config;
    }
}
