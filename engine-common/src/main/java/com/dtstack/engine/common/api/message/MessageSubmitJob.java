package com.dtstack.engine.common.api.message;

import com.dtstack.engine.common.JobClient;

import java.io.Serializable;

public class MessageSubmitJob implements Serializable {
    private static final long serialVersionUID = -2912952198447317247L;
    private JobClient jobClient;

    public MessageSubmitJob(JobClient jobClient){
        this.jobClient = jobClient;
    }

    public JobClient getJobClient() {
        return jobClient;
    }

    public void setJobClient(JobClient jobClient) {
        this.jobClient = jobClient;
    }
}
