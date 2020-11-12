package com.dtstack.engine.flink.factory;

import com.dtstack.engine.common.JobIdentifier;
import org.apache.flink.client.program.ClusterClient;

public interface IClientFactory {


    /**
     * 获取Flink 对应的ClusterClient
     * @param jobIdentifier
     * @return
     */
    ClusterClient getClusterClient(JobIdentifier jobIdentifier);

    default void dealWithClientError() {}

}
