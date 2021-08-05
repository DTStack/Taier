package com.dtstack.engine.remote.netty.node;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.remote.exception.RemoteException;
import com.dtstack.engine.remote.exception.RemotingTimeoutException;
import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.netty.command.Command;
import com.dtstack.engine.remote.netty.config.NettyConfig;
import com.dtstack.engine.remote.netty.future.ResponseFuture;
import com.dtstack.engine.remote.node.AbstractNode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: dazhi
 * @Date: 2021/8/3 5:34 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NettyNode extends AbstractNode {
    private final Logger LOGGER = LoggerFactory.getLogger(NettyNode.class);
    private Channel channel;

    public NettyNode(String identifier, String ip, Integer port, Channel channel) {
        super(identifier, ip, port);
        this.channel = channel;
    }

    @Override
    public Message sendMessage(Message message) {
        Command command = Command.buildBody(message);

        final long opaque = command.getOpaque();
        final ResponseFuture responseFuture = new ResponseFuture(opaque, NettyConfig.getSendTimeoutMillis(), null, null);
        channel.writeAndFlush(command).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                responseFuture.setSendOk(true);
                return;
            } else {
                responseFuture.setSendOk(false);
            }
            responseFuture.setCause(future.cause());
            responseFuture.putResponse(null);
            LOGGER.error("send command {} to host {} failed", command, getAddress());
        });

        Command result = null;
        try {
            result = responseFuture.waitResponse();
        } catch (InterruptedException e) {
            LOGGER.error("responseFuture wait failed {} to host {} failed e:{}", command, getAddress(),e);
            throw new RemoteException(e);
        }

        if (result == null) {
            if (responseFuture.isSendOK()) {
                throw new RemotingTimeoutException(getAddress(), NettyConfig.getSendTimeoutMillis(), responseFuture.getCause());
            } else {
                throw new RemoteException(getAddress()+ responseFuture.getCause());
            }
        }

        return JSON.parseObject(new String(result.getBody()), Message.class);
    }

    @Override
    public void init() {
    }



    @Override
    public void close() {
        if (channel != null) {
            channel.close();
        }
    }
}
