package com.dtstack.engine.remote.netty;

import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @Auther: dazhi
 * @Date: 2021/7/30 5:18 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    public NettyClientHandler(NettyRemoteClient client, ExecutorService callbackExecutor) {

    }
}
