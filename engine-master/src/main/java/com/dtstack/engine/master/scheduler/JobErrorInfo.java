package com.dtstack.engine.master.scheduler;

/**
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/30
 */
public class JobErrorInfo {

    private String taskName;

    private String jobKey;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }
}
