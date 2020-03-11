package com.dtstack.engine.common;

import java.io.Serializable;

/**
 * Reason:
 * Date: 2018/11/5
 * Company: www.dtstack.com
 * @author xuchao
 */

public class JobIdentifier implements Serializable {

    private String engineJobId;

    private String applicationId;

    private String taskId;

    public JobIdentifier(String engineJobId, String applicationId, String taskId){
        this.engineJobId = engineJobId;
        this.applicationId = applicationId;
        this.taskId = taskId;
    }

    public static JobIdentifier createInstance(String jobId, String applicationId, String taskId){
        return new JobIdentifier(jobId, applicationId, taskId);
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "JobIdentifier{" +
                "engineJobId='" + engineJobId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", taskId='" + taskId + '\'' +
                '}';
    }
}
