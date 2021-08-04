package com.dtstack.engine.remote.netty.util;

import com.dtstack.engine.remote.constant.ServerConstant;
import com.dtstack.engine.remote.netty.config.NettyConfig;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Auther: dazhi
 * @Date: 2021/7/30 4:47 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NettyUtils {
    private NettyUtils() {
    }

    public static boolean useEpoll() {
        String osName = ServerConstant.OS_NAME;
        if (!osName.toLowerCase().contains("linux")) {
            return false;
        }
        if (!Epoll.isAvailable()) {
            return false;
        }
        String enableNettyEpoll = NettyConfig.getEpollEnableSwitch();
        return Boolean.parseBoolean(enableNettyEpoll);
    }

    public static Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        if (useEpoll()) {
            return EpollServerSocketChannel.class;
        }
        return NioServerSocketChannel.class;
    }

    public static Class<? extends SocketChannel> getSocketChannelClass() {
        if (useEpoll()) {
            return EpollSocketChannel.class;
        }
        return NioSocketChannel.class;
    }

}
