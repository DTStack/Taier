package com.dtstack.engine.common.message;

import com.dtstack.engine.common.JobClient;

import java.io.Serializable;

public class MessageContainerInfos implements Serializable {
    private static final long serialVersionUID = 1L;

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
