package com.dtstack.engine.master.resource;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.master.env.EnvironmentContext;
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

    private static final String SPLIT = "_";

    @Autowired
    private CommonResource commonResource;

    @Autowired
    private EnvironmentContext environmentContext;

    public String getJobResource(JobClient jobClient) {
        ComputeResourceType computeResourceType = commonResource.newInstance(jobClient);

        String plainType = environmentContext.getComputeResourcePlain();
        String jobResource = null;
        if (ComputeResourcePlain.Cluster.name().equalsIgnoreCase(plainType)) {
            jobResource = StringUtils.substringBefore(jobClient.getGroupName(), SPLIT);
        } else if (ComputeResourcePlain.ClusterQueue.name().equalsIgnoreCase(plainType)) {
            jobResource = jobClient.getGroupName();
        } else if (ComputeResourcePlain.EngineTypeClusterQueue.name().equalsIgnoreCase(plainType)) {
            jobResource = jobClient.getEngineType() + SPLIT + jobClient.getGroupName();
        } else {
            jobResource = jobClient.getGroupName();
        }

        return jobResource + SPLIT + computeResourceType.name();
    }
}
