package com.dtstack.rdos.engine.execution.flink150;

import com.google.common.collect.Maps;
import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * TODO 添加定时检查ClusterClient 是否可用线程
 * Date: 2018/11/5
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ClusterClientCache {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterClientCache.class);

    public Map<String, ClusterClient> clusterClientCache = Maps.newConcurrentMap();

    private AbstractYarnClusterDescriptor yarnClusterDescriptor;

    public ClusterClientCache(AbstractYarnClusterDescriptor yarnClusterDescriptor){
        this.yarnClusterDescriptor = yarnClusterDescriptor;
    }

    public ClusterClient getClusterClient(String applicationId){
        ClusterClient clusterClient = clusterClientCache.get(applicationId);
        if(clusterClient != null){
            return clusterClient;
        }

        clusterClient = clusterClientCache.computeIfAbsent(applicationId, key -> {
            try {
                return yarnClusterDescriptor.retrieve(ConverterUtils.toApplicationId(key));
            } catch (ClusterRetrieveException e) {
                LOG.error("", e);
                return null;
            }
        });

        return clusterClient;
    }

    public boolean put(String applicationId, ClusterClient clusterClient){
        if(clusterClientCache.containsKey(applicationId)){
            return false;
        }

        clusterClientCache.put(applicationId, clusterClient);
        return true;
    }
}
