package com.dtstack.engine.remote.netty.node;

import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.netty.NettyRemoteClient;
import com.dtstack.engine.remote.node.AbstractNode;
import io.netty.channel.Channel;
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

    public NettyNode(String identifier, String ip, int port, Channel channel) {
        super(identifier, ip, port);
        this.channel = channel;
    }

    @Override
    protected Message sendMessage(Message message) {
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
