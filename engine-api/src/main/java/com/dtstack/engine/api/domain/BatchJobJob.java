package com.dtstack.engine.api.domain;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class BatchJobJob extends AppTenantEntity {

    private String jobKey;

    private String parentJobKey;


    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public String getParentJobKey() {
        return parentJobKey;
    }

    public void setParentJobKey(String parentJobKey) {
        this.parentJobKey = parentJobKey;
    }
}
