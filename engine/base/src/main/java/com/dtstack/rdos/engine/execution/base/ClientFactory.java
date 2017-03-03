package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.ClientType;
import com.dtstack.rdos.engine.execution.flink120.FlinkClient;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class ClientFactory {

    public static IClient getClient(ClientType type){

        IClient client = null;

        switch (type){
            case Flink:
                client = new FlinkClient();
                break;
        }

        return client;
    }
}
