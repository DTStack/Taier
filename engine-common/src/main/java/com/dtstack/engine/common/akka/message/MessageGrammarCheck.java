package com.dtstack.engine.common.akka.message;

import com.dtstack.engine.common.JobClient;

import java.io.Serializable;

public class MessageGrammarCheck implements Serializable {
    private static final long serialVersionUID = 1L;

    private JobClient jobClient;

    public MessageGrammarCheck(JobClient jobClient){
        this.jobClient = jobClient;
    }

    public JobClient getJobClient() {
        return jobClient;
    }

    public void setJobClient(JobClient jobClient) {
        this.jobClient = jobClient;
    }
}
