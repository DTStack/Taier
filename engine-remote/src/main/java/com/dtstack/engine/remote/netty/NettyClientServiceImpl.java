package com.dtstack.engine.remote.netty;

import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: dazhi
 * @Date: 2021/7/30 3:14 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NettyClientServiceImpl implements ClientService {
    private final Logger LOGGER = LoggerFactory.getLogger(NettyClientServiceImpl.class);
    private NettyRemoteClient client;

    @Override
    public Message sendMassage(Message message) throws Exception {
        return client.send(message);
    }

    @Override
    public void destroy() throws Exception {
        client.close();
    }

    public void setClient(NettyRemoteClient client) {
        this.client = client;
    }
}
