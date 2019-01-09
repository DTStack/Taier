package com.dtstack.rdos.engine.execution.flink170;

import com.dtstack.rdos.engine.execution.base.JobIdentifier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 用于缓存连接perjob对应application的ClusterClient
 * Date: 2018/11/5
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ClusterClientCache {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterClientCache.class);

    public Cache<String, ClusterClient> clusterClientCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();

    private AbstractYarnClusterDescriptor yarnClusterDescriptor;

    public ClusterClientCache(AbstractYarnClusterDescriptor yarnClusterDescriptor){
        this.yarnClusterDescriptor = yarnClusterDescriptor;
    }

    public ClusterClient getClusterClient(JobIdentifier jobIdentifier){

        String applicationId = jobIdentifier.getApplicationId();
        String taskId = jobIdentifier.getTaskId();

        ClusterClient clusterClient;
        try {
            clusterClient = clusterClientCache.get(applicationId, () -> {
                yarnClusterDescriptor.getFlinkConfiguration().setString(HighAvailabilityOptions.HA_CLUSTER_ID, taskId);
                return yarnClusterDescriptor.retrieve(ConverterUtils.toApplicationId(applicationId));
            });

        } catch (ExecutionException e) {
            throw new RuntimeException("get yarn cluster client exception:", e);
        }

        return clusterClient;
    }

    public boolean put(String applicationId, ClusterClient clusterClient){
        if(clusterClientCache.getIfPresent(applicationId) != null){
            return false;
        }

        clusterClientCache.put(applicationId, clusterClient);
        return true;
    }
}
