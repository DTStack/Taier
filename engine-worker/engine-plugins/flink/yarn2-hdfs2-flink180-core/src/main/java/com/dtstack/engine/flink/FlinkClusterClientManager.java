package com.dtstack.engine.flink;

import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.flink.factory.AbstractClientFactory;
import com.dtstack.engine.flink.factory.IClientFactory;
import org.apache.flink.client.program.ClusterClient;
/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/8/27
 */
public class FlinkClusterClientManager {

    private IClientFactory iClientFactory;

    public FlinkClusterClientManager(FlinkClientBuilder flinkClientBuilder) throws Exception {
        this.iClientFactory = AbstractClientFactory.createClientFactory(flinkClientBuilder);
    }

    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {
        return iClientFactory.getClusterClient(jobIdentifier);
    }

    public void dealWithClientError() {
        iClientFactory.dealWithClientError();
    }

    public IClientFactory getClientFactory() {
        return iClientFactory;
    }

}