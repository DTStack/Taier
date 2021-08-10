package com.dtstack.engine.remote.netty.config;

import com.dtstack.engine.remote.config.RemoteConfig;
import com.dtstack.engine.remote.constant.ServerConstant;
import javafx.beans.property.ReadOnlyProperty;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/7/30 4:41 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NettyConfig {
    public static int getWorkerThread() {
        return Integer.parseInt(RemoteConfig.getValueWithDefault("remote.work.threads", ServerConstant.CPUS+""));
    }

    public static int getWorkerThreads() {
        return Integer.parseInt(RemoteConfig.getValueWithDefault("remote.work.threads", ServerConstant.CPUS+""));
    }

    public static String getEpollEnableSwitch() {
        return RemoteConfig.getValueWithDefault("remote.epoll.enable.switch","false");
    }

    public static Boolean isSoKeepalive() {
        return Boolean.parseBoolean(RemoteConfig.getValueWithDefault("remote.so.keep.alive","true"));
    }

    public static Boolean isTcpNoDelay() {
        return Boolean.parseBoolean(RemoteConfig.getValueWithDefault("remote.tcp.no.delay","true"));
    }

    public static Integer getSendBufferSize() {
        return Integer.parseInt(RemoteConfig.getValueWithDefault("remote.send.buffer.size","65535"));
    }


    public static Integer getReceiveBufferSize() {
        return Integer.parseInt(RemoteConfig.getValueWithDefault("remote.receive.buffer.size","65535"));
    }

    public static Integer getConnectTimeoutMillis() {
        return Integer.parseInt(RemoteConfig.getValueWithDefault("remote.connect.timeout.millis","3000"));
    }

    public static Integer getSoBacklog() {
        return Integer.parseInt(RemoteConfig.getValueWithDefault("remote.so.backlog","1024"));
    }

    public static Integer getListenPort() {
        return Integer.parseInt(RemoteConfig.getValueWithDefault("remote.listen.port","8199"));
    }


    public static Long getSendTimeoutMillis() {
        return Long.parseLong(RemoteConfig.getValueWithDefault("remote.send.timeout.millis","6000"));
    }

    public static ApplicationContext getApplicationContext() {
        return RemoteConfig.getApplicationContext();
    }

}
