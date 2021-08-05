package com.dtstack.engine.remote.netty;

import com.dtstack.engine.remote.constant.ServerConstant;
import com.dtstack.engine.remote.exception.RemoteException;
import com.dtstack.engine.remote.factory.RemoteThreadFactory;
import com.dtstack.engine.remote.netty.codec.NettyDecoder;
import com.dtstack.engine.remote.netty.codec.NettyEncoder;
import com.dtstack.engine.remote.netty.config.NettyConfig;
import com.dtstack.engine.remote.netty.handler.NettyServerHandler;
import com.dtstack.engine.remote.netty.util.NettyUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Auther: dazhi
 * @Date: 2021/8/3 2:49 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NettyRemoteServer {
    private final Logger LOGGER = LoggerFactory.getLogger(NettyRemoteServer.class);

    /**
     * server bootstrap
     */
    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

    /**
     * encoder
     */
    private final NettyEncoder encoder = new NettyEncoder();

    /**
     * default executor
     */
    private final ExecutorService defaultExecutor = Executors.newFixedThreadPool(ServerConstant.CPUS);

    /**
     * boss group
     */
    private final EventLoopGroup bossGroup;

    /**
     * worker group
     */
    private final EventLoopGroup workGroup;

    /**
     * server handler
     */
    private final NettyServerHandler serverHandler = new NettyServerHandler(this);

    /**
     * started flag
     */
    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    /**
     * server init
     *
     */
    public NettyRemoteServer() {
        if (NettyUtils.useEpoll()) {
            this.bossGroup = new EpollEventLoopGroup(1, new RemoteThreadFactory(this.getClass().getName()+"_NettyServerBossThread"));
            this.workGroup = new EpollEventLoopGroup(NettyConfig.getWorkerThread(), new RemoteThreadFactory(this.getClass().getName()+"_NettyServerWorkerThread"));
        } else {
            this.bossGroup = new NioEventLoopGroup(1, new RemoteThreadFactory(this.getClass().getName()+"_NettyServerBossThread"));
            this.workGroup = new NioEventLoopGroup(NettyConfig.getWorkerThread(),new RemoteThreadFactory(this.getClass().getName()+"_NettyServerWorkerThread"));
        }
    }

    /**
     * server start
     */
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            this.serverBootstrap
                    .group(this.bossGroup, this.workGroup)
                    .channel(NettyUtils.getServerSocketChannelClass())
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_BACKLOG, NettyConfig.getSoBacklog())
                    .childOption(ChannelOption.SO_KEEPALIVE, NettyConfig.isSoKeepalive())
                    .childOption(ChannelOption.TCP_NODELAY, NettyConfig.isTcpNoDelay())
                    .childOption(ChannelOption.SO_SNDBUF, NettyConfig.getSendBufferSize())
                    .childOption(ChannelOption.SO_RCVBUF, NettyConfig.getReceiveBufferSize())
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            initNettyChannel(ch);
                        }
                    });

            ChannelFuture future;
            try {
                future = serverBootstrap.bind(NettyConfig.getListenPort()).sync();
            } catch (Exception e) {
                LOGGER.error("NettyRemotingServer bind fail {}, exit", e.getMessage(), e);
                throw new RemoteException(String.format("NettyRemotingServer bind %s fail", NettyConfig.getListenPort()));
            }
            if (future.isSuccess()) {
                LOGGER.info("NettyRemotingServer bind success at port : {}", NettyConfig.getListenPort());
            } else if (future.cause() != null) {
                throw new RemoteException(String.format("NettyRemotingServer bind %s fail", NettyConfig.getListenPort())+"  \n"+future.cause());
            } else {
                throw new RemoteException(String.format("NettyRemotingServer bind %s fail", NettyConfig.getListenPort()));
            }
        }
    }

    /**
     * init netty channel
     *
     * @param ch socket channel
     */
    private void initNettyChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("encoder", encoder);
        pipeline.addLast("decoder", new NettyDecoder());
        pipeline.addLast("handler", serverHandler);
    }




}
