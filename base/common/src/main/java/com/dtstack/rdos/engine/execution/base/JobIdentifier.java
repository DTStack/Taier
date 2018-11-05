package com.dtstack.rdos.engine.execution.base;

/**
 * Reason:
 * Date: 2018/11/5
 * Company: www.dtstack.com
 * @author xuchao
 */

public class JobIdentifier {

    private String jobId;

    private String applicationId;

    public JobIdentifier(String jobId, String applicationId){
        this.jobId = jobId;
        this.applicationId = applicationId;
    }

    public static JobIdentifier createInstance(String jobId, String applicationId){
        return new JobIdentifier(jobId, applicationId);
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
