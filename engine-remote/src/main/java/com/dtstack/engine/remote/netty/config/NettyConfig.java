package com.dtstack.engine.remote.netty.config;

import com.dtstack.engine.remote.config.RemoteConfig;
import com.dtstack.engine.remote.constant.ServerConstant;
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
        return Integer.parseInt(getValueWithDefault("remote.work.threads", ServerConstant.CPUS+""));
    }

    public static int getWorkerThreads() {
        return Integer.parseInt(getValueWithDefault("remote.work.threads", ServerConstant.CPUS+""));
    }

    public static String getEpollEnableSwitch() {
        return getValueWithDefault("remote.epoll.enable.switch","false");
    }

    public static Boolean isSoKeepalive() {
        return Boolean.parseBoolean(getValueWithDefault("remote.so.keep.alive","true"));
    }

    public static Boolean isTcpNoDelay() {
        return Boolean.parseBoolean(getValueWithDefault("remote.tcp.no.delay","true"));
    }

    public static Integer getSendBufferSize() {
        return Integer.parseInt(getValueWithDefault("remote.send.buffer.size","65535"));
    }


    public static Integer getReceiveBufferSize() {
        return Integer.parseInt(getValueWithDefault("remote.receive.buffer.size","65535"));
    }

    public static Integer getConnectTimeoutMillis() {
        return Integer.parseInt(getValueWithDefault("remote.connect.timeout.millis","3000"));
    }

    public static Integer getSoBacklog() {
        return Integer.parseInt(getValueWithDefault("remote.so.backlog","1024"));
    }

    public static Integer getListenPort() {
        return Integer.parseInt(getValueWithDefault("remote.listen.port","8099"));
    }


    public static Long getSendTimeoutMillis() {
        return Long.parseLong(getValueWithDefault("remote.send.timeout.millis","6000"));
    }
}
