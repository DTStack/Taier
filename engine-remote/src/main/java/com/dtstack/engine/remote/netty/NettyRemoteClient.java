package com.dtstack.engine.remote.netty;

import com.dtstack.engine.remote.exception.NoNodeException;
import com.dtstack.engine.remote.factory.RemoteThreadFactory;
import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.netty.codec.NettyDecoder;
import com.dtstack.engine.remote.netty.codec.NettyEncoder;
import com.dtstack.engine.remote.netty.config.NettyConfig;
import com.dtstack.engine.remote.netty.future.ResponseFuture;
import com.dtstack.engine.remote.netty.util.CallerThreadExecutePolicy;
import com.dtstack.engine.remote.netty.util.NettyUtils;
import com.dtstack.engine.remote.node.RemoteNodes;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Auther: dazhi
 * @Date: 2021/7/30 3:34 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NettyRemoteClient {
    private final Logger LOGGER = LoggerFactory.getLogger(NettyRemoteClient.class);

    /**
     * client bootstrap
     */
    private final Bootstrap bootstrap = new Bootstrap();


    private final NettyEncoder encoder = new NettyEncoder();

    /**
     * channels
     */
    private final ConcurrentHashMap<String, RemoteNodes> channels = new ConcurrentHashMap(128);

    /**
     * started flag
     */
    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    /**
     * worker group
     */
    private final EventLoopGroup workerGroup;

    /**
     * 处理器
     */
    private final NettyClientHandler nettyClientHandler;

    /**
     * saync semaphore
     */
    private final Semaphore asyncSemaphore = new Semaphore(200, true);

    /**
     * callback thread executor
     */
    private final ExecutorService callbackExecutor;

    /**
     * response future executor
     */
    private final ScheduledExecutorService responseFutureExecutor;

    /**
     * server init
     *
     */
    public NettyRemoteClient() {
        if (NettyUtils.useEpoll()) {
            this.workerGroup = new EpollEventLoopGroup(NettyConfig.getWorkerThreads(), new RemoteThreadFactory(this.getClass().getName()));
        } else {
            this.workerGroup = new NioEventLoopGroup(NettyConfig.getWorkerThreads(), new RemoteThreadFactory(this.getClass().getName()));
        }
        this.callbackExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(1000), new RemoteThreadFactory("CallbackExecutor"),
                new CallerThreadExecutePolicy());
        this.responseFutureExecutor = Executors.newSingleThreadScheduledExecutor(new RemoteThreadFactory("ResponseFutureExecutor"));
        this.nettyClientHandler = new NettyClientHandler(this, callbackExecutor);


        this.start();
    }

    /**
     * start
     */
    private void start() {

        this.bootstrap
                .group(this.workerGroup)
                .channel(NettyUtils.getSocketChannelClass())
                .option(ChannelOption.SO_KEEPALIVE, NettyConfig.isSoKeepalive())
                .option(ChannelOption.TCP_NODELAY, NettyConfig.isTcpNoDelay())
                .option(ChannelOption.SO_SNDBUF, NettyConfig.getSendBufferSize())
                .option(ChannelOption.SO_RCVBUF, NettyConfig.getReceiveBufferSize())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, NettyConfig.getConnectTimeoutMillis())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new NettyDecoder(),
                                nettyClientHandler,
                                encoder);
                    }
                });
        this.responseFutureExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ResponseFuture.scanFutureTable();
            }
        }, 5000, 1000, TimeUnit.MILLISECONDS);
        //
        isStarted.compareAndSet(false, true);
    }

    /**
     * close
     */
    public void close() {
        if (isStarted.compareAndSet(true, false)) {
            try {
                closeChannels();
                if (workerGroup != null) {
                    this.workerGroup.shutdownGracefully();
                }
                if (callbackExecutor != null) {
                    this.callbackExecutor.shutdownNow();
                }
                if (this.responseFutureExecutor != null) {
                    this.responseFutureExecutor.shutdownNow();
                }
            } catch (Exception ex) {
                LOGGER.error("netty client close exception", ex);
            }
            LOGGER.info("netty client closed");
        }
    }

    /**
     * close channels
     */
    private void closeChannels() {
        for (Map.Entry<String, RemoteNodes> entry : channels.entrySet()) {
            RemoteNodes nodes = entry.getValue();
            nodes.close();
        }
        this.channels.clear();
    }

    /**
     * close channel
     *
     * @param identifier 节点标识
     */
    public void closeChannel(String identifier) {
        RemoteNodes nodes = this.channels.remove(identifier);
        if (nodes != null) {
            nodes.close();
        }
    }

    public Message sendMessage(Message message) {
        RemoteNodes nodes = this.channels.get(message.getIdentifier());

        if (nodes == null) {
            throw new NoNodeException(String.format("node not find,identifier:%s",message.getIdentifier()));
        }

        return null;
    }
}
