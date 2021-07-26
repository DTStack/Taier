package com.dtstack.engine.common.api.message;

import com.dtstack.engine.common.JobClient;

import java.io.Serializable;

public class MessageContainerInfos implements Serializable {

    private static final long serialVersionUID = 7344103223135090544L;
    private JobClient jobClient;

    public MessageContainerInfos(JobClient jobClient){
        this.jobClient = jobClient;
    }

    public JobClient getJobClient() {
        return jobClient;
    }

    public void setJobClient(JobClient jobClient) {
        this.jobClient = jobClient;
    }
}
