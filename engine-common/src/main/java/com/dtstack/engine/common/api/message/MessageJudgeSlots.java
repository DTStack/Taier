package com.dtstack.engine.common.api.message;

import com.dtstack.engine.common.JobClient;

import java.io.Serializable;

public class MessageJudgeSlots implements Serializable {
    private static final long serialVersionUID = 2719700743057188135L;
    private JobClient jobClient;

    public MessageJudgeSlots(JobClient jobClient){
        this.jobClient = jobClient;
    }

    public JobClient getJobClient() {
        return jobClient;
    }

    public void setJobClient(JobClient jobClient) {
        this.jobClient = jobClient;
    }
}
