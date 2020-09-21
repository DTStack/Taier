package com.dtstack.engine.flink.factory;

import com.dtstack.engine.common.JobClient;
import org.apache.flink.client.program.ClusterClient;

public interface IClientFactory {

    ClusterClient getClusterClient(JobClient jobClient);

    ClusterClient retrieveClusterClient(String clusterId, JobClient jobClient);
}
