package com.dtstack.engine.flink.factory;

import org.apache.flink.client.program.ClusterClient;

public interface IClientFactory {
    ClusterClient getClusterClient();
}
