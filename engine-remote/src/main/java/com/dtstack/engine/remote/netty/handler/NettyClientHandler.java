package com.dtstack.engine.remote.netty.handler;

import com.dtstack.engine.remote.netty.NettyRemoteClient;
import com.dtstack.engine.remote.netty.command.Command;
import com.dtstack.engine.remote.netty.future.ResponseFuture;
import com.dtstack.engine.remote.netty.util.ChannelUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
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
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);

    /**
     * netty client
     */
    private final NettyRemoteClient client;

    private static byte[] heartBeatData = "heart_beat".getBytes();

    /**
     * callback thread executor
     */
    private final ExecutorService callbackExecutor;

    public NettyClientHandler(NettyRemoteClient client, ExecutorService callbackExecutor) {
        this.client = client;
        this.callbackExecutor = callbackExecutor;
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
        client.closeChannel(ChannelUtils.toAddress(ctx.channel()));
        ctx.channel().close();
    }

    /**
     * The current channel reads data from the remote
     *
     * @param ctx channel handler context
     * @param msg message
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        processReceived(ctx.channel(), (Command) msg);
    }

    /**
     * process received logic
     *
     * @param command command
     */
    private void processReceived(final Channel channel, final Command command) {
        ResponseFuture future = ResponseFuture.getFuture(command.getOpaque());
        if (future != null) {
            future.setResponseCommand(command);
            future.release();
            if (future.getInvokeCallback() != null) {
                this.callbackExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        future.executeInvokeCallback();
                    }
                });
            } else {
                future.putResponse(command);
            }
        }
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
        LOGGER.error("exceptionCaught : {}", cause);
        client.closeChannel(ChannelUtils.toAddress(ctx.channel()));
        ctx.channel().close();
    }


}
