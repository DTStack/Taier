package com.dtstack.taier.flink.client;

import com.dtstack.taier.pluginapi.JobIdentifier;
import org.apache.flink.client.program.ClusterClient;
import org.apache.hadoop.yarn.client.api.YarnClient;

/**
 * @program: engine-plugins
 * @author: xiuzhu
 * @create: 2021/07/15
 */

public interface IClientManager {

    /**
     * get flink clusterClient
     * @param jobIdentifier jobIndentifier
     * @return yarnClient
     */
    ClusterClient getClusterClient(JobIdentifier jobIdentifier);


    /**
     * get yarn client
     * @return yarnClient
     */
    YarnClient getYarnClient();

    /**
     * do something when job submit failed
     */
    default void dealWithClientError() {}
}
