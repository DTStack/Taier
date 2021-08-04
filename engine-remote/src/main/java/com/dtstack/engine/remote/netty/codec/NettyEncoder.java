package com.dtstack.engine.remote.netty.codec;


import com.dtstack.engine.remote.netty.command.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *  netty encoder
 */
@Sharable
public class NettyEncoder extends MessageToByteEncoder<Command> {

    /**
     * encode
     *
     * @param ctx channel handler context
     * @param msg command
     * @param out byte buffer
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws Exception {
        if(msg == null){
            throw new Exception("encode msg is null");
        }
        out.writeByte(Command.MAGIC);
        out.writeByte(msg.getType().ordinal());
        out.writeLong(msg.getOpaque());
        out.writeInt(msg.getBody().length);
        out.writeBytes(msg.getBody());
    }

}

