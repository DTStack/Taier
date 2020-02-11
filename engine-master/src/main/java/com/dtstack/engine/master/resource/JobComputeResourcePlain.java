package com.dtstack.engine.master.resource;

import com.dtstack.engine.common.JobClient;
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

    public String getJobResource(JobClient jobClient) {
        ComputeResourceType computeResourceType = commonResource.getComputeResourceType(jobClient);
        return jobClient.getGroupName() + "_" + computeResourceType.name();
    }
}
