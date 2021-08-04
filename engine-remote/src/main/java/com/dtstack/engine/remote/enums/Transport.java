package com.dtstack.engine.remote.enums;

import com.dtstack.engine.remote.akka.config.AkkaServerConfig;
import com.dtstack.engine.remote.annotation.EnableRemoteClient;
import com.dtstack.engine.remote.config.ServerConfig;
import com.dtstack.engine.remote.netty.config.NettyServerConfig;

/**
 * @Auther: dazhi
 * @Date: 2021/8/3 3:48 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum Transport {
    Akka("akka", AkkaServerConfig.class),Netty("netty", NettyServerConfig.class);

    Transport(String name, Class<? extends ServerConfig> className) {
        this.name = name;
        this.className = className;
    }

    private final String name;

    private Class<? extends ServerConfig> className;

    public String getName() {
        return name;
    }

    public Class<? extends ServerConfig> getClassName() {
        return className;
    }

    public static Class<? extends ServerConfig> getClass(String name) {
        Transport[] values = Transport.values();

        for (Transport value : values) {
            if (value.getName().equals(name)) {
                return value.getClassName();
            }
        }
        return null;
    }
}
