package com.dtstack.engine.remote.service;

import com.dtstack.engine.remote.message.Message;


/**
 * @Auther: dazhi
 * @Date: 2020/9/2 10:45 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ClientService {

    Message sendMassage(Message message) throws Exception;

    void destroy() throws Exception;

}
