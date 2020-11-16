package com.dtstack.engine.master.jobdealer.resource;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.dao.EngineTenantDao;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ClusterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
@Component
public class JobComputeResourcePlain {

    @Autowired
    private CommonResource commonResource;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private EngineTenantDao engineTenantDao;

    @Autowired
    private ClusterService clusterService;


    public String getJobResource(JobClient jobClient) {
        this.buildJobClientGroupName(jobClient);
        ComputeResourceType computeResourceType = commonResource.newInstance(jobClient);

        String plainType = environmentContext.getComputeResourcePlain();
        String jobResource = null;
        if (ComputeResourcePlain.EngineTypeClusterQueue.name().equalsIgnoreCase(plainType)) {
            jobResource = jobClient.getEngineType() + ConfigConstant.SPLIT + jobClient.getGroupName();
        } else {
            jobResource = jobClient.getEngineType() + ConfigConstant.SPLIT + jobClient.getGroupName() + ConfigConstant.SPLIT + jobClient.getComputeType().name().toLowerCase();
        }
        return jobResource + ConfigConstant.SPLIT + computeResourceType.name();
    }


    private void buildJobClientGroupName(JobClient jobClient) {
        Long clusterId = engineTenantDao.getClusterIdByTenantId(jobClient.getTenantId());
        if(null == clusterId){
            return;
        }
        Cluster cluster = clusterService.getOne(clusterId);
        if (null == cluster) {
            return;
        }
        String clusterName = cluster.getClusterName();
        //%s_default
        String groupName = clusterName + ConfigConstant.SPLIT + ConfigConstant.RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT;

        String namespace = clusterService.getNamespace(jobClient.getParamAction(),
                jobClient.getTenantId(), jobClient.getEngineType(), jobClient.getComputeType());
        if (StringUtils.isNotBlank(namespace)) {
            groupName = clusterName + ConfigConstant.SPLIT + namespace;
            jobClient.setGroupName(groupName);
            return;
        }
        Queue queue = clusterService.getQueue(jobClient.getTenantId(), clusterId);
        if (null != queue) {
            groupName = clusterName + ConfigConstant.SPLIT  + queue.getQueueName();
        }
        jobClient.setGroupName(groupName);
    }
}
