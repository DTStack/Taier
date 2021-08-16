package com.dtstack.engine.remote.netty.handler;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.remote.exception.RemoteException;
import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.message.TargetInfo;
import com.dtstack.engine.remote.netty.NettyRemoteServer;
import com.dtstack.engine.remote.netty.command.Command;
import com.dtstack.engine.remote.netty.command.CommandType;
import com.dtstack.engine.remote.netty.config.NettyConfig;
import com.dtstack.engine.remote.node.SerializableUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

/**
 * @Auther: dazhi
 * @Date: 2021/8/3 2:51 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);

    /**
     * netty remote server
     */
    private final NettyRemoteServer nettyRemoteServer;

    public NettyServerHandler(NettyRemoteServer nettyRemoteServer) {
        this.nettyRemoteServer = nettyRemoteServer;
    }

    /**
     * When the current channel is not active,
     * the current channel has reached the end of its life cycle
     *
     * @param ctx channel handler context
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        closeChannel(ctx);
    }

    private void closeChannel(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        // 清除handler
        channel.pipeline().remove("encoder");
        channel.pipeline().remove("decoder");
        channel.pipeline().remove("handler");
        channel.close();
        channel.eventLoop().shutdownGracefully();
    }

    /**
     * The current channel reads data from the remote end
     *
     * @param ctx channel handler context
     * @param msg message
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Command command =  (Command) msg;
        if (CommandType.PING.equals(command.getType())) {
            processPING(command,ctx.channel());
        } if (CommandType.REQUEST.equals(command.getType())) {
            processReceived(ctx.channel(), command);
        }
    }

    private void processPING(Command command,Channel channel) {
        ExecutorService defaultExecutor = nettyRemoteServer.getDefaultExecutor();
        defaultExecutor.submit(()->{
            channel.writeAndFlush(Command.buildBeat(command,CommandType.PONG));
        });
    }


    private void processReceived(Channel channel, Command msg) {
        Message message = deserialization(msg.getBody());
        ExecutorService defaultExecutor = nettyRemoteServer.getDefaultExecutor();
        defaultExecutor.submit(() -> {
            try {
                TargetInfo targetInfo = message.getTargetInfo();
                if (targetInfo == null || targetInfo.getClazz() == null || targetInfo.getMethod() == null) {
                    channel.writeAndFlush(Command.response(msg,message.ask(new RemoteException("无目标信息，无法调用"), Message.MessageStatue.ERROR)));
                    return;
                }
                Object transport = message.getTransport();

                String methodName = targetInfo.getMethod();
                Object bean = NettyConfig.getApplicationContext().getBean(Class.forName(targetInfo.getClazz()));

                Method targetMethod;
                if (transport.getClass().isArray()) {
                    Object[] objects = (Object[]) transport;
                    Class<?>[] clazzs = new Class<?>[objects.length];

                    for (int i = 0; i < objects.length; i++) {
                        clazzs[i] = objects[i].getClass();
                    }
                    targetMethod = bean.getClass().getMethod(methodName, clazzs);
                    channel.writeAndFlush(Command.response(msg,message.ask(targetMethod.invoke(bean, objects), Message.MessageStatue.RESULT)));
                } else {
                    channel.writeAndFlush(Command.response(msg,message.ask(new RemoteException("Parameter error :"+transport),Message.MessageStatue.ERROR)));
                }
            } catch (Exception e) {
                e.printStackTrace();
                channel.writeAndFlush(Command.response(msg,message.ask(new RemoteException(e),Message.MessageStatue.ERROR)));
            }
        });
    }

    private Message deserialization(byte[] body) {
        Object data = SerializableUtil.ByteToObject(body);

        if (data instanceof Message) {
            return (Message)data;
        }

        LOGGER.error("The serial number is abnormal:{}",JSON.toJSONString(data));
        throw new RemoteException("The serial number is abnormal");
    }

    /**
     * caught exception
     *
     * @param ctx   channel handler context
     * @param cause cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("exceptionCaught : {}", cause.getMessage(), cause);
        closeChannel(ctx);
    }


}
