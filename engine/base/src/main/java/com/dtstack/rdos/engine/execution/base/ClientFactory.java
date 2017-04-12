package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.ClientType;
import com.dtstack.rdos.engine.execution.flink120.FlinkClient;
import com.dtstack.rdos.engine.execution.spark210.SparkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class ClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(ClientFactory.class);

    public static IClient getClient(String type){

        IClient client = null;
        type = type.toLowerCase();

        switch (type){
            case "flink":
                client = new FlinkClient();
                break;

            case "spark":
                client = new SparkClient();
                break;

                default:
                    logger.error("not support for engine type " + type);
        }

        return client;
    }
}
