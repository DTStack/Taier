package com.dtstack.engine.remote.netty.node;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.remote.exception.RemoteException;
import com.dtstack.engine.remote.exception.RemotingTimeoutException;
import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.netty.command.Command;
import com.dtstack.engine.remote.netty.command.CommandType;
import com.dtstack.engine.remote.netty.config.NettyConfig;
import com.dtstack.engine.remote.netty.future.ResponseFuture;
import com.dtstack.engine.remote.node.AbstractNode;
import com.dtstack.engine.remote.node.SerializableUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @Auther: dazhi
 * @Date: 2021/8/3 5:34 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NettyNode extends AbstractNode {
    private final Logger LOGGER = LoggerFactory.getLogger(NettyNode.class);
    private Channel channel;
    private final Bootstrap bootstrap;

    public NettyNode(String identifier, String ip, Integer port, Bootstrap bootstrap) {
        super(identifier, ip, port);
        this.bootstrap = bootstrap;
        channel= getChannel(ip,port,Boolean.TRUE);
        if (channel == null) {
            this.setStatus(AbstractNode.NodeStatus.UNAVAILABLE);
        } else {
            this.setStatus(AbstractNode.NodeStatus.USABLE);
        }
    }

    @Override
    public Message sendMessage(Message message) {
        Command command = Command.buildBody(message);
        byte[] body = send(command).getBody();
        return (Message)SerializableUtil.ByteToObject(body);
    }

    private Command send(Command command) {
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

        return result;
    }

    @Override
    public Boolean heartbeat() {
        Command command = new Command();
        command = Command.buildBeat(command,CommandType.PING);
        try {
            Command send = send(command);
            if (CommandType.PONG.equals(send.getType())) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            LOGGER.error(""+e);
            return Boolean.FALSE;
        }
    }

    @Override
    public Boolean connect() {
        channel = getChannel(this.getIp(), this.getPort(), Boolean.TRUE);
        if (channel == null) {
            LOGGER.info("address:{} 连接失败",this.getAddress());
            this.setStatus(AbstractNode.NodeStatus.UNAVAILABLE);
            return Boolean.FALSE;
        } else {
            LOGGER.info("address:{} 连接成功",this.getAddress());
            this.setStatus(AbstractNode.NodeStatus.USABLE);
            return Boolean.TRUE;
        }
    }


    private Channel getChannel(String ip, Integer port, boolean isSync) {
        ChannelFuture future;
        try {
            synchronized (bootstrap) {
                future = bootstrap.connect(new InetSocketAddress(ip, port));
            }
            if (isSync) {
                future.sync();
            }
            if (future.isSuccess()) {
                return future.channel();
            }
        } catch (Exception ex) {
            LOGGER.warn(String.format("connect to %s error", ip + ":" + port), ex);
        }
        return null;
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
