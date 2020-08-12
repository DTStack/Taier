package com.dtstack.engine.master.jobdealer.resource;

import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ClusterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
@Component
public class JobComputeResourcePlain {

    private static final String SPLIT = "_";

    @Autowired
    private CommonResource commonResource;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;


    public String getJobResource(JobClient jobClient) {
        this.buildJobClientGroupName(jobClient);
        ComputeResourceType computeResourceType = commonResource.newInstance(jobClient);

        String plainType = environmentContext.getComputeResourcePlain();
        String jobResource = null;
        if (ComputeResourcePlain.Cluster.name().equalsIgnoreCase(plainType)) {
            jobResource = StringUtils.substringBefore(jobClient.getGroupName(), SPLIT);
        } else if (ComputeResourcePlain.ClusterQueue.name().equalsIgnoreCase(plainType)) {
            jobResource = jobClient.getGroupName();
        } else if (ComputeResourcePlain.EngineTypeClusterQueue.name().equalsIgnoreCase(plainType)) {
            jobResource = jobClient.getEngineType() + SPLIT + jobClient.getGroupName();
        } else if (ComputeResourcePlain.EngineTypeClusterQueueComputeType.name().equalsIgnoreCase(plainType)) {
            jobResource = jobClient.getEngineType() + SPLIT + jobClient.getGroupName() + SPLIT + jobClient.getComputeType().name().toLowerCase();
        } else {
            jobResource = jobClient.getGroupName();
        }

        return jobResource + SPLIT + computeResourceType.name();
    }


    private void buildJobClientGroupName(JobClient jobClient){
        jobClient.setGroupName(ConfigConstant.DEFAULT_GROUP_NAME);
        ClusterVO cluster = clusterService.getClusterByTenant(jobClient.getTenantId());
        if(Objects.isNull(cluster)){
            return;
        }
        Queue queue = clusterService.getQueue(jobClient.getTenantId(), cluster.getClusterId());
        jobClient.setGroupName(cluster.getClusterName() + SPLIT + (queue == null ? null : queue.getQueueName()));
    }
}
