package com.dtstack.engine.remote.netty.config;

import com.dtstack.engine.remote.config.RemoteConfig;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/7/30 4:41 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NettyConfig extends RemoteConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyConfig.class);
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



    public static Long getSendTimeoutMillis() {
        return 6000L;
    }

    public static List<String> getWorkerNodeIdentifiers() {
        return Lists.emptyList();
    }
}
